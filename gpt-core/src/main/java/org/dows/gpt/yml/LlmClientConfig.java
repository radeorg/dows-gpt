package org.dows.gpt.yml;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class LlmClientConfig {

    private Boolean enabled;

    private String apiUrl;

    private String apiKey;

    private String model;

    private BigDecimal pricePerToken;

}