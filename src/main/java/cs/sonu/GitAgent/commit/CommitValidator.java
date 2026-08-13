package cs.sonu.GitAgent.commit;

import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class CommitValidator {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "feat", "fix", "refactor", "perf", "test", "docs", "build", "ci", "chore", "revert");

    public ValidationResult validate(GeneratedCommit commit) {
        if (commit == null) {
            return ValidationResult.invalid("Agent produced an empty commit response.");
        }
        if (commit.type() == null || !ALLOWED_TYPES.contains(commit.type().toLowerCase())) {
            return ValidationResult.invalid(
                    "Invalid commit type: '" + commit.type() + "'. Expected standard Conventional Commit type.");
        }
        if (commit.subject() == null || commit.subject().isBlank()) {
            return ValidationResult.invalid("Commit subject cannot be empty.");
        }
        if (commit.subject().endsWith(".")) {
            return ValidationResult.invalid("Commit subject must not end with a period.");
        }
        return ValidationResult.valid();
    }

    public record ValidationResult(boolean isValid, String reason) {
        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult invalid(String reason) {
            return new ValidationResult(false, reason);
        }
    }
}