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
import java.util.UUID;


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

    public String generateKeyFileName() {
        return UUID.randomUUID().toString();
    }

    /**
     * @param keyFileName
     * @param file
     * @throws SdkClientException     If any errors are encountered in the client while making the
     *                                request or handling the response.
     * @throws AmazonServiceException If any errors occurred in Amazon S3 while processing the
     *                                request.
     */
    private String uploadFileTos3bucket(String keyFileName, File file) {
        String keyFileNameS3 = null;
        try {
            PutObjectResult putObjectResult = s3Client.putObject(new PutObjectRequest(s3Properties.getBucketName(), keyFileName, file));
            keyFileNameS3 = keyFileName;
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
        return keyFileNameS3;
    }

    /**
     * upload file to S3Bucket, return fileName, which is the key to get the file
     *
     * @param multipartFile
     * @return null if error
     */
    public String uploadFile(MultipartFile multipartFile, String keyFileName) {
        File file = convertMultiPartToFileTmp(multipartFile);
        String keyFileNameS3 = uploadFileTos3bucket(keyFileName, file);
        file.delete();
        return keyFileNameS3;
    }

    /**
     * @param keyFileName
     * @return null if error
     */

    public URL GeneratePresignedURL(String keyFileName) {
        URL url = null;
        try {
            // Set the presigned URL to expire after expires-in pref: aws.
            java.util.Date expiration = new java.util.Date();
            expiration.setTime(expiration.getTime() + Long.valueOf(s3Properties.getExpiresIn()));

            // Generate the presigned URL.
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(s3Properties.getBucketName(), keyFileName)
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
     * if keyPrefix != null, then delete all Pictures with  filter lotId
     * if keyPrefix == null, then delete all Pictures with  filter userId
     *
     * @param lotId
     * @param userId
     * @return successfulDeletes - count of deleted files by prefix
     */

    public int deleteLotPictures(Long lotId, Long userId) {
        int successfulDeletes = 0;
        // test
        ListObjectsRequest listObjectsRequestTest = new ListObjectsRequest().withBucketName(s3Properties.getBucketName());
        ObjectListing objectListingTest = s3Client.listObjects(listObjectsRequestTest);

        List<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<DeleteObjectsRequest.KeyVersion>();
        // all Pictures with  filter userId
        if (userId > 0) {
            keys = getKeysUserLots(userId);
        }
        // all Pictures with  filter lotId
        else {
            keys = getKeysLot(lotId);
        }
        if (keys.size() > 0) {
            // Delete the sample objects without specifying versions.
            DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(this.s3Properties.getBucketName()).withKeys(keys)
                    .withQuiet(false);
            //  Verify that delete markers were successfully added to the objects.
            DeleteObjectsResult delObjRes = s3Client.deleteObjects(multiObjectDeleteRequest);
            successfulDeletes = delObjRes.getDeletedObjects().size();
        }

        // test
        objectListingTest = s3Client.listObjects(listObjectsRequestTest);

        return successfulDeletes;
    }

    /**
     *
     * @param userId
     * @return keys all Pictures with  filter userId
     */
    public List<DeleteObjectsRequest.KeyVersion> getKeysUserLots(Long userId) {
        List<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<DeleteObjectsRequest.KeyVersion>();
        ListObjectsRequest listObjectsRequestAll = new ListObjectsRequest().withBucketName(s3Properties.getBucketName());
        ObjectListing objects = s3Client.listObjects(listObjectsRequestAll);
        List<S3ObjectSummary> objectSummaries = objects.getObjectSummaries();
        for (S3ObjectSummary objectSummarie : objectSummaries) {
            int firstFlesh = objectSummarie.getKey().indexOf("/", 1);
            int secondFlesh = objectSummarie.getKey().indexOf("/", firstFlesh + 1);
            if (objectSummarie.getKey().substring(firstFlesh + 1, secondFlesh).equals(Long.toString(userId))) {
                keys.add(new DeleteObjectsRequest.KeyVersion(objectSummarie.getKey()));
            }
        }
        return keys;
    }

    /**
     * @param lotId
     * @return keys all Pictures with  filter lotId
     */
    public List<DeleteObjectsRequest.KeyVersion> getKeysLot(Long lotId) {
        String keyPrefix = Long.toString(lotId)+ "/";
        List<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<DeleteObjectsRequest.KeyVersion>();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(s3Properties.getBucketName())
                .withPrefix(keyPrefix);
//                .withDelimiter("/");    // only files, not delete folder
        ObjectListing objects = s3Client.listObjects(listObjectsRequest);
        List<S3ObjectSummary> objectSummaries = objects.getObjectSummaries();
        for (S3ObjectSummary objectSummarie : objectSummaries) {
            // Gather the new object keys without version IDs.
            keys.add(new DeleteObjectsRequest.KeyVersion(objectSummarie.getKey()));
        }
        return keys;
    }

}