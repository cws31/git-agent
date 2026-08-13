package cs.sonu.GitAgent.git;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

@Component
public class GitHookManager {

    private final GitService gitService;

    public GitHookManager(GitService gitService) {
        this.gitService = gitService;
    }

    public void installHook(String jarAbsolutePath) throws IOException {
        String repoRoot = gitService.executeCommand("git", "rev-parse", "--show-toplevel");
        Path hookPath = Paths.get(repoRoot, ".git", "hooks", "pre-push");

        // Convert backslashes for Windows path safety inside shell script
        String formattedJarPath = jarAbsolutePath.replace("\\", "/");

        String hookScript = """
                #!/bin/sh
                # AI Git Commit Agent Pre-Push Hook

                # Check if --no-verify flag was passed or bypass requested
                if [ "$1" = "--no-verify" ]; then
                    exit 0
                fi

                # Re-attach interactive terminal input stream for prompts
                exec < /dev/tty

                # Execute AI Agent JAR
                java -jar "%s" pre-push "$@"

                # Capture the exit status from the AI Agent
                EXIT_CODE=$?

                if [ $EXIT_CODE -ne 0 ]; then
                    echo "AI Git Agent rejected the push. Push cancelled."
                    exit $EXIT_CODE
                fi

                exit 0
                """.formatted(formattedJarPath);

        Files.writeString(hookPath, hookScript);
        File hookFile = hookPath.toFile();

        // Make the pre-push hook executable (Linux/macOS/Windows)
        try {
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(hookPath);
            perms.add(PosixFilePermission.OWNER_EXECUTE);
            perms.add(PosixFilePermission.GROUP_EXECUTE);
            perms.add(PosixFilePermission.OTHERS_EXECUTE);
            Files.setPosixFilePermissions(hookPath, perms);
        } catch (UnsupportedOperationException e) {
            // Windows OS fallback
            hookFile.setExecutable(true);
        }

        System.out.println("✓ Pre-push hook successfully installed at: " + hookPath);
    }
}