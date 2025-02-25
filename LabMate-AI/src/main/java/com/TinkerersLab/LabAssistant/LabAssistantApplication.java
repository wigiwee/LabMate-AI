package com.TinkerersLab.LabAssistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.TinkerersLab.LabAssistant.config.properties.LLMProviderProperties;
import com.TinkerersLab.LabAssistant.config.properties.ReRankingProviderProperties;
import com.TinkerersLab.LabAssistant.config.properties.VectorStoreProperties;

@EnableConfigurationProperties({
		LLMProviderProperties.class,
		VectorStoreProperties.class,
		ReRankingProviderProperties.class })
@SpringBootApplication
public class LabAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(LabAssistantApplication.class, args);
	}

}
