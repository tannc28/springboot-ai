# 🚀 Spring Boot Product Management API - Enterprise Edition

A comprehensive, production-ready Spring Boot application demonstrating enterprise-grade development practices, security, monitoring, and scalability patterns.

## 🎯 Project Overview

This project showcases how to build a **real-world, enterprise-grade backend application** using modern Java and Spring Boot technologies. It's designed to help developers understand:

- **Enterprise Architecture Patterns**
- **Security Best Practices**
- **Monitoring & Observability**
- **Database Design & Migrations**
- **Testing Strategies**
- **DevOps & Deployment**

## ✨ Enterprise Features

### 🔐 **Security & Authentication**
- **JWT-based Authentication** with access/refresh tokens
- **Role-based Authorization** (USER, ADMIN roles)
- **Password Encryption** using BCrypt
- **CORS Configuration** for cross-origin requests
- **Stateless Session Management**
- **Security Headers** and CSRF protection

### 📊 **Data Management & Persistence**
- **PostgreSQL Database** with JPA/Hibernate
- **Redis Caching** for improved performance
- **Database Migrations** using Flyway
- **Soft Delete** functionality
- **Audit Fields** (created_at, updated_at)
- **Connection Pooling** with HikariCP
- **Database Indexing** for performance

### 🔍 **API Design & Documentation**
- **RESTful API Design** with proper HTTP status codes
- **Input Validation** using Bean Validation
- **Global Exception Handling** with standardized error responses
- **API Versioning** (v1)
- **Pagination** support for list endpoints
- **OpenAPI 3.0 Documentation** (Swagger UI)
- **Response Standardization** with ApiResponse wrapper

### 📈 **Monitoring & Observability**
- **Health Checks** for database and Redis
- **Custom Metrics** using Micrometer
- **Prometheus Integration** for metrics collection
- **Distributed Tracing** with Zipkin
- **Structured Logging** with Logback
- **ELK Stack Integration** (Elasticsearch, Logstash, Kibana)
- **Application Performance Monitoring**

### 🧪 **Testing & Quality Assurance**
- **Unit Tests** with JUnit 5 and Mockito
- **Integration Tests** with Spring Boot Test
- **Security Tests** with Spring Security Test
- **Test Coverage** for all layers
- **Test Containers** for database testing
- **Performance Testing** capabilities

### 🐳 **DevOps & Deployment**
- **Docker Containerization** with multi-stage builds
- **Docker Compose** for local development
- **Environment-specific Configurations**
- **Health Check Endpoints**
- **Graceful Shutdown** handling
- **Resource Management**

## 🛠️ Technology Stack

### **Core Framework**
- **Java 21** - Latest LTS version with modern features
- **Spring Boot 3.5.0** - Rapid application development
- **Spring Security 6** - Comprehensive security framework
- **Spring Data JPA** - Data access layer
- **Spring Web** - RESTful web services

### **Database & Caching**
- **PostgreSQL 15** - Robust relational database
- **Redis 7** - In-memory data structure store
- **HikariCP** - High-performance connection pool
- **Flyway** - Database migration tool

### **Security**
- **JWT (JSON Web Tokens)** - Stateless authentication
- **BCrypt** - Password hashing
- **Spring Security** - Authentication & authorization
- **CORS** - Cross-origin resource sharing

### **Monitoring & Observability**
- **Micrometer** - Application metrics
- **Prometheus** - Metrics collection
- **Zipkin** - Distributed tracing
- **ELK Stack** - Log aggregation and analysis
- **Actuator** - Application monitoring

### **Testing**
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework
- **Spring Boot Test** - Integration testing
- **TestContainers** - Database testing

### **Documentation & API**
- **OpenAPI 3.0** - API specification
- **Swagger UI** - Interactive API documentation
- **SpringDoc** - OpenAPI integration

### **DevOps**
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **Maven** - Build automation
- **Git** - Version control

## 🚀 Quick Start

### Prerequisites
- **Java 21** (OpenJDK or Oracle JDK)
- **Docker & Docker Compose** (latest version)
- **Maven 3.8+**
- **Git**

### 1. Clone the Repository
```bash
git clone <repository-url>
cd springboot-ai
```

### 2. Start Infrastructure Services
```bash
# Start all required services (PostgreSQL, Redis, ELK Stack)
docker-compose up -d postgres redis elasticsearch logstash kibana redisinsight
```

**Services started:**
- **PostgreSQL**: `localhost:5432` (Database)
- **Redis**: `localhost:6379` (Cache)
- **RedisInsight**: `localhost:8001` (Redis GUI)
- **Elasticsearch**: `localhost:9200` (Search engine)
- **Logstash**: `localhost:5000` (Log processing)
- **Kibana**: `localhost:5601` (Log visualization)

