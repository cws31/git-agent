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

        String formattedJarPath = jarAbsolutePath.replace("\\", "/");

        // Universal hook for Windows CMD, Git Bash, macOS, and Linux
        String hookScript = """
                #!/bin/sh
                # AI Git Commit Agent Pre-Push Hook

                echo "----------------------------------------------------"
                echo "Git Agent: Inspecting pre-push commit..."
                echo "----------------------------------------------------"

                # Give the Java CLI access to the interactive terminal
                if [ -e /dev/tty ]; then
                    exec < /dev/tty
                fi

                java -jar "%s" pre-push "$@"

                EXIT_CODE=$?

                if [ $EXIT_CODE -ne 0 ]; then
                    echo "❌ AI Git Agent rejected the push. Push cancelled."
                    exit $EXIT_CODE
                fi

                echo "✓ AI Git Agent approved the push."
                exit 0
                """.formatted(formattedJarPath);

        Files.writeString(hookPath, hookScript);
        File hookFile = hookPath.toFile();

        try {
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(hookPath);
            perms.add(PosixFilePermission.OWNER_EXECUTE);
            perms.add(PosixFilePermission.GROUP_EXECUTE);
            perms.add(PosixFilePermission.OTHERS_EXECUTE);
            Files.setPosixFilePermissions(hookPath, perms);
        } catch (UnsupportedOperationException e) {
            hookFile.setExecutable(true);
        }

        System.out.println("✓ Pre-push hook successfully re-installed at: " + hookPath);
    }
}