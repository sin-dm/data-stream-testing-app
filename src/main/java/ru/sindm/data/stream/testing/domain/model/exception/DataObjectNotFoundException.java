package ru.sindm.data.stream.testing.domain.model.exception;

public class DataObjectNotFoundException extends Exception {
    public DataObjectNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
