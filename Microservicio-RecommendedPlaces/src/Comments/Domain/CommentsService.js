const commentsRepository = require('../Infrastructure/CommentsRepository');
const pinRepository = require('../../Pin/Infrastructure/PinRepository');
class CommentsService{

    async crearComentario(comment){
        try{
            if(!await pinRepository.getById(comment.pinId)){
                throw new Error('El pin no existe');
            }
            
            return await commentsRepository.createComment(comment);
        }catch(error){
            console.error('Error creando el comentario:', error);
            throw new Error('No se pudo crear el comentario');
        }
    }
    async getComments(pinId){
        try{
            if(!await pinRepository.getById(pinId)){
                throw new Error('El pin no existe');
            }
            return await commentsRepository.getComments(pinId);
        }catch(error){
            console.error('Error obteniendo los comentarios:', error);
            throw new Error('No se pudieron obtener los comentarios');
        }
    }

    async getComment(commentId){
        try{
            return await commentsRepository.getComment(commentId);
        }catch(error){
            console.error('Error obteniendo el comentario:', error);
            throw new Error('No se pudo obtener el comentario');
        }
    }
    async updateComment(commentId, comment){
        try{
            return await commentsRepository.updateComment(commentId, comment);
        }catch(error){
            console.error('Error actualizando el comentario:', error);
            throw new Error('No se pudo actualizar el comentario');
        }
    }   
    async getCantidadComentarios(pinId){
        try{
            if(!await pinRepository.getById(pinId)){
                throw new Error('El pin no existe');
            }
            return await commentsRepository.getCantidadComentarios(pinId);
        }catch(error){
            console.error('Error obteniendo la cantidad de comentarios:', error);
            throw new Error('No se pudo obtener la cantidad de comentarios');
        }
    }
    async getCommentariosPorPalabra(palabra){
        try{
            return await commentsRepository.getCommentariosPorPalabra(palabra);
        }catch(error){
            console.error('Error obteniendo los comentarios por palabra:', error);
            throw new Error('No se pudieron obtener los comentarios por palabra');
        }
    }
    async deleteComment(commentId){
        try{
            return await commentsRepository.deleteComment(commentId);
        }catch(error){
            console.error('Error eliminando el comentario:', error);
            throw new Error('No se pudo eliminar el comentario');
        }
    }

    async eliminarComentarios(pinId){
        try{
            if(!await pinRepository.getById(pinId)){
                throw new Error('El pin no existe');
            }
            return await commentsRepository.deleteComments(pinId);
        }catch(error){
            console.error('Error eliminando los comentarios:', error);
            throw new Error('No se pudieron eliminar los comentarios');
        }
    }


}
module.exports = new CommentsService();