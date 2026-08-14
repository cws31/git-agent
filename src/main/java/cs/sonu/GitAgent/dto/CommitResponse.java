package cs.sonu.GitAgent.dto;

public record CommitResponse(
        String commitMessage,
        boolean success,
        String error) {
    public static CommitResponse ok(String commitMessage) {
        return new CommitResponse(commitMessage, true, null);
    }

    public static CommitResponse fail(String error) {
        return new CommitResponse(null, false, error);
    }
}