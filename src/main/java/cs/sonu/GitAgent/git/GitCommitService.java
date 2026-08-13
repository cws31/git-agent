package cs.sonu.GitAgent.git;

import org.springframework.stereotype.Service;

@Service
public class GitCommitService {

    private final GitService gitService;

    public GitCommitService(GitService gitService) {
        this.gitService = gitService;
    }

    public void amendCommit(String newMessage) {
        // We use --allow-empty in case the user's hook runs on an empty commit test
        gitService.executeCommand("git", "commit", "--amend", "--allow-empty", "-m", newMessage);
    }
}