### 3. Run the Application
```bash
# Build and run the application
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Verify Installation

#### Check Application Health
```bash
curl http://localhost:8080/actuator/health
```

#### Access API Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

#### Check Monitoring Tools
- **Kibana**: http://localhost:5601
- **RedisInsight**: http://localhost:8001

## 🔐 Authentication & Authorization

### Default Users
The application comes with pre-configured users:

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| `admin` | `admin123` | ADMIN | System administrator |
| `user` | `user123` | USER | Regular user |

### Register a New User
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "password123",
    "fullName": "New User"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user",
    "password": "user123"
  }'
```

**Response:**
```json
{
  "status": "success",
  "message": "User authenticated successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userInfo": {
      "id": 2,
      "username": "user",
      "email": "user@example.com",
      "fullName": "Regular User",
      "role": "USER"
    }
  }
}
```

### Use JWT Token
```bash
# Store token in variable
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

# Use token for authenticated requests
curl -X GET http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer $TOKEN"
```

## 📋 API Endpoints

### Authentication Endpoints
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/api/v1/auth/register` | Register new user | No |
| `POST` | `/api/v1/auth/login` | Login user | No |

### Product Management Endpoints
| Method | Endpoint | Description | Auth Required | Role |
|--------|----------|-------------|---------------|------|
| `GET` | `/api/v1/products` | Get all products | Yes | USER/ADMIN |
| `GET` | `/api/v1/products/paginated` | Get paginated products | Yes | USER/ADMIN |
| `GET` | `/api/v1/products/{id}` | Get product by ID | Yes | USER/ADMIN |
| `POST` | `/api/v1/products` | Create new product | Yes | USER/ADMIN |
| `PUT` | `/api/v1/products/{id}` | Update product | Yes | USER/ADMIN |
| `DELETE` | `/api/v1/products/{id}` | Delete product | Yes | USER/ADMIN |

### Example API Usage

#### Get All Products
```bash
curl -X GET http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer $TOKEN"
```

#### Get Paginated Products
```bash
curl -X GET "http://localhost:8080/api/v1/products/paginated?page=0&size=10&sortBy=name&sortDir=ASC" \
  -H "Authorization: Bearer $TOKEN"
```

#### Create New Product
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro M3",
    "description": "Latest MacBook with M3 chip",
    "price": 1999.99
  }'
```

#### Update Product
```bash
curl -X PUT http://localhost:8080/api/v1/products/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro M3 Max",
    "description": "Updated description",
    "price": 2499.99
  }'
```

#### Delete Product
```bash
curl -X DELETE http://localhost:8080/api/v1/products/1 \
  -H "Authorization: Bearer $TOKEN"
```

## 🧪 Testing

### Run All Tests
```bash
mvn clean test
```

### Run Unit Tests Only
```bash
mvn test -Dtest=*Test
```

### Run Integration Tests Only
```bash
mvn test -Dtest=*IntegrationTest
```

### Run Tests with Coverage
```bash
mvn clean test jacoco:report
```

### Test Coverage Report
After running tests with coverage, view the report at:
```
target/site/jacoco/index.html
```

## 📊 Monitoring & Observability

### Health Checks
```bash
# Overall health
curl http://localhost:8080/actuator/health

# Detailed health
curl http://localhost:8080/actuator/health -H "Authorization: Bearer $TOKEN"
```

### Metrics
```bash
# All metrics
curl http://localhost:8080/actuator/metrics

# Specific metric
curl http://localhost:8080/actuator/metrics/product.created

# Prometheus format
curl http://localhost:8080/actuator/prometheus
```

### Application Info
```bash
# Application information
curl http://localhost:8080/actuator/info

# Environment details
curl http://localhost:8080/actuator/env
```

### Logging
```bash
# View application logs
tail -f logs/application.log

# View Docker container logs
docker-compose logs -f app
```

## 🔧 Configuration

### Environment Variables
```bash
# Database Configuration
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/productdb
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres

# Redis Configuration
export SPRING_DATA_REDIS_HOST=localhost
export SPRING_DATA_REDIS_PORT=6379

# JWT Configuration
export JWT_SECRET=your-secret-key-here
export JWT_EXPIRATION=86400000
export JWT_REFRESH_TOKEN_EXPIRATION=604800000

# Application Configuration
export SPRING_PROFILES_ACTIVE=development
export SERVER_PORT=8080
```

### Application Properties
Key configurations in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/productdb
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: validate  # Use 'update' for development
    show-sql: false       # Set to true for debugging
  data:
    redis:
      host: localhost
      port: 6379

jwt:
  secret: your-secret-key
  expiration: 86400000      # 24 hours
  refresh-token:
    expiration: 604800000   # 7 days

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized

