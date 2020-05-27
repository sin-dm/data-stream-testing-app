package ru.sindm.data.stream.testing.domain.service;

import ru.sindm.data.stream.testing.domain.model.exception.DataObjectNotFoundException;
import ru.sindm.data.stream.testing.domain.model.exception.InvalidKeyException;

public interface FileService {

    String createFolder(String typeName);

    String createFile(String fileFolder, String fileName, byte[] data) throws InvalidKeyException;

    void updateFile(String folderName, String fileName, byte[] data) throws DataObjectNotFoundException;

    void deleteFile(String folderName, String fileName) throws DataObjectNotFoundException;
}
