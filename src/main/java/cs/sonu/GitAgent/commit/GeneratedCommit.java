package cs.sonu.GitAgent.commit;

import dev.langchain4j.model.output.structured.Description;

public record GeneratedCommit(
                @Description("The conventional commit type, e.g., feat, fix, chore, refactor, docs, etc.") String type,

                @Description("The scope of the change. Leave empty if not applicable or unclear.") String scope,

                @Description("A short, imperative summary of the change.") String subject,

                @Description("A detailed explanation of what and why. Leave empty if unnecessary.") String body,

                @Description("True only if this commit introduces a breaking change.") boolean breakingChange) {

        /**
         * Parses a raw AI response string into a structured GeneratedCommit record.
         */
        public static GeneratedCommit fromRawMessage(String rawMessage) {
                if (rawMessage == null || rawMessage.isBlank()) {
                        return new GeneratedCommit("chore", "", "update codebase", "", false);
                }

                String cleaned = rawMessage.trim();
                String[] lines = cleaned.split("\n", 2);
                String header = lines[0].trim();
                String body = lines.length > 1 ? lines[1].trim() : "";

                String type = "chore";
                String scope = "";
                String subject = header;
                boolean breaking = header.contains("!") || rawMessage.contains("BREAKING CHANGE");

                // Parse Conventional Commit header format: "type(scope): subject"
                if (header.contains(":")) {
                        String[] parts = header.split(":", 2);
                        String prefix = parts[0].trim();
                        subject = parts[1].trim();

                        if (prefix.contains("(") && prefix.contains(")")) {
                                type = prefix.substring(0, prefix.indexOf("(")).trim();
                                scope = prefix.substring(prefix.indexOf("(") + 1, prefix.indexOf(")")).trim();
                        } else {
                                type = prefix.replace("!", "").trim();
                        }
                }

                return new GeneratedCommit(type, scope, subject, body, breaking);
        }
}