package com.palette.palettepetsback.config.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoMemberExistException extends RuntimeException {

    private long memberId;

    public NoMemberExistException() {
        super();
    }

    public NoMemberExistException(String message) {
        super(message);
    }

    public NoMemberExistException(String message, Throwable cause) {
        super(message, cause);
    }

}