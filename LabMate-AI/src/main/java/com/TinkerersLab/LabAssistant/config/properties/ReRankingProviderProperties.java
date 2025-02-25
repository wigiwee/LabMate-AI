package com.TinkerersLab.LabAssistant.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "reranking.provider")
public class ReRankingProviderProperties {

    private String apiKey;

    private String modelName;
}
