const photosService = require('../Domain/PhotosService');

class PhotosController {
    async subirphotos(req, res) {
        upload(req, res, async (err) => {
            if (err) {
                return res.status(400).json({ error: 'Error al cargar el archivo' });
            }

            try {
                const file = req.file; 
                const { pinId, userId, description } = req.body;  

                if (!file) {
                    return res.status(400).json({ error: 'No se subió ningún archivo' });
                }

                const result = await photosService.uploadPhoto({ file, pinId, userId, description });
                res.status(200).json({ message: 'Foto subida exitosamente', data: result });
            } catch (error) {
                res.status(400).json({ error: error.message });
            }
        });
    }
    async obtenerphotos(req, res) {
        try {
            const { pinId } = req.params;
            const photos = await photosService.getPhotosByPinId(pinId);
            res.status(200).json({ photos });
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }

    async obtenerphoto(req, res) {
        try {
            const { objectKey } = req.params;
            const photo = await photosService.getPhotoByObjectKey(objectKey);
            res.status(200).json({ photo });
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }

    async eliminarphoto(req, res) {
        try {
            const { objectKey } = req.params;
            await photosService.deletePhotoByObjectKey(objectKey);
            res.status(200).json({ message: 'Photo deleted successfully' });
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }

    async eliminarphotosPin(req, res) {
        try {
            const { pinId } = req.params;
            await photosService.deletePhotosByPinId(pinId);
            res.status(200).json({ message: 'Photos deleted successfully' });
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }   

    async obtenerPresignedUrl(req, res) {
        try {
            const { objectKey } = req.params;
            const url = await photosService.getPresignedUrl(objectKey);
            res.status(200).json({ url });
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }
}

module.exports =  PhotosController;