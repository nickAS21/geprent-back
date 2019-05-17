package com.georent.s3;

import java.io.IOException;

public class FileException extends Exception{
    public FileException (String message){
        super(message);
    }
}
