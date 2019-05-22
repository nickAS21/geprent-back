package com.georent.s3;

import com.amazonaws.auth.AWSCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeoRentS3Credentials implements AWSCredentials {

    private final S3Properties properties;

@Autowired
    public GeoRentS3Credentials(S3Properties properties) {
        this.properties = properties;
    }

    public String getAWSAccessKeyId(){
//        String accessKey = null;
//        if(properties.getAccessKey()!=null){
//        accessKey = properties.getAccessKey();
//    } else {
//            throw new S3PropertiesException("AccessKey does not exist!");
//        }
    return properties.getAccessKey();
    }

    public String getAWSSecretKey(){
//    String secretKey = null;
//    if(properties.getSecretKey()!= null){
//        secretKey = properties.getSecretKey();
//    } else {
//        throw new S3PropertiesException("SecretKey does not exist!");
//    }
    return properties.getSecretKey();
    }
}
