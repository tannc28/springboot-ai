# Modern Backend Technologies Integration Guide

## 🚀 Overview

This Spring Boot project has been enhanced with the latest modern backend technologies to create a robust, scalable, and enterprise-ready application.

## 📋 Technologies Added

### 1. **Apache Kafka** - Event Streaming Platform
- **Port**: 9092 (Kafka), 2181 (Zookeeper), 8081 (Kafka UI)
- **Purpose**: Real-time event streaming, message queuing, data pipeline
- **Features**:
  - Event-driven architecture
  - Stream processing
  - Fault-tolerant messaging
  - Horizontal scalability

### 2. **RabbitMQ** - Message Broker
- **Port**: 5672 (AMQP), 15672 (Management UI)
- **Purpose**: Reliable message queuing, pub/sub patterns
- **Features**:
  - Multiple exchange types (Direct, Topic, Fanout)
  - Dead letter queues
  - Message acknowledgments
  - Routing patterns

### 3. **MongoDB** - NoSQL Database
- **Port**: 27017 (MongoDB), 8082 (MongoDB Express)
- **Purpose**: Document storage, flexible schema
- **Features**:
  - JSON-like documents
  - Horizontal scaling
  - Rich queries
  - Aggregation framework

### 4. **Apache Cassandra** - Distributed Database
- **Port**: 9042 (CQL), 7000-7001 (Inter-node), 9160 (Thrift)
- **Purpose**: High availability, linear scalability
- **Features**:
  - Multi-datacenter support
  - Linear scalability
  - Fault tolerance
  - Time-series data

### 5. **Neo4j** - Graph Database
- **Port**: 7474 (HTTP), 7687 (Bolt)
- **Purpose**: Graph data modeling, relationship queries
- **Features**:
  - Graph algorithms
  - Cypher query language
  - ACID transactions
  - Graph visualization

### 6. **Prometheus & Grafana** - Monitoring Stack
- **Port**: 9090 (Prometheus), 3000 (Grafana)
- **Purpose**: Metrics collection, visualization, alerting
- **Features**:
  - Time-series metrics
  - Custom dashboards
  - Alerting rules
  - Service discovery

### 7. **Jaeger** - Distributed Tracing
- **Port**: 16686 (UI), 14268 (HTTP), 14250 (gRPC)
- **Purpose**: Request tracing, performance monitoring
- **Features**:
  - Distributed tracing
  - Performance analysis
  - Error tracking
  - Service dependencies

### 8. **Consul** - Service Discovery & Configuration
- **Port**: 8500 (HTTP), 8600 (DNS)
- **Purpose**: Service discovery, health checking, configuration
- **Features**:
  - Service registration
  - Health checks
  - Key-value store
  - DNS interface

### 9. **HashiCorp Vault** - Secret Management
- **Port**: 8200 (HTTP)
- **Purpose**: Secure secret storage, dynamic credentials
- **Features**:
  - Secret encryption
  - Dynamic credentials
  - Access control
  - Audit logging

### 10. **Nginx** - Load Balancer & Reverse Proxy
- **Port**: 80 (HTTP), 443 (HTTPS)
- **Purpose**: Load balancing, SSL termination, rate limiting
- **Features**:
  - Load balancing
  - Rate limiting
  - SSL termination
  - Caching

## 🛠️ Configuration

### Application Properties

Add these configurations to `application.yml`:

```yaml
# Kafka Configuration
spring:
  kafka:
    bootstrap-servers: kafka:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: springboot-app
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"

  # RabbitMQ Configuration
  rabbitmq:
    host: rabbitmq
    port: 5672
    username: guest
    password: guest
    virtual-host: /

  # MongoDB Configuration
  data:
    mongodb:
      uri: mongodb://mongodb:27017/productdb
      database: productdb

  # Cassandra Configuration
  data:
    cassandra:
      contact-points: cassandra:9042
      keyspace-name: productdb
      local-datacenter: datacenter1
      port: 9042

  # Neo4j Configuration
  data:
    neo4j:
      uri: bolt://neo4j:7687
      authentication:
        username: neo4j
        password: password

  # Consul Configuration
  cloud:
    consul:
      host: consul
      port: 8500
      discovery:
        enabled: true
        service-name: springboot-app
      config:
        enabled: true

  # Vault Configuration
  cloud:
    vault:
      host: vault
      port: 8200
      scheme: http
      authentication: TOKEN
      token: dev-token

# Actuator Configuration
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true

# Sleuth Configuration
spring:
  sleuth:
    sampler:
      probability: 1.0
```

