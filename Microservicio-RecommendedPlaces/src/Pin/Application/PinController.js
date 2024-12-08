const pinService = require('../Domain/PinService');

class PinController {
    async guardarPin(req, res) {
        try {
            const pin = await pinService.guardarPin(req.body);
            res.status(201).json(pin);
        } catch (err) {
            console.log(err);
            res.status(400).json({ error: err.message });
        }
    }

    async obtenerPines(req, res) {
        try {
            const pines = await pinService.obtenerPines();
            res.status(200).json(pines);
        } catch (err) {
            res.status(400).json({ error: err.message });
        }
    }

    async eliminarPin(req, res) {
        try {
            const { id } = req.params;
            await pinService.eliminarPin(id);
            res.status(204).end();
        } catch (err) {
            res.status(404).json({ message: err.message });
        }
    }

    async obtenerPinPorId(req, res) {
        try {
            const { id } = req.params;
            const pin = await pinService.obtenerPinEspecifico(id);
            res.status(200).json(pin);
        } catch (err) {
            res.status(404).json({ message: err.message });
        }
    }


    async eliminarVisitante(req, res) {
        try {
            const { id, userId } = req.params;
            await pinService.eliminarVisitante(id, userId);
            res.status(204).end();
        } catch (err) {
            res.status(404).json({ message: err.message });
        }
    }
    async editaRating(req, res) {
        try {
            const data = req.body;
            await pinService.editarRating(data);
            res.status(204).end();
        } catch (err) {
            res.status(404).json({ message: err.message });
        }
    }
    async editarInformacion(req, res) {
        try {
            const data = req.body;
            await pinService.editarDescripcion(data);
            res.status(204).end();
        } catch (err) {
            res.status(404).json({ message: err.message });
        }
    }
    async eliminarVisitante(req, res) {
        try {
            const { id, userId } = req.params;
            await pinService.eliminarVisitante(id, userId);
            res.status(204).end();
        } catch (err) {
            res.status(404).json({ message: err.message });
        }
    }

    async obtenerTodosLosVisitantes(req, res) {
        try {
            const { id } = req.params;
            const visitantes = await pinService.obtenerTodosLosVisitantes(id);
            res.status(200).json(visitantes);
        } catch (err) {
            res.status(404).json({ message: err.message });
        }
    }

    async agregarVisitantes(req, res) {
        try {
            const { id, userId } = req.body;
            await pinService.agregarVisitantes(id, userId);
            res.status(204).end();
        } catch (err) {
            res.status(404).json({ message: err.message });
        }
    }
    async editarTitulo(req, res) {
        try {
            const data = req.body;
            await pinService.editarTitulo(data);
            res.status(204).end();
        } catch (err) {
            res.status(404).json({ message: err.message });
        }
    }

    async getPinPorPalabra(req, res) {
        try {
            const { palabra } = req.query;
            const pins = await pinService.getPinPorPalabra(palabra);
            res.status(200).json(pins);
        } catch (error) {
            res.status(400).json({ message: error.message });
        }
    }

}

module.exports = PinController;
