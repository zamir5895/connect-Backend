const mongoose = require('mongoose');  

const PinSchema = new mongoose.Schema({  
    userids: [{
        type: Number,
        required: true,
    }],
    titulo: {
        type: String,
        required: true,
        minlength: 5,  
        maxlength: 200
    },
    descripcion: {
        type: String,
        required: true,
        minlength: 5,  
        maxlength: 1000 
    },
    rating: {
        type: Number,
        required: false,
        default: 0,
        min: 0,
        max: 5
    },
    ratingTotal: {
        type: Number,
        required: false,
        default: 0,
    },
    ratingCount: {
        type: Number,
        required: false,
        default: 0,
    },
    latitude: {
        type: Number,
        required: true
    },
    longitude: {
        type: Number,
        required: true
    },
    createdAt: {
        type: Date,
        default: Date.now,
    }
});

module.exports = mongoose.model('Pin', PinSchema); 
