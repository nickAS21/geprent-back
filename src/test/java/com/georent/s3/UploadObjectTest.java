package com.georent.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.georent.service.AWSS3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UploadObjectTest {

    private AWSS3Service mockAwsS3Service;
    private S3Properties mockS3Properties;
    private UploadObject uploadObject;
    private MultipartFile mockMultipartFile;
    private String string;
    private File mockFile;
    private AmazonS3 mockAmazonS3;

    @BeforeEach
    void init() {
        mockAwsS3Service = mock(AWSS3Service.class);
        mockS3Properties = mock(S3Properties.class);
        uploadObject = new UploadObject(mockAwsS3Service, mockS3Properties);
        mockMultipartFile = mock(MultipartFile.class);
        string = "testString";
        mockFile = mock(File.class);
        mockAmazonS3 = mock(AmazonS3.class);
    }

    @Test
    void convertMultiPartToFile() throws IOException {
        when(mockMultipartFile.getOriginalFilename()).thenReturn(string);
        when(mockMultipartFile.getBytes()).thenReturn(string.getBytes());
        File file = uploadObject.convertMultiPartToFile(mockMultipartFile);
        assertNotNull(file);
    }

    @Test
    void generateFileName() {
        when(mockMultipartFile.getOriginalFilename()).thenReturn(string);
        assertNotNull(uploadObject.generateFileName(mockMultipartFile));
    }

    @Test
    void uploadFileTos3bucket() {
        when(mockAwsS3Service.getS3client()).thenReturn(mockAmazonS3);
        when(mockS3Properties.getBucketName()).thenReturn("TestBucketName");
        uploadObject.uploadFileTos3bucket(string, mockFile);
    }

    @Test
    void uploadFile() throws IOException {
        when(mockMultipartFile.getOriginalFilename()).thenReturn(string);
        when(mockMultipartFile.getBytes()).thenReturn(string.getBytes());
        when(mockAwsS3Service.getS3client()).thenReturn(mockAmazonS3);
        when(mockS3Properties.getBucketName()).thenReturn("TestBucketName");
        assertNotNull(uploadObject.uploadFile(mockMultipartFile));
    }
}