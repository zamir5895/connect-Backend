const asyncHandler = require("express-async-handler");
const Message = require('./messageModel');
const LatestMessage = require('./latestMessageModel');
const { verifyAuthentication, updateChatLastMessage } = require('./utils');
const axios = require('axios');
const multer = require('multer');
const FormData = require('form-data');

const storage = multer.memoryStorage();
const upload = multer({ storage });

const allMessages = asyncHandler(async (req, res) => {
  const token = req.headers.authorization;

  if (!token || !(await verifyAuthentication(token))) {
    return res.status(401).json({ message: "Usuario no autenticado" });
  }

  const messages = await Message.find({ chatId: req.params.chatId })
    .populate("sender", "name pic email")
    .populate("chatId");
  res.status(200).json(messages);
});

const sendMessage = asyncHandler(async (req, res) => {
  const { content, chatId, userId, fullName, userFoto } = req.body;
  if (!content || !chatId) {
    return res.status(400).json({ message: "Datos inválidos" });
  }

  const token = req.headers.authorization;

  if (!token || !(await verifyAuthentication(token))) {
    return res.status(401).json({ message: "Usuario no autenticado" });
  }

  const newMessage = {
    sender: {
        userId,
        fullName,
        userFoto,
    },
    content,
    chatId,
  };
  const message = await Message.create(newMessage);

  await LatestMessage.findOneAndUpdate(
    { chatId },
    { latestMessage: message._id },
    { new: true, upsert: true }
  );
  if (!(await updateChatLastMessage(chatId))) {
    return res.status(404).json({ message: "El chat no existe o no se pudo actualizar el último mensaje" });
  }
  res.status(200).json(message);
});

const modifymessage = asyncHandler(async (req, res) => {
  const { chatId, messageId } = req.params;
  const { content } = req.body;

  if (!content) {
    console.log("Datos inválidos en la solicitud");
    return res.status(400).json({ message: "Datos inválidos" });
  }

  const token = req.headers.authorization;

  if (!token || !(await verifyAuthentication(token))) {
    return res.status(401).json({ message: "Usuario no autenticado" });
  }

  if (!(await updateChatLastMessage(chatId))) {
    return res.status(404).json({ message: "El chat no existe o no se pudo actualizar el último mensaje" });
  }

  const message = await Message.findOneAndUpdate(
    { _id: messageId, chatId },
    { content },
    { new: true }
  );

  if (!message) {
    return res.status(404).json({ message: "Mensaje no encontrado" });
  }

  res.status(200).json(message);
});


const uploadFile = asyncHandler(async (req, res) => {
    const { chatId, messageId } = req.params;
    const file = req.file; 

    if (!file) {
        console.log("Archivo no proporcionado en la solicitud");
        return res.status(400).json({ message: "Archivo requerido" });
    }

    const token = req.headers.authorization;

    if (!token || !(await verifyAuthentication(token))) {
        return res.status(401).json({ message: "Usuario no autenticado" });
    }

    

    try {
        const formData = new FormData();
        formData.append('file', file.buffer, file.originalname); 

        const response = await axios.post(
            `http://localhost:8080/api/multimedia/${chatId}/${messageId}/subir`,
            formData,
            {
                headers: {
                    ...formData.getHeaders(), 
                },
            }
        );
        
        console.log("Respuesta de subir archivo:", response.data);

        const multimedia = {
            multimediaId: response.data.id,
            url: response.data.url,
        };

        const message = await Message.findOneAndUpdate(
            { _id: messageId, chatId },
            { $push: { multimedia: multimedia } },
            { new: true }
        );

        if (!message) {
            return res.status(404).json({ message: "Mensaje no encontrado" });
        }
        if (!(await updateChatLastMessage(chatId))) {
            return res.status(404).json({ message: "El chat no existe o no se pudo actualizar el último mensaje" });
        }

        res.status(200).json(message);
    } catch (error) {
        console.log("Error al subir el archivo:", error);
        return res.status(500).json({ message: "Error al subir el archivo" });
    }
});


