package ru.sindm.data.stream.testing.domain.service.impl;

import org.springframework.stereotype.Service;
import ru.sindm.data.stream.testing.domain.model.exception.DataObjectNotFoundException;
import ru.sindm.data.stream.testing.domain.model.exception.InvalidKeyException;
import ru.sindm.data.stream.testing.domain.service.FileService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String createFolder(String typeName) {
        File folder = new File("data/" + typeName);
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder.toString();
    }

    @Override
    public String createFile(String fileFolder, String fileName, byte[] data) throws InvalidKeyException {
        File file = new File(fileFolder + "/" + fileName + ".txt");
        if (file.exists()) {
            throw new InvalidKeyException("File with filename: " + fileName + " already exists!");
        } else {
            try {
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(data);
                fileOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return file.toString();
    }

    @Override
    public void updateFile(String folderName, String fileName, byte[] data) throws DataObjectNotFoundException {
        File file = new File(folderName + "/" + fileName + ".txt");
        if (!file.exists()) {
            throw new DataObjectNotFoundException("Data object with key: " + fileName + " doen't exists!");
        } else {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(data);
                fileOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public void deleteFile(String folderName, String fileName) throws DataObjectNotFoundException {
        File file = new File(folderName + "/" + fileName + ".txt");
        if (!file.exists()) {
            throw new DataObjectNotFoundException("Data object with key: " + fileName + " doen't exists!");
        } else {
            file.delete();
        }
    }
}
