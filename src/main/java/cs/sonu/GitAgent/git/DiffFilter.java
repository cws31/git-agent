package cs.sonu.GitAgent.git;

import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class DiffFilter {

    private static final String[] IGNORED_PATTERNS = {
            "package-lock.json", "yarn.lock", "pnpm-lock.yaml", "Cargo.lock",
            "go.sum", ".min.js", ".min.css", ".map"
    };

    public String cleanDiff(String rawDiff) {
        if (rawDiff == null || rawDiff.isBlank())
            return "";

        return Arrays.stream(rawDiff.split("\n"))
                .filter(line -> Arrays.stream(IGNORED_PATTERNS).noneMatch(ext -> line.contains(ext)))
                .collect(Collectors.joining("\n"));
    }
}