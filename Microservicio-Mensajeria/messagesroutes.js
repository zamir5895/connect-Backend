const express = require("express");
const {
  allMessages,
  sendMessage,
  uploadFile,
  modifymessage,
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
} = require("./messageControllers");

const multer = require("multer");
const upload = multer({ storage: multer.memoryStorage() }); 

const router = express.Router();

router.route("/:chatId").get(allMessages);
router.route("/create").post(sendMessage);
router.post('/:chatId/:messageId/upload', upload.single('file'), uploadFile);
router.route("/:chatId/modificar").put(modifymessage);
router.route("/:chatId/eliminar/:mensajeId").delete(deletemessage);
router.route("/:chatId/:mensajeId").get(getmessage);
router.route("/:chatId/:mensajeId/read").put(readmessage);
router.route("/:chatId/:mensajeId/unread").put(unreadmessage);
router.route("/:chatId/:mensajeId/like").put(likemessage);
router.route("/:chatId/:mensajeId/like").get(getLikes);
router.route("/:chatId/:mensajeId/dislike").put(dislikemessage);
router.route("/:chatId/search").get(searchmessage);
router.route("/:chatId/:mensajeId/:multimediaId").delete(deleteMultimedia);
router.route("/:chatId/:mensajeId/:multimediaId").get(getMultimedia);
router.route("/:chatId/:mensajeId/multimedias").put(getAllMultimedias);
router.route("/:chatId/allMensajes").delete(deleteMessagesByChatId);
router.route("/:chatId/latestMessage").get(latestMessage);

module.exports = router;