package cs.sonu.GitAgent.commit;

import org.springframework.stereotype.Component;

@Component
public class CommitFormatter {
    
    public String format(GeneratedCommit commit) {
        StringBuilder header = new StringBuilder(commit.type());
        
        if (commit.scope() != null && !commit.scope().isBlank()) {
            header.append("(").append(commit.scope().trim()).append(")");
        }
        
        if (commit.breakingChange()) {
            header.append("!");
        }
        
        header.append(": ").append(commit.subject().trim());

        if (commit.body() == null || commit.body().isBlank()) {
            return header.toString();
        }

        return header.toString() + "\n\n" + commit.body().trim();
    }
}