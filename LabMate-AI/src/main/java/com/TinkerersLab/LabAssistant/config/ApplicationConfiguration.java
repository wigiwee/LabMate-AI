package com.TinkerersLab.LabAssistant.config;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.TinkerersLab.LabAssistant.config.properties.LLMProviderProperties;
import com.TinkerersLab.LabAssistant.config.properties.ReRankingProviderProperties;
import com.TinkerersLab.LabAssistant.config.properties.VectorStoreProperties;
import com.TinkerersLab.LabAssistant.model.llm.RagAiAssistant;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.cohere.CohereScoringModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.aggregator.ReRankingContentAggregator;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.transformer.ExpandingQueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationConfiguration {

    LLMProviderProperties llmProviderProperties;

    VectorStoreProperties vectorStoreProperties;

    ReRankingProviderProperties reRankingProviderProperties;

    @Bean
    EmbeddingModel embeddingModel() {

        return OllamaEmbeddingModel.builder()
                .baseUrl(llmProviderProperties.getBaseUrl())
                .modelName(llmProviderProperties.getEmbeddingModel())
                .build();
    }

    @Bean
    EmbeddingStore<TextSegment> embeddingStore() {
        return PgVectorEmbeddingStore.builder()
                .host(vectorStoreProperties.getHost())
                .port(vectorStoreProperties.getPort())
                .user(vectorStoreProperties.getUser())
                .password(vectorStoreProperties.getPassword())
                .database(vectorStoreProperties.getDatabase())
                .table(vectorStoreProperties.getTable())
                .createTable(vectorStoreProperties.isCreateTable())
                .dimension(embeddingModel().dimension())
                .useIndex(true)
                .indexListSize(vectorStoreProperties.getIndexListSize())
                .dropTableFirst(true)
                .build();

    }

    @Bean
    CohereScoringModel scoringModel() {
        return CohereScoringModel.builder()
                .apiKey(reRankingProviderProperties.getApiKey())
                .modelName(reRankingProviderProperties.getModelName())
                .build();
    }

    @Bean
    RagAiAssistant ragAiAssistant() {
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore())
                .embeddingModel(embeddingModel())
                .minScore(0.7)
                .maxResults(15)
                .build();

        DefaultContentInjector contentInjector = DefaultContentInjector.builder()
                .metadataKeysToInclude(List.of("file_name", "index"))
                .build();

        ContentAggregator contentAggregator = ReRankingContentAggregator.builder()
                .scoringModel(scoringModel())
                .querySelector(queryToContent -> {
                    return queryToContent.entrySet().stream()
                            .max(Comparator.comparingInt(entry -> entry.getValue().size()))
                            .map(Map.Entry::getKey)
                            .orElseThrow(() -> new IllegalArgumentException("No queries found"));
                })
                .minScore(0.8)
                .build();

        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .contentInjector(contentInjector)
                .contentAggregator(contentAggregator)
                .queryTransformer(new ExpandingQueryTransformer(chatLanguageModel()))
                .build();

        return AiServices.builder(RagAiAssistant.class)
                .chatLanguageModel(chatLanguageModel())
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    @Bean
    ChatLanguageModel chatLanguageModel() {
        return OllamaChatModel.builder()
                .baseUrl(llmProviderProperties.getBaseUrl())
                .modelName(llmProviderProperties.getChatModel())
                .temperature(llmProviderProperties.getTemperature())
                .logRequests(llmProviderProperties.isLogRequests())
                .logResponses(llmProviderProperties.isLogResponses())
                .build();
    }
}
