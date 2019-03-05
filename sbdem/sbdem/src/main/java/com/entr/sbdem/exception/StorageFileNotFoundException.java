package com.entr.sbdem.exception;

public class StorageFileNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -7029445824951434137L;

    public StorageFileNotFoundException(String message) {
        super(message);
    }
    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageFileNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
