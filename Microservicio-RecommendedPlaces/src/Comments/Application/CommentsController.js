const commentsService = require('../Domain/CommentsService');

class CommentsController {
    async createComment(req, res) {
        try {
            const comment = req.body;
            const result = await commentsService.crearComentario(comment);
            res.status(201).json(result);
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }

    async getComments(req, res) {
        try {
            const { pinId } = req.params;
            const comments = await commentsService.getComments(pinId);
            res.status(200).json(comments);
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }

    async getComment(req, res) {
        try {
            const { commentId } = req.params;
            const comment = await commentsService.getComment(commentId);
            res.status(200).json(comment);
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }

    async updateComment(req, res) {
        try {
            const { commentId } = req.params;
            const comment = req.body;
            const result = await commentsService.updateComment(commentId, comment);
            res.status(200).json(result);
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }

    async getCantidadComentarios(req, res) {
        try {
            const { pinId } = req.params;
            const cantidad = await commentsService.getCantidadComentarios(pinId);
            res.status(200).json({ cantidad });
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }

    async getCommentariosPorPalabra(req, res) {
        try {
            const { palabra } = req.params;
            const comments = await commentsService.getCommentariosPorPalabra(palabra);
            res.status(200).json(comments);
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }

    async deleteComment(req, res) {
        try {
            const { commentId } = req.params;
            await commentsService.deleteComment(commentId);
            res.status(200).json({ message: 'Comentario eliminado exitosamente' });
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }
}

module.exports = new CommentsController();