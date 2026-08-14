package cs.sonu.GitAgent;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class GitAgentApplication {

	public static void main(String[] args) {
		if (args.length > 0) {
			// CLI Mode: Disable web server (Tomcat)
			new SpringApplicationBuilder(GitAgentApplication.class)
					.web(WebApplicationType.NONE)
					.run(args);
		} else {
			// Server Mode: Run full web server (Tomcat on port 8080)
			new SpringApplicationBuilder(GitAgentApplication.class)
					.web(WebApplicationType.SERVLET)
					.run(args);
		}
	}
}