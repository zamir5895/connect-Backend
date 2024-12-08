const visitHistoryService =  require('../Domain/VisitHistoryService');

class VisitHistoryController {

    async saveVisitHistory(req, res) {
    try{
        const visita = req.body;
        const newVisitHistory = await visitHistoryService.saveHistory(visita);
        res.status(201).json(newVisitHistory);
    }
    catch (error) {
        res.status(400).json({ error: error.message });
    }
    
    }
    async getVisitHistory(req, res) {
        try {
            const userId = req.query.userId;
            const visitados = await visitHistoryService.getHistory(userId);
            res.status(200).json(visitados);
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }

    async deleteVisitHistory(req, res) {
        try {
            const { id } = req.params;
            await visitHistoryService.deleteHistory(id);
            res.status(204).end();
        } catch (error) {
            res.status(404).json({ message: error.message });
        }
    }
    async isvisited(req, res) {
        try {
            const { userId, pinId } = req.params;
            const visitado = await visitHistoryService.isVisited(pinId, userId);
            res.status(200).json({ visited: visitado }); // Devuelve un objeto consistente
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }
    


}
module.exports =  VisitHistoryController;

