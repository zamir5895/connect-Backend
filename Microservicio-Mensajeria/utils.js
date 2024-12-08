const axios = require('axios');

const verifyAuthentication = async (token) => {
  try {
    const response = await axios.get('http://localhost:8080/api/auth/authentication', {
      headers: { 'Authorization': token }
    });
    return response.status === 200;
  } catch (error) {
    console.log("Error en la autenticación:", error);
    return false;
  }
};

const updateChatLastMessage = async (chatId) => {
  try {
    const response = await axios.patch(`http://localhost:8080/api/chat/update/status/${chatId}`);
    return response.status === 202;
  } catch (error) {
    console.log("Error al actualizar el último mensaje del chat:", error);
    return false;
  }
};

module.exports = { verifyAuthentication, updateChatLastMessage };