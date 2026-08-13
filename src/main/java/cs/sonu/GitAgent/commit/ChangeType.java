package cs.sonu.GitAgent.commit;

public enum ChangeType {
    ADDED, MODIFIED, DELETED, RENAMED, UNKNOWN;
    
    public static ChangeType fromGitStatus(String status) {
        if (status == null || status.isEmpty()) return UNKNOWN;
        return switch (status.charAt(0)) {
            case 'A' -> ADDED;
            case 'M' -> MODIFIED;
            case 'D' -> DELETED;
            case 'R' -> RENAMED;
            default -> UNKNOWN;
        };
    }
}