const express = require('express');
const router = express.Router();
const TagsController = require('./Application/TagsController');

const tagsController = new TagsController();

/**
 * @swagger
 * /tags/{pindId}:
 *   get:
 *     tags:
 *       - Tags
 *     summary: Obtiene todos los tags de un pin
 *     description: Recupera una lista de tags asociados a un pin específico.
 *     parameters:
 *       - in: path
 *         name: pindId
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del pin para obtener sus tags
 *     responses:
 *       200:
 *         description: Lista de tags obtenida con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.get('/:pindId', tagsController.getTags);

/**
 * @swagger
 * /tags/{pinId}/tag:
 *   get:
 *     tags:
 *       - Tags
 *     summary: Obtiene un tag específico de un pin
 *     description: Recupera un tag específico asociado a un pin utilizando el ID del pin y el ID del tag.
 *     parameters:
 *       - in: path
 *         name: pinId
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del pin
 *       - in: query
 *         name: tagId
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del tag a obtener
 *     responses:
 *       200:
 *         description: Tag obtenido con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.get('/:pinId/:tagId', tagsController.getTag);

/**
 * @swagger
 * /tags:
 *   post:
 *     tags:
 *       - Tags
 *     summary: Crea un nuevo tag
 *     description: Agrega un nuevo tag a la base de datos para un pin específico.
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               pinId:
 *                 type: string
 *                 description: ID del pin al que pertenece el tag
 *               name:
 *                 type: string
 *                 description: Nombre del tag
 *               rating:
 *                 type: number
 *                 description: Rating inicial del tag
 *     responses:
 *       201:
 *         description: Tag creado con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.post('/', tagsController.createTag);

/**
 * @swagger
 * /tags/{tagId}/rating:
 *   put:
 *     tags:
 *       - Tags
 *     summary: Actualiza el rating de un tag
 *     description: Modifica el rating de un tag específico utilizando su ID.
 *     parameters:
 *       - in: path
 *         name: tagId
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del tag a actualizar
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               rating:
 *                 type: number
 *                 description: Nuevo rating para el tag
 *     responses:
 *       200:
 *         description: Rating del tag actualizado con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.put('/:tagId/rating', tagsController.updateRatingTag);

/**
 * @swagger
 * /tags/{tagId}:
 *   delete:
 *     tags:
 *       - Tags
 *     summary: Elimina un tag específico
 *     description: Elimina un tag de la base de datos utilizando su ID.
 *     parameters:
 *       - in: path
 *         name: tagId
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del tag a eliminar
 *     responses:
 *       204:
 *         description: Tag eliminado con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.delete('/:tagId', tagsController.deleteTag);

/**
 * @swagger
 * /tags/{pinId}/rating:
 *   get:
 *     tags:
 *       - Tags
 *     summary: Obtiene el promedio de rating de un pin
 *     description: Calcula y devuelve el promedio de rating de los tags de un pin específico.
 *     parameters:
 *       - in: path
 *         name: pinId
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del pin para obtener el promedio de rating
 *     responses:
 *       200:
 *         description: Promedio de rating obtenido con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.get('/:pinId/rating', tagsController.getRatingFOrPin);

module.exports = router;
