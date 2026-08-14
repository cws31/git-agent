package cs.sonu.GitAgent.git;

import cs.sonu.GitAgent.commit.ChangeType;
import cs.sonu.GitAgent.commit.ChangedFile;
import cs.sonu.GitAgent.commit.CommitContext;
import cs.sonu.GitAgent.security.SecretSanitizer;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GitContextCollector {

    private final GitService gitService;
    private final SecretSanitizer secretSanitizer;
    private final DiffFilter diffFilter;
    private static final int MAX_DIFF_LENGTH = 8000;

    public GitContextCollector(GitService gitService, SecretSanitizer secretSanitizer, DiffFilter diffFilter) {
        this.gitService = gitService;
        this.secretSanitizer = secretSanitizer;
        this.diffFilter = diffFilter;
    }

    public CommitContext collectContext() {
        String sha = gitService.executeCommand("git", "rev-parse", "HEAD");
        String message = gitService.executeCommand("git", "log", "-1", "--pretty=%B");
        String rawDiff = collectDiff();

        // Clean & redact secrets from diff
        String cleanDiff = diffFilter.cleanDiff(rawDiff);
        String sanitizedDiff = secretSanitizer.sanitize(cleanDiff);

        List<ChangedFile> files = collectChangedFiles();
        List<String> history = collectRecentCommits();

        return new CommitContext(sha, message, files, sanitizedDiff, history);
    }

    private String collectDiff() {
        String diff;
        try {
            diff = gitService.executeCommand("git", "diff", "HEAD~1", "HEAD");
        } catch (Exception e) {
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
            String output = gitService.executeCommand("git", "log", "-10", "--skip=1", "--pretty=%s");
            if (output.isBlank())
                return Collections.emptyList();
            return Arrays.asList(output.split("\n"));
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}