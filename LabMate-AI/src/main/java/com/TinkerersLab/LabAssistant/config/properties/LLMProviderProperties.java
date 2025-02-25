package com.TinkerersLab.LabAssistant.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "labmate.llm-provider")
public class LLMProviderProperties {

    private String baseUrl;
    private String chatModel;
    private double temperature;
    private boolean logRequests;
    private boolean logResponses;
    private String embeddingModel;

}
