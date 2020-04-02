package com.foxconn.iisd.rcadsvc.service.Impl;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.foxconn.iisd.rcadsvc.service.FileService;

import io.minio.MinioClient;
import io.minio.http.Method;

@Component("minioFileService")
public class MinioFileService implements FileService {
    @Value("${rca.minio.endpoint}")
    private String endPoint;

    @Value("${rca.minio.accessKey}")
    private String accessKey;

    @Value("${rca.minio.secretKey}")
    private String secretKey;

    @Value("${rca.minio.bucket}")
    private String bucket;

    @Value("${rca.minio.bu}")
    private String bu;

    public static String CONTENT_TYPE = "application/octet-stream";

    @Override
    public void save(Long faCaseId, String category, String name, MultipartFile file) throws Exception {
        MinioClient minioClient = new MinioClient(endPoint, accessKey, secretKey);
        if (!minioClient.bucketExists(bucket)) {
            minioClient.makeBucket(bucket);
        }
        String objectName = category + "/" + bu + "/" + faCaseId + "/" + name;
        minioClient.putObject(bucket, objectName, file.getInputStream(), CONTENT_TYPE);
    }

    @Override
    public InputStream find(Long id, String category, String name) throws Exception {
        MinioClient minioClient = new MinioClient(endPoint, accessKey, secretKey);
        String objectName = category + "/" + bu + "/" + id + "/" + name;
        return minioClient.getObject(bucket, objectName);
    }
    
    @Override
    public String getURL(String name) throws Exception {
    	MinioClient minioClient = new MinioClient(endPoint, accessKey, secretKey);
    	return minioClient.getPresignedObjectUrl(Method.GET, bucket, name, 60*60*24*7, null);
    }
    
    @Override
    public void delete(String name) throws Exception {
    	MinioClient minioClient = new MinioClient(endPoint, accessKey, secretKey);
    	minioClient.removeObject(bucket, name);
    }
}
