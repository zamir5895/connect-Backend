const favoritesService = require('../Domain/FavoritesService');

class FavoritesController {
    async addFavorite(req, res) {
        try {
            const { userId, placeId } = req.body;
            const favorite = await favoritesService.addFavorite({ userId, placeId });
            res.status(200).json({ message: 'Favorite added successfully', data: favorite });
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }

    async getFavorites(req, res) {
        try {
            const { userId } = req.params;
            const favorites = await favoritesService.getFavorites(userId);
            res.status(200).json({ favorites });
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }
    async deleteFavorite(req, res) {
        try {
            const { userId, placeId } = req.body;
            await favoritesService.deleteFavorite({ userId, placeId });
            res.status(200).json({ message: 'Favorite deleted successfully' });
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }

    async eliminarAllFavoritesFromUser(req, res) {
        try {
            const { userId } = req.params;
            await favoritesService.deleteFavoritesByUserId(userId);
            res.status(200).json({ message: 'Favorites deleted successfully' });
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }

    async getFavoritesByPlaceId(req, res) {
        try {
            const { placeId } = req.params;
            const favorites = await favoritesService.getFavoritesByPlaceId(placeId);
            res.status(200).json({ favorites });
        } catch (error) {
            res.status(400).json({ error: error.message });
        }
    }

}
module.exports =  new FavoritesController();
