const express = require('express');
const mongoose = require('mongoose');
const http = require('http');
const cors = require('cors');
const socketIo = require('socket.io');

require('dotenv').config();

const app = express();
const server = http.createServer(app);

const corsOptions = {
    origin: "*",
    methods: "GET,POST,PUT,DELETE,PATCH",
};
app.use(cors(corsOptions));  

const PORT = process.env.PORT || 3000;
const MONGODB_URI = process.env.MONGODB_URIM;

mongoose.connect(MONGODB_URI)
  .then(() => console.log('Conectado a MongoDB'))
  .catch(err => console.error('Error al conectar a MongoDB:', err));

app.use(express.json());

app.get('/', (req, res) => {
  res.send('API funcionando');
});

const messagesRoutes = require('./messagesroutes');
app.use('/api/messages', messagesRoutes);

server.listen(PORT, () => {
  console.log(`Servidor corriendo en el puerto ${PORT}`);
});

const io = socketIo(server, {
  pingTimeout: 60000,
  cors: {
    origin: "*",
  },
});
io.on("connection", (socket) => {
  console.log("Usuario conectado al socket");

  socket.on("setup", (userData) => {
    socket.join(userData.userId);
    console.log("Usuario unido a la sala:", userData.userId);
    socket.emit("connected");
  });

  socket.on("join chat", (chatId) => {
    socket.join(chatId);
    console.log("Usuario unido al chat:", chatId);
  });

  socket.on("new message", (newMessage) => {
    console.log("Nuevo mensaje recibido:", newMessage);

    const { chat, users } = newMessage;
    if (!users || !chat) return;

    users.forEach((user) => {
      if (user.userId === newMessage.chat.sender.userId) return; // No enviar al remitente
      io.to(user.userId).emit("message recieved", newMessage);
    });
  });
});
