package com.georent.exception;

import java.io.IOException;

public class FileException extends RuntimeException{
    public FileException (String message){
        super(message);
    }
    public FileException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
