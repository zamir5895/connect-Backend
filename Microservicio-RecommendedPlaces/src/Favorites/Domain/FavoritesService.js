const favoritesRepository = require('../Infrastructure/FavoritesRepository');
const pinRepository = require('../../Pin/Infrastructure/PinRepository');
class FavoritesService {
    async addFavorite({ userId, placeId }) {
        if (!await pinRepository.getPinById(placeId)) {
            throw new Error('placeId is required');
        }
        return await favoritesRepository.addFavorite({ userId, placeId });
    }

    async getFavorites(userId) {
        return await favoritesRepository.getFavorites(userId);
    }

    async deleteFavorite({ userId, placeId }) {
        if (!await pinRepository.getPinById(placeId)) {
            throw new Error('placeId is required');
        }
        await favoritesRepository.deleteFavorite({ userId, placeId });
    }

    async deleteFavoritesByUserId(userId) {
        await favoritesRepository.deleteFavoritesByUserId(userId);
    }

    async getFavoritesByPlaceId(placeId) {
        if (!await pinRepository.getPinById(placeId)) {
            throw new Error('placeId is required');
        }
        return await favoritesRepository.getFavoritesByPlaceId(placeId);
    }

    async eliminarFavoritos(placeId) {
        if (!await pinRepository.getPinById(placeId)) {
            throw new Error('placeId is required');
        }
        await favoritesRepository.deleteFavoritesByPlaceId(placeId);
    }
}
module.exports =  new FavoritesService();