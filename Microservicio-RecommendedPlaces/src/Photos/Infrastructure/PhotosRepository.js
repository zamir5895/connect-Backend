const Photo = require('../Domain/photos');  

class PhotoRepository {
    async savePhoto(photoData) {
        try{
            const photo = new Photo(photoData);
            return await photo.save();
        }catch(error){
            return error;
        }
    }

    async getPhotosByPinId(pinId) {
        try{
            return await Photo.find({ pinId });
        }catch(error){
            return error;
        }
    }

    async getPhotoByObjectKey(objectKey) {
        try{
            return await Photo.findOne({ objectKey });
        }catch(error){
            return error;
        }
    }

    async deletePhotoByObjectKey(objectKey) {
        try{
            return await Photo.deleteOne({ objectKey });
        }catch(error){
            return error;
        }
    }

    async deletePhotosByPinId(pinId) {
        try{
            return await Photo.deleteMany({ pinId });
        }catch(error){
            return error;
        }
    }
}

module.exports =  new PhotoRepository();
