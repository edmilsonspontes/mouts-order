# **Order API - Gestão de Pedidos 📦🚀**

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.8-brightgreen) ![Java](https://img.shields.io/badge/Java-17-blue) ![Docker](https://img.shields.io/badge/Docker-Compose-informational)

## **📌 Visão Geral**
Este projeto é uma **API de Gestão de Pedidos** construída com **Spring Boot 3.3.8**, **Java 17**, **MongoDB**, **Redis** e **RabbitMQ**.  
Ele processa pedidos recebidos via **fila do RabbitMQ**, armazena os dados no **MongoDB**, e utiliza **Redis para otimização de consultas**.

---

## **📌 Ambientes**

### **🔹 Desenvolvimento**
📌 Para rodar localmente, utilize **Docker Compose**.

### **🔹 Teste**
📌 Testes automatizados utilizando **JUnit e Testcontainers** para MongoDB, Redis e RabbitMQ.

### **🔹 Produção**
📌 Aplicação pronta para rodar em **Docker**.

---

## **📌 Tecnologias Utilizadas**
✅ **Spring Boot 3.3.8** - Framework principal da aplicação  
✅ **Java 17** - Versão do Java utilizada  
✅ **MongoDB** - Banco de dados NoSQL para armazenamento de pedidos  
✅ **Redis** - Cache para otimizar o acesso aos pedidos  
✅ **RabbitMQ** - Fila de mensagens para comunicação assíncrona  
✅ **Docker e Docker Compose** - Gerenciamento de containers  

---

## **📌 Como Rodar o Projeto**
### **1️⃣ Configurar Variáveis de Ambiente**
Antes de rodar a aplicação, execute o script para configurar as variáveis de ambiente:
```bash
source set-env-vars.sh
