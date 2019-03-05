package com.entr.sbdem.exception;

public class RoleNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -1343615089639077809L;

    public RoleNotFoundException(String s) {
        super(s);
    }

    public RoleNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RoleNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
