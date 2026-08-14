package cs.sonu.GitAgent.dto;

public record CommitRequest(
        String originalMessage,
        String commitSha,
        String changedFiles,
        String recentCommits,
        String diff) {
}