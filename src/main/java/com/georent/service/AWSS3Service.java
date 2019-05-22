package com.georent.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.georent.s3.GeoRentS3Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class AWSS3Service {

    @Autowired
    private GeoRentS3Credentials geoRentS3Credentials;

    @Bean
    public AmazonS3 getS3client() {
        final AWSCredentials credentials = new BasicAWSCredentials(geoRentS3Credentials.getAWSAccessKeyId(), geoRentS3Credentials.getAWSSecretKey());

       final AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_WEST_1)
                .build();
        return s3client;
    }
}



