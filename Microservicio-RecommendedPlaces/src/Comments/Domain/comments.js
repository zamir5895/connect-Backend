const mongoose = require('mongoose');

const CommentSchema = new mongoose.Schema({
    pinId: {  
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Pin',
        required: true,
    },
    userId: {  
        type: Number,
        required: true,
    },
    text: {
        type: String,
        required: true,
        minlength: 1,
        maxlength: 500,
    },
    createdAt: {
        type: Date,
        default: Date.now,
    },
});

module.exports = mongoose.model('Comment', CommentSchema);
