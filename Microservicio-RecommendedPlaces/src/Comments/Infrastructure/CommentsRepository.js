const Comment = require('../Domain/comments');

class CommentsRepository {
    async createComment(comment) {
        try{
            console.log('comment', comment);
            return await Comment.create(comment);
        }catch(error){
            console.error('Error creando el comentario:', error);
            throw new Error('No se pudo crear el comentario');
        }
    }

    async getComments(pinId) {
        try {
            return await Comment.find({pinId});
        }catch(error){
            console.error('Error obteniendo los comentarios:', error);
            throw new Error('No se pudieron obtener los comentarios');
        }
    }

    async getComment(commentId) {
        try {
            return await Comment.findById(commentId);
        }catch(error){
            console.error('Error obteniendo el comentario:', error);
            throw new Error('No se pudo obtener el comentario');
        }
    }

    async updateComment(commentId, comment) {
        try {
            return await Comment.findByIdAndUpdate(
                commentId,
                { $set: { text: comment.text } }, 
                { new: true }
            );
        } catch (error) {
            console.error('Error actualizando el comentario:', error);
            throw new Error('No se pudo actualizar el comentario');
        }
    }

    async getCantidadComentarios(pinId) {
        try {
            return await Comment.countDocuments({pinId});
        }catch(error){
            console.error('Error obteniendo la cantidad de comentarios:', error);
            throw new Error('No se pudo obtener la cantidad de comentarios');
        }
    }

    async getCommentariosPorPalabra(palabra) {
        try {
            return await Comment.find({text: new RegExp(palabra, 'i')});
        }catch(error){
            console.error('Error obteniendo los comentarios por palabra:', error);
            throw new Error('No se pudieron obtener los comentarios por palabra');
        }
    }

    async deleteComment(commentId) {
        try {
            return await Comment.findByIdAndDelete(commentId);
        }catch(error){
            console.error('Error eliminando el comentario:', error);
            throw new Error('No se pudo eliminar el comentario');
        }
    }

    async deleteComments(pinId) {
        try {
            return await Comment.deleteMany({pinId});
        }catch(error){
            console.error('Error eliminando los comentarios:', error);
            throw new Error('No se pudieron eliminar los comentarios');
        }
    }
}
module.exports = new CommentsRepository();