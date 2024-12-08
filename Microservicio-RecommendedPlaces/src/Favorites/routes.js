const express = require('express');
const router = express.Router();
const favoritesController = require('./Application/FavoritesController');

/**
 * @swagger
 * /favorites:
 *   post:
 *     tags:
 *       - Favoritos
 *     summary: Añade un lugar a favoritos
 *     description: Agrega un lugar a la lista de favoritos de un usuario.
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               userId:
 *                 type: string
 *                 description: ID del usuario
 *               placeId:
 *                 type: string
 *                 description: ID del lugar que se añadirá a favoritos
 *     responses:
 *       201:
 *         description: Favorito añadido exitosamente
 *       400:
 *         description: Error en la solicitud
 */
router.post('/', favoritesController.addFavorite);

/**
 * @swagger
 * /favorites/{userId}:
 *   get:
 *     tags:
 *       - Favoritos
 *     summary: Obtiene los favoritos de un usuario
 *     description: Recupera la lista de lugares favoritos de un usuario específico.
 *     parameters:
 *       - in: path
 *         name: userId
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del usuario
 *     responses:
 *       200:
 *         description: Lista de favoritos obtenida con éxito
 *       404:
 *         description: Usuario no encontrado
 */
router.get('/:userId', favoritesController.getFavorites);

/**
 * @swagger
 * /favorites:
 *   delete:
 *     tags:
 *       - Favoritos
 *     summary: Elimina un lugar de favoritos
 *     description: Elimina un lugar específico de la lista de favoritos de un usuario.
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               userId:
 *                 type: string
 *                 description: ID del usuario
 *               placeId:
 *                 type: string
 *                 description: ID del lugar a eliminar de favoritos
 *     responses:
 *       204:
 *         description: Favorito eliminado con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.delete('/', favoritesController.deleteFavorite);

/**
 * @swagger
 * /favorites/{userId}:
 *   delete:
 *     tags:
 *       - Favoritos
 *     summary: Elimina todos los favoritos de un usuario
 *     description: Elimina todos los lugares de la lista de favoritos de un usuario específico.
 *     parameters:
 *       - in: path
 *         name: userId
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del usuario
 *     responses:
 *       204:
 *         description: Todos los favoritos del usuario eliminados con éxito
 *       404:
 *         description: Usuario no encontrado
 */
router.delete('/:userId', favoritesController.eliminarAllFavoritesFromUser);

/**
 * @swagger
 * /favorites/place/{placeId}:
 *   get:
 *     tags:
 *       - Favoritos
 *     summary: Obtiene los usuarios que tienen un lugar específico como favorito
 *     description: Recupera una lista de usuarios que han añadido un lugar específico a sus favoritos.
 *     parameters:
 *       - in: path
 *         name: placeId
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del lugar
 *     responses:
 *       200:
 *         description: Lista de usuarios obtenida con éxito
 *       404:
 *         description: Lugar no encontrado
 */
router.get('/place/:placeId', favoritesController.getFavoritesByPlaceId);

module.exports = router;
