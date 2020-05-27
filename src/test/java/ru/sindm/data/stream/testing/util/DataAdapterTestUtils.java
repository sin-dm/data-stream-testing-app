package ru.sindm.data.stream.testing.util;

import ru.sindm.data.stream.testing.domain.service.FileService;
import ru.sindm.data.stream.testing.domain.service.impl.FileServiceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class DataAdapterTestUtils {

    private static final FileService fileService = new FileServiceImpl();

    public static String readDataFile(String fileName, String fileFolder) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get("data/" + fileFolder + "/" + fileName + ".txt"));
            return new String(bytes, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String prepareReferenceDataFile(String folderName) {
        try {
            String fileName = UUID.randomUUID().toString();
            String createdFolderName = fileService.createFolder(folderName);
            fileService.createFile(createdFolderName, fileName, "THIS IS DEFAULT TEST DATA".getBytes());
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Boolean checkFileNotExist(String fileName, String folderName) {
        try {
            File file = new File("data/" + folderName + "/" + fileName + ".txt");
            return file.exists();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
