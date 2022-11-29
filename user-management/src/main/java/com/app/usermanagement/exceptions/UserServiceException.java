package com.app.usermanagement.exceptions;

public class UserServiceException extends RuntimeException{

    private static final long SerialVersionUID = 1L;
    public UserServiceException(String message) {
        super(message);
    }
}
