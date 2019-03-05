package com.entr.sbdem.exception;

public class UserAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 2024381141217242351L;

    public UserAlreadyExistsException(String s) {
        super(s);
    }

    public UserAlreadyExistsException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public UserAlreadyExistsException(Throwable throwable) {
        super(throwable);
    }
}
