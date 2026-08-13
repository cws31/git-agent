package cs.sonu.GitAgent.agent;

import cs.sonu.GitAgent.commit.GeneratedCommit;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface CommitMessageAgent {

    @SystemMessage("""
            You are a senior software engineer specializing in production-grade Git commit messages.

            Analyze the developer's original commit message, changed files, Git diff, and recent commit history.
            Generate a Conventional Commit.

            Rules:
            1. The Git diff is the primary source of truth.
            2. Preserve the actual intent of the developer.
            3. Never invent changes.
            4. Determine the appropriate commit type (feat, fix, refactor, docs, chore, etc.).
            5. Use a scope only when clearly justified.
            6. Use imperative language in the subject.
            7. Keep the subject concise (under 72 characters).
            8. Add a useful body when appropriate.
            9. Detect breaking changes conservatively.
            10. Do not modify source code.
            11. Do not suggest unrelated changes.
            """)
    @UserMessage("{{it}}")
    GeneratedCommit generate(String formattedPrompt);
}