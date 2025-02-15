package com.TinkerersLab.LabAssistant.service;

import java.io.File;
import java.net.MalformedURLException;

import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.TinkerersLab.LabAssistant.config.ApplicationConstants;
import com.TinkerersLab.LabAssistant.util.Utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class IngestionService {

    private final VectorStore vectorStore;

    private final Utils utils;

    public void ingestAll(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles();

        for (File file : files) {
            String fileHash = ApplicationConstants.INGESTION_RECORD.get(file.getName());
            if (fileHash != null) {
                continue;
            }
            log.info("embedding text to vector " + file.getName());
            Resource resource;
            try {
                resource = new FileUrlResource(file.getAbsolutePath());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                continue;
            }

            var pdfReader = new PagePdfDocumentReader(resource);
            TextSplitter textSplitter = new TokenTextSplitter();
            vectorStore.accept(textSplitter.apply(pdfReader.get()));

            ApplicationConstants.INGESTION_RECORD.put(file.getName(), utils.getFileHash(file));
            log.info("Vector store loaded with " + file.getName());
        }
    }

}
