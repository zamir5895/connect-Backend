const Pin = require('../Domain/Pin');

class PinRepository {
    async savePin(pinData) {
        try {
            const newPin = new Pin(pinData);
            console.log("pin creado", newPin);
            return await newPin.save();
        } catch (error) {
            console.error('Error guardando el pin:', error);
            throw new Error('No se pudo guardar el pin');
        }
    }

    async getAllPins() {
        try {
            return await Pin.find();
        } catch (error) {
            console.error('Error obteniendo todos los pines:', error);
            throw new Error('No se pudieron obtener los pines');
        }
    }

    async deletePins(id) {
        try {
            return await Pin.findByIdAndDelete(id);
        } catch (error) {
            console.error('Error eliminando el pin:', error);
            throw new Error('No se pudo eliminar el pin');
        }
    }

    async getById(id) {
        try {
            console.log("id", id);
            return await Pin.findById(id);
        } catch (error) {
            console.error('Error obteniendo el pin por ID:', error);
            throw new Error('No se pudo obtener el pin por ID');
        }
    }

    async addVisitor(id, userId) {
        try {
            return await Pin.findByIdAndUpdate(
                id,
                { $addToSet: { userids: userId } },
                { new: true }
            );
        } catch (error) {
            console.error('Error agregando visitante:', error);
            throw new Error('No se pudo agregar el visitante');
        }
    }

    async deleteVisitantFromPIn(id, userId) {
        try {
            return await Pin.findByIdAndUpdate(
                id, 
                { $pull: { userids: userId } },
                { new: true }
            );
        } catch (error) {
            console.error('Error eliminando visitante:', error);
            throw new Error('No se pudo eliminar el visitante');
        }
    }

    async changeDescription(data) {
        try {
            const pinsaved = await Pin.findById(data.id);
            if (data.userId !== pinsaved.userids[0]) {
                throw new Error('No puedes editar este pin');
            }
            return await pin.findByIdAndUpdate(
                data.id,
                { $set: { descripcion: data.descripcion } },
                { new: true }
            );
        } catch (error) {
            console.error('Error cambiando la descripción:', error);
            throw new Error('No se pudo cambiar la descripción');
        }
    }

    async changeTitle(data) {
        try {
            const pinsaved = await Pin.findById(data.id);
            if (data.userId !== pinsaved.userids[0]) {
                throw new Error('No puedes editar este pin');
            }
            return await pin.findByIdAndUpdate(
                data.id,
                { $set: { titulo: data.titulo } },
                { new: true }
            );
        } catch (error) {
            console.error('Error cambiando el título:', error);
            throw new Error('No se pudo cambiar el título');
        }
    }

    async getAllVisitants(id) {
        try {
            const pinget = await Pin.findById(id).populate('userids');
            return pinget ? pinget.userids : [];
        } catch (error) {
            console.error('Error obteniendo todos los visitantes:', error);
            throw new Error('No se pudo obtener todos los visitantes');
        }
    }

    async changeRating(data) {
        try {
            console.log("data", data.pinId);
            const id = data.pinId;
            console.log("id prueb aca si falla", id);
            const pinRes = await this.getById(id);
            console.log("pinRes", pinRes);
            if (!pinRes) {
                throw new Error('No existe el pin');
            }
            pinRes.ratingTotal += data.rating;
            pinRes.ratingCount += 1;
            pinRes.rating = pinRes.ratingTotal / pinRes.ratingCount;
            return await pinRes.save();
        } catch (error) {
            console.error('Error cambiando el rating:', error);
            throw new Error('No se pudo cambiar el rating');
        }
    }

    async getPinsByWord(palabra) {
        try {
            return await Pin.find({ $or:
                [{titulo: { $regex: palabra, $options: 'i' } },
                {descripcion: { $regex: palabra, $options: 'i' } }]
            }
            );
            
        } catch (error) {
            console.error('Error obteniendo pin por palabra:', error);
            throw new Error('No se pudo obtener el pin por palabra');
        }
    }

}

module.exports = new PinRepository();