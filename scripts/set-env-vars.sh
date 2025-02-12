#!/bin/bash

# RabbitMQ
export RABBITMQ_USER="admin"
export RABBITMQ_PASS="admin"

# Redis
export REDIS_PASSWORD="admin"

# MongoDB
export MONGO_URI="mongodb://mongodb:27017/orderdb"

# API
export SERVER_PORT=8080

echo "Variáveis de ambiente configuradas com sucesso!"
echo "RABBITMQ_USER=$RABBITMQ_USER"
echo "RABBITMQ_PASS=$RABBITMQ_PASS"
echo "REDIS_PASSWORD=$REDIS_PASSWORD"
echo "MONGO_URI=$MONGO_URI"
echo "SERVER_PORT=$SERVER_PORT"
