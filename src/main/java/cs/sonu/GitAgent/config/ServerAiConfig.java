package cs.sonu.GitAgent.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Configuration
public class ServerAiConfig {

    @Value("${groq.api.key:}")
    private String groqApiKey;

    @Value("${groq.base-url:https://api.groq.com/openai/v1}")
    private String groqBaseUrl;

    @Value("${groq.model-name:llama-3.3-70b-versatile}")
    private String groqModelName;

    @Bean
    @Primary
    public ChatLanguageModel chatLanguageModel() {
        String apiKeyToUse = (groqApiKey != null && !groqApiKey.isBlank())
                ? groqApiKey
                : System.getenv("GROQ_API_KEY");

        if (apiKeyToUse == null || apiKeyToUse.isBlank()) {
            apiKeyToUse = "dummy-key-set-groq-api-key-env-var";
        }

        return OpenAiChatModel.builder()
                .baseUrl(groqBaseUrl)
                .apiKey(apiKeyToUse)
                .modelName(groqModelName)
                .temperature(0.1)
                .timeout(Duration.ofSeconds(15))
                .build();
    }
}