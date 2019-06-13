package com.georent.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.georent.config.S3ConfigurationProperties;
import com.georent.message.Message;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.georent.service.ServiceTestUtils.getMultipartFiles;
import static com.georent.service.ServiceTestUtils.getUrl;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AWSS3ServiceTest {

    private S3ConfigurationProperties mockS3Properties = mock(S3ConfigurationProperties.class);
    private AmazonS3 mockS3Client = mock(AmazonS3.class);
    private AWSS3Service awss3Service;
    private MultipartFile[] multipartFiles;
    private MultipartFile mockMultipartFile = mock(MultipartFile.class);
    private String keyFileName = "/1/1/1";
    private String bucketName =  "geo-rent-bucket";
    private String nameUrl = "/geo-rent-bucket.s3.eu-west-1.amazonaws.com/";
    private String contentType = "image/jpeg";
    private String expiresIn = "60000";

    @BeforeEach
    void init() {

        multipartFiles = getMultipartFiles();
        awss3Service = new AWSS3Service(mockS3Client, mockS3Properties);
    }

    @Test
    void whenUploadFileSuccessful_Return_keyFileNameS3_equals_keyFileName() {
        when(mockMultipartFile.getContentType()).thenReturn(contentType);
        when(mockMultipartFile.getSize()).thenReturn(199999L);
        String keyFileNameS3 = awss3Service.uploadFile(mockMultipartFile, keyFileName);
        Assert.assertEquals(keyFileNameS3, keyFileName);
    }

    @Test
    void whenUploadFileGetExtensionError_Return_keyFileNameS3_equals_keyFileName() {
        when(mockMultipartFile.getContentType()).thenReturn(contentType + "png");
        when(mockMultipartFile.getSize()).thenReturn(219999L);
        Throwable exception = assertThrows(RuntimeException.class, () -> awss3Service.uploadFile(mockMultipartFile, keyFileName));
        Assert.assertEquals(Message.INVALID_FILE_EXTENSION_JPG.getDescription(), exception.getMessage());
    }

    @Test
    void whenUploadFileGetSizeError_Return_keyFileNameS3_equals_keyFileName() {
        when(mockMultipartFile.getContentType()).thenReturn(contentType);
        when(mockMultipartFile.getSize()).thenReturn(219999L);
        Throwable exception = assertThrows(RuntimeException.class, () -> awss3Service.uploadFile(mockMultipartFile, keyFileName));
        Assert.assertEquals(Message.INVALID_FILE_SIZE.getDescription(), exception.getMessage());
    }

    @Test
    void whenGeneratePresignedURL_Return_URL() throws MalformedURLException {
        String keyFileNameUrl = nameUrl + keyFileName;
        URL urlS3 = getUrl(keyFileNameUrl);
        when(mockS3Client.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).thenReturn(urlS3);
        when(mockS3Properties.getExpiresIn()).thenReturn(expiresIn);
        URL urlOut = awss3Service.generatePresignedURL(keyFileName);
        Assert.assertEquals(urlS3, urlOut);
    }

    @Test
    void whenDeleteLotPicturesUserIdSuccessful_Return_CntDelFiles() {
        ObjectListing objectListing = new ObjectListing();
        S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
        s3ObjectSummary.setKey(keyFileName);
        objectListing.setBucketName(bucketName);
        objectListing.getObjectSummaries().add(s3ObjectSummary);
        DeleteObjectsResult.DeletedObject deletedObject = new DeleteObjectsResult.DeletedObject();
        List<DeleteObjectsResult.DeletedObject> deletedObjects = new ArrayList<>();
        deletedObjects.add(deletedObject);
        DeleteObjectsResult deleteObjectsResult = new DeleteObjectsResult(deletedObjects);
        when(mockS3Properties.getBucketName()).thenReturn(bucketName);
        when(mockS3Client.listObjects(any(ListObjectsRequest.class))).thenReturn(objectListing);
        when(mockS3Client.deleteObjects(any(DeleteObjectsRequest.class))).thenReturn(deleteObjectsResult);
        Assert.assertEquals(1, awss3Service.deleteLotPictures(1L, 1L));
    }

    @Test
    void whenDeleteLotPicturesLotIdSuccessful_Return_CntDelFiles() {
        ObjectListing objectListing = new ObjectListing();
        S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
        s3ObjectSummary.setKey(keyFileName);
        objectListing.setBucketName(bucketName);
        objectListing.getObjectSummaries().add(s3ObjectSummary);
        DeleteObjectsResult.DeletedObject deletedObject = new DeleteObjectsResult.DeletedObject();
        List<DeleteObjectsResult.DeletedObject> deletedObjects = new ArrayList<>();
        deletedObjects.add(deletedObject);
        DeleteObjectsResult deleteObjectsResult = new DeleteObjectsResult(deletedObjects);
        when(mockS3Properties.getBucketName()).thenReturn(bucketName);
        when(mockS3Client.listObjects(any(ListObjectsRequest.class))).thenReturn(objectListing);
        when(mockS3Client.deleteObjects(any(DeleteObjectsRequest.class))).thenReturn(deleteObjectsResult);
        Assert.assertEquals(1, awss3Service.deleteLotPictures(1L, 0L));
    }
}