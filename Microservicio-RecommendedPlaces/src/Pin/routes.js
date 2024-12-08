const express = require('express');
const router = express.Router();
const PinController = require('./Application/PinController');

const pinController = new PinController();

/**
 * @swagger
 * /pins:
 *   post:
 *     tags:
 *       - Pin
 *     summary: Crea un nuevo pin
 *     description: Guarda un nuevo pin en la base de datos.
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               titulo:
 *                 type: string
 *                 description: Título del pin
 *               descripcion:
 *                 type: string
 *                 description: Descripción del pin
 *               latitude:
 *                 type: number
 *                 description: Latitud de la ubicación del pin
 *               longitude:
 *                 type: number
 *                 description: Longitud de la ubicación del pin
 *     responses:
 *       201:
 *         description: Pin creado con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.post('/', pinController.guardarPin);

/**
 * @swagger
 * /pins:
 *   get:
 *     tags:
 *       - Pin
 *     summary: Obtiene todos los pines
 *     description: Recupera una lista de todos los pines disponibles en la base de datos.
 *     responses:
 *       200:
 *         description: Lista de pines obtenida con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.get('/', pinController.obtenerPines);

/**
 * @swagger
 * /pins/{id}:
 *   get:
 *     tags:
 *       - Pin
 *     summary: Obtiene un pin por ID
 *     description: Recupera los detalles de un pin específico mediante su ID.
 *     parameters:
 *       - in: path
 *         name: id
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del pin a obtener
 *     responses:
 *       200:
 *         description: Pin obtenido con éxito
 *       404:
 *         description: Pin no encontrado
 */
router.get('/:id', pinController.obtenerPinPorId);

/**
 * @swagger
 * /pins/{id}:
 *   delete:
 *     tags:
 *       - Pin
 *     summary: Elimina un pin por ID
 *     description: Elimina un pin de la base de datos mediante su ID.
 *     parameters:
 *       - in: path
 *         name: id
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del pin a eliminar
 *     responses:
 *       204:
 *         description: Pin eliminado con éxito
 *       404:
 *         description: Pin no encontrado
 */
router.delete('/:id', pinController.eliminarPin);

/**
 * @swagger
 * /pins/{id}/visitantes:
 *   get:
 *     tags:
 *       - Pin
 *     summary: Obtiene todos los visitantes de un pin
 *     description: Recupera una lista de todos los visitantes de un pin específico.
 *     parameters:
 *       - in: path
 *         name: id
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del pin
 *     responses:
 *       200:
 *         description: Lista de visitantes obtenida con éxito
 *       404:
 *         description: Pin no encontrado
 */
router.get('/:id/visitantes', pinController.obtenerTodosLosVisitantes);

/**
 * @swagger
 * /pins/visitantes:
 *   post:
 *     tags:
 *       - Pin
 *     summary: Agrega un visitante a un pin
 *     description: Añade un nuevo visitante a un pin en particular.
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               id:
 *                 type: string
 *                 description: ID del pin
 *               userId:
 *                 type: string
 *                 description: ID del usuario visitante
 *     responses:
 *       204:
 *         description: Visitante agregado con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.post('/visitantes', pinController.agregarVisitantes);

/**
 * @swagger
 * /pins/{id}/visitantes/{userId}:
 *   delete:
 *     tags:
 *       - Pin
 *     summary: Elimina un visitante de un pin
 *     description: Elimina el registro de un visitante específico de un pin mediante su ID de usuario.
 *     parameters:
 *       - in: path
 *         name: id
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del pin
 *       - in: path
 *         name: userId
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del usuario visitante a eliminar
 *     responses:
 *       204:
 *         description: Visitante eliminado con éxito
 *       404:
 *         description: Pin o visitante no encontrado
 */
router.delete('/:id/visitantes/:userId', pinController.eliminarVisitante);

/**
 * @swagger
 * /pins/rating:
 *   put:
 *     tags:
 *       - Pin
 *     summary: Edita el rating de un pin
 *     description: Actualiza el rating de un pin específico.
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               id:
 *                 type: string
 *                 description: ID del pin
 *               rating:
 *                 type: number
 *                 description: Nuevo rating
 *     responses:
 *       204:
 *         description: Rating actualizado con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.put('/rating', pinController.editaRating);

/**
 * @swagger
 * /pins/descripcion:
 *   put:
 *     tags:
 *       - Pin
 *     summary: Edita la descripción de un pin
 *     description: Actualiza la descripción de un pin específico.
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               id:
 *                 type: string
 *                 description: ID del pin
 *               descripcion:
 *                 type: string
 *                 description: Nueva descripción
 *     responses:
 *       204:
 *         description: Descripción actualizada con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.put('/descripcion', pinController.editarInformacion);

/**
 * @swagger
 * /pins/titulo:
 *   put:
 *     tags:
 *       - Pin
 *     summary: Edita el título de un pin
 *     description: Actualiza el título de un pin específico.
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               id:
 *                 type: string
 *                 description: ID del pin
 *               titulo:
 *                 type: string
 *                 description: Nuevo título
 *     responses:
 *       204:
 *         description: Título actualizado con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.put('/titulo', pinController.editarTitulo);

/**
 * @swagger
 * /pins/buscar:
 *   get:
 *     tags:
 *       - Pin
 *     summary: Busca pines por palabra clave
 *     description: Recupera pines que coincidan con una palabra clave en el título o descripción.
 *     parameters:
 *       - in: query
 *         name: palabra
 *         schema:
 *           type: string
 *         required: true
 *         description: Palabra clave para buscar pines
 *     responses:
 *       200:
 *         description: Pines encontrados con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.get('/buscar', pinController.getPinPorPalabra);

module.exports = router;