logging:
  level:
    com.example.springboot: INFO
    org.springframework.security: DEBUG
```

## 🐳 Docker

### Build Application Image
```bash
docker build -t springboot-product-api .
```

### Run Application Container
```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/productdb \
  -e SPRING_DATA_REDIS_HOST=host.docker.internal \
  springboot-product-api
```

### Docker Compose Commands
```bash
# Start all services
docker-compose up -d

# Start specific services
docker-compose up -d postgres redis

# View logs
docker-compose logs -f app

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Rebuild and restart
docker-compose up -d --build
```

## 📁 Project Structure

```
springboot-ai/
├── src/
│   ├── main/
│   │   ├── java/com/example/springboot/
│   │   │   ├── config/                    # Configuration classes
│   │   │   │   ├── OpenApiConfig.java     # OpenAPI configuration
│   │   │   │   ├── TracingConfig.java     # Distributed tracing
│   │   │   │   └── HealthCheckConfig.java # Health checks
│   │   │   ├── controller/                # REST controllers
│   │   │   │   ├── ProductController.java # Product management
│   │   │   │   └── AuthController.java    # Authentication
│   │   │   ├── dto/                       # Data Transfer Objects
│   │   │   │   ├── ProductRequest.java    # Product input DTO
│   │   │   │   ├── ProductResponse.java   # Product output DTO
│   │   │   │   ├── ApiResponse.java       # Standard response wrapper
│   │   │   │   ├── AuthRequest.java       # Authentication input
│   │   │   │   ├── AuthResponse.java      # Authentication output
│   │   │   │   └── RegisterRequest.java   # Registration input
│   │   │   ├── entity/                    # JPA entities
│   │   │   │   ├── Product.java           # Product entity
│   │   │   │   └── User.java              # User entity
│   │   │   ├── exception/                 # Custom exceptions
│   │   │   │   ├── GlobalExceptionHandler.java # Global error handling
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── ValidationException.java
│   │   │   │   └── ErrorResponse.java     # Error response DTO
│   │   │   ├── repository/                # Data repositories
│   │   │   │   ├── ProductRepository.java # Product data access
│   │   │   │   └── UserRepository.java    # User data access
│   │   │   ├── security/                  # Security configuration
│   │   │   │   ├── SecurityConfig.java    # Security configuration
│   │   │   │   ├── ApplicationConfig.java # Application beans
│   │   │   │   ├── JwtService.java        # JWT utilities
│   │   │   │   └── JwtAuthenticationFilter.java # JWT filter
│   │   │   └── service/                   # Business logic
│   │   │       ├── ProductService.java    # Product business logic
│   │   │       ├── AuthService.java       # Authentication logic
│   │   │       └── MetricsService.java    # Custom metrics
│   │   └── resources/
│   │       ├── db/migration/              # Database migrations
│   │       │   ├── V1__Create_products_table.sql
│   │       │   ├── V2__Create_users_table.sql
│   │       │   └── V3__Insert_sample_data.sql
│   │       ├── application.yml            # Application configuration
│   │       └── logback-spring.xml         # Logging configuration
│   └── test/
│       └── java/com/example/springboot/
│           ├── controller/                # Integration tests
│           │   └── ProductControllerIntegrationTest.java
│           └── service/                   # Unit tests
│               └── ProductServiceTest.java
├── docker-compose.yml                     # Multi-container setup
├── Dockerfile                             # Application container
├── pom.xml                                # Maven dependencies
└── README.md                              # Project documentation
```

## 🔒 Security Features

### JWT Authentication
- **Access Tokens**: 24-hour validity
- **Refresh Tokens**: 7-day validity
- **Stateless Authentication**: No server-side session storage
- **Token Validation**: Automatic validation on each request

### Authorization
- **Role-based Access Control**: USER and ADMIN roles
- **Method-level Security**: @PreAuthorize annotations
- **URL-based Security**: Request matchers in SecurityConfig

### Password Security
- **BCrypt Hashing**: Industry-standard password hashing
- **Password Validation**: Minimum length and complexity rules
- **Account Protection**: Account lockout mechanisms

### CORS Configuration
- **Cross-origin Support**: Configured for web applications
- **Security Headers**: CSRF protection and security headers
- **Flexible Configuration**: Environment-specific CORS settings

## 📈 Performance Features

### Caching Strategy
- **Redis Caching**: Distributed caching for products
- **Cache Eviction**: Automatic cache invalidation
- **Cache Warming**: Pre-loading frequently accessed data

### Database Optimization
- **Connection Pooling**: HikariCP for efficient connections
- **Query Optimization**: Indexed queries and optimized JPA
- **Soft Delete**: Logical deletion for data integrity

### Monitoring & Metrics
- **Response Time Tracking**: Custom timers for operations
- **Error Rate Monitoring**: Exception tracking and alerting
- **Resource Utilization**: Memory, CPU, and database metrics

## 🚀 Deployment

### Development Environment
```bash
# Local development with hot reload
mvn spring-boot:run

