const mongoose = require("mongoose");

const TagSchema = new mongoose.Schema({
  pinId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "Pin",
    required: true,
  },

  nombre: {
    type: String,
    required: true,
    enum: ["Fiesta", "Tranquilidad", "Comida", "Deportes", "Cultura", "Naturaleza"], 
  },
  rating: {
    type: Number,
    default: 0,
    min: 0,
    max: 5,
  },
  ratingTotal: { 
    type: Number,
    default: 0,
  },
  ratingCount: {  
    type: Number,
    default: 0,
  }
});

module.exports = mongoose.model("Tag", TagSchema);
