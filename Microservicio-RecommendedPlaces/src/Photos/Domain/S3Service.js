const { S3Client, PutObjectCommand, GetObjectCommand, DeleteObjectCommand } = require('@aws-sdk/client-s3');
const { getSignedUrl } = require('@aws-sdk/s3-request-presigner'); // Importar getSignedUrl
const fs = require('fs').promises; // Usar promesas para mejor manejo
const path = require('path');
const { v4: uuidv4 } = require('uuid');
require('dotenv').config();

// Configuración del cliente S3
const s3 = new S3Client({
    credentials: {
        accessKeyId: process.env.AWS_ACCESS_KEY_ID,
        secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY,
        sessionToken: process.env.AWS_SESSION_TOKEN,
    },
    region: process.env.AWS_REGION,
});

const bucketName = process.env.S3_BUCKET_NAME;

class S3Service {
    static validateFile(file) {
        if (!file) throw new Error("Archivo no proporcionado");
        if (file.size > 500 * 1024 * 1024) throw new Error("El archivo excede el tamaño máximo de 500MB");
    }

    static validateObjectKey(objectKey) {
        if (!objectKey || !objectKey.includes(".")) throw new Error("Formato de clave de objeto no válido");
        if (objectKey.startsWith("/") || objectKey.endsWith("/")) throw new Error("Formato de clave de objeto no válido");
    }

    static async convertToTempFile(file) {
        const tempFilePath = path.join(__dirname, `${uuidv4()}-${file.originalname}`);
        await fs.writeFile(tempFilePath, file.buffer);
        return tempFilePath;
    }

    static async uploadToS3(file, objectKey) {
        this.validateFile(file);
        this.validateObjectKey(objectKey);

        const tempFilePath = await this.convertToTempFile(file);

        try {
            const metadata = {
                'x-amz-meta-object-key': objectKey,
            };
            const uploadParams = {
                Bucket: bucketName,
                Key: `photos/${objectKey}`,
                Body: fs.createReadStream(tempFilePath),
                ContentType: file.mimetype,
                Metadata: metadata,
                ACL: 'public-read',
            };

            const data = await s3.send(new PutObjectCommand(uploadParams));
            return `https://${bucketName}.s3.${process.env.AWS_REGION}.amazonaws.com/photos/${objectKey}`;
        } catch (error) {
            throw new Error(`Error al subir el archivo a S3: ${error.message}`);
        } finally {
            await fs.unlink(tempFilePath); // Cambiado a `unlink` (async)
        }
    }

    static async getPresignedUrl(objectKey) {
        this.validateObjectKey(objectKey);

        const command = new GetObjectCommand({
            Bucket: bucketName,
            Key: `photos/${objectKey}`,
        });

        try {
            const url = await getSignedUrl(s3, command, { expiresIn: 3600 * 24 * 365 });
            return url;
        } catch (error) {
            throw new Error(`Error al generar la URL presignada: ${error.message}`);
        }
    }

    static async deleteFile(objectKey) {
        this.validateObjectKey(objectKey);

        const deleteParams = {
            Bucket: bucketName,
            Key: `photos/${objectKey}`,
        };

        try {
            await s3.send(new DeleteObjectCommand(deleteParams));
        } catch (error) {
            throw new Error(`Error al eliminar el archivo de S3: ${error.message}`);
        }
    }
}

module.exports = S3Service;
