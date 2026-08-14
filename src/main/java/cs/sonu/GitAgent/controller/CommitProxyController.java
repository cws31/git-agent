package cs.sonu.GitAgent.controller;

import cs.sonu.GitAgent.dto.CommitRequest;
import cs.sonu.GitAgent.dto.CommitResponse;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CommitProxyController {

    private final ChatLanguageModel chatLanguageModel;

    public CommitProxyController(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    @PostMapping("/generate-commit")
    public ResponseEntity<CommitResponse> generateCommit(@RequestBody CommitRequest request) {
        try {
            String systemPrompt = """
                    You are a senior engineer git agent. Analyze the given git context and generate a single conventional commit message.
                    Rules:
                    1. Format: <type>(<scope>): <subject>
                    2. Return ONLY the commit header and concise body. No markdown formatting, code blocks, or chatter.
                    """;

            String userPrompt = String.format("""
                    Original Message: %s
                    Commit SHA: %s
                    Changed Files: %s
                    Recent Commits: %s

                    Diff:
                    %s
                    """,
                    request.originalMessage(), request.commitSha(),
                    request.changedFiles(), request.recentCommits(), request.diff());

            String aiResponse = chatLanguageModel.generate(systemPrompt + "\n\n" + userPrompt);
            return ResponseEntity.ok(CommitResponse.ok(aiResponse.trim()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(CommitResponse.fail("AI Proxy processing failed: " + e.getMessage()));
        }
    }
}