const deletemessage = asyncHandler(async (req, res) => {
    const { chatId, messageId } = req.params;
    const token = req.headers.authorization;

    if (!token || !(await verifyAuthentication(token))) {
        return res.status(401).json({ message: "Usuario no autenticado" });
    }

    const message = await Message.findOneAndDelete({ _id: messageId, chatId });

    if (!message) {
        return res.status(404).json({ message: "Mensaje no encontrado" });
    }

    if (message.multimedia && message.multimedia.multimediaId) {
        try {
            const response = await axios.delete(
                `http://localhost:8080/api/multimedia/${chatId}/${messageId}/${message.multimedia.multimediaId}`,
                { headers: { Authorization: token } }
            );
            console.log("Respuesta de eliminar archivo:", response.data);
        } catch (error) {
            console.log("Error al eliminar el archivo multimedia:", error);
            return res.status(500).json({ message: "Error al eliminar el archivo multimedia" });
        }
    }

    const latestMessageRecord = await LatestMessage.findOne({ chatId });
    if (latestMessageRecord && latestMessageRecord.latestMessage.toString() === messageId) {
        const previousMessage = await Message.findOne({ chatId })
            .sort({ fechaMensaje: -1 }) 
            .limit(1);

        if (previousMessage) {
            latestMessageRecord.latestMessage = previousMessage._id;
            await latestMessageRecord.save();
        } else {
            await LatestMessage.deleteOne({ chatId }); 
        }
    }

    res.status(200).json({ message: "Mensaje y multimedia eliminados correctamente" });
});


const getmessage = asyncHandler(async (req, res) => {
    const { chatId, messageId } = req.params;
    
    const token = req.headers.authorization;
    
    if (!token || !(await verifyAuthentication(token))) {
        return res.status(401).json({ message: "Usuario no autenticado" });
    }
    
    const message = await Message.findOne({ _id: messageId, chatId })    
    if (!message) {
        return res.status(404).json({ message: "Mensaje no encontrado" });
    }
    
    res.status(200).json(message);
    }
);


const readmessage = asyncHandler(async (req, res) => {
    const { chatId, messageId } = req.params;
    const { userId, userFullName, userFoto } = req.body;
  
    const token = req.headers.authorization;
  
    if (!token || !(await verifyAuthentication(token))) {
      return res.status(401).json({ message: "Usuario no autenticado" });
    }
  
    const message = await Message.findOneAndUpdate(
      { _id: messageId, chatId },
      { 
        $set: { status: "LEIDO" },
        $addToSet: { readBy: { userId, fullName: userFullName, userFoto } } 
      },
      { new: true }
    );
  
    if (!message) {
      return res.status(404).json({ message: "Mensaje no encontrado" });
    }
  
    res.status(200).json(message);
  });
  

const unreadmessage = asyncHandler(async (req, res) => {
    const { chatId, messageId } = req.params;
    const { userId } = req.body;
  
    const token = req.headers.authorization;
  
    if (!token || !(await verifyAuthentication(token))) {
      return res.status(401).json({ message: "Usuario no autenticado" });
    }
  
    const message = await Message.findOneAndUpdate(
      { _id: messageId, chatId },
      { 
        $set: { status: "ENVIADO" },
        $pull: { readBy: { userId } } 
      },
      { new: true }
    );
  
    if (!message) {
      return res.status(404).json({ message: "Mensaje no encontrado" });
    }
  
    res.status(200).json(message);
  });
  

const likemessage = asyncHandler(async (req, res) => {
    const { chatId, messageId } = req.params;
    
    const token = req.headers.authorization;
    
    if (!token || !(await verifyAuthentication(token))) {
        return res.status(401).json({ message: "Usuario no autenticado" });
    }
    
    const message = await Message.findOne({ _id: messageId, chatId });
    
    if (!message) {
        return res.status(404).json({ message: "Mensaje no encontrado" });
    }
    
    const user = {
        userId: req.body.userId,
        userFullName: req.body.userFullName,
    };
    
    const like = message.likes.find((like) => like.userId == user.userId);
    
    if (like) {
        message.likes.pull(like);
    } else {
        message.likes.push(user);
    }
    
    message.likesCount = message.likes.length;
    
    await message.save();
    
    if (!(await updateChatLastMessage(chatId))) {
        return res.status(404).json({ message: "El chat no existe o no se pudo actualizar el último mensaje" });
    }
    res.status(200).json(message);
    }
);

const getLikes = asyncHandler(async (req, res) => {
    const { chatId, messageId } = req.params;
    
    const token = req.headers.authorization;
    
    if (!token || !(await verifyAuthentication(token))) {
        return res.status(401).json({ message: "Usuario no autenticado" });
    }
    
    const message = await Message.findOne({ _id: messageId, chatId });
    
    if (!message) {
        return res.status(404).json({ message: "Mensaje no encontrado" });
    }
    
    res.status(200).json(message.likes);
});

