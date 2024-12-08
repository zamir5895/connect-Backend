const Tag = require('../Domain/tags'); // Ajusta la ruta según tu proyecto

class TagsRepository {
    // Obtener todos los tags asociados a un pin
    async getTags(pinId) {
        try {
            const tagsForPin = await Tag.find({ pinId });
            return tagsForPin.length > 0 ? tagsForPin : [];
        } catch (error) {
            console.error('Error obteniendo los tags:', error);
            throw new Error('No se pudieron obtener los tags');
        }
    }

    // Obtener un tag específico por su ID
    async getTag(tagId) {
        try {
            if (!tagId) {
                throw new Error('El ID del tag no puede ser nulo');
            }
            return await Tag.findById(tagId);
        } catch (error) {
            console.error('Error obteniendo el tag:', error);
            throw new Error('No se pudo obtener el tag');
        }
    }

    // Crear un nuevo tag
    async createTag(tag) {
        try {
            if (!tag) {
                throw new Error('El tag proporcionado no puede ser nulo');
            }
            return await Tag.create(tag);
        } catch (error) {
            console.error('Error creando el tag:', error);
            throw new Error('No se pudo crear el tag');
        }
    }

    // Actualizar el rating de un tag
    async updateRatingTag(tagId, data) {
        try {
            console.log('tagId', tagId);
            if (!tagId || !data.newRating) {
                throw new Error('ID del tag o nueva calificación no pueden ser nulos');
            }

            const tag = await Tag.findById(tagId);
            if (!tag) {
                throw new Error('No se encontró el tag');
            }

            tag.ratingTotal += data.newRating;
            tag.ratingCount += 1;
            tag.rating = tag.ratingTotal / tag.ratingCount;

            return await tag.save();
        } catch (error) {
            console.error('Error actualizando el tag:', error);
            throw new Error('No se pudo actualizar el tag');
        }
    }

    // Eliminar un tag por ID y verificar el propietario
    async deleteTag(tagId, userId) {
        try {
            if (!tagId || !userId) {
                throw new Error('ID del tag o del usuario no pueden ser nulos');
            }

            const tag = await Tag.findById(tagId);
            if (!tag) {
                throw new Error('No se encontró el tag');
            }

            if (tag.userId !== userId) {
                throw new Error('No tienes permisos para eliminar este tag');
            }

            return await Tag.deleteOne({ _id: tagId });
        } catch (error) {
            console.error('Error eliminando el tag:', error);
            throw new Error('No se pudo eliminar el tag');
        }
    }

    // Eliminar todos los tags asociados a un pin
    async eliminarTags(pinId) {
        try {
            if (!pinId) {
                throw new Error('El ID del pin no puede ser nulo');
            }

            return await Tag.deleteMany({ pinId });
        } catch (error) {
            console.error('Error eliminando los tags:', error);
            throw new Error('No se pudieron eliminar los tags');
        }
    }
}

module.exports = new TagsRepository();
