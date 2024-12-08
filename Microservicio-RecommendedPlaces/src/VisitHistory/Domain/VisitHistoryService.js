const VisitHistoryRepository =  require('../Infrastructure/VisitHistoryRepository');
const pinRepository = require('../../Pin/Infrastructure/PinRepository');
class VisitHistoryService {
    async saveHistory(visitHistory) {
        try {
            return await VisitHistoryRepository.save(visitHistory);
        } catch (error) {
            console.error('Error guardando el historial de visitas:', error);
            throw new Error('No se pudo guardar el historial de visitas');
        }
    }

    async getHistory(userId) {
        try {
            const visitados =  await VisitHistoryRepository.getHistory(userId);
            if(!visitados || visitados.length === 0){
                return [];
            }
            const pins = await Promise.all(visitados.map(async visitado =>{
                const pin = await pinRepository.getById(visitado.pinId);
                return {...pin.toObject(), fecha: visitado.fecha};
            }))
                
            return pins;

        } catch (error) {
            console.error('Error obteniendo el historial de visitas:', error);
            throw new Error('No se pudo obtener el historial de visitas');
        }
    }

    async deleteHistory(id) {
        try {
            const history = await VisitHistoryRepository.getHistoryById(id);
            if (!history) {
                throw new Error('No se encontró el historial de visitas');
            }
            return await VisitHistoryRepository.deleteHistory(id);
        } catch (error) {
            console.error('Error eliminando el historial de visitas:', error);
            throw new Error('No se pudo eliminar el historial de visitas');
        }
    }

    async deleteHistoryUser(userId) {
        try {
            return await VisitHistoryRepository.deleteHistoryUser(userId);
        } catch (error) {
            console.error('Error eliminando el historial de visitas:', error);
            throw new Error('No se pudo eliminar el historial de visitas');
        }
    }

    async deleteHistoryPin(pinId) {
        try {
            if(!await pinRepository.getById(pinId)){
                throw new Error('El pin no existe');
            }
            return await VisitHistoryRepository.deleteHistoryPin(pinId);
        } catch (error) {
            console.error('Error eliminando el historial de visitas:', error);
            throw new Error('No se pudo eliminar el historial de visitas');
        }
    }
    async isVisited(pinId, userId) {
        try {
            const visitado = await VisitHistoryRepository.isVisited(pinId, userId);
            return !!visitado; 
        } catch (error) {
            console.error('Error verificando si el pin fue visitado:', error);
            throw new Error('No se pudo verificar si el pin fue visitado');
        }
    }

}
module.exports =  new VisitHistoryService();