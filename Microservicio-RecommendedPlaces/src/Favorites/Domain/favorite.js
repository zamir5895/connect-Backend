const mongoose = require('mongoose');

const FavoriteSchema = new mongoose.Schema({
    userId: { 
        type: Number,
        required: true,
    },
    pinId: { 
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Pin',
        required: true,
    },
    savedAt: {
        type: Date,
        default: Date.now,
    },
});

module.exports = mongoose.model('Favorite', FavoriteSchema);
