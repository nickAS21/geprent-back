package com.georent.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.georent.config.S3ConfigurationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AWSS3ServiceTest {

    private S3ConfigurationProperties mockS3Properties;
    private AmazonS3 mockClient;
    private AWSS3Service awss3Service;
    private MultipartFile mockMultipartFile;
    private String string;
    private S3Object s3Object;
    private S3ObjectInputStream inputStream;
    private Path mockPath;

    @BeforeEach
    void init() {
        mockS3Properties = mock(S3ConfigurationProperties.class);
        mockClient = mock(AmazonS3.class);
        awss3Service = new AWSS3Service(mockClient, mockS3Properties);
        mockMultipartFile = mock(MultipartFile.class);
        string = "testString";
        s3Object = mock(S3Object.class);
        inputStream = mock(S3ObjectInputStream.class);
        mockPath = mock(Path.class);
    }

    @Test
    void getS3Object() throws IOException {
        when(mockClient.getObject(any())).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(inputStream);
   //     assertNotNull(awss3Service.getS3Object(string, mockPath));
    }

    @Test
    void uploadFileSuccessful() throws IOException {
        when(mockMultipartFile.getContentType()).thenReturn("image/jpeg");
        when(mockMultipartFile.getOriginalFilename()).thenReturn(string);
        when(mockS3Properties.getBucketName()).thenReturn("TestBucketName");
        assertNotNull(awss3Service.uploadFile(mockMultipartFile));
    }
}