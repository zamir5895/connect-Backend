const mongoose = require('mongoose');

const VisitHistorySchema = new mongoose.Schema({
    userId: {  
        type: Number,
        required: true,
    },
    pinId: { 
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Pin',
        required: true,
    },
    fecha: {
        type: Date,
        default: Date.now,
    },
});

module.exports = mongoose.model('VisitHistory', VisitHistorySchema);
