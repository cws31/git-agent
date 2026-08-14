package cs.sonu.GitAgent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GitAgentApplication {

	public static void main(String[] args) {

		if (args.length > 0) {

			// CLI MODE
			var context = new SpringApplication(
					GitAgentApplication.class);

			context.setWebApplicationType(WebApplicationType.NONE);

			var applicationContext = context.run(args);

			// Execute Picocli command
			var command = applicationContext.getBean(
					cs.sonu.GitAgent.cli.GitAgentCommand.class);

			int exitCode = new picocli.CommandLine(command)
					.execute(args);

			System.exit(exitCode);

		} else {

			// SERVER MODE
			SpringApplication app = new SpringApplication(GitAgentApplication.class);

			app.setWebApplicationType(WebApplicationType.SERVLET);

			app.run(args);
		}
	}
}