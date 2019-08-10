package com.csye6225.lms.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.csye6225.lms.controller.ImageController;
import com.csye6225.lms.exception.ResourceNotFoundException;
import com.csye6225.lms.pojo.Book;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
public class AmazonS3ImageService {
    private final static Logger logger = LoggerFactory.getLogger(ImageController.class);

    private AmazonS3 amazonS3;

    @Value("${S3.Bucket.Name}")
    private String s3BucketName;

    @Autowired
    private ImageService imageService;

    @Autowired
    public AmazonS3ImageService()
    {
        this.amazonS3 = AmazonS3ClientBuilder.standard().withCredentials(new InstanceProfileCredentialsProvider(false)).build();
    }

    public String uploadImageToS3(Book book, MultipartFile multipartFile) throws IOException {
        // File fileToUpload = convertMultiPartToFile(multipartFile);
        logger.info("Going to upload image in S3 bucket");
        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentType("image");
        String key = book.getId()+ "_" + multipartFile.getOriginalFilename();
        /* save file to S3 */
        InputStream file = multipartFile.getInputStream();
        amazonS3.putObject(new PutObjectRequest(s3BucketName, key, file, objMeta));
        String signedUrl = getPreSignedUrl(key);
        return signedUrl;
    }

    public void deleteImageFromS3(String key){
        amazonS3.deleteObject(new DeleteObjectRequest(s3BucketName, key));
    }

    public String getPreSignedUrl(String key){
        if(!imageExistsInS3(key)){
            logger.error("Image doesn't exist :" +key);
            logger.info("Get image from S3- Response code: " + HttpStatus.NOT_FOUND);
            throw new ResourceNotFoundException("Image not found in S3 bucket");
        }
        /* get signed URL (valid for 2 minutes) */
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(s3BucketName, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(DateTime.now().plusMinutes(2).toDate());
        URL signedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        logger.info("Signed Url for the image : " +signedUrl.toString());
        return signedUrl.toString();
    }

    private boolean imageExistsInS3(String key){
        ObjectListing objectListing = amazonS3.listObjects(s3BucketName);
        for(S3ObjectSummary os : objectListing.getObjectSummaries()) {
            if(os.getKey().equals(key)){
                return true;
            }
        }
        return false;
    }

    public void deleteAll(){
        ObjectListing objectListing = amazonS3.listObjects(s3BucketName);
        for(S3ObjectSummary os : objectListing.getObjectSummaries()) {
            amazonS3.deleteObject(new DeleteObjectRequest(s3BucketName, os.getKey()));
        }
    }

}
