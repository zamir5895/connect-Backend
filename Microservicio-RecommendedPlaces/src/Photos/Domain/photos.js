const mongoose = require('mongoose');

const PhotoSchema = new mongoose.Schema({
    pinId: {  
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Pin',
        required: true,
    },
    userId: {  
        type: Number,
        required: true,
    },
    url: {  
        type: String,
        required: true,
    },
    description: {
        type: String,
        maxlength: 500,
    },
    createdAt: {
        type: Date,
        default: Date.now,
    },
});

module.exports = mongoose.model('Photo', PhotoSchema);
