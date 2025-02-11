#!/bin/bash

echo "Iniciando a aplicação order-api..."

if [ ! -f "target/order-0.0.1-SNAPSHOT.jar" ]; then
    echo "ERRO: Arquivo JAR não encontrado. Certifique-se de que a aplicação foi compilada."
    exit 1
fi

java -jar target/order-0.0.1-SNAPSHOT.jar --server.port=${SERVER_PORT:-8080}
