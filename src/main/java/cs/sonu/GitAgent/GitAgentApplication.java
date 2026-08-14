package cs.sonu.GitAgent;

import cs.sonu.GitAgent.cli.GitAgentCommand;
import picocli.CommandLine;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class GitAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(GitAgentApplication.class, args);
	}
}

// Only execute CLI when specifically running with the "cli" profile
@Component
@Profile("cli")
class CliRunner implements CommandLineRunner, ExitCodeGenerator {

	private final GitAgentCommand command;
	private final CommandLine.IFactory factory;
	private int exitCode;

	public CliRunner(GitAgentCommand command, CommandLine.IFactory factory) {
		this.command = command;
		this.factory = factory;
	}

	@Override
	public void run(String... args) {
		exitCode = new CommandLine(command, factory).execute(args);
	}

	@Override
	public int getExitCode() {
		return exitCode;
	}
}