const mongoose = require('mongoose');

const notificationSchema = new mongoose.Schema({
    senderId: {
        type: String,
        required: true
    },
    usernameSender:{
        type:String,
        required: true
    },
    usernameReceiver:{
        type:String,
        required: true
    },
    receiverId: {
        type: String,
        required: true
    },
    message: {
        type: String,
        required: true
    },
    eventoId:{
        type:String,
        required:true
    },
    type: {
        type: String,
        enum: ['Like', 'Comentario', 'Review'],
        required: true
    },
    creacion: {
        type: Date,
        default: Date.now
    },
    leido:{
        type:Boolean,
        default:false
    }

});

module.exports = mongoose.model('Notification', notificationSchema);
