package com.booking.app.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import com.booking.app.exception.MyException;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AwsS3Service {
  private final String bucketName = "hotel-images-27bcc";

  @Value("${aws.s3.access.key}")
  private String awsS3AccessKey;

  @Value("${aws.s3.secret.key}")
  private String awsS3SecretKey;

  public String saveImageToS3(MultipartFile photo) {
    try {
      String s3FileName = photo.getOriginalFilename();

      BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey);
      AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
        .withRegion(Regions.AP_SOUTHEAST_1)
        .build();

      InputStream inputStream = photo.getInputStream();

      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentType("image/jpeg");

      PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, inputStream, metadata);
      s3Client.putObject(putObjectRequest);
      return "https://" + bucketName + ".s3.amazonaws.com/" + s3FileName;
    } catch (Exception e) {
      e.printStackTrace();
      throw new MyException("Unable to upload image to s3 bucket" + e.getMessage());
    }
  }
}