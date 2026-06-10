package com.movietracker.infrastructure.minio;

import com.movietracker.config.MinioProperties;
import com.movietracker.exception.StorageException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PosterStorageService {

    private static final String POSTER_OBJECT_PATTERN = "posters/%d.jpg";
    private static final String CONTENT_TYPE_JPEG = "image/jpeg";
    private static final String PUBLIC_READ_POLICY = """
            {
              "Version": "2012-10-17",
              "Statement": [
                {
                  "Effect": "Allow",
                  "Principal": {"AWS": ["*"]},
                  "Action": ["s3:GetObject"],
                  "Resource": ["arn:aws:s3:::%s/*"]
                }
              ]
            }
            """;

    private final MinioClient minioClient;
    private final MinioProperties properties;

    private volatile boolean bucketReady;

    /**
     * Poster rasmni MinIO ga saqlaydi va public URL qaytaradi.
     */
    public String storePoster(int tmdbId, byte[] image) {
        ensureBucket();
        String objectName = POSTER_OBJECT_PATTERN.formatted(tmdbId);
        try (ByteArrayInputStream stream = new ByteArrayInputStream(image)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(properties.bucket())
                    .object(objectName)
                    .stream(stream, image.length, -1)
                    .contentType(CONTENT_TYPE_JPEG)
                    .build());
        } catch (Exception e) {
            throw new StorageException("Poster MinIO ga saqlanmadi: " + objectName, e);
        }
        return "%s/%s/%s".formatted(properties.publicUrl(), properties.bucket(), objectName);
    }

    public void deletePoster(int tmdbId) {
        String objectName = POSTER_OBJECT_PATTERN.formatted(tmdbId);
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(properties.bucket())
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.warn("Poster o'chirilmadi: {}", objectName, e);
        }
    }

    private synchronized void ensureBucket() {
        if (bucketReady) {
            return;
        }
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(properties.bucket()).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(properties.bucket()).build());
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                        .bucket(properties.bucket())
                        .config(PUBLIC_READ_POLICY.formatted(properties.bucket()))
                        .build());
            }
            bucketReady = true;
        } catch (Exception e) {
            throw new StorageException("MinIO bucket tayyorlanmadi: " + properties.bucket(), e);
        }
    }
}
