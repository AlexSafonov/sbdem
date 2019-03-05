package com.entr.sbdem.exception;

public class StorageException extends RuntimeException {


    private static final long serialVersionUID = -8570160310058390657L;

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageException(Throwable throwable) {
        super(throwable);
    }
}