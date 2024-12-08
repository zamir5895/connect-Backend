const pinRepository = require('../Infrastructure/PinRepository');
const visitHistoryService = require('../../VisitHistory/Domain/VisitHistoryService');
const tagsService = require('../../Tags/Domain/TagsService');
const commentsService = require('../../Comments/Domain/CommentsService');
const photosService = require('../../Photos/Domain/PhotosService');
const favoriteService = require('../../Favorites/Domain/FavoritesService');


class PinService {
    async guardarPin(pin) {
        try {
            const savedPin = await pinRepository.savePin(pin);
            savedPin.userids.forEach(async (userId) => {
                const visitHistory = { userId, pinId: savedPin._id };
                await visitHistoryService.saveHistory(visitHistory);
            });
            return savedPin;
        } catch (error) {
            console.error('Error guardando el pin:', error);
            throw new Error('No se pudo guardar el pin');
        }
    }

    async obtenerPines() {
        try {
            return await pinRepository.getAllPins();
        } catch (error) {
            console.error('Error obteniendo los pines:', error);
            throw new Error('No se pudieron obtener los pines');
        }
    }

    async eliminarPin(id) {
        try {
            await tagsService.eliminarTags(id);
            await commentsService.eliminarComentarios(id);
            await photosService.eliminarPhotosPin(id);
            await favoriteService.eliminarFavoritos(id);
            await visitHistoryService.deleteHistoryPin(id);
            const pindelete = await pinRepository.deletePins(id);
            return pindelete;
        } catch (error) {
            console.error('Error eliminando el pin:', error);
            throw new Error('No se pudo eliminar el pin');
        }
    }

    async obtenerPinEspecifico(id) {
        try {
            return await pinRepository.getById(id);
        } catch (error) {
            console.error('Error obteniendo el pin específico:', error);
            throw new Error('No se pudo obtener el pin específico');
        }
    }
    

    async agregarVisitantes(id, userId) {
        try {
            const updatedPin = await pinRepository.addVisitor(id, userId);
            const data = { pinId: id, userId };
            await visitHistoryService.saveHistory(data);
            return updatedPin;
        } catch (error) {
            console.error('Error agregando visitantes:', error);
            throw new Error('No se pudo agregar visitantes');
        }
    }

    async obtenerTodosLosVisitantes(id) {
        try {
            const pins = await pinRepository.getAllVisitants(id);
            if (pins.length === 0) {
                throw new Error('No existe el pin');
            } else {
                return pins;
            }
        } catch (error) {
            console.error('Error obteniendo todos los visitantes:', error);
            throw new Error('No se pudo obtener todos los visitantes');
        }
    }

    async eliminarVisitante(id, userId) {
        try {
            return await pinRepository.deleteVisitantFromPIn(id, userId);
        } catch (error) {
            console.error('Error eliminando visitante:', error);
            throw new Error('No se pudo eliminar el visitante');
        }
    }

    async editarRating(data) {
        try {
            return await pinRepository.changeRating(data);
        } catch (error) {
            console.error('Error editando rating:', error);
            throw new Error('No se pudo editar el rating');
        }
    }

    async editarDescripcion(data) {
        try {
            return await pinRepository.changeDescription(data);
        } catch (error) {
            console.error('Error editando descripción:', error);
            throw new Error('No se pudo editar la descripción');
        }
    }

    async editarTitulo(data) {
        try {
            return await pinRepository.changeTitle(data);
        } catch (error) {
            console.error('Error editando título:', error);
            throw new Error('No se pudo editar el título');
        }
    }
    
    async getPinPorPalabra(palabra) {
        try{
            return await pinRepository.getPinsByWord(palabra);
        }catch(error){
            console.error('Error obteniendo pin por palabra:', error);
            throw new Error('No se pudo obtener el pin por palabra');
        }
    }
}

module.exports = new PinService();