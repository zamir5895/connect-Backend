const mongoose = require("mongoose");

const likeSchema = mongoose.Schema({
  userId: { type: Number },
  userFullName: { type: String },
  date: { type: Date, default: Date.now },
});

const userSchema = mongoose.Schema({
  userId: { type: Number },
  fullName: { type: String },
  userFoto: { type: String },
});

const multimediaSchema = mongoose.Schema({
  multimediaId: { type: Number },
  url: { type: String },
});



const messageSchema = mongoose.Schema(
  {
    sender: userSchema, 
    content: { type: String, trim: true },
    chatId: { type: Number },
    readBy: [userSchema],
    likes: [likeSchema], 
    likesCount: { type: Number, default: 0 },
    status: { 
        type: String, 
        enum: ["ENVIADO", "LEIDO"],  
        default: "ENVIADO" 
      },    
    fechaMensaje: { type: Date, default: Date.now },
    multimedia: [multimediaSchema],
  },
);

const Message = mongoose.model("Message", messageSchema);
module.exports = Message;