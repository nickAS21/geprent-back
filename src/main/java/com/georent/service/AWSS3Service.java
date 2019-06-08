package com.georent.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.georent.config.S3ConfigurationProperties;
import com.georent.exception.FileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
        //S3Object s3Object = s3Client.getObject(new GetObjectRequest(s3Properties.getBucketName(), keyFile));
        return s3Object;
    }


    //TODO jpeg files AND write exception
    //TODO somehow validate size (up to 200kb) AND write exception
    private File convertMultiPartToFile(MultipartFile file) {
        Assert.notNull(file, "File must not be empty!");
        if (!file.getContentType().equals("image/jpeg")) {
            throw new RuntimeException("Only JPG images are accepted");
        }
        long size = file.getSize();
        if (size > 200000) {
            throw new RuntimeException("Size is too big");
        }
        Path tempFile = null;
        String filename = file.getOriginalFilename();
        try {
            tempFile = Files.createTempFile("tmp_", filename);
        } catch (IOException e) {
            log.error("Unable to save file.", e);
            throw new FileException("Unable to save file.", e);
        }
        return tempFile.toFile();
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    //TODO what will be an identifier of a file
    private void uploadFileTos3bucket(String fileName, File file) {
        PutObjectResult putObjectResult = s3Client.putObject(new PutObjectRequest
                (s3Properties.getBucketName(), fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
    }

    /*
     * upload file to S3Bucket, return fileUrl, which is the key to get the file */
    public String uploadFile(MultipartFile multipartFile) {
        String fileUrl = "";
        String fileName = generateFileName(multipartFile);
        fileUrl = s3Properties.getAndPointUrl() + "/" + s3Properties.getBucketName() + "/" + fileName;
        File file = convertMultiPartToFile(multipartFile);
        uploadFileTos3bucket(fileName, file);
        file.delete();
        return fileUrl;
    }
}