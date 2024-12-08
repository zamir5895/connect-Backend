const express = require('express');
const router = express.Router();
const commentsController = require('./Application/CommentsController');

/**
 * @swagger
 * /comments:
 *   post:
 *     tags:
 *       - Comentarios
 *     summary: Crea un nuevo comentario
 *     description: Agrega un comentario a un pin específico.
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               pinId:
 *                 type: string
 *                 description: ID del pin asociado al comentario
 *               userId:
 *                 type: string
 *                 description: ID del usuario que crea el comentario
 *               content:
 *                 type: string
 *                 description: Contenido del comentario
 *     responses:
 *       201:
 *         description: Comentario creado con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.post('/', commentsController.createComment);

/**
 * @swagger
 * /comments/{pinId}:
 *   get:
 *     tags:
 *       - Comentarios
 *     summary: Obtiene los comentarios de un pin
 *     description: Recupera una lista de comentarios asociados a un pin específico.
 *     parameters:
 *       - in: path
 *         name: pinId
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del pin
 *     responses:
 *       200:
 *         description: Lista de comentarios obtenida con éxito
 *       404:
 *         description: Pin no encontrado
 */
router.get('/:pinId', commentsController.getComments);

/**
 * @swagger
 * /comments/single/{commentId}:
 *   get:
 *     tags:
 *       - Comentarios
 *     summary: Obtiene un comentario específico
 *     description: Recupera los detalles de un comentario utilizando su ID.
 *     parameters:
 *       - in: path
 *         name: commentId
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del comentario
 *     responses:
 *       200:
 *         description: Comentario obtenido con éxito
 *       404:
 *         description: Comentario no encontrado
 */
router.get('/single/:commentId', commentsController.getComment);

/**
 * @swagger
 * /comments/{commentId}:
 *   put:
 *     tags:
 *       - Comentarios
 *     summary: Actualiza un comentario
 *     description: Modifica el contenido de un comentario específico utilizando su ID.
 *     parameters:
 *       - in: path
 *         name: commentId
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del comentario a actualizar
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               content:
 *                 type: string
 *                 description: Nuevo contenido del comentario
 *     responses:
 *       200:
 *         description: Comentario actualizado con éxito
 *       404:
 *         description: Comentario no encontrado
 */
router.put('/:commentId', commentsController.updateComment);


/**
 * @swagger
 * /comments/cantidad/{pinId}:
 *   get:
 *     tags:
 *       - Comentarios
 *     summary: Obtiene la cantidad de comentarios de un pin
 *     description: Devuelve el número total de comentarios asociados a un pin específico.
 *     parameters:
 *       - in: path
 *         name: pinId
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del pin
 *     responses:
 *       200:
 *         description: Cantidad de comentarios obtenida con éxito
 *       404:
 *         description: Pin no encontrado
 */
router.get('/cantidad/:pinId', commentsController.getCantidadComentarios);

/**
 * @swagger
 * /comments/{commentId}:
 *   delete:
 *     tags:
 *       - Comentarios
 *     summary: Elimina un comentario
 *     description: Elimina un comentario específico utilizando su ID.
 *     parameters:
 *       - in: path
 *         name: commentId
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del comentario a eliminar
 *     responses:
 *       200:
 *         description: Comentario eliminado con éxito
 *       404:
 *         description: Comentario no encontrado
 */
router.delete('/:commentId', commentsController.deleteComment);

module.exports = router;
