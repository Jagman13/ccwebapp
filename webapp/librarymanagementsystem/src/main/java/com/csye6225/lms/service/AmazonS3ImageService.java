package com.csye6225.lms.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.csye6225.lms.exception.ResourceNotFoundException;
import com.csye6225.lms.pojo.Book;
import com.csye6225.lms.pojo.Image;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
public class AmazonS3ImageService {

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
            throw new ResourceNotFoundException("Image not found in S3 bucket");
        }
        /* get signed URL (valid for 2 minutes) */
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(s3BucketName, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(DateTime.now().plusMinutes(2).toDate());
        URL signedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
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

}
