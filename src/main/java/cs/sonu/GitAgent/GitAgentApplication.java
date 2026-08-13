package cs.sonu.GitAgent;

import cs.sonu.GitAgent.cli.GitAgentCommand;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
public class GitAgentApplication implements CommandLineRunner, ExitCodeGenerator {

	private final GitAgentCommand gitAgentCommand;
	private int exitCode;

	public GitAgentApplication(GitAgentCommand gitAgentCommand) {
		this.gitAgentCommand = gitAgentCommand;
	}

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(GitAgentApplication.class, args)));
	}

	@Override
	public void run(String... args) {
		// Run PicoCLI command
		this.exitCode = new CommandLine(gitAgentCommand).execute(args);
	}

	@Override
	public int getExitCode() {
		return this.exitCode;
	}
}