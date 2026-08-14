package cs.sonu.GitAgent;

import cs.sonu.GitAgent.cli.GitAgentCommand;
import picocli.CommandLine;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class GitAgentApplication {

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(GitAgentApplication.class, args)));
	}
}

@Component
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