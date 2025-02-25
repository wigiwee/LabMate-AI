package com.TinkerersLab.LabAssistant.service;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.TinkerersLab.LabAssistant.exception.IngestionFailedException;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IngestionService {

    EmbeddingModel embeddingModel;

    EmbeddingStore<TextSegment> embeddingStore;

    public void ingest(String path, int chunkSize) throws IngestionFailedException {

        File[] files = (new File(path)).listFiles();
        List<Document> docs = new ArrayList<>();

        for (File file : files) {
            docs.add(loadDocument(file.getAbsolutePath()));
        }
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(chunkSize, 50))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        ingestor.ingest(docs);

        log.info("ingested all documents at " + path);
    }

}