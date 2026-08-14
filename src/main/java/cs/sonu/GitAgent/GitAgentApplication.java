package cs.sonu.GitAgent;

import cs.sonu.GitAgent.cli.GitAgentCommand;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@SpringBootApplication
public class GitAgentApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(GitAgentApplication.class);

		if (args.length > 0) {
			app.setWebApplicationType(WebApplicationType.NONE);
			ConfigurableApplicationContext context = app.run(args);
			GitAgentCommand command = context.getBean(GitAgentCommand.class);
			CommandLine.IFactory factory = context.getBean(CommandLine.IFactory.class);
			int exitCode = new CommandLine(command, factory).execute(args);
			System.exit(exitCode);
		} else {
			app.setWebApplicationType(WebApplicationType.SERVLET);
			app.run(args);
		}
	}
}