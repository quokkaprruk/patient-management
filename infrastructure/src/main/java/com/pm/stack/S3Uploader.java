package com.pm.stack;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.http.apache.ApacheHttpClient;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;

public class S3Uploader {

    private static final String ENDPOINT = "http://localhost:4566";
    private static final Region REGION = Region.US_EAST_1;
    private static final String ACCESS_KEY = "test";
    private static final String SECRET_KEY = "test";


    public static void createBucketAndUploadFile(String bucketName, String filePath) {
        try (S3Client s3 = createS3Client()) {
            createBucketIfNotExists(s3, bucketName);
            uploadFile(s3, bucketName, filePath);
        }
    }

    public static S3Client createS3Client() {
        return S3Client.builder()
                .region(REGION)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)))
                .endpointOverride(URI.create(ENDPOINT))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // Important for LocalStack
                        .build())
                .httpClientBuilder(ApacheHttpClient.builder())
                .build();
    }

    private static void createBucketIfNotExists(S3Client s3, String bucketName) {
        try {
            boolean exists = s3.listBuckets().buckets().stream()
                    .anyMatch(b -> b.name().equals(bucketName));

            if (!exists) {
                s3.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
                System.out.println("✅ Created bucket: " + bucketName);
            } else {
                System.out.println("Bucket already exists: " + bucketName);
            }

        } catch (S3Exception e) {
            System.err.println("Failed to create bucket: " + e.awsErrorDetails().errorMessage());
        }
    }

    private static void uploadFile(S3Client s3, String bucketName, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("❌ File not found: " + filePath);
            return;
        }

        try {
            s3.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(file.getName())
                            .build(),
                    Paths.get(filePath));

            System.out.println("✅ Uploaded: " + file.getName() + " to bucket: " + bucketName);
        } catch (SdkException e) {
            System.err.println("Upload failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        createBucketAndUploadFile("s3-patient-management", "infrastructure/cdk.out/localstack.template.json");
    }


}
