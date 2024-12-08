const express = require('express');
const router = express.Router();
const multer = require('multer');
const PhotosController = require('./Application/PhotosController');

const photosController = new PhotosController();
const upload = multer({ storage: multer.memoryStorage() }).single('file');

/**
 * @swagger
 * /photos/upload:
 *   post:
 *     tags:
 *       - Fotos
 *     summary: Sube una nueva foto
 *     description: Sube una foto y la asocia a un pin específico.
 *     requestBody:
 *       required: true
 *       content:
 *         multipart/form-data:
 *           schema:
 *             type: object
 *             properties:
 *               file:
 *                 type: string
 *                 format: binary
 *                 description: Archivo de imagen a subir
 *               pinId:
 *                 type: string
 *                 description: ID del pin al que pertenece la foto
 *               userId:
 *                 type: string
 *                 description: ID del usuario que sube la foto
 *               description:
 *                 type: string
 *                 description: Descripción de la foto
 *     responses:
 *       200:
 *         description: Foto subida exitosamente
 *       400:
 *         description: Error en la solicitud
 */
router.post('/upload', upload, (req, res) => photosController.subirphotos(req, res));

/**
 * @swagger
 * /photos/pin/{pinId}:
 *   get:
 *     tags:
 *       - Fotos
 *     summary: Obtiene todas las fotos de un pin
 *     description: Recupera todas las fotos asociadas a un pin específico.
 *     parameters:
 *       - in: path
 *         name: pinId
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del pin para obtener sus fotos
 *     responses:
 *       200:
 *         description: Fotos obtenidas con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.get('/pin/:pinId', (req, res) => photosController.obtenerphotos(req, res));

/**
 * @swagger
 * /photos/{objectKey}:
 *   get:
 *     tags:
 *       - Fotos
 *     summary: Obtiene una foto por clave de objeto
 *     description: Recupera una foto específica utilizando su clave de objeto.
 *     parameters:
 *       - in: path
 *         name: objectKey
 *         schema:
 *           type: string
 *         required: true
 *         description: Clave del objeto de la foto
 *     responses:
 *       200:
 *         description: Foto obtenida con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.get('/:objectKey', (req, res) => photosController.obtenerphoto(req, res));

/**
 * @swagger
 * /photos/{objectKey}:
 *   delete:
 *     tags:
 *       - Fotos
 *     summary: Elimina una foto por clave de objeto
 *     description: Elimina una foto de la base de datos y del almacenamiento utilizando su clave de objeto.
 *     parameters:
 *       - in: path
 *         name: objectKey
 *         schema:
 *           type: string
 *         required: true
 *         description: Clave del objeto de la foto a eliminar
 *     responses:
 *       200:
 *         description: Foto eliminada con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.delete('/:objectKey', (req, res) => photosController.eliminarphoto(req, res));

/**
 * @swagger
 * /photos/pin/{pinId}:
 *   delete:
 *     tags:
 *       - Fotos
 *     summary: Elimina todas las fotos de un pin
 *     description: Elimina todas las fotos asociadas a un pin específico.
 *     parameters:
 *       - in: path
 *         name: pinId
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del pin para eliminar sus fotos
 *     responses:
 *       200:
 *         description: Fotos eliminadas con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.delete('/pin/:pinId', (req, res) => photosController.eliminarphotosPin(req, res));

/**
 * @swagger
 * /photos/presigned-url/{objectKey}:
 *   get:
 *     tags:
 *       - Fotos
 *     summary: Obtiene una URL presignada para acceder a una foto
 *     description: Genera una URL presignada para acceder a una foto específica mediante su clave de objeto.
 *     parameters:
 *       - in: path
 *         name: objectKey
 *         schema:
 *           type: string
 *         required: true
 *         description: Clave del objeto de la foto para obtener la URL presignada
 *     responses:
 *       200:
 *         description: URL presignada obtenida con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.get('/presigned-url/:objectKey', (req, res) => photosController.obtenerPresignedUrl(req, res));

module.exports = router;
