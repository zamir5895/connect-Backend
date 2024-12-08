package com.backend.places.S3;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.databasemigrationservice.model.S3AccessDeniedException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class StorageService {

    @Value("${S3_BUCKET_NAME}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    public void testS3Connection() {
        try {
            // Verificar si el bucket existe
            boolean bucketExists = s3Client.doesBucketExistV2(bucketName);
            System.out.println("Conexión exitosa al bucket: " + bucketExists);
        } catch (Exception e) {
            System.err.println("Error en la conexión con S3: " + e.getMessage());
        }
    }

    // Convierte MultipartFile a archivo temporal en el sistema de archivos
    private File convertirMultipartFile(MultipartFile file) throws IOException {
        File tempFile = Files.createTempFile("temp-", Objects.requireNonNull(file.getOriginalFilename())).toFile();
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }
        return tempFile;
    }

    // Valida si el archivo cumple con los requisitos
    private void validarArchivo(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Archivo vacío");
        }
        if (file.getSize() > 500L * 1024 * 1024) { // Tamaño máximo de 500MB
            throw new IllegalArgumentException("El archivo excede el tamaño permitido de 500MB");
        }
    }

    // Valida el formato del nombre clave (key) del objeto en S3
    private void validarClave(String objectKey) {
        if (objectKey.isEmpty()) {
            throw new IllegalArgumentException("El key está vacío");
        }
        if (objectKey.endsWith("/") || objectKey.startsWith("/") || objectKey.startsWith("\\") || objectKey.endsWith("\\")) {
            throw new IllegalArgumentException("Formato inválido para el key");
        }
    }

    public String subirAlS3File(MultipartFile file, String objectKey) throws Exception, IOException {
        testS3Connection();
        validarArchivo(file);
        validarClave(objectKey);
        System.out.println("Subiendo archivo con key: " + objectKey);

        File fileObj = convertirMultipartFile(file);
        try {
            deleteFile(objectKey);

            Map<String, String> userMetadata = new HashMap<>();
            userMetadata.put("x-amz-meta-object-key", objectKey);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setUserMetadata(userMetadata);

            s3Client.putObject(new PutObjectRequest(bucketName, objectKey, fileObj).withMetadata(metadata));
            System.out.println("Archivo subido exitosamente a S3: " + objectKey);
            return objectKey;

        } catch (AmazonS3Exception e) {
            System.err.println("Error al subir archivo a S3: " + e.getMessage());
            throw new S3AccessDeniedException("No se pudo subir el archivo " + objectKey + ": " + e.getErrorCode() + " " + e.getMessage());
        } finally {
            if (fileObj != null && fileObj.exists()) {
                if (!fileObj.delete()) {
                    System.err.println("No se pudo eliminar el archivo temporal: " + fileObj.getAbsolutePath());
                } else {
                    System.out.println("Archivo temporal eliminado: " + fileObj.getAbsolutePath());
                }
            }
        }
    }

    public String obtenerURL(String objectKey) {
        boolean fileExist = s3Client.doesObjectExist(bucketName, objectKey);
        if (!fileExist) {
            System.out.println("El archivo no existe en S3: " + objectKey);
            return "";
        }
        return "https://" + bucketName + ".s3.amazonaws.com/" + objectKey;
    }


    public void deleteFile(String objectKey) {
        try {
            boolean fileExist = s3Client.doesObjectExist(bucketName, objectKey);
            if (!fileExist) {
                System.out.println("El archivo no existe en S3: " + objectKey);
                return;
            }

            s3Client.deleteObject(bucketName, objectKey);
            System.out.println("Archivo eliminado de S3: " + objectKey);
        } catch (AmazonServiceException e) {
            System.err.println("Error al eliminar el archivo de S3: " + e.getErrorMessage());
            throw new AmazonServiceException("No se pudo eliminar el archivo en S3: " + objectKey, e);
        }
    }
}