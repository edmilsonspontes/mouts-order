#!/bin/bash

PROJECT_NAME="order-api"

echo "Parando e removendo todos os containers e volumes do projeto..."
docker-compose down --volumes --remove-orphans

echo "Verificando se há imagens do projeto para remover..."
IMAGES=$(docker images -q $PROJECT_NAME)
if [ -n "$IMAGES" ]; then
    echo "Removendo imagens antigas do projeto..."
    docker rmi $IMAGES
else
    echo "Nenhuma imagem antiga para remover."
fi

echo "Limpando cache do Docker..."
docker system prune -af --volumes

echo "Reconstruindo os containers e imagens..."
docker-compose up -d --build

echo "Todos os containers foram recriados com sucesso!"
docker ps
