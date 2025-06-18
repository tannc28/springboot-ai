# 📖 Hướng dẫn sử dụng Spring Boot Product Management API

> **Hướng dẫn chi tiết cách setup, build, và sử dụng ứng dụng Spring Boot enterprise-grade**

---

## 📋 Mục lục

1. [Yêu cầu hệ thống](#1-yêu-cầu-hệ-thống)
2. [Cài đặt môi trường](#2-cài-đặt-môi-trường)
3. [Setup dự án](#3-setup-dự-án)
4. [Build và chạy ứng dụng](#4-build-và-chạy-ứng-dụng)
5. [Sử dụng API](#5-sử-dụng-api)
6. [Monitoring và Debug](#6-monitoring-và-debug)
7. [Troubleshooting](#7-troubleshooting)
8. [Development Workflow](#8-development-workflow)

---

## 1. Yêu cầu hệ thống

### 1.1 Minimum Requirements

| Component | Version | Description |
|-----------|---------|-------------|
| **Java** | 21+ | OpenJDK hoặc Oracle JDK |
| **Maven** | 3.8+ | Build tool |
| **Docker** | 20.10+ | Containerization |
| **Docker Compose** | 2.0+ | Multi-container orchestration |
| **Git** | 2.30+ | Version control |

### 1.2 Recommended Specifications

| Resource | Minimum | Recommended |
|----------|---------|-------------|
| **RAM** | 8GB | 16GB+ |
| **CPU** | 4 cores | 8 cores+ |
| **Storage** | 20GB free | 50GB+ free |
| **Network** | 10 Mbps | 100 Mbps+ |

---

## 2. Cài đặt môi trường

### 2.1 Cài đặt Java 21

**Windows:**
```bash
# Download OpenJDK 21 from https://adoptium.net/
# Hoặc sử dụng Chocolatey
choco install openjdk21

# Verify installation
java -version
javac -version
```

**macOS:**
```bash
# Sử dụng Homebrew
brew install openjdk@21

# Add to PATH
echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Verify installation
java -version
```

**Linux (Ubuntu/Debian):**
```bash
# Update package list
sudo apt update

# Install OpenJDK 21
sudo apt install openjdk-21-jdk

# Verify installation
java -version
```

### 2.2 Cài đặt Maven

**Windows:**
```bash
# Download from https://maven.apache.org/download.cgi
# Hoặc sử dụng Chocolatey
choco install maven

# Verify installation
mvn -version
```

**macOS:**
```bash
# Sử dụng Homebrew
brew install maven

# Verify installation
mvn -version
```

**Linux:**
```bash
# Install Maven
sudo apt install maven

# Verify installation
mvn -version
```

### 2.3 Cài đặt Docker

**Windows:**
```bash
# Download Docker Desktop from https://www.docker.com/products/docker-desktop
# Install và restart computer
# Verify installation
docker --version
docker-compose --version
```

**macOS:**
```bash
# Download Docker Desktop from https://www.docker.com/products/docker-desktop
# Install và start Docker Desktop
# Verify installation
docker --version
docker-compose --version
```

**Linux:**
```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Add user to docker group
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installation
docker --version
docker-compose --version
```

### 2.4 Cài đặt Git

**Windows:**
```bash
# Download from https://git-scm.com/download/win
# Hoặc sử dụng Chocolatey
choco install git

# Verify installation
git --version
```

**macOS:**
```bash
# Sử dụng Homebrew
brew install git

# Verify installation
git --version
```

**Linux:**
```bash
# Install Git
sudo apt install git

# Verify installation
git --version
```

---

## 3. Setup dự án

### 3.1 Clone Repository

```bash
# Clone the repository
git clone <repository-url>
cd springboot-ai

# Verify project structure
ls -la
```

**Expected project structure:**
```
springboot-ai/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

### 3.2 Verify Dependencies

```bash
# Check if all dependencies are available
mvn dependency:tree

# Download dependencies
mvn dependency:resolve
```

### 3.3 Environment Setup

**Create environment file:**
```bash
# Create .env file (optional)
cp .env.example .env

# Edit environment variables
nano .env
```

**Example .env file:**
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/productdb
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# Redis Configuration
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-here-make-it-long-and-secure
JWT_EXPIRATION=86400000
JWT_REFRESH_TOKEN_EXPIRATION=604800000

# Application Configuration
SPRING_PROFILES_ACTIVE=development
SERVER_PORT=8080
```

---

## 4. Build và chạy ứng dụng

### 4.1 Start Infrastructure Services

**Option 1: Using Docker Compose (Recommended)**
```bash
# Start all required services
docker-compose up -d postgres redis elasticsearch logstash kibana redisinsight

# Check if services are running
docker-compose ps

# View logs
docker-compose logs -f
```

**Option 2: Manual Setup**
```bash
# Start PostgreSQL
docker run -d \
  --name postgres \
  -e POSTGRES_DB=productdb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine

# Start Redis
docker run -d \
  --name redis \
  -p 6379:6379 \
  redis:7-alpine

# Verify services
docker ps
```

### 4.2 Build Application

**Clean and Build:**
```bash
# Clean previous builds
mvn clean

# Compile and package
mvn compile

# Run tests
mvn test

# Package application
mvn package -DskipTests

# Verify JAR file
ls -la target/*.jar
```

**Build with Docker:**
```bash
# Build Docker image
docker build -t springboot-product-api .

# Verify image
docker images | grep springboot-product-api
```

### 4.3 Run Application

**Option 1: Using Maven**
```bash
# Run with Maven
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring.profiles.active=development

# Run with custom port
mvn spring-boot:run -Dserver.port=8081
```

**Option 2: Using JAR file**
```bash
# Run JAR file
java -jar target/springboot-0.0.1-SNAPSHOT.jar

# Run with custom configuration
java -jar target/springboot-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=development \
  --server.port=8081
```

**Option 3: Using Docker**
```bash
# Run with Docker
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/productdb \
  -e SPRING_DATA_REDIS_HOST=host.docker.internal \
  springboot-product-api

# Run with Docker Compose
docker-compose up app
```

### 4.4 Verify Application

**Check Application Health:**
```bash
# Health check
curl http://localhost:8080/actuator/health

# Expected response:
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    },
    "redis": {
      "status": "UP"
    }
  }
}
```

**Check Application Info:**
```bash
# Application information
curl http://localhost:8080/actuator/info

# Environment details
curl http://localhost:8080/actuator/env
```

**Access API Documentation:**
```bash
# Open in browser
open http://localhost:8080/swagger-ui.html

# Or get OpenAPI JSON
curl http://localhost:8080/v3/api-docs
```

---

## 5. Sử dụng API

### 5.1 Authentication

**Register a new user:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**Store JWT token:**
```bash
# Extract token from login response
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

# Verify token
echo $TOKEN | cut -d'.' -f2 | base64 -d | jq
```

### 5.2 Product Management

**Get all products:**
```bash
curl -X GET http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer $TOKEN"
```

**Get paginated products:**
```bash
curl -X GET "http://localhost:8080/api/v1/products/paginated?page=0&size=10&sortBy=name&sortDir=ASC" \
  -H "Authorization: Bearer $TOKEN"
```

**Get product by ID:**
```bash
curl -X GET http://localhost:8080/api/v1/products/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Create new product:**
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

**Update product:**
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

**Delete product:**
```bash
curl -X DELETE http://localhost:8080/api/v1/products/1 \
  -H "Authorization: Bearer $TOKEN"
```

### 5.3 Advanced API Usage

**Search products:**
```bash
# Search by name
curl -X GET "http://localhost:8080/api/v1/products/paginated?name=MacBook" \
  -H "Authorization: Bearer $TOKEN"

# Filter by price range
curl -X GET "http://localhost:8080/api/v1/products/paginated?minPrice=1000&maxPrice=3000" \
  -H "Authorization: Bearer $TOKEN"
```

**Bulk operations:**
```bash
# Create multiple products
for i in {1..5}; do
  curl -X POST http://localhost:8080/api/v1/products \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"name\": \"Product $i\",
      \"description\": \"Description for product $i\",
      \"price\": $((100 + $i * 50))
    }"
done
```

### 5.4 Error Handling Examples

**Invalid input:**
```bash
# Try to create product with invalid price
curl -X POST http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "description": "Test Description",
    "price": -100
  }'
```

**Unauthorized access:**
```bash
# Try to access without token
curl -X GET http://localhost:8080/api/v1/products
```

**Resource not found:**
```bash
# Try to get non-existent product
curl -X GET http://localhost:8080/api/v1/products/999999 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 6. Monitoring và Debug

### 6.1 Application Metrics

**View all metrics:**
```bash
curl http://localhost:8080/actuator/metrics
```

**Specific metrics:**
```bash
# Product creation counter
curl http://localhost:8080/actuator/metrics/product.created

# Response time
curl http://localhost:8080/actuator/metrics/http.server.requests

# JVM metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

**Prometheus format:**
```bash
curl http://localhost:8080/actuator/prometheus
```

### 6.2 Logging

**View application logs:**
```bash
# If running with Maven
tail -f logs/application.log

# If running with Docker
docker-compose logs -f app

# If running JAR directly
tail -f nohup.out
```

**Change log level:**
```bash
# Set log level for specific package
curl -X POST http://localhost:8080/actuator/loggers/com.example.springboot \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'
```

### 6.3 Health Checks

**Detailed health check:**
```bash
curl http://localhost:8080/actuator/health \
  -H "Authorization: Bearer $TOKEN"
```

**Individual health indicators:**
```bash
# Database health
curl http://localhost:8080/actuator/health/db

# Redis health
curl http://localhost:8080/actuator/health/redis
```

### 6.4 Monitoring Tools

**Kibana (Log Analysis):**
```bash
# Access Kibana
open http://localhost:5601

# Default credentials: none (development mode)
```

**RedisInsight (Redis GUI):**
```bash
# Access RedisInsight
open http://localhost:8001

# Connect to Redis
# Host: localhost
# Port: 6379
# No password (development)
```

### 6.5 Performance Monitoring

**Monitor response times:**
```bash
# Time API calls
time curl -X GET http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer $TOKEN"
```

**Load testing:**
```bash
# Install Apache Bench (if not available)
# Ubuntu: sudo apt install apache2-utils
# macOS: brew install httpd

# Run load test
ab -n 1000 -c 10 -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/products
```

---

## 7. Troubleshooting

### 7.1 Common Issues

**Application won't start:**
```bash
# Check if port is already in use
netstat -tulpn | grep 8080

# Kill process using port
sudo lsof -ti:8080 | xargs kill -9

# Check Java version
java -version

# Check Maven version
mvn -version
```

**Database connection issues:**
```bash
# Check if PostgreSQL is running
docker-compose ps postgres

# Check database logs
docker-compose logs postgres

# Test database connection
docker exec -it springboot-ai-postgres-1 psql -U postgres -d productdb -c "SELECT 1;"

# Restart database
docker-compose restart postgres
```

**Redis connection issues:**
```bash
# Check if Redis is running
docker-compose ps redis

# Test Redis connection
docker exec -it springboot-ai-redis-1 redis-cli ping

# Check Redis logs
docker-compose logs redis

# Restart Redis
docker-compose restart redis
```

**JWT token issues:**
```bash
# Check JWT configuration
curl http://localhost:8080/actuator/env | grep jwt

# Verify token format
echo "YOUR_TOKEN" | cut -d'.' -f2 | base64 -d | jq

# Check token expiration
echo "YOUR_TOKEN" | cut -d'.' -f2 | base64 -d | jq '.exp'
```

### 7.2 Performance Issues

**Slow database queries:**
```bash
# Check database performance
docker exec -it springboot-ai-postgres-1 psql -U postgres -d productdb -c "
SELECT query, mean_time, calls 
FROM pg_stat_statements 
ORDER BY mean_time DESC 
LIMIT 10;
"

# Check database indexes
docker exec -it springboot-ai-postgres-1 psql -U postgres -d productdb -c "
SELECT schemaname, tablename, indexname 
FROM pg_indexes 
WHERE tablename = 'products';
"
```

**High memory usage:**
```bash
# Check JVM memory usage
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# Check heap dump (if enabled)
# Look for heapdump.hprof in logs directory

# Monitor with JConsole
jconsole localhost:8080
```

**Cache performance:**
```bash
# Check Redis memory usage
docker exec -it springboot-ai-redis-1 redis-cli info memory

# Check cache hit rate
docker exec -it springboot-ai-redis-1 redis-cli info stats | grep keyspace

# Clear cache if needed
docker exec -it springboot-ai-redis-1 redis-cli flushall
```

### 7.3 Debug Mode

**Enable debug logging:**
```bash
# Set debug level
curl -X POST http://localhost:8080/actuator/loggers/com.example.springboot \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'

# Set Spring Security debug
curl -X POST http://localhost:8080/actuator/loggers/org.springframework.security \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'
```

**Remote debugging:**
```bash
# Start application with debug port
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
  -jar target/springboot-0.0.1-SNAPSHOT.jar

# Connect with IDE (IntelliJ IDEA, Eclipse, VS Code)
# Debug port: 5005
```

---

## 8. Development Workflow

### 8.1 Development Setup

**IDE Configuration:**
```bash
# Import project in IntelliJ IDEA
# File -> Open -> Select pom.xml

# Import project in Eclipse
# File -> Import -> Maven -> Existing Maven Projects

# VS Code setup
code .  # Open in VS Code
# Install Java Extension Pack
```

**Git workflow:**
```bash
# Create feature branch
git checkout -b feature/new-feature

# Make changes
# ... edit files ...

# Commit changes
git add .
git commit -m "Add new feature"

# Push to remote
git push origin feature/new-feature

# Create pull request
# Go to GitHub/GitLab and create PR
```

### 8.2 Testing

**Run tests:**
```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=ProductServiceTest

# Run specific test method
mvn test -Dtest=ProductServiceTest#createProduct_WithValidRequest_ShouldCreateProduct

# Run with coverage
mvn clean test jacoco:report
```

**Integration tests:**
```bash
# Run integration tests only
mvn test -Dtest=*IntegrationTest

# Run with test containers
mvn test -Dspring.profiles.active=test
```

### 8.3 Code Quality

**Code formatting:**
```bash
# Format code with Maven
mvn spring-javaformat:apply

# Check code style
mvn spring-javaformat:validate
```

**Static analysis:**
```bash
# Run SpotBugs
mvn spotbugs:check

# Run PMD
mvn pmd:check

# Run Checkstyle
mvn checkstyle:check
```

### 8.4 Database Migrations

**Create new migration:**
```bash
# Create new migration file
touch src/main/resources/db/migration/V4__Add_new_feature.sql

# Add SQL content
echo "ALTER TABLE products ADD COLUMN new_column VARCHAR(255);" > \
  src/main/resources/db/migration/V4__Add_new_feature.sql
```

**Run migrations:**
```bash
# Migrations run automatically on startup
# Or run manually
mvn flyway:migrate

# Check migration status
mvn flyway:info
```

### 8.5 Deployment

**Build for production:**
```bash
# Build with production profile
mvn clean package -Pproduction

# Build Docker image
docker build -t springboot-product-api:latest .

# Tag for registry
docker tag springboot-product-api:latest your-registry/springboot-product-api:latest
```

**Deploy to production:**
```bash
# Push to registry
docker push your-registry/springboot-product-api:latest

# Deploy with Docker Compose
docker-compose -f docker-compose.prod.yml up -d

# Deploy to Kubernetes
kubectl apply -f k8s/
```

---

## 🎯 Kết luận

Bạn đã hoàn thành việc setup và sử dụng Spring Boot Product Management API! 

### Next Steps:

1. **Explore the API**: Sử dụng Swagger UI để test các endpoints
2. **Monitor the application**: Sử dụng các monitoring tools
3. **Add new features**: Implement thêm business logic
4. **Improve performance**: Optimize database queries và caching
5. **Deploy to production**: Setup production environment

### Useful Commands Summary:

```bash
# Quick start
docker-compose up -d postgres redis
mvn spring-boot:run

# Development
mvn clean test
mvn spring-boot:run -Dspring.profiles.active=development

# Monitoring
curl http://localhost:8080/actuator/health
open http://localhost:8080/swagger-ui.html

# Troubleshooting
docker-compose logs -f
mvn clean package -DskipTests
```

---

**Happy coding! 🚀** 