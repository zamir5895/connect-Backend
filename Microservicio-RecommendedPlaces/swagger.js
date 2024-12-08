const swaggerJsdoc = require('swagger-jsdoc');
const swaggerUi = require('swagger-ui-express');

const swaggerOptions = {
    swaggerDefinition: {
        openapi: '3.0.0',
        info: {
            title: 'Lugares recomendados API',
            version: '1.0.0',
            description: 'API para manejar los lugares recomendados por los viajeros',
            contact: {
                name: 'Rodrigo',
                email: 'rodrigo@utec.edu.pe'
            }
        },
        servers: [
            {
                url: 'http://localhost:4000',
                description: 'Servidor de desarrollo'
            }
        ]
    },
    apis: ['./src/**/*.js'],  
};

const swaggerDocs = swaggerJsdoc(swaggerOptions);

module.exports = {
    swaggerUi,
    swaggerDocs
};