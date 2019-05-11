package com.georent.s3;

import com.amazonaws.auth.AWSCredentials;
import org.springframework.stereotype.Component;

@Component
public class CredentialsProvider implements AWSCredentials {

    private static final String ACCESS_KEY_ID = "AKIA4BDR5OXQEJFE574O";
    private static final String SECRET_KEY_ID = "0YEd7sI2PvzbFNcun/E6EcO9tUOey9O+xwK/nJtV";

    public CredentialsProvider() {
    }

    @Override
    public String getAWSAccessKeyId() {
        String accessKeyId = null;
        if(!ACCESS_KEY_ID.equals(null)) accessKeyId = ACCESS_KEY_ID;
        return accessKeyId;
    }

    @Override
    public String getAWSSecretKey() {
        String secretKeyId = null;
        if(!SECRET_KEY_ID.equals(null)) {
            secretKeyId = ACCESS_KEY_ID;
        }
        return secretKeyId;
    }
}
