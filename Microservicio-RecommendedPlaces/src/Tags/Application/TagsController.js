const tagsService = require('../Domain/TagsService');

class TagsController {
    async getTags(req, res) {
        try {
            const {pindId} = req.params;
            const tags = await tagsService.getTags(pindId);
            res.status(200).json(tags);
        } catch (error) {
            console.error('Error obteniendo los tags:', error);
            res.status(500).send('Error obteniendo los tags');
        }
    }
    async getTag(req, res) {
        try {
            const {pinId} = req.params;
            const {tagId} = req.query;
            con
            const tag = await tagsService.getTag(pinId, tagId);
            res.status(200).json(tag);
        } catch (error) {
            console.error('Error obteniendo el tag:', error);
            res.status(500).send('Error obteniendo el tag');
        }
    }
    async createTag(req, res) {
        try {
            const tag = await tagsService.createTag(req.body);
            res.status(201).json(tag);
        } catch (error) {
            console.error('Error creando el tag:', error);
            res.status(500).send('Error creando el tag');
        }
    }
    async updateRatingTag(req, res) {
        try {
            const {tagId} = req.params;
            const tag = await tagsService.updateRatingTag(tagId, req.body);
            res.status(200).json(tag);
        } catch (error) {
            console.error('Error actualizando el tag:', error);
            res.status(500).send('Error actualizando el tag');
        }
    }

    async deleteTag(req, res) {
        try {
            const {tagId} = req.params;
            await tagsService.deleteTagFromPin(tagId, req.body);
            res.status(204).send();
        } catch (error) {
            console.error('Error eliminando el tag:', error);
            res.status(500).send('Error eliminando el tag');
        }
    }
    async getRatingFOrPin(req, res) {
        try {
            const {pinId} = req.params;
            const ratings = await tagsService.obtenerPromedioRatingPerTagForEveryPin(pinId);
            res.status(200).json(ratings);
        } catch (error) {
            console.error('Error obteniendo el rating del pin:', error);
            res.status(500).send('Error obteniendo el rating del pin');
        }
    }

}
module.exports = TagsController;