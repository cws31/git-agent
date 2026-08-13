package cs.sonu.GitAgent.commit;

import java.util.List;

public record CommitContext(
        String commitSha,
        String originalMessage,
        List<ChangedFile> changedFiles,
        String diff,
        List<String> recentCommits
) {}