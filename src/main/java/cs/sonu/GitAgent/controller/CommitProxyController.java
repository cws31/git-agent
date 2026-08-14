package cs.sonu.GitAgent.controller;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CommitProxyController {

    private final ChatLanguageModel chatLanguageModel;

    public CommitProxyController(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    @PostMapping("/generate-commit")
    public ResponseEntity<String> generateCommit(@RequestBody String contextPayload) {
        try {
            String systemPrompt = """
                    You are a senior engineer git agent. Analyze the given git context and generate a single conventional commit message.
                    Rules:
                    1. Format: <type>(<scope>): <subject>
                    2. Return ONLY the commit header and concise body. No explanations, no markdown tutorial code blocks, no preamble.
                    """;

            String fullPrompt = systemPrompt + "\n\nGit Context:\n" + contextPayload;
            String aiResponse = chatLanguageModel.generate(fullPrompt);

            return ResponseEntity.ok(aiResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing request: " + e.getMessage());
        }
    }
}