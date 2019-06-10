package com.georent.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.georent.config.S3ConfigurationProperties;
import com.georent.exception.FileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;


@Slf4j
@Service
public class AWSS3Service {

    private final AmazonS3 s3Client;
    private final S3ConfigurationProperties s3Properties;

    @Autowired
    public AWSS3Service(AmazonS3 s3Client,
                        S3ConfigurationProperties s3ConfigurationProperties) {
        this.s3Client = s3Client;
        this.s3Properties = s3ConfigurationProperties;
    }

    /*
     * return S3object and save it in saveFilePath
     * */
    //TODO change the way we pass file to client.
    public S3Object getS3Object(String keyFile,
                                Path saveFilePath) throws IOException {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(s3Properties.getBucketName());
        ObjectListing objectListing = s3Client.listObjects(listObjectsRequest);
        S3Object s3Object = s3Client.getObject(new GetObjectRequest(s3Properties.getBucketName(), keyFile));
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        Files.copy(inputStream, saveFilePath, StandardCopyOption.REPLACE_EXISTING);
        return s3Object;
    }


    //TODO jpeg files AND write exception
    //TODO somehow validate size (up to 200kb) AND write exception
    private File convertMultiPartToFile(MultipartFile multipart) {
        Assert.notNull(multipart, "File must not be empty!");
        if (!multipart.getContentType().equals("image/jpeg")) {
            throw new RuntimeException("Only JPG images are accepted");
        }
        if (multipart.getSize() > 200000) {
            throw new RuntimeException("Size is too big");
        }
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("tmp_", multipart.getOriginalFilename());
            multipart.transferTo(tempFile);
        } catch (IOException e) {
            log.error("Unable to save file.", e);
            throw new FileException("Unable to save file.", e);
        }
        return tempFile.toFile();
    }

    private String generateKeyFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    //TODO what will be an identifier of a file
    private void uploadFileTos3bucket(String keyFileName, File file) {
        PutObjectResult putObjectResult = s3Client.putObject(new PutObjectRequest(s3Properties.getBucketName(), keyFileName, file));
            // Test
        URL url = GeneratePresignedURL(keyFileName);
    }

    /*
     * upload file to S3Bucket, return fileUrl, which is the key to get the file */
    public String uploadFile(MultipartFile multipartFile) {
        String keyFileName = generateKeyFileName(multipartFile);
        File file = convertMultiPartToFile(multipartFile);
        uploadFileTos3bucket(keyFileName, file);
        file.delete();
        return keyFileName;
    }

    public URL GeneratePresignedURL(String objectKey) {
        URL url = null;
        try {
            // Set the presigned URL to expire after one hour.
            java.util.Date expiration = new java.util.Date();
            expiration.setTime(expiration.getTime() + Long.valueOf(s3Properties.getExpiresIn()));

            // Generate the presigned URL.
            System.out.println("Generating pre-signed URL.");
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(s3Properties.getBucketName(), objectKey)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);
            url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
            System.out.println("Pre-Signed URL: " + url.toString());
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
            return url;
        }
        return url;
    }
}