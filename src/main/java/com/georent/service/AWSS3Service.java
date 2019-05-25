package com.georent.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
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

    public void getS3Object(String bucketName,
                            String s3FolderPathAdnFileName,
                            String saveFilePath) throws IOException {
        S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, s3FolderPathAdnFileName));
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        FileUtils.copyInputStreamToFile(inputStream, new File(saveFilePath));
    }


    private File convertMultiPartToFile(MultipartFile file) {
        Assert.notNull(file, "File must not be empty!");

        //TODO jpeg files
        String filename = file.getOriginalFilename();

        //TODO somehow validate size (up to 200kb)
        long size = file.getSize();
        Path tempFile = null;

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

    private void uploadFileTos3bucket(String fileName, File file) {
        PutObjectResult putObjectResult = s3Client.putObject(new PutObjectRequest
                (s3Properties.getBucketName(), fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));

        //TODO what will be an identifier of a file
        ObjectMetadata metadata = putObjectResult.getMetadata();
        putObjectResult.getETag();
    }

    public String uploadFile(MultipartFile multipartFile) {

        String fileUrl = "";
        if (convertMultiPartToFile(multipartFile) == null) {

        } else {
            File file = convertMultiPartToFile(multipartFile);
            String fileName = generateFileName(multipartFile);

            //TODO change the way we pass file to client.
            fileUrl = s3Properties.getAndPointUrl() + "/" + s3Properties.getBucketName() + "/" + fileName;
            uploadFileTos3bucket(fileName, file);
            file.delete();
        }
        return fileUrl;
    }

}



