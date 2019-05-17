package com.georent.s3;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.georent.service.AWSS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class FileHandler {

    AWSS3Service awss3Service;
    S3Properties s3Properties;

    @Autowired
    public FileHandler(AWSS3Service awss3Service, S3Properties s3Properties) {
        this.awss3Service = awss3Service;
        this.s3Properties = s3Properties;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        awss3Service.getS3client().putObject(new PutObjectRequest(s3Properties.getBucketName(), fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String fileUrl = "";
        if(convertMultiPartToFile(multipartFile)==null){
            throw new IOException("file does not exist!");
        }
        else{
            File file = convertMultiPartToFile(multipartFile);
            String fileName = generateFileName(multipartFile);
            fileUrl = s3Properties.getAndPointUrl() + "/" + s3Properties.getBucketName() + "/" + fileName;
            uploadFileTos3bucket(fileName, file);
            file.delete();
        }
        return fileUrl;
    }

}
