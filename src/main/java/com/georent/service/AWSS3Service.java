package com.georent.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.georent.config.S3ConfigurationProperties;
import com.georent.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
public class AWSS3Service {

    public final AmazonS3 s3Client;
    public final S3ConfigurationProperties s3Properties;

    @Autowired
    public AWSS3Service(AmazonS3 s3Client,
                        S3ConfigurationProperties s3ConfigurationProperties) {
        this.s3Client = s3Client;
        this.s3Properties = s3ConfigurationProperties;
    }

    /**
     * if file null - exception
     * if multipart.getContentType() not MediaType.IMAGE_JPEG_VALUE - exception
     * if size file more 200 kb - - exception
     *
     * @param multipart
     * @return
     */
    public boolean validMultiPartFile(MultipartFile multipart) {
        Assert.notNull(multipart, Message.INVALID_FILE_NULL.getDescription());
        if (!multipart.getContentType().equals(MediaType.IMAGE_JPEG_VALUE)) {
            throw new RuntimeException(Message.INVALID_FILE_EXTENSION_JPG.getDescription());
        }
        if (multipart.getSize() > this.s3Properties.getFileSizeMax()) {
            throw new RuntimeException(Message.INVALID_FILE_SIZE.getDescription());
        }
        return true;
    }

    public String generateKeyFileName() {
        return UUID.randomUUID().toString();
    }

    /**
     *
     * @param multipartFile
     * @param keyFileName
     * @throws SdkClientException     If any errors are encountered in the client while making the
     *                                request or handling the response. return null
     * @throws AmazonServiceException If any errors occurred in Amazon S3 while processing the
     *                                request. return null
     */
    public String uploadFileToS3bucket(MultipartFile multipartFile, String keyFileName) {
        try {
            InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(multipartFile.getBytes().length);
            meta.setContentType(MediaType.IMAGE_JPEG_VALUE);
            s3Client.putObject(new PutObjectRequest(s3Properties.getBucketName(), keyFileName, inputStream, meta));
            return  keyFileName;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * @param keyFileName
     * @return URL or null if error
     */

    public URL generatePresignedURL(String keyFileName) {
        URL url = null;
        try {
            // Set the presigned URL to expire after expires-in pref: aws.
            java.util.Date expiration = new java.util.Date();
            expiration.setTime(expiration.getTime() + s3Properties.getExpiresIn());
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(s3Properties.getBucketName(), keyFileName)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);
            url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
//            throw new FileException(Message.INVALID_PICTURE_LOAD_AMAZONE_SERVICES.getDescription(), e);
//            e.printStackTrace();
            return null;
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
//            throw new FileException(Message.INVALID_PICTURE_LOAD_SDK_CLIENT.getDescription(), e);
//            e.printStackTrace();
            return null;
        }
        return url;
    }

    /**
     * delete all Pictures with  filter lotId   *
     * @param lotId
     * @return successfulDeletes - count of deleted files by prefix
     */

    public int deleteLotPictures(Long lotId) {
        int successfulDeletes = 0;
        // test
        ListObjectsRequest listObjectsRequestTest = new ListObjectsRequest().withBucketName(s3Properties.getBucketName());
        ObjectListing objectListingTest = s3Client.listObjects(listObjectsRequestTest);

        List<DeleteObjectsRequest.KeyVersion> keys = getKeysLot(lotId);
        if (keys.size() > 0) {
            successfulDeletes = delObjRequest(keys);
        }

        // test
        objectListingTest = s3Client.listObjects(listObjectsRequestTest);

        return successfulDeletes;
    }
    /**
     * delete all Pictures with  filter userId
     * @param userId
     * @return successfulDeletes - count of deleted files by prefix
     */
    public int deletePicturesFromAllLotsUser(Long userId) {
        int successfulDeletes = 0;
        // test
        ListObjectsRequest listObjectsRequestTest = new ListObjectsRequest().withBucketName(s3Properties.getBucketName());
        ObjectListing objectListingTest = s3Client.listObjects(listObjectsRequestTest);

        List<DeleteObjectsRequest.KeyVersion> keys = getKeysUserLots(userId);
        if (keys.size() > 0) {
            successfulDeletes = delObjRequest (keys);
        }

        // test
        objectListingTest = s3Client.listObjects(listObjectsRequestTest);

        return successfulDeletes;
    }

    /**
     * @param userId
     * @return keys all Pictures with  filter userId
     */
    public List<DeleteObjectsRequest.KeyVersion> getKeysUserLots(Long userId) {
        List<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<DeleteObjectsRequest.KeyVersion>();
        ListObjectsRequest listObjectsRequestAll = new ListObjectsRequest().withBucketName(s3Properties.getBucketName());
        ObjectListing objectListing = s3Client.listObjects(listObjectsRequestAll);
        objectListing
                .getObjectSummaries()
                .stream()
                .filter(summary -> validKeyToUserId(summary.getKey(), userId))
                .forEach(summary -> keys.add(new DeleteObjectsRequest.KeyVersion(summary.getKey())));
        return keys;
    }

    /**
     * Structure of Image key: {lotId}/{userId}/pictureName
     * @param lotId
     * @return keys all Pictures withPrefix(lotId + "/")
     */
    public List<DeleteObjectsRequest.KeyVersion> getKeysLot(Long lotId) {
        List<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<DeleteObjectsRequest.KeyVersion>();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(s3Properties.getBucketName())
                .withPrefix(lotId + "/");
        ObjectListing objectListing = s3Client.listObjects(listObjectsRequest);
        objectListing
                .getObjectSummaries()
                .stream()
                .forEach(summary -> keys.add(new DeleteObjectsRequest.KeyVersion(summary.getKey())));
        return keys;
    }

    /**
     * Structure of Image key: {lotId}/{userId}/pictureName
     * @param key
     * @param userId
     * @return
     */
    private boolean validKeyToUserId(String key, Long userId) {
        String[] res = key.split("/", 3);
        if (res.length == 3 && res[1].equals("" + userId)) {
            return true;
        }
        return false;
    }

    private int delObjRequest (List<DeleteObjectsRequest.KeyVersion> keys){
        // Delete the sample objects without specifying versions.
        DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(this.s3Properties.getBucketName()).withKeys(keys)
                .withQuiet(false);
        //  Verify that delete markers were successfully added to the objects.
        DeleteObjectsResult delObjRes = s3Client.deleteObjects(multiObjectDeleteRequest);
        return delObjRes.getDeletedObjects().size();
    }

}