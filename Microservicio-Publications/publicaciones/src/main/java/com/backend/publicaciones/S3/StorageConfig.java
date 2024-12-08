package com.backend.publicaciones.S3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class StorageConfig {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.credentials.sessionToken}")
    private String sessionToken;

    @Value("${cloud.aws.region.static:us-east-1}")
    private String region;

    @Bean
    public AmazonS3 generateS3Client() {
        BasicSessionCredentials credentials = new BasicSessionCredentials(accessKey, secretKey, sessionToken);
        System.out.println("Credenciales: " + credentials);
        System.out.println("Region: " + region);
        System.out.println("SessionToken: " + sessionToken);
        System.out.println("AWS secretKey: " + secretKey);
        System.out.println("AWS accessKey: " + accessKey);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();

        try {
            System.out.println("Probando conexión a S3...");
            s3Client.listBuckets();
            System.out.println("Conexión a S3 exitosa.");
        } catch (AmazonS3Exception e) {
            System.err.println("Error en la conexión a S3: " + e.getMessage());
        }

        return s3Client;
    }

}