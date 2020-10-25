package com.project.backend.exceptions;

public class SpringRedditException extends RuntimeException{
    public SpringRedditException(String exMessage) {
        super(exMessage);
    }
}
