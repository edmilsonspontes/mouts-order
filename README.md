# Order API - Gestão de Pedidos

## 📌 Visão Geral
Este projeto consiste em uma API desenvolvida em **Java 17** utilizando **Spring Boot 3.3.8** para gestão de pedidos. O sistema é capaz de receber, processar e armazenar pedidos, garantindo escalabilidade e integração com tecnologias modernas.

## 📌 Arquitetura do Sistema

Abaixo está a arquitetura do sistema, ilustrando a interação entre os componentes:

![Arquitetura](https://github.com/edmilsonspontes/mouts-order/raw/master/docs/mouts-order-arquitetura-v1.png)

## 📌 Fluxo de funcionamento
1. **Produto Externo A** gera pedidos e publica na fila `orders.generated.queue`.
2. **Order API** consome mensagens dessa fila, processa os pedidos e armazena no banco.
3. **Order API** publica os pedidos processados na fila `orders.processed.queue`.
4. **Produto Externo B** consome os pedidos processados da fila `orders.processed.queue`.
5. **Cliente HTTP** pode consultar pedidos diretamente na API REST.
6. **Redis** é usado para cache e **MongoDB** para persistência.

## 📌 Tecnologias Utilizadas
- **Java 17**
- **Spring Boot 3.3.8**
- **MongoDB** (banco de dados NoSQL)
- **Redis** (cache para otimização de consultas)
- **RabbitMQ** (mensageria para processar pedidos)
- **Docker & Docker Compose** (gerenciamento de containers)
- **Testcontainers** (testes de integração isolados)
- **JUnit & Mockito** (testes unitários e de integração)
- **OpenAPI / Swagger** (documentação da API)
- **Actuator** (monitoramento da aplicação)
- **Docker e Docker Compose** (containerização)

## 📌 Estrutura do Projeto
A arquitetura segue princípios de **Clean Architecture**, **SOLID** e **Hexagonal Architecture**, garantindo modularidade e facilidade de manutenção.

- **`application/`** - Casos de uso e serviços da aplicação
- **`domain/`** - Entidades e regras de negócio
- **`infrastructure/`** - Adaptadores externos (Banco de Dados, RabbitMQ, Redis)
- **`config/`** - Configurações do Spring Boot
- **`tests/`** - Testes unitários e de integração

---

## 📌 Configuração de Variáveis de Ambiente
Antes de rodar o projeto, configure as variáveis de ambiente necessárias. Utilize os scripts na pasta `scripts/` para facilitar essa configuração.

### Exemplo de `.env`
```sh
# MongoDB
MONGO_URI=mongodb://localhost:27017/order-db

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=guest
```

### Configurar variáveis no ambiente Linux/MacOS
```sh
source scripts/set-env-vars.sh
```

---

## 📌 Como Rodar a Aplicação
A aplicação pode ser executada utilizando **Docker Compose** ou diretamente pelo **Maven**.

### 1. Rodar com Docker Compose
```sh
docker-compose up --build
```
A API estará disponível em: [http://localhost:8080](http://localhost:8080)

### 2. Rodar sem Docker
Certifique-se de que o MongoDB, Redis e RabbitMQ estão rodando localmente e execute:
```sh
mvn spring-boot:run
```

---

## 📌 Testes
A aplicação conta com **testes unitários e de integração**.

### Executar todos os testes:
```sh
mvn test
```

### Executar testes de unidade:
```sh
mvn -Dtest=OrderServiceTest test
```

### Executar testes de integração:
```sh
mvn -Dtest=OrderControllerIT test
```

---

## 📌 Monitoramento e Logs
A aplicação expõe endpoints de monitoramento via **Spring Boot Actuator**:

- **Health Check**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- **Métricas**: [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics)

Os logs são configurados com **Logback** e armazenados na pasta `logs/`.

---

## 📌 Documentação da API
A documentação interativa da API pode ser acessada pelo Swagger:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 📌 Contato
Para mais informações sobre o projeto, acesse o repositório no GitHub ou entre em contato:

🔗 **GitHub**: [https://github.com/edmilsonspontes/mouts-order](https://github.com/edmilsonspontes/mouts-order)  
📧 **Email**: [profissional@edmilsonpontes.com](mailto:profissional@edmilsonpontes.com)



