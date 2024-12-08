const express = require('express');
const router = express.Router();
const VisitHistoryController = require('./Application/VisitHistoryController');

const visitHistoryController = new VisitHistoryController();

/**
 * @swagger
 * /visit-history:
 *   post:
 *     tags:
 *       - Visit History
 *     summary: Guarda un historial de visitas
 *     description: Guarda un nuevo registro en el historial de visitas para un usuario específico.
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               userId:
 *                 type: string
 *                 description: ID del usuario que visita
 *               pinId:
 *                 type: string
 *                 description: ID del pin visitado
 *     responses:
 *       201:
 *         description: Historial de visitas guardado con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.post('/', visitHistoryController.saveVisitHistory);

/**
 * @swagger
 * /visit-history:
 *   get:
 *     tags:
 *       - Visit History
 *     summary: Obtiene el historial de visitas
 *     description: Recupera el historial de visitas de un usuario específico mediante su ID.
 *     parameters:
 *       - in: query
 *         name: userId
 *         schema:
 *           type: string
 *         description: ID del usuario para obtener su historial de visitas
 *     responses:
 *       200:
 *         description: Historial de visitas obtenido con éxito
 *       400:
 *         description: Error en la solicitud
 */
router.get('/', visitHistoryController.getVisitHistory);

/**
 * @swagger
 * /visit-history/{id}:
 *   delete:
 *     tags:
 *       - Visit History
 *     summary: Elimina un historial de visita
 *     description: Elimina un registro específico del historial de visitas utilizando su ID.
 *     parameters:
 *       - in: path
 *         name: id
 *         schema:
 *           type: string
 *         required: true
 *         description: ID del historial de visita a eliminar
 *     responses:
 *       204:
 *         description: Historial de visita eliminado con éxito
 *       404:
 *         description: Historial de visita no encontrado
 */
router.delete('/:id', visitHistoryController.deleteVisitHistory);

router.get('/:userId/:pinId', visitHistoryController.isvisited);


module.exports = router;
