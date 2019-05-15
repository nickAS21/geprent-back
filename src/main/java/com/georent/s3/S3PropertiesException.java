package com.georent.s3;

import com.amazonaws.AbortedException;

public class S3PropertiesException extends AbortedException {

    public S3PropertiesException(String message) {
        super(message);
    }
}