const dislikemessage = asyncHandler(async (req, res) => {
    const { chatId, messageId } = req.params;
    const token = req.headers.authorization;

    if (!token || !(await verifyAuthentication(token))) {
        return res.status(401).json({ message: "Usuario no autenticado" });
    }

    const message = await Message.findOne({ _id: messageId, chatId });
    if (!message) {
        return res.status(404).json({ message: "Mensaje no encontrado" });
    }

    const userId = req.body.userId;

    const likeIndex = message.likes.findIndex((like) => like.userId === userId);

    if (likeIndex !== -1) {
        message.likes.splice(likeIndex, 1);
        message.likesCount = message.likes.length; 
    } else {
        return res.status(400).json({ message: "No se ha dado like a este mensaje" });
    }

    await message.save();

    if (!(await updateChatLastMessage(chatId))) {
        return res.status(404).json({ message: "El chat no existe o no se pudo actualizar el último mensaje" });
    }

    res.status(200).json({ message: "Dislike realizado con éxito", data: message });
});

const searchmessage = asyncHandler(async (req, res) => {
    const { chatId } = req.params;
    const { query } = req.query;
    const token = req.headers.authorization;

    if (!token || !(await verifyAuthentication(token))) {
        return res.status(401).json({ message: "Usuario no autenticado" });
    }

    if (!query || query.trim() === "") {
        return res.status(400).json({ message: "Parámetro de búsqueda inválido" });
    }

    try {
        const messages = await Message.find({
            chatId,
            content: { $regex: query, $options: "i" }
        });

        res.status(200).json(messages);
    } catch (error) {
        console.error("Error al buscar mensajes:", error);
        res.status(500).json({ message: "Error al buscar mensajes" });
    }
});


const deleteMultimedia = asyncHandler(async (req, res) => {
    const { chatId, messageId, multimediaId } = req.params;

    const token = req.headers.authorization;

    if (!token || !(await verifyAuthentication(token))) {
        return res.status(401).json({ message: "Usuario no autenticado" });
    }

    const response = await axios.delete(
        `http://localhost:8080/api/multimedia/${chatId}/${messageId}/${multimediaId}`
    );
    console.log("Respuesta de eliminar archivo:", response.data);
    

    const message = await Message.findOneAndUpdate(
        { _id: messageId, chatId },
        { $pull: { multimedia: { multimediaId } } },
        { new: true }
    );

    if (!message) {
        return res.status(404).json({ message: "Mensaje no encontrado" });
    }

    if (!(await updateChatLastMessage(chatId))) {
        return res.status(404).json({ message: "El chat no existe o no se pudo actualizar el último mensaje" });
    }

    res.status(200).json(message);
});


const getMultimedia = asyncHandler(async (req, res) => {
    const { chatId, messageId, multimediaId } = req.params;

    const token = req.headers.authorization;

    if (!token || !(await verifyAuthentication(token))) {
        return res.status(401).json({ message: "Usuario no autenticado" });
    }

    const message = await Message.findOne({ _id: messageId, chatId });

    if (!message) {
        return res.status(404).json({ message: "Mensaje no encontrado" });
    }

    const multimedia = message.multimedia.find((m) => m.multimediaId == multimediaId);

    if (!multimedia) {
        return res.status(404).json({ message: "Multimedia no encontrado" });
    }

    res.status(200).json(multimedia);
});


const getAllMultimedias = asyncHandler(async (req, res) => {
    const { chatId, messageId } = req.params;

    const token = req.headers.authorization;

    if (!token || !(await verifyAuthentication(token))) {
        return res.status(401).json({ message: "Usuario no autenticado" });
    }

    const message = await Message.findOne({ _id: messageId, chatId });

    if (!message) {
        return res.status(404).json({ message: "Mensaje no encontrado" });
    }

    res.status(200).json(message.multimedia);
});

const deleteMessagesByChatId = asyncHandler(async (req, res) => {
    const { chatId } = req.params;

    const token = req.headers.authorization;

    if (!token || !(await verifyAuthentication(token))) {
        return res.status(401).json({ message: "Usuario no autenticado" });
    }

    const messages = await Message.deleteMany({ chatId });

    if (!messages) {
        return res.status(404).json({ message: "Mensajes no encontrados" });
    }

    res.status(200).json({ message: "Mensajes eliminados correctamente" });
});

const latestMessage = asyncHandler(async (req, res) => {
    const { chatId } = req.params;

    const token = req.headers.authorization;

    if (!token || !(await verifyAuthentication(token))) {
        return res.status(401).json({ message: "Usuario no autenticado" });
    }

    const latestMessage = await LatestMessage.findOne({ chatId });

    if (!latestMessage) {
        return res.status(404).json({ message: "Mensaje más reciente no encontrado" });
    }

    res.status(200).json(latestMessage);
});


module.exports = { 
    allMessages, 
    sendMessage, 
    modifymessage,
    uploadFile,
    deletemessage,
    getmessage,
    readmessage,
    unreadmessage,
    likemessage,
    dislikemessage,
    searchmessage,
    deleteMultimedia,
    getLikes,
    getMultimedia,
    getAllMultimedias,
    deleteMessagesByChatId,
    latestMessage


};
