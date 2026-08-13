package cs.sonu.GitAgent.commit;

import dev.langchain4j.model.output.structured.Description;

public record GeneratedCommit(
        @Description("The conventional commit type, e.g., feat, fix, chore, refactor, docs, etc.")
        String type,
        
        @Description("The scope of the change. Leave empty if not applicable or unclear.")
        String scope,
        
        @Description("A short, imperative summary of the change.")
        String subject,
        
        @Description("A detailed explanation of what and why. Leave empty if unnecessary.")
        String body,
        
        @Description("True only if this commit introduces a breaking change.")
        boolean breakingChange
) {}