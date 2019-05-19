package com.georent.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Data
@Component
public class GeoRentS3Credentials implements AWSCredentials {

    private S3Properties properties;

@Autowired
    public GeoRentS3Credentials(S3Properties properties) {
        this.properties = properties;
    }

    public String getAWSAccessKeyId(){
        String accessKey = null;
        if(properties.getAccessKey()!=null){
        accessKey = properties.getAccessKey();
    } else {
            throw new S3PropertiesException("AccessKey does not exist!");
        }
    return accessKey;
    }

    public String getAWSSecretKey(){
    String secretKey = null;
    if(properties.getSecretKey()!= null){
        secretKey = properties.getSecretKey();
    } else {
        throw new S3PropertiesException("SecretKey does not exist!");
    }
    return secretKey;
    }

    private  final AWSCredentials credentials = new BasicAWSCredentials(
            getAWSAccessKeyId(),
            getAWSSecretKey()
    );

//@Bean
//public AmazonS3 createAmazonS3Client() {
//    AmazonS3 s3client = AmazonS3ClientBuilder
//            .standard()
//            .withCredentials(new AWSStaticCredentialsProvider(credentials))
//            .withRegion(Regions.EU_WEST_1)
//            .build();
//    return s3client;
//}

}
