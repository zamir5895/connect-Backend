#!/bin/bash

# Lista de rutas con archivos docker-compose.yml
services=(
    "./Microservicio-Notificaciones"
    "./Microservicio-RecommendedPlaces"
    "./Microservicio-Publications/publicaciones"
    "./Microservicio-Usuario/Usuarios"
    "./Microservicio-Mensajeria"
    "./Microservicio-Places/places"
)

# Recorrer cada servicio y ejecutar docker-compose up --build -d en segundo plano
for service in "${services[@]}"; do
    echo "======== Ejecutando docker-compose en $service ========"
    (
        cd "$service" || { echo "Error: No se pudo acceder a $service"; exit 1; }
        if [ -f docker-compose.yml ]; then
            sudo docker-compose up --build -d &
        else
            echo "No se encontró docker-compose.yml en $service"
        fi
    )
done

# Esperar a que todos los procesos secundarios terminen
wait

# Mostrar contenedores en ejecución
echo "======== Contenedores en ejecución ========"
sudo docker ps

# Mostrar todas las imágenes disponibles
echo "======== Imágenes de Docker disponibles ========"
sudo docker images

echo "Todos los servicios han sido iniciados y listados."
