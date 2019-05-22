package com.georent.s3;

import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.georent.service.AWSS3Service;
import org.apache.commons.io.FileUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;


@Component
public class GetObject {

    AWSS3Service awss3Service;

    @Autowired
    public GetObject(AWSS3Service awss3Service) {
        this.awss3Service = awss3Service;
    }

    public void getS3Object(String bucketName, String s3FolderPathAdnFileName, String saveFilePath) throws IOException {
        S3Object s3Object = awss3Service.getS3client().getObject(new GetObjectRequest(bucketName, s3FolderPathAdnFileName));
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        FileUtils.copyInputStreamToFile(inputStream, new File(saveFilePath));
    }
}

