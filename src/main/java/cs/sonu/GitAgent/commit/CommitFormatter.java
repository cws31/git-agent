package cs.sonu.GitAgent.commit;

import org.springframework.stereotype.Component;

@Component
public class CommitFormatter {

    /**
     * Formats the commit message by generating a concise category header line
     * while preserving the user's full original commit message in the body.
     */
    public String format(GeneratedCommit generatedCommit, String originalMessage) {
        String header = generatedCommit != null && generatedCommit.header() != null
                ? generatedCommit.header().trim()
                : "chore: update code";

        if (originalMessage == null || originalMessage.isBlank()) {
            return header;
        }

        String cleanOriginal = originalMessage.trim();

        // Avoid duplicating header if user already formatted it manually
        if (cleanOriginal.startsWith(header)) {
            return cleanOriginal;
        }

        // Format: Categorized Short Header -> Blank Line -> Preserved Original Message
        return String.format("%s%n%n%s", header, cleanOriginal);
    }
}