## 🚀 Getting Started

### 1. Start All Services

```bash
docker-compose up -d
```

### 2. Access Services

| Service | URL | Username | Password |
|---------|-----|----------|----------|
| Application | http://localhost:8080 | - | - |
| Kafka UI | http://localhost:8081 | - | - |
| RabbitMQ UI | http://localhost:15672 | guest | guest |
| MongoDB Express | http://localhost:8082 | admin | password |
| Neo4j Browser | http://localhost:7474 | neo4j | password |
| Consul UI | http://localhost:8500 | - | - |
| Vault UI | http://localhost:8200 | - | dev-token |
| Prometheus | http://localhost:9090 | - | - |
| Grafana | http://localhost:3000 | admin | admin |
| Jaeger UI | http://localhost:16686 | - | - |
| Kibana | http://localhost:5601 | - | - |
| Redis Insight | http://localhost:8001 | - | - |
| Adminer | http://localhost:8083 | - | - |

### 3. Health Checks

```bash
# Application health
curl http://localhost:8080/actuator/health

# All services health
curl http://localhost:8080/actuator/health/readiness
```

## 📊 Monitoring & Observability

### Prometheus Metrics

The application exposes metrics at `/actuator/prometheus`:

- JVM metrics
- HTTP request metrics
- Database connection metrics
- Custom business metrics

### Grafana Dashboards

Pre-configured dashboards for:
- Application performance
- Database metrics
- Kafka/RabbitMQ metrics
- Infrastructure metrics

### Jaeger Tracing

Distributed tracing for:
- HTTP requests
- Database queries
- Message processing
- External service calls

## 🔐 Security

### Vault Integration

Store sensitive data in Vault:
- Database credentials
- API keys
- JWT secrets
- SSL certificates

### Consul Security

- Service authentication
- Access control lists
- Encryption in transit
- Audit logging

## 📈 Scalability Features

### Horizontal Scaling

- Stateless application design
- Database sharding support
- Message queue clustering
- Load balancer configuration

### Performance Optimization

- Connection pooling
- Caching strategies
- Async processing
- Batch operations

## 🔧 Development Tools

### Testing

```bash
# Unit tests
mvn test

# Integration tests
mvn test -Dtest=*IntegrationTest

# End-to-end tests
mvn test -Dtest=*E2ETest
```

### Database Migrations

```bash
# Flyway migrations
mvn flyway:migrate

# MongoDB scripts
docker exec -it mongodb mongo productdb /docker-entrypoint-initdb.d/init.js

# Cassandra scripts
docker exec -it cassandra cqlsh -f /docker-entrypoint-initdb.d/init.cql
```

## 🚨 Troubleshooting

### Common Issues

1. **Service not starting**: Check Docker logs
   ```bash
   docker-compose logs [service-name]
   ```

2. **Connection refused**: Verify service dependencies
   ```bash
   docker-compose ps
   ```

3. **Memory issues**: Adjust JVM heap size
   ```bash
   export JAVA_OPTS="-Xmx2g -Xms1g"
   ```

### Logs

- Application logs: `./logs/application.log`
- Docker logs: `docker-compose logs`
- Service-specific logs: Check individual service containers

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)
- [MongoDB Documentation](https://docs.mongodb.com/)
- [Cassandra Documentation](https://cassandra.apache.org/doc/)
- [Neo4j Documentation](https://neo4j.com/docs/)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Jaeger Documentation](https://www.jaegertracing.io/docs/)
- [Consul Documentation](https://www.consul.io/docs)
- [Vault Documentation](https://www.vaultproject.io/docs)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License. 