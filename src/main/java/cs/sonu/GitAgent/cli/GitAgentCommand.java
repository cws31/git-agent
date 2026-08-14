package cs.sonu.GitAgent.cli;

import cs.sonu.GitAgent.commit.*;
import cs.sonu.GitAgent.dto.CommitRequest;
import cs.sonu.GitAgent.dto.CommitResponse;
import cs.sonu.GitAgent.git.GitCommitService;
import cs.sonu.GitAgent.git.GitContextCollector;
import cs.sonu.GitAgent.git.GitHookManager;
import cs.sonu.GitAgent.interaction.ApprovalService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

@Component
@Command(name = "aigit", mixinStandardHelpOptions = true, version = "1.0.0", description = "Universal AI-powered Git commit quality agent.")
public class GitAgentCommand implements Callable<Integer> {

    private final GitContextCollector contextCollector;
    private final CommitFormatter formatter;
    private final CommitValidator validator;
    private final GitCommitService commitService;
    private final GitHookManager hookManager;
    private final ApprovalService approvalService;
    private final RestTemplate restTemplate;

    @Value("${cwsgit.backend.url:http://localhost:8080/api/v1/generate-commit}")
    private String backendUrl;

    public GitAgentCommand(GitContextCollector contextCollector,
            CommitFormatter formatter,
            CommitValidator validator,
            GitCommitService commitService,
            GitHookManager hookManager,
            ApprovalService approvalService,
            RestTemplate restTemplate) {
        this.contextCollector = contextCollector;
        this.formatter = formatter;
        this.validator = validator;
        this.commitService = commitService;
        this.hookManager = hookManager;
        this.approvalService = approvalService;
        this.restTemplate = restTemplate;
    }

    @Override
    public Integer call() {
        System.out.println("AI Git Agent CLI active. Run 'cwsgit --help' for options.");
        return 0;
    }

    @Command(name = "install", description = "Install the AI agent pre-push hook into current repository")
    public int installHook() {
        try {
            ApplicationHome home = new ApplicationHome(GitAgentCommand.class);
            File jarFile = home.getSource();

            if (jarFile == null) {
                System.err.println("Failed to determine JAR path.");
                return 1;
            }

            hookManager.installHook(jarFile.getAbsolutePath());
            return 0;
        } catch (Exception e) {
            System.err.println("Failed to install hook: " + e.getMessage());
            return 1;
        }
    }

    @Command(name = "pre-push", description = "Analyze current commit and suggest improvements before push")
    public int runPrePushHook(@Parameters(arity = "0..*") List<String> gitArgs) {
        System.out.println("\nAI Git Agent");
        System.out.println("────────────\n");

        CommitContext context;
        try {
            System.out.println("Analyzing repository context...");
            context = contextCollector.collectContext();
        } catch (Exception e) {
            System.err.println("Error reading Git context: " + e.getMessage());
            return cancelPushPrompt();
        }

        System.out.println("✓ Context collected (" + context.changedFiles().size() + " files modified)");
        System.out.println("Generating production-grade commit message via AI proxy...\n");

        GeneratedCommit generatedCommit;
        try {
            CommitRequest requestPayload = new CommitRequest(
                    context.originalMessage() != null ? context.originalMessage().trim() : "",
                    context.commitSha() != null ? context.commitSha() : "",
                    context.changedFiles() != null && !context.changedFiles().isEmpty()
                            ? context.changedFiles().toString()
                            : "None",
                    context.recentCommits() != null && !context.recentCommits().isEmpty()
                            ? String.join(" | ", context.recentCommits())
                            : "None",
                    context.diff() != null && !context.diff().isBlank() ? context.diff() : "No diff available");

            ResponseEntity<CommitResponse> response = restTemplate.postForEntity(backendUrl, requestPayload,
                    CommitResponse.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null
                    || !response.getBody().success()) {
                String errorMsg = response.getBody() != null ? response.getBody().error() : "Unknown proxy error";
                throw new RuntimeException(errorMsg);
            }

            generatedCommit = GeneratedCommit.fromRawMessage(response.getBody().commitMessage());

        } catch (Exception e) {
            System.err.println(
                    "\nUnable to generate a commit message. AI provider unavailable or timed out: " + e.getMessage());
            return cancelPushPrompt();
        }

        CommitValidator.ValidationResult validation = validator.validate(generatedCommit);
        if (!validation.isValid()) {
            System.err.println("\nValidation warning: " + validation.reason());
            return cancelPushPrompt();
        }

        String finalMessage = formatter.format(generatedCommit);
        ApprovalService.Choice choice = approvalService.promptUser(context.originalMessage(), finalMessage);

        try {
            switch (choice) {
                case YES -> {
                    commitService.amendCommit(finalMessage);
                    System.out.println("\n\033[32m✓ Commit amended successfully. Proceeding with push...\033[0m\n");
                    return 0;
                }
                case EDIT -> {
                    String editedMessage = approvalService.openSystemEditor(finalMessage);
                    commitService.amendCommit(editedMessage);
                    System.out.println("\n\033[32m✓ Custom commit applied. Proceeding with push...\033[0m\n");
                    return 0;
                }
                case NO -> {
                    return cancelPushPrompt();
                }
            }
        } catch (Exception e) {
            System.err.println("\nFailed to amend commit: " + e.getMessage());
            return cancelPushPrompt();
        }

        return 0;
    }

    private int cancelPushPrompt() {
        System.out.println("\n\033[33mPush cancelled.\033[0m Your original commit remains untouched.");
        System.out.println("To push without AI review, run:\n");
        System.out.println("  \033[1mgit push --no-verify origin main\033[0m\n");
        return 1;
    }
}