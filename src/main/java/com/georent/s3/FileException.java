package com.georent.s3;

import org.springframework.stereotype.Component;

import java.io.IOException;

public class FileException extends Exception{
    public FileException (String message){
        super(message);
    }
}
