package com.georent.s3;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.georent.service.AWSS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@Component
public class FileHandler {

    private final GeoRentS3Credentials geoRentS3Credentials;
    private final S3Properties s3Properties;

    @Autowired
    public FileHandler(GeoRentS3Credentials geoRentS3Credentials, S3Properties s3Properties) {
        this.geoRentS3Credentials = geoRentS3Credentials;
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
        geoRentS3Credentials.getS3client().putObject(new PutObjectRequest(s3Properties.getBucketName(), fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    private String uploadFile(MultipartFile multipartFile) throws IOException {
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
