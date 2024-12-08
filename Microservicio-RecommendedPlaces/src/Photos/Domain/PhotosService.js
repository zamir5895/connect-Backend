const S3Service = require('./S3Service');
const PhotoRepository = require('../Infrastructure/PhotosRepository');
const { v4: uuidv4 } = require('uuid'); 
const PinRepository = require('../../Pin/Infrastructure/PinRepository'); 

class PhotosService {
    async uploadPhoto({ file, pinId, userId, description }) {  
        const objectKey = `${uuidv4()}-${file.originalname}`; 
        const url = await S3Service.uploadToS3(file, objectKey);

        const photoData = {
            pinId,
            userId,
            url,
            description,
            objectKey,
        };

        return await PhotoRepository.savePhoto(photoData);
    }

    async getPhotosByPinId(pinId) {
        return await PhotoRepository.getPhotosByPinId(pinId);
    }

    async getPhotoByObjectKey(objectKey) {
        return await PhotoRepository.getPhotoByObjectKey(objectKey);
    }

    async deletePhotoByObjectKey(objectKey) {
        await S3Service.deleteFile(objectKey);
        await PhotoRepository.deletePhotoByObjectKey(objectKey);
    }

    async eliminarPhotosPin(pinId) {
        const pin = await PinRepository.getPinById(pinId);
        if (!pin) {
            throw new Error('Pin ID is required');
        }
        
        const photos = await PhotoRepository.getPhotosByPinId(pinId);

        await Promise.all(photos.map(async (photo) => {
            await S3Service.deleteFile(photo.objectKey);
            await PhotoRepository.deletePhotoByObjectKey(photo.objectKey);
        }));
    }

    async getPresignedUrl(objectKey) {
        return await S3Service.getPresignedUrl(objectKey);
    }
}

module.exports = new PhotosService();
