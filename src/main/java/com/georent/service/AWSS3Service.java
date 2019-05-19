package com.georent.service;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.georent.s3.GeoRentS3Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AWSS3Service {

    @Autowired
    private GeoRentS3Credentials geoRentS3credentials;



    private AmazonS3 s3client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(geoRentS3credentials.getCredentials()))
            .withRegion(Regions.EU_WEST_1)
            .build();

    public AmazonS3 getS3client() {
        return s3client;
    }
}



