package cs.sonu.GitAgent.git;

import cs.sonu.GitAgent.commit.ChangeType;
import cs.sonu.GitAgent.commit.ChangedFile;
import cs.sonu.GitAgent.commit.CommitContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GitContextCollector {

    private final GitService gitService;
    private static final int MAX_DIFF_LENGTH = 8000;

    public GitContextCollector(GitService gitService) {
        this.gitService = gitService;
    }

    public CommitContext collectContext() {
        String sha = gitService.executeCommand("git", "rev-parse", "HEAD");
        String message = gitService.executeCommand("git", "log", "-1", "--pretty=%B");
        String diff = collectDiff();
        List<ChangedFile> files = collectChangedFiles();
        List<String> history = collectRecentCommits();

        return new CommitContext(sha, message, files, diff, history);
    }

    private String collectDiff() {
        String diff;
        try {
            // Try comparing HEAD to previous commit (works for 2nd commit onwards)
            diff = gitService.executeCommand("git", "diff", "HEAD~1", "HEAD");
        } catch (Exception e) {
            // Root commit fallback: show the diff of the initial commit itself
            try {
                diff = gitService.executeCommand("git", "show", "--pretty=", "HEAD");
            } catch (Exception ex) {
                diff = "Unable to retrieve diff.";
            }
        }

        if (diff.length() > MAX_DIFF_LENGTH) {
            return diff.substring(0, MAX_DIFF_LENGTH) + "\n\n... [DIFF TRUNCATED FOR LENGTH]";
        }
        return diff;
    }

    private List<ChangedFile> collectChangedFiles() {
        try {
            String output = gitService.executeCommand("git", "diff-tree", "--no-commit-id", "--name-status", "-r",
                    "HEAD");
            if (output.isBlank())
                return Collections.emptyList();

            return Arrays.stream(output.split("\n"))
                    .map(line -> line.split("\\s+", 2))
                    .filter(parts -> parts.length == 2)
                    .map(parts -> new ChangedFile(parts[1], ChangeType.fromGitStatus(parts[0])))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<String> collectRecentCommits() {
        try {
            // Get last 10 commits, skip the current HEAD
            String output = gitService.executeCommand("git", "log", "-10", "--skip=1", "--pretty=%s");
            if (output.isBlank())
                return Collections.emptyList();
            return Arrays.asList(output.split("\n"));
        } catch (Exception e) {
            // Root commit has no previous history
            return Collections.emptyList();
        }
    }
}