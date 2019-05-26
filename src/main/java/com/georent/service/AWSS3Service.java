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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import static com.sun.activation.registries.LogSupport.log;

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

//    public void getS3Object(String bucketName,
    public void getS3Object(String s3FolderPathAdnFileName,
                            Path saveFilePath) throws IOException {
//        S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, s3FolderPathAdnFileName));
//        s3FolderPathAdnFileName = "1558853689584-drel.png";
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(s3Properties.getBucketName());
        ObjectListing objectListing = s3Client.listObjects(listObjectsRequest);
        s3FolderPathAdnFileName = "shurup.jpg";
        S3Object s3Object = s3Client.getObject(new GetObjectRequest(s3Properties.getBucketName(), s3FolderPathAdnFileName));
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
//        FileUtils.copyInputStreamToFile(inputStream, new File(saveFilePath));
        Files.copy(inputStream, saveFilePath, StandardCopyOption.REPLACE_EXISTING);
    }

   private Path convertMultiPartToFile(MultipartFile file) {
       Assert.notNull(file, "File must not be empty!");
        //TODO jpeg files
       String originalFilename = file.getOriginalFilename();

        //TODO somehow validate size (up to 200kb)
        long size = file.getSize();
       Path tempFile = null;
       try(InputStream inputStream = file.getInputStream()) {
            tempFile = Files.createTempFile("tmp_", originalFilename);
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.getMessage();
        }
        return tempFile;
    }

//    private String generateFileName(MultipartFile multiPart) {
//        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
//    }

    private void uploadFileTos3bucket(String fileName, File file) { // file name = 1558853689584-drel.png
        log("SecretKey = " + s3Properties.getSecretKey());
        log("AccessKey = " + s3Properties.getAccessKey());
//        PutObjectResult putObjectResult = s3Client.putObject(new PutObjectRequest
//                (s3Properties.getBucketName(), fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
        PutObjectResult putObjectResult = s3Client.putObject(new PutObjectRequest
                (s3Properties.getBucketName(), fileName, file));

        //TODO what will be an identifier of a file
        ObjectMetadata metadata = putObjectResult.getMetadata();
        putObjectResult.getETag();
    }


    /**
     1) fileName = {userId}/{lotId}/{index in list picture}/"MultipartFile.getOriginalFilename()"

     index in list picture -> gjrf == "0" если до отьезда не успею переделсть сущность Lot

     2) Перед записью - проверяем наличие по:

     {userId}/{lotId}/{index in list picture}

     и если есть - удаляем

     3) запсиь нового

     4) fileUrl = s3Properties.getAndPointUrl() + "/" + s3Properties.getBucketName() + "/" + fileName;

     5) fileUrl нового храним в :

     Description -> itemName (котрый потом переделаем в List <String>
     * @param multipartFile
     * @return
     */

    public String uploadFile(MultipartFile multipartFile) {

        String fileUrl = "";
//        File file = convertMultiPartToFile(multipartFile);
        Path filePath = convertMultiPartToFile(multipartFile);
        if ( !(filePath == null) ) {
//            String fileName = generateFileName(multipartFile);
            String fileName =  multipartFile.getOriginalFilename();

            //TODO change the way we pass file to client.
            fileUrl = s3Properties.getAndPointUrl() + "/" + s3Properties.getBucketName() + "/" + fileName;
            //uploadFileTos3bucket(fileName, file);
            File file = filePath.toFile();
            uploadFileTos3bucket(fileName, file);
            file.delete();
        }
        return fileUrl;
    }

}



