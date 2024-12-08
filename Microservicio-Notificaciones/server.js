require('dotenv').config();
const express = require('express');
const http = require('http');
const socketIo = require('socket.io');
const mongoose = require('mongoose');
const cors = require('cors');
const Notification = require('./notificacion');

const app = express();
const server = http.createServer(app);
const io = socketIo(server, {
    cors: {
        origin: '*',
        methods: ['GET', 'POST', 'PUT','DELETE']
    }
});

app.use(cors());
app.use(express.json());

mongoose.connect(process.env.MONGO_URI)
    .then(() => console.log('Conectado a MongoDB'))
    .catch((error) => console.error('No se pudo conectar a MongoDB:', error));

const onlineUsers = new Map();

io.on('connection', (socket) => {
    console.log('Usuario conectado:', socket.id);

    socket.on('join', async (userId) => {
        onlineUsers.set(userId, socket.id);
        socket.join(userId);
        console.log(`Usuario ${userId} unido a su sala de notificaciones`);

        await sendInitialNotifications(userId);
    });

    socket.on('disconnect', () => {
        onlineUsers.forEach((value, key) => {
            if (value === socket.id) {
                onlineUsers.delete(key);
            }
        });
        console.log('Usuario desconectado:', socket.id);
    });
});

async function sendInitialNotifications(userId) {
    try {
        const unreadCount = await Notification.countDocuments({ receiverId: userId, leido: false });
        const pendingNotifications = await Notification.find({ receiverId: userId, leido: false }).sort({ creacion: -1 });
        const readNotifications = await Notification.find({ receiverId: userId, leido: true }).sort({ creacion: -1 });
        const allNotifications = await Notification.find({ receiverId: userId }).sort({ creacion: -1 });

        io.to(userId).emit('unread_count', unreadCount);
        io.to(userId).emit('unread_notifications', pendingNotifications);
        io.to(userId).emit('read_notifications', readNotifications);
        io.to(userId).emit('all_notifications', allNotifications);
    } catch (error) {
        console.error('Error al enviar notificaciones iniciales:', error);
    }
}

app.post('/notifications', async (req, res) => {
    const { senderId, usernameSender, usernameReceiver, receiverId, message, eventoId, type } = req.body;
    try {
        const notification = new Notification({
            senderId,
            usernameSender,
            usernameReceiver,
            receiverId,
            message,
            eventoId,
            type,
            leido: false
        });
        await notification.save();

        if (onlineUsers.has(receiverId)) {
            io.to(receiverId).emit('notification', notification);
            const unreadCount = await Notification.countDocuments({ receiverId, leido: false });
            io.to(receiverId).emit('unread_count', unreadCount);
            io.to(receiverId).emit('unread_notifications', [notification]);
            io.to(receiverId).emit('all_notifications', [notification]);
        }

        res.status(201).json(notification);
    } catch (error) {
        console.error('Error al crear la notificación:', error);
        res.status(500).json({ error: 'Error al crear la notificación' });
    }
});

app.get('/notifications/:userId/all', async (req, res) => {
    const { userId } = req.params;
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 10;
    const skip = (page - 1) * limit;

    try {
        const allNotifications = await Notification.find({ receiverId: userId })
            .sort({ creacion: -1 })
            .skip(skip)
            .limit(limit);

        const totalNotifications = await Notification.countDocuments({ receiverId: userId });
        const totalPages = Math.ceil(totalNotifications / limit);

        res.json({
            page,
            totalPages,
            totalNotifications,
            notifications: allNotifications
        });
    } catch (error) {
        res.status(500).json({ error: 'Error al obtener todas las notificaciones' });
    }
});

app.get('/notifications/:userId/unread', async (req, res) => {
    const { userId } = req.params;
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 10;
    const skip = (page - 1) * limit;

    try {
        const unreadNotifications = await Notification.find({ receiverId: userId, leido: false })
            .sort({ creacion: -1 })
            .skip(skip)
            .limit(limit);

        const unreadCount = await Notification.countDocuments({ receiverId: userId, leido: false });
        const totalPages = Math.ceil(unreadCount / limit);

        res.json({
            page,
            totalPages,
            unreadCount,
            notifications: unreadNotifications
        });
    } catch (error) {
        res.status(500).json({ error: 'Error al obtener las notificaciones no leídas' });
    }
});

app.get('/notifications/:userId/read', async (req, res) => {
    const { userId } = req.params;
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 10;
    const skip = (page - 1) * limit;

    try {
        const readNotifications = await Notification.find({ receiverId: userId, leido: true })
            .sort({ creacion: -1 })
            .skip(skip)
            .limit(limit);

        const readCount = await Notification.countDocuments({ receiverId: userId, leido: true });
        const totalPages = Math.ceil(readCount / limit);

        res.json({
            page,
            totalPages,
            readCount,
            notifications: readNotifications
        });
    } catch (error) {
        res.status(500).json({ error: 'Error al obtener las notificaciones leídas' });
    }
});

app.put('/notifications/:id/read', async (req, res) => {
    const { id } = req.params;
    try {
        const notification = await Notification.findByIdAndUpdate(id, { leido: true }, { new: true });
        const unreadCount = await Notification.countDocuments({ receiverId: notification.receiverId, leido: false });

        io.to(notification.receiverId).emit('unread_count', unreadCount);
        io.to(notification.receiverId).emit('notification_read', notification);
        io.to(notification.receiverId).emit('read_notifications', [notification]);

        res.json(notification);
    } catch (error) {
        console.error('Error al marcar la notificación como leída:', error);
        res.status(500).json({ error: 'Error al marcar la notificación como leída' });
    }
});

app.put('/notifications/:id/unread', async (req, res) => {
    const { id } = req.params;
    try {
        const notification = await Notification.findByIdAndUpdate(id, { leido: false }, { new: true });
        const unreadCount = await Notification.countDocuments({ receiverId: notification.receiverId, leido: false });

        io.to(notification.receiverId).emit('unread_count', unreadCount);
        io.to(notification.receiverId).emit('notification_unread', notification);
        io.to(notification.receiverId).emit('unread_notifications', [notification]);

        res.json(notification);
    } catch (error) {
        console.error('Error al marcar la notificación como no leída', error);
        res.status(500).json({ error: 'Error al marcar la notificación como no leída' });
    }
});

app.delete('/notifications/:id', async (req, res) => {
    const { id } = req.params;
    try {
        const notification = await Notification.findByIdAndDelete(id);
        if (!notification) {
            return res.status(404).json({ error: 'Notificación no encontrada' });
        }

        const unreadCount = await Notification.countDocuments({ receiverId: notification.receiverId, leido: false });
        io.to(notification.receiverId).emit('unread_count', unreadCount);
        io.to(notification.receiverId).emit('notification_deleted', notification);

        res.json({ message: 'Notificación eliminada' });
    } catch (error) {
        res.status(500).json({ error: 'Error al eliminar la notificación' });
    }
});

app.get('/notifications/:userId/unread-count', async (req, res) => {
    const { userId } = req.params;
    try {
        const unreadCount = await Notification.countDocuments({ receiverId: userId, leido: false });
        res.json({ unreadCount });
    } catch (error) {
        res.status(500).json({ error: 'Error al obtener el conteo de notificaciones no leídas' });
    }
});

const PORT = process.env.PORTN || 3000;
server.listen(PORT, () => {
    console.log(`Servidor corriendo en el puerto ${PORT}`);
});
