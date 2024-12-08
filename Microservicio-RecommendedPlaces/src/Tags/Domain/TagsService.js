const tagsRepository = require('../Infrastructure/TagsRepository');
const pinRepository = require('../../Pin/Infrastructure/PinRepository');
class TagsService {
    async getTags(pinId) {
        try {
            return await tagsRepository.getTags(pinId);
        } catch (error) {
            console.error('Error obteniendo los tags:', error);
            throw new Error('No se pudieron obtener los tags');
        }
    }

    async getTag(pinId, tagId) {
        try {
            const tag =  await tagsRepository.getTag(tagId);
            if(!tag){
                throw new Error('No se encontró el tag');
            }
            if(tag.pinId !== pinId){
                throw new Error('El tag no pertenece al pin');
            }
            return tag;
        } catch (error) {
            console.error('Error obteniendo el tag:', error);
            throw new Error('No se pudo obtener el tag');
        }
    }

    async createTag(tag) {
        try {
            return await tagsRepository.createTag(tag);
        } catch (error) {
            console.error('Error creando el tag:', error);
            throw new Error('No se pudo crear el tag');
        }
    }

    async updateRatingTag(tagId, tag) {
        try {
            const tagUpdate =  await tagsRepository.getTag(tagId);
            if(!tagUpdate){
                throw new Error('No se encontró el tag');
            }
            return await tagsRepository.updateRatingTag(tagId, tag);
        } catch (error) {
            console.error('Error actualizando el tag:', error);
            throw new Error('No se pudo actualizar el tag');
        }
    }

    async deleteTagFromPin(tagId, userId) {
        try {
            return await tagsRepository.deleteTag(tagId, userId);
        } catch (error) {
            console.error('Error eliminando el tag:', error);
            throw new Error('No se pudo eliminar el tag');
        }
    }
    async eliminarTags(pinId){
        try{
            if(!await pinRepository.getPin(pinId)){
                throw new Error('El pin no existe');
            }

            return await tagsRepository.eliminarTags(pinId);
        }catch(error){
            console.error('Error eliminando los tags:', error);
            throw new Error('No se pudieron eliminar los tags');
        }
    }

    async obtenerPromedioRatingPerTagForEveryPin(pinId){
        try{
            const alltags = await tagsRepository.getTags(pinId);
            const tags = alltags.map(tag => tag.nombre);
            const tagsSet = new Set(tags);
            const tagsArray = Array.from(tagsSet);
            const tagsWithRating = tagsArray.map(tag => {
                const tagsFiltered = alltags.filter(t => t.nombre === tag);
                const rating = tagsFiltered.reduce((acumulado, tag) => acumulado + tag.rating, 0) / tagsFiltered.length;
                return {tag,  rating};
            });
            return tagsWithRating;

        }catch(error){
            console.error('Error obteniendo el promedio de rating por tag:', error);
            throw new Error('No se pudo obtener el promedio de rating por tag');
        }
    }
}
module.exports = new TagsService();