# With specific profile
mvn spring-boot:run -Dspring.profiles.active=development
```

### Production Considerations

#### 1. Environment Variables
```bash
# Use proper secrets management
export JWT_SECRET=$(openssl rand -base64 32)
export SPRING_DATASOURCE_PASSWORD=$(openssl rand -base64 32)
```

#### 2. Database Configuration
- Use managed PostgreSQL service (AWS RDS, Google Cloud SQL)
- Configure connection pooling for production load
- Set up automated backups and monitoring

#### 3. Redis Configuration
- Use managed Redis service (AWS ElastiCache, Google Cloud Memorystore)
- Configure Redis clustering for high availability
- Set up Redis monitoring and alerting

#### 4. Monitoring Setup
- Deploy Prometheus and Grafana for metrics
- Configure ELK stack for centralized logging
- Set up alerting for critical metrics

#### 5. Security Hardening
- Enable HTTPS with proper SSL certificates
- Configure rate limiting (Spring Cloud Gateway)
- Set up API gateway for additional security
- Implement proper secrets management

### Kubernetes Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: product-api
  template:
    metadata:
      labels:
        app: product-api
    spec:
      containers:
      - name: product-api
        image: springboot-product-api:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: url
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
```

## 🤝 Contributing

### Development Workflow
1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Make your changes** following coding standards
4. **Add tests** for new functionality
5. **Run all tests**: `mvn clean test`
6. **Commit your changes**: `git commit -m 'Add amazing feature'`
7. **Push to the branch**: `git push origin feature/amazing-feature`
8. **Create a Pull Request**

### Coding Standards
- Follow Java naming conventions
- Use meaningful variable and method names
- Add comprehensive JavaDoc comments
- Ensure test coverage > 80%
- Follow REST API best practices

### Testing Requirements
- Unit tests for all service methods
- Integration tests for all controllers
- Security tests for authentication/authorization
- Performance tests for critical paths

## 🆘 Troubleshooting

### Common Issues

#### 1. Database Connection Issues
```bash
# Check if PostgreSQL is running
docker-compose ps postgres

# Check database logs
docker-compose logs postgres

# Restart database
docker-compose restart postgres
```

#### 2. Redis Connection Issues
```bash
# Check if Redis is running
docker-compose ps redis

# Test Redis connection
docker exec -it springboot-ai-redis-1 redis-cli ping

# Restart Redis
docker-compose restart redis
```

#### 3. Application Startup Issues
```bash
# Check application logs
docker-compose logs -f app

# Check health endpoint
curl http://localhost:8080/actuator/health

# Restart application
docker-compose restart app
```

#### 4. JWT Token Issues
```bash
# Check JWT configuration
curl http://localhost:8080/actuator/env | grep jwt

# Verify token format
echo "YOUR_TOKEN" | cut -d'.' -f2 | base64 -d | jq
```

### Performance Issues

#### 1. Slow Database Queries
- Check database indexes
- Review JPA query optimization
- Monitor connection pool usage

#### 2. High Memory Usage
- Check for memory leaks
- Review caching strategy
- Monitor garbage collection

#### 3. Slow API Responses
- Check Redis cache hit rate
- Review database query performance
- Monitor external service calls

## 📚 Learning Resources

### Spring Boot
- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Boot Guides](https://spring.io/guides)
- [Spring Boot Best Practices](https://github.com/spring-projects/spring-boot/wiki/Best-Practices)

### Security
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [JWT Best Practices](https://auth0.com/blog/a-look-at-the-latest-draft-for-jwt-bcp/)
- [OWASP Security Guidelines](https://owasp.org/www-project-api-security/)

### Testing
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

### Monitoring
- [Micrometer Documentation](https://micrometer.io/docs)
- [Prometheus Best Practices](https://prometheus.io/docs/practices/)
- [ELK Stack Guide](https://www.elastic.co/guide/index.html)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

### Getting Help
- **Documentation**: Check this README and API documentation
- **Issues**: Create an issue in the repository
- **Discussions**: Use GitHub Discussions for questions
- **Community**: Join Spring Boot community forums

### Reporting Bugs
When reporting bugs, please include:
- **Environment**: OS, Java version, Docker version
- **Steps to reproduce**: Detailed steps to reproduce the issue
- **Expected behavior**: What you expected to happen
- **Actual behavior**: What actually happened
- **Logs**: Relevant application logs
- **Screenshots**: If applicable

---

**Built with ❤️ using Spring Boot and modern Java technologies**

**Perfect for learning enterprise development practices and building production-ready applications!** 