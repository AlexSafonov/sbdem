package com.entr.sbdem.exception;

public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -4161675502185196209L;

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public UserNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
