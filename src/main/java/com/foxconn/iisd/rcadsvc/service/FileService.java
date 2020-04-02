package com.foxconn.iisd.rcadsvc.service;

import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface FileService {
    void save(Long faCaseId, String category, String name, MultipartFile file) throws InvalidPortException, InvalidEndpointException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, InvalidArgumentException, Exception;

    InputStream find(Long id, String category, String name) throws Exception;
    
    String getURL(String name) throws Exception;
    
    void delete(String name) throws Exception;
}
