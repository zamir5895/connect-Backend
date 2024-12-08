const mongoose = require('mongoose');

const latestMessageSchema = mongoose.Schema({
  chatId: { type: Number, required: true },
  latestMessage: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "Message",
  },
});

const LatestMessage = mongoose.model("LatestMessage", latestMessageSchema);
module.exports = LatestMessage;   