package cs.sonu.GitAgent.security;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class SecretSanitizer {

    private static final Pattern[] SECRET_PATTERNS = new Pattern[] {
            // API Keys & Tokens
            Pattern.compile("(?i)(api[_-]?key|secret|password|bearer|token|auth)\\s*[:=]\\s*['\"]?([^'\"\\s]+)['\"]?"),
            // AWS Credentials
            Pattern.compile("(A3T[A-Z0-9]|AKIA|AGPA|AIDA|AROA|AIPA|ANPA|ANVA|ASIA)[A-Z0-9]{16}"),
            // Private Keys
            Pattern.compile("-----BEGIN [A-Z ]+ PRIVATE KEY-----")
    };

    public String sanitize(String diff) {
        if (diff == null || diff.isBlank())
            return diff;

        String sanitized = diff;
        for (Pattern pattern : SECRET_PATTERNS) {
            sanitized = pattern.matcher(sanitized).replaceAll("$1: [REDACTED_BY_GITAGENT]");
        } // adede for check
        return sanitized;
    }
}