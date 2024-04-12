package com.example.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class FileManager {

    private static String PRINT_FORM_PATH = "C:\\Users\\enazy\\IdeaProjects\\DocFiller_\\src\\main\\resources\\static\\printForms\\";
    private static String REPORT_PATH = "C:\\Users\\enazy\\IdeaProjects\\DocFiller_\\src\\main\\resources\\static\\reports\\";

    public static void upload(byte[] resource, String uuid, boolean isPrintForm) throws IOException {
        Path path = isPrintForm ? Paths.get(PRINT_FORM_PATH + uuid) : Paths.get(REPORT_PATH + uuid);
        Path file = Files.createFile(path);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file.toString());
            stream.write(resource);
        } finally {
            stream.close();
        }
    }

    public static Resource download(String uuid, boolean isPrintForm) throws IOException {
        Path path = isPrintForm ? Paths.get(PRINT_FORM_PATH + uuid) : Paths.get(REPORT_PATH + uuid);
        Resource resource = new UrlResource(path.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("Не удается скачать печатную форму из локального хранилища");
        }
    }

    public static String generateUuid(String name) {
        return DigestUtils.md5Hex(name + LocalDateTime.now());
    }
}
