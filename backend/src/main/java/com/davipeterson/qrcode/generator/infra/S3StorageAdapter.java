package com.davipeterson.qrcode.generator.infra;

import com.davipeterson.qrcode.generator.ports.StoragePorts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;

@Component
@Profile("s3")
public class S3StorageAdapter implements StoragePorts {
    private final S3Client s3Client;
    private final String bucketName;
    private final String publicUrl;

    public S3StorageAdapter(
            @Value("${aws.endpoint}") String endpoint,
            @Value("${aws.region}") String region,
            @Value("${aws.s3.bucket-name}") String bucketName,
            @Value("${aws.s3.public-url}") String publicUrl,
            @Value("${aws.access-key-id}") String accessKeyId,
            @Value("${aws.secret-access-key}") String secretAccessKey
    ) {
        this.bucketName = bucketName;
        this.publicUrl = publicUrl;

        var credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        var builder = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials));

        if (!endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint));
            builder.forcePathStyle(true);
        }

        this.s3Client = builder.build();
        createBucketIfNotExists();
    }

    private void createBucketIfNotExists() {
        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        } catch (BucketAlreadyOwnedByYouException e) {
            // bucket ja existe
        }
    }

    @Override
    public String uploadFile(byte[] fileData, String fileName, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName + ".png")
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));

        return publicUrl + "/" + bucketName + "/" + fileName + ".png";
    }
}
