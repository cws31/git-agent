package cs.sonu.GitAgent.git;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Service
public class GitService {

    public String executeCommand(String... command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String output = reader.lines().collect(Collectors.joining("\n"));
                int exitCode = process.waitFor();
                
                if (exitCode != 0) {
                    throw new RuntimeException("Git command failed: " + String.join(" ", command) + "\nOutput: " + output);
                }
                return output.trim();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute git command", e);
        }
    }
}