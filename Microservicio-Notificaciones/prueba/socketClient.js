const io = require('socket.io-client');
const socket = io('http://localhost:3000');

const receiverId = '456'; // Ajusta el ID de usuario según corresponda
socket.emit('join', receiverId);

// Escuchar la notificación específica de cada tipo al conectarse
socket.on('unread_count', (count) => {
    console.log('Conteo actualizado de no leídas:', count);
});

socket.on('unread_notifications', (notifications) => {
    console.log('Notificaciones no leídas:', notifications);
});

socket.on('read_notifications', (notifications) => {
    console.log('Notificaciones leídas:', notifications);
});

socket.on('all_notifications', (notifications) => {
    console.log('Todas las notificaciones:', notifications);
});

socket.on('notification', (notification) => {
    console.log('Notificación recibida:', notification);
});

socket.on('notification_read', (notification) => {
    console.log('Notificación marcada como leída:', notification);
});

socket.on('notification_unread', (notification) => {
    console.log('Notificación marcada como no leída:', notification);
});
