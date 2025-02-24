package com.TinkerersLab.LabAssistant.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import com.TinkerersLab.LabAssistant.config.ApplicationConstants;

public class Utils {

    public static String getFileHash(File file) {
        byte[] buffer = new byte[8192];
        int count;
        MessageDigest messageDigest;

        try {
            messageDigest = MessageDigest.getInstance(ApplicationConstants.DEFAULT_HASH_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            messageDigest = null;
        }

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            while ((count = bis.read(buffer)) > 0) {
                messageDigest.update(buffer, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] hash = messageDigest.digest();

        return Base64.getEncoder().encodeToString(hash);
    }

    public static List<String> getCensoredWords() {

        List<String> censoredWords = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(ApplicationConstants.DEFAULT_CENSORED_WORDS_PATH))) {

            while (scanner.hasNextLine()) {
                censoredWords.add(scanner.nextLine().toLowerCase().trim());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return censoredWords;
        }

        return censoredWords;
    }

}
