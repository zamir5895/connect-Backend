const express = require('express');
const cors = require('cors');
const mongoose = require('mongoose');
const app = express();

const corsOptions = {
  origin: "*",
  methods: "GET,POST,PUT,DELETE,PATCH",
};
app.use(cors(corsOptions));

app.use(express.json());

const commentsRoutes = require('./src/Comments/routes');
const favoritesRoutes = require('./src/Favorites/routes');
const photosRoutes = require('./src/Photos/routes');
const visitHistoryRoutes = require('./src/VisitHistory/routes');
const tagsRoutes = require('./src/Tags/routes');
const pinsRoutes = require('./src/Pin/routes');

const { swaggerUi, swaggerDocs } = require('./swagger');
app.use('/documentation', swaggerUi.serve, swaggerUi.setup(swaggerDocs));

app.get('/', (req, res) => {
  res.json({ message: 'Bienvenido a mi API' });
});

app.use((req, res, next) => {
  console.log(`${req.method} ${req.url}`);
  next();
});

app.use('/api/comments', commentsRoutes);
app.use('/api/favorites', favoritesRoutes);
app.use('/api/photos', photosRoutes);
app.use('/api/visit-history', visitHistoryRoutes);
app.use('/api/tags', tagsRoutes);
app.use('/api/pins', pinsRoutes);

mongoose.connect("mongodb://34.200.132.185:27017/lugares")
  .then(() => console.log('Conectado a MongoDB'))
  .catch((error) => console.error('No se pudo conectar a MongoDB:', error));

const port = process.env.PORT || 4000;
app.listen(port, () => {
  console.log(`Server running on port ${port}`);
});
