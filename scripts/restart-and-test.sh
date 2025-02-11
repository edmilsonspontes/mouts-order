#!/bin/bash

echo "Reiniciando containers do projeto..."
docker-compose restart

echo "Aguardando 10 segundos para estabilização..."
sleep 10

echo "Verificando status dos serviços..."
docker ps

# Teste de conexão com MongoDB
if docker exec -it mongodb mongo --eval "db.runCommand({ ping: 1 })" > /dev/null 2>&1; then
    echo "MongoDB está respondendo corretamente."
else
    echo "ERRO: MongoDB não está respondendo."
fi

# Teste de conexão com Redis
if docker exec -it redis redis-cli ping | grep -q "PONG"; then
    echo "Redis está respondendo corretamente."
else
    echo "ERRO: Redis não está respondendo."
fi
