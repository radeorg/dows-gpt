package org.dows.gpt.yml;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "dows.llm")
public class LlmClientsProperties {

    private Map<String, LlmClientConfig> clients;

}