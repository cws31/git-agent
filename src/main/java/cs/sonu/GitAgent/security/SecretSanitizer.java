package cs.sonu.GitAgent.security;

import org.springframework.stereotype.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SecretSanitizer {

    private record SanitizerRule(Pattern pattern, String replacement) {
    }

    private static final SanitizerRule[] RULES = new SanitizerRule[] {

            new SanitizerRule(
                    Pattern.compile(
                            "(?i)(api[_-]?key|secret|password|bearer|token|auth)\\s*[:=]\\s*['\"]?([^'\"\\s]+)['\"]?"),
                    "$1: [REDACTED_BY_GITAGENT]"),

            new SanitizerRule(
                    Pattern.compile("(A3T[A-Z0-9]|AKIA|AGPA|AIDA|AROA|AIPA|ANPA|ANVA|ASIA)[A-Z0-9]{16}"),
                    "[AWS_KEY_REDACTED]"),

            new SanitizerRule(
                    Pattern.compile("-----BEGIN [A-Z ]+ PRIVATE KEY-----"),
                    "-----BEGIN REDACTED PRIVATE KEY-----")
    };

    public String sanitize(String diff) {
        if (diff == null || diff.isBlank()) {
            return diff;
        }

        String sanitized = diff;
        for (SanitizerRule rule : RULES) {
            sanitized = rule.pattern().matcher(sanitized).replaceAll(rule.replacement());
        }
        return sanitized;
    }
}