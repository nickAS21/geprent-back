package com.georent.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.georent.config.S3ConfigurationProperties;
import com.georent.domain.Lot;
import com.georent.exception.FileException;
import com.georent.message.Message;
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
import java.util.ArrayList;
import java.util.List;


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

    /**
     * if file null - exception
     * if file extension not jpeg - exception
     * if size file more 200 kb - - exception
     *
     * @param multipart
     * @return
     */

    private File convertMultiPartToFileTmp(MultipartFile multipart) {
        Assert.notNull(multipart, Message.INVALID_FILE_NULL.getDescription());
        if (!multipart.getContentType().equals("image/jpeg")) {
            throw new RuntimeException(Message.INVALID_FILE_EXTENSION_JPG.getDescription());
        }
        if (multipart.getSize() > 200000) {
            throw new RuntimeException(Message.INVALID_FILE_SIZE.getDescription());
        }
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("tmp_", multipart.getOriginalFilename());
            multipart.transferTo(tempFile);
        } catch (IOException e) {
            log.error(Message.INVALID_FILE_SAVE_TMP.getDescription(), e);
            throw new FileException(Message.INVALID_FILE_SAVE_TMP.getDescription(), e);
        }
        return tempFile.toFile();
    }

//    private String generateKeyFileName(MultipartFile multiPart) {
//        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
//    }

    /**
     * @param keyFileName
     * @param file
     * @throws SdkClientException     If any errors are encountered in the client while making the
     *                                request or handling the response.
     * @throws AmazonServiceException If any errors occurred in Amazon S3 while processing the
     *                                request.
     */
    private String uploadFileTos3bucket(String keyFileName, File file) {
        try {
            PutObjectResult putObjectResult = s3Client.putObject(new PutObjectRequest(s3Properties.getBucketName(), keyFileName, file));
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
            return null;
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
            return null;
        }
        return keyFileName;
    }

    /**
     * upload file to S3Bucket, return fileName, which is the key to get the file
     *
     * @param multipartFile
     */
    public String uploadFile(MultipartFile multipartFile, String keyFileName) {
        File file = convertMultiPartToFileTmp(multipartFile);
        String keyFileNameS3 = uploadFileTos3bucket(keyFileName, file);
        file.delete();
        return keyFileNameS3;
    }

    /**
     * @param objectKey
     * @return url for the Picture from S3
     */

    public URL GeneratePresignedURL(String objectKey) {
        URL url = null;
        try {
            // Set the presigned URL to expire after expires-in pref: aws.
            java.util.Date expiration = new java.util.Date();
            expiration.setTime(expiration.getTime() + Long.valueOf(s3Properties.getExpiresIn()));

            // Generate the presigned URL.
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(s3Properties.getBucketName(), objectKey)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);
            url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
//            throw new FileException(Message.INVALID_PICTURE_LOAD_AMAZONE_SERVICES.getDescription(), e);
            e.printStackTrace();
            return null;
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
//            throw new FileException(Message.INVALID_PICTURE_LOAD_SDK_CLIENT.getDescription(), e);
            e.printStackTrace();
            return null;
        }
        return url;
    }

    /**
     *
     * @param lot
     * @return successfulDeletes - count of deleted files by prefix
     */

    public int deleteLotPictures(String keyPrefix) {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(s3Properties.getBucketName())
                .withPrefix(keyPrefix);
        ListObjectsRequest listObjectsRequestTest = new ListObjectsRequest().withBucketName(s3Properties.getBucketName());
        ObjectListing objectListing = s3Client.listObjects(listObjectsRequestTest);

        ObjectListing objects = s3Client.listObjects(listObjectsRequest);
        List<S3ObjectSummary> objectSummaries = objects.getObjectSummaries();
        List<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<DeleteObjectsRequest.KeyVersion>();
        for (S3ObjectSummary objectSummarie  : objectSummaries) {
            // Gather the new object keys without version IDs.
            keys.add(new DeleteObjectsRequest.KeyVersion(objectSummarie.getKey()));
        }

        // Delete the sample objects without specifying versions.
        DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(this.s3Properties.getBucketName()).withKeys(keys)
                .withQuiet(false);
        //  Verify that delete markers were successfully added to the objects.
        DeleteObjectsResult delObjRes =  s3Client.deleteObjects(multiObjectDeleteRequest);
        int successfulDeletes = delObjRes.getDeletedObjects().size();
        objectListing = s3Client.listObjects(listObjectsRequestTest);
        return successfulDeletes;
    }

}