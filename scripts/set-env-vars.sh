#!/bin/bash

# RabbitMQ
export RABBITMQ_USER="admin"
export RABBITMQ_PASS="admin"

# Redis
export REDIS_PASSWORD="admin"

# MongoDB
export MONGO_URI="mongodb://mongodb:27017/orderdb"

# Datadog
export DD_API_KEY="ac0cefe18cd0f5b9668044d0211eb824"
export DD_SITE="us5.datadoghq.com"

# API
export SERVER_PORT=8080

echo "Variáveis de ambiente configuradas com sucesso!"
echo "RABBITMQ_USER=$RABBITMQ_USER"
echo "RABBITMQ_PASS=$RABBITMQ_PASS"
echo "REDIS_PASSWORD=$REDIS_PASSWORD"
echo "MONGO_URI=$MONGO_URI"
echo "DD_API_KEY=$DD_API_KEY"
echo "SERVER_PORT=$SERVER_PORT"