package cs.sonu.GitAgent.interaction;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.Scanner;

@Service
public class ApprovalService {

    public enum Choice {
        YES, EDIT, NO
    }

    public Choice promptUser(String originalCommit, String suggestedCommit) {
        System.out.println("\nAI Git Commit Agent");
        System.out.println("────────────────────────────────────────");
        System.out.println("Original commit:");
        System.out.println(originalCommit);
        System.out.println("────────────────────────────────────────");
        System.out.println("Generated commit:");
        System.out.println(suggestedCommit);
        System.out.println("────────────────────────────────────────\n");

        while (true) {
            System.out.print("Use generated commit? [Yes / Edit / No]: ");
            System.out.flush();

            String input = readInteractiveLine();

            if (input == null) {
                System.out.println("\nNo interactive terminal detected. Cancelling push.");
                return Choice.NO;
            }

            input = input.trim().toLowerCase();

            if (input.equals("y") || input.equals("yes")) {
                return Choice.YES;
            } else if (input.equals("e") || input.equals("edit")) {
                return Choice.EDIT;
            } else if (input.equals("n") || input.equals("no")) {
                return Choice.NO;
            } else {
                System.out.println("Please enter Y, E, or N.");
            }
        }
    }

    private String readInteractiveLine() {
        Console console = System.console();
        if (console != null) {
            return console.readLine();
        }

        try {
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            String ttyDevice = isWindows ? "CON" : "/dev/tty";
            try (BufferedReader ttyReader = new BufferedReader(new FileReader(ttyDevice))) {
                return ttyReader.readLine();
            }
        } catch (Exception ignored) {
        }

        try {
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNextLine()) {
                return scanner.nextLine();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public String openSystemEditor(String initialContent) {
        try {
            // Create a temporary file containing the AI commit message
            File tempFile = File.createTempFile("GIT_AGENT_EDIT_", ".txt");
            tempFile.deleteOnExit();
            Files.writeString(tempFile.toPath(), initialContent);

            // Determine system default editor (Notepad on Windows, $EDITOR or nano/vim on
            // Linux/macOS)
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            String editor = System.getenv("EDITOR");

            ProcessBuilder pb;
            if (isWindows) {
                editor = (editor != null && !editor.isBlank()) ? editor : "notepad.exe";
                pb = new ProcessBuilder(editor, tempFile.getAbsolutePath());
            } else {
                editor = (editor != null && !editor.isBlank()) ? editor : "nano";
                pb = new ProcessBuilder("sh", "-c", editor + " " + tempFile.getAbsolutePath());
            }

            // Inherit I/O so the editor opens interactively in terminal/window
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();

            // Read the edited content back from the file
            String editedContent = Files.readString(tempFile.toPath()).trim();
            return editedContent.isEmpty() ? initialContent : editedContent;

        } catch (Exception e) {
            System.err.println("Failed to launch editor, retaining AI message: " + e.getMessage());
            return initialContent;
        }
    }
}