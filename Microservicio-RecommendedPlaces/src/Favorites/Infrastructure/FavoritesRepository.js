const favorites = require('../Domain/favorite')

class FavoritesRepository { 

    async addFavorite({ userId, placeId }) {
       try{
            const favorite = new favorites({ userId, placeId });
            return await favorite.save();
        }catch(error){
            return error;
        }    
    }

    async getFavorites(userId) {
        try{
            return await favorites.find({ userId });
        }catch(error){
            return error;   
        }
    }

    async deleteFavorite({ userId, placeId }) {
        try{
            return await favorites.deleteOne({ userId, placeId });
        }catch(error){
            return error;
        }
    }

    async deleteFavoritesByUserId(userId) {
        try{
            return await favorites.deleteMany({ userId });
        }catch(error){
            return error;
        }
    }

    async getFavoritesByPlaceId(placeId) {
        try{
            return await favorites.find({ placeId });
        }catch(error){
            return error;
        }    
    }
    async deleteFavoritesByPlaceId(placeId) {
        try{
            return await favorites.deleteMany({ placeId });
        }
        catch(error){
            return error;
        }
    }
}
module.exports =  new FavoritesRepository();