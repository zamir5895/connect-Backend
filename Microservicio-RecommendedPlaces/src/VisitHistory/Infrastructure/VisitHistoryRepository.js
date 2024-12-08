const history = require('../Domain/visithistory')


class VisitHistoryRepository {
    async save(visitHistory) {
        return await history.create(visitHistory);
    }

    async getHistory(userId) {
        return await history.find({ userId });
    }

    async getHistoryById(id) {
        return await history.findById(id);
    }

    async deleteHistory(id) {
        return await history.findByIdAndDelete(id);
    }
    async deleteHistoryUser(userId) {
        return await history.deleteMany({ userId });
    }
    async deleteHistoryPin(pinId) {
        return await history.deleteMany({ pinId });
    }
    async isVisited(pinId, userId) {
        try {
            const resultado = await history.findOne({ userId, pinId }); 
            return !!resultado; 
        } catch (error) {
            console.error('Error en el repositorio al verificar si el pin fue visitado:', error);
            throw new Error('Error en la base de datos al verificar si el pin fue visitado');
        }
    }
    
}
module.exports =  new VisitHistoryRepository();