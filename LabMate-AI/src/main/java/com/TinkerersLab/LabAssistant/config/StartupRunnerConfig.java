package com.TinkerersLab.LabAssistant.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.TinkerersLab.LabAssistant.service.IngestionService;
import com.TinkerersLab.LabAssistant.utils.Utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StartupRunnerConfig {

    IngestionService ingestionService;

    @Bean
    @SuppressWarnings("unchecked")
    public CommandLineRunner commandLineRunner() {
        return args -> {
            File ingestionRecordFile = new File(ApplicationConstants.DEFAULT_INGESTION_RECORD_PATH);
            if (ingestionRecordFile.createNewFile()) {
                // file dosen't exist new file created
                log.info("Ingestion Record File created at " + ApplicationConstants.DEFAULT_INGESTION_RECORD_PATH);
                ApplicationConstants.INGESTION_RECORD = new HashMap<>();
            } else {
                // file already exists, existing file loaded to
                // ApplicationConstants.INGESTION_RECORD
                log.info("Ingestion file loaded to ApplicationConstatns.INGESTION_RECORD");

                ObjectInputStream oIn = new ObjectInputStream(new FileInputStream(ingestionRecordFile));

                Object obj = oIn.readObject();
                oIn.close();
                if (obj instanceof HashMap<?, ?>) {
                    ApplicationConstants.INGESTION_RECORD = (HashMap<String, String>) obj;
                } else {
                    throw new ClassCastException("Expected a HashMap<String, String> but found " + obj.getClass()
                            + "at " + ApplicationConstants.DEFAULT_INGESTION_RECORD_PATH);
                }
            }

            for (String path : ApplicationConstants.DEFAULT_RESOURCE_PATH) {
                ingestionService.ingest(path, 500);
                log.info("All files ingested from " + path);
            }

            // writing the ApplicationConstants.INGESTION_RECORD to file
            ObjectOutputStream oOut = new ObjectOutputStream(new FileOutputStream(ingestionRecordFile));
            oOut.writeObject(ApplicationConstants.INGESTION_RECORD);
            oOut.close();
            log.info("ApplicationConstants.INGESTION_RECORD object has been serialized and written to "
                    + ApplicationConstants.DEFAULT_INGESTION_RECORD_PATH);

            ApplicationConstants.CENSORED_WORDS = Utils.getCensoredWords();
            log.info("Censored Words successfully loaded from " + ApplicationConstants.DEFAULT_CENSORED_WORDS_PATH);
        };
    }

}
