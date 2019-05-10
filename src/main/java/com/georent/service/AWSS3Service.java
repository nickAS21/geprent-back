package com.georent.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.georent.amazonaws.CredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;

/* is taken from https://www.baeldung.com/aws-s3-java */

public class AWSS3Service {

    static CredentialsProvider credentialsProvider;

    @Autowired
    public AWSS3Service(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    static AWSCredentials credentials = new BasicAWSCredentials(
            credentialsProvider.getAWSAccessKeyId(),
            credentialsProvider.getAWSSecretKey()
    );

    static AmazonS3 s3client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.EU_WEST_1)
            .build();

}
