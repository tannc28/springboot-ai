# MongoDB: NoSQL Database Evolution & Implementation Guide

## 📖 Table of Contents
- [Evolution & History](#evolution--history)
- [Why MongoDB?](#why-mongodb)
- [Architecture Overview](#architecture-overview)
- [Performance Comparison](#performance-comparison)
- [Implementation Guide](#implementation-guide)
- [Monitoring & Metrics](#monitoring--metrics)
- [Best Practices](#best-practices)
- [Real-world Use Cases](#real-world-use-cases)

## 🚀 Evolution & History

### Before MongoDB: The Relational Database Era

```mermaid
graph TD
    A[Relational Databases] --> B[ACID Properties]
    B --> C[Atomicity]
    B --> D[Consistency]
    B --> E[Isolation]
    B --> F[Durability]
    
    A --> G[SQL Databases]
    G --> H[Oracle]
    G --> I[MySQL]
    G --> J[PostgreSQL]
    G --> K[SQL Server]
    
    H --> L[Problems: Expensive]
    I --> M[Problems: Complex Schema]
    J --> N[Problems: Scaling Issues]
    K --> O[Problems: Vendor Lock-in]
```

**Relational database challenges:**

1. **Schema Rigidity**: Schema changes require migrations
2. **Scaling Complexity**: Vertical scaling limitations
3. **Performance Issues**: Complex joins and relationships
4. **Cost**: Expensive licensing and hardware requirements
5. **Development Speed**: Slow iteration due to schema constraints

### The NoSQL Revolution & MongoDB

```mermaid
timeline
    title MongoDB Evolution Timeline
    2007 : 10gen founded (later MongoDB Inc.)
    2009 : MongoDB 1.0 released
    2010 : First production deployments
    2012 : MongoDB 2.0 with aggregation framework
    2013 : MongoDB 2.4 with text search
    2014 : MongoDB 2.6 with write concerns
    2015 : MongoDB 3.0 with WiredTiger storage engine
    2016 : MongoDB 3.2 with document validation
    2017 : MongoDB 3.4 with faceted search
    2018 : MongoDB 4.0 with multi-document transactions
    2019 : MongoDB 4.2 with distributed transactions
    2020 : MongoDB 4.4 with hedged reads
    2021 : MongoDB 5.0 with time series collections
    2022 : MongoDB 6.0 with cluster-to-cluster sync
    2023 : MongoDB 7.0 with queryable encryption
```

**MongoDB's Goals:**
- Document-oriented data model
- Horizontal scalability
- High performance
- Developer productivity
- Flexible schema

## 🎯 Why MongoDB?

### Problems Solved by MongoDB

| Problem | Relational Solution | MongoDB Solution |
|---------|-------------------|------------------|
| **Schema Flexibility** | Complex migrations | Schema-less documents |
| **Horizontal Scaling** | Sharding complexity | Built-in sharding |
| **Performance** | Optimized queries | Indexed queries |
| **Development Speed** | Schema-first design | Schema-last development |
| **Data Variety** | Normalized tables | Embedded documents |

### Performance Comparison

```mermaid
graph LR
    A[Read Performance] --> B[MySQL: 10K ops/s]
    A --> C[MongoDB: 50K ops/s]
    A --> D[PostgreSQL: 15K ops/s]
    
    E[Write Performance] --> F[MySQL: 5K ops/s]
    E --> G[MongoDB: 20K ops/s]
    E --> H[PostgreSQL: 8K ops/s]
    
    I[Scalability] --> J[MySQL: Vertical]
    I --> K[MongoDB: Horizontal]
    I --> L[PostgreSQL: Complex]
    
    M[Schema Flexibility] --> N[MySQL: Rigid]
    M --> O[MongoDB: Flexible]
    M --> P[PostgreSQL: Semi-flexible]
```

**MongoDB Strengths:**
- **Document Model**: Natural data representation
- **Horizontal Scaling**: Built-in sharding
- **High Performance**: Indexed queries, in-memory operations
- **Developer Friendly**: JSON-like documents
- **Rich Query Language**: Aggregation framework

## 🏗️ Architecture Overview

### Core Components

```mermaid
graph TB
    subgraph "MongoDB Cluster"
        M1[Mongos Router]
        M2[Mongos Router]
        M3[Mongos Router]
    end
    
    subgraph "Config Servers"
        CS1[Config Server 1]
        CS2[Config Server 2]
        CS3[Config Server 3]
    end
    
    subgraph "Shard 1"
        S1P[Primary]
        S1S1[Secondary 1]
        S1S2[Secondary 2]
    end
    
    subgraph "Shard 2"
        S2P[Primary]
        S2S1[Secondary 1]
        S2S2[Secondary 2]
    end
    
    subgraph "Shard 3"
        S3P[Primary]
        S3S1[Secondary 1]
        S3S2[Secondary 2]
    end
    
    M1 --> CS1
    M2 --> CS2
    M3 --> CS3
    
    M1 --> S1P
    M1 --> S2P
    M1 --> S3P
    
    S1P --> S1S1
    S1P --> S1S2
    S2P --> S2S1
    S2P --> S2S2
    S3P --> S3S1
    S3P --> S3S2
```

### Document Structure

```mermaid
graph LR
    subgraph "User Document"
        UD[User Document]
        UD --> UID[_id: ObjectId]
        UD --> UN[name: String]
        UD --> UEM[email: String]
        UD --> UAD[address: Object]
        UAD --> UAC[city: String]
        UAD --> UAS[state: String]
        UD --> UPH[phones: Array]
        UD --> UOR[orders: Array]
    end
    
    subgraph "Order Document"
        OD[Order Document]
        OD --> OID[_id: ObjectId]
        OD --> OUS[user_id: ObjectId]
        OD --> OIT[items: Array]
        OD --> OST[status: String]
        OD --> ODT[date: Date]
    end
```

### Replication Architecture

```mermaid
graph TB
    subgraph "Replica Set"
        P[Primary]
        S1[Secondary 1]
        S2[Secondary 2]
        A[Arbiter]
    end
    
    P --> S1
    P --> S2
    P --> A
    
    S1 -.-> S2
    S2 -.-> S1
```

## ⚡ Performance Comparison

### Throughput Benchmarks

| Database | Read Ops/sec | Write Ops/sec | Storage | Memory |
|----------|-------------|---------------|---------|--------|
| **MySQL** | 10K | 5K | 100GB | 8GB |
| **PostgreSQL** | 15K | 8K | 100GB | 8GB |
| **MongoDB** | 50K | 20K | 100GB | 16GB |
| **Redis** | 100K+ | 100K+ | Memory only | 32GB |
| **Cassandra** | 30K | 25K | 200GB | 16GB |

### Resource Usage Comparison

```mermaid
graph LR
    subgraph "MongoDB"
        A[Memory: 16GB] --> B[CPU: 40%]
        B --> C[Storage: SSD]
        C --> D[Network: 1Gbps]
    end
    
    subgraph "MySQL"
        E[Memory: 8GB] --> F[CPU: 60%]
        F --> G[Storage: SSD]
        G --> H[Network: 1Gbps]
    end
    
    subgraph "PostgreSQL"
        I[Memory: 12GB] --> J[CPU: 50%]
        J --> K[Storage: SSD]
        K --> L[Network: 1Gbps]
    end
```

## 🛠️ Implementation Guide

### 1. Docker Setup

```yaml
# docker-compose.yml
version: '3.8'
services:
  mongodb:
    image: mongo:7.0
    container_name: mongodb
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: password
      MONGO_INITDB_DATABASE: productdb
    volumes:
      - mongodb_data:/data/db
      - ./src/main/resources/db/mongodb/init.js:/docker-entrypoint-initdb.d/init.js:ro
    command: mongod --auth

  mongodb-express:
    image: mongo-express:latest
    container_name: mongodb-express
    ports:
      - "8082:8081"
    environment:
      ME_CONFIG_MONGODB_ADMINUSERNAME: admin
      ME_CONFIG_MONGODB_ADMINPASSWORD: password
      ME_CONFIG_MONGODB_URL: mongodb://admin:password@mongodb:27017/
      ME_CONFIG_BASICAUTH_USERNAME: admin
      ME_CONFIG_BASICAUTH_PASSWORD: password
    depends_on:
      - mongodb

volumes:
  mongodb_data:
```

### 2. Spring Boot Configuration

```java
@Configuration
@EnableMongoRepositories(basePackages = "com.example.springboot.repository.mongo")
public class MongoDBConfig extends AbstractMongoClientConfiguration {
    
    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;
    
    @Value("${spring.data.mongodb.database}")
    private String databaseName;
    
    @Override
    protected String getDatabaseName() {
        return databaseName;
    }
    
    @Override
    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(mongoUri);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .retryReads(true)
                .build();
        return MongoClients.create(mongoClientSettings);
    }
    
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient(), getDatabaseName());
        
        // Configure custom conversions
        MappingMongoConverter converter = (MappingMongoConverter) mongoTemplate.getConverter();
        converter.setCustomConversions(customConversions());
        converter.afterPropertiesSet();
        
        return mongoTemplate;
    }
    
    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
            // Add custom converters here if needed
        ));
    }
}
```

### 3. Document Models

```java
@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String email;
    
    @Indexed
    private String username;
    
    private String firstName;
    private String lastName;
    
    @Indexed
    private String role;
    
    private Address address;
    
    private List<String> phoneNumbers;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
}

@Document(collection = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    private String id;
    
    @Indexed
    private String name;
    
    private String description;
    
    @Indexed
    private String category;
    
    private BigDecimal price;
    
    @Indexed
    private Integer stock;
    
    private List<String> tags;
    
    private Map<String, Object> attributes;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}

@Document(collection = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    @Indexed
    private String status;
    
    private List<OrderItem> items;
    
    private BigDecimal totalAmount;
    
    private Address shippingAddress;
    
    private PaymentInfo paymentInfo;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

### 4. Repository Implementation

```java
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    // Basic queries
    Optional<User> findByEmail(String email);
    List<User> findByRole(String role);
    List<User> findByFirstNameAndLastName(String firstName, String lastName);
    
    // Complex queries
    @Query("{'address.city': ?0}")
    List<User> findByCity(String city);
    
    @Query("{'price': {$gte: ?0, $lte: ?1}}")
    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Aggregation queries
    @Aggregation(pipeline = {
        "{'$match': {'status': ?0}}",
        "{'$group': {'_id': '$userId', 'totalOrders': {'$sum': 1}}}",
        "{'$sort': {'totalOrders': -1}}"
    })
    List<OrderSummary> getOrderSummaryByStatus(String status);
    
    // Text search
    @Query("{'$text': {'$search': ?0}}")
    List<Product> searchProducts(String searchTerm);
    
    // Geospatial queries
    @Query("{'location': {'$near': {'$geometry': {'type': 'Point', 'coordinates': [?0, ?1]}, '$maxDistance': ?2}}}")
    List<User> findUsersNearLocation(double longitude, double latitude, double maxDistance);
}

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
    List<Product> findByCategory(String category);
    
    @Query("{'price': {$gte: ?0}}")
    List<Product> findByPriceGreaterThan(BigDecimal price);
    
    @Query("{'stock': {$gt: 0}}")
    List<Product> findAvailableProducts();
    
    @Query("{'tags': {$in: ?0}}")
    List<Product> findByTags(List<String> tags);
}
```

### 5. Service Implementation

```java
@Service
@Slf4j
public class MongoDBService {
    
    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    // User operations
    public User createUser(User user) {
        log.info("Creating user: {}", user.getEmail());
        return userRepository.save(user);
    }
    
    public Optional<User> findUserById(String id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<User> findUsersByRole(String role) {
        return userRepository.findByRole(role);
    }
    
    public User updateUser(String id, User user) {
        if (userRepository.existsById(id)) {
            user.setId(id);
            return userRepository.save(user);
        }
        throw new ResourceNotFoundException("User not found with id: " + id);
    }
    
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
    
    // Product operations
    public Product createProduct(Product product) {
        log.info("Creating product: {}", product.getName());
        return productRepository.save(product);
    }
    
    public List<Product> findProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    public List<Product> findAvailableProducts() {
        return productRepository.findAvailableProducts();
    }
    
    // Complex queries using MongoTemplate
    public List<User> findUsersWithHighValueOrders(BigDecimal minOrderValue) {
        Criteria criteria = Criteria.where("orders.totalAmount").gte(minOrderValue);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, User.class);
    }
    
    public List<Product> searchProductsByText(String searchTerm) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matching(searchTerm);
        Query query = TextQuery.queryText(criteria).sortByScore();
        return mongoTemplate.find(query, Product.class);
    }
    
    // Aggregation pipeline
    public List<OrderSummary> getOrderSummary() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.group("status")
                .count().as("count")
                .sum("totalAmount").as("totalAmount"),
            Aggregation.sort(Sort.Direction.DESC, "count")
        );
        
        AggregationResults<OrderSummary> results = 
            mongoTemplate.aggregate(aggregation, "orders", OrderSummary.class);
        
        return results.getMappedResults();
    }
    
    // Bulk operations
    public void bulkInsertProducts(List<Product> products) {
        mongoTemplate.insert(products, Product.class);
    }
    
    public void bulkUpdateProductPrices(String category, BigDecimal priceIncrease) {
        Query query = new Query(Criteria.where("category").is(category));
        Update update = new Update().inc("price", priceIncrease);
        mongoTemplate.updateMulti(query, update, Product.class);
    }
}
```

### 6. Index Management

```java
@Component
public class MongoDBIndexManager {
    
    private final MongoTemplate mongoTemplate;
    
    public void createIndexes() {
        // Create indexes for users collection
        mongoTemplate.indexOps(User.class)
            .ensureIndex(new Index().on("email", Sort.Direction.ASC).unique());
        
        mongoTemplate.indexOps(User.class)
            .ensureIndex(new Index().on("username", Sort.Direction.ASC).unique());
        
        mongoTemplate.indexOps(User.class)
            .ensureIndex(new Index().on("role", Sort.Direction.ASC));
        
        // Create indexes for products collection
        mongoTemplate.indexOps(Product.class)
            .ensureIndex(new Index().on("name", Sort.Direction.ASC));
        
        mongoTemplate.indexOps(Product.class)
            .ensureIndex(new Index().on("category", Sort.Direction.ASC));
        
        mongoTemplate.indexOps(Product.class)
            .ensureIndex(new Index().on("price", Sort.Direction.ASC));
        
        // Create text index for search
        mongoTemplate.indexOps(Product.class)
            .ensureIndex(new Index().on("name", Sort.Direction.ASC)
                .on("description", Sort.Direction.ASC)
                .named("text_search"));
        
        // Create compound indexes
        mongoTemplate.indexOps(Order.class)
            .ensureIndex(new Index().on("userId", Sort.Direction.ASC)
                .on("status", Sort.Direction.ASC)
                .on("createdAt", Sort.Direction.DESC));
    }
    
    public void dropIndexes() {
        mongoTemplate.indexOps(User.class).dropAll();
        mongoTemplate.indexOps(Product.class).dropAll();
        mongoTemplate.indexOps(Order.class).dropAll();
    }
}
```

## 📊 Monitoring & Metrics

### Key Metrics to Monitor

```mermaid
graph TB
    subgraph "Performance Metrics"
        PM1[Operations per second]
        PM2[Query latency]
        PM3[Connection count]
        PM4[Active sessions]
    end
    
    subgraph "Resource Metrics"
        RM1[CPU usage]
        RM2[Memory usage]
        RM3[Disk I/O]
        RM4[Network I/O]
    end
    
    subgraph "Database Metrics"
        DM1[Document count]
        DM2[Index size]
        DM3[Storage size]
        DM4[Cache hit ratio]
    end
    
    subgraph "Replication Metrics"
        RLM1[Replication lag]
        RLM2[Oplog size]
        RLM3[Replica set health]
        RLM4[Election count]
    end
```

### Prometheus Configuration

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'mongodb'
    static_configs:
      - targets: ['mongodb:9216']
    metrics_path: '/metrics'
    scrape_interval: 15s
```

### Grafana Dashboard Queries

```sql
-- Operations per second
rate(mongodb_mongod_op_latencies_latency_total[5m])

-- Query latency
histogram_quantile(0.95, rate(mongodb_mongod_op_latencies_latency_bucket[5m]))

-- Connection count
mongodb_connections

-- Memory usage
mongodb_memory_resident

-- Document count
mongodb_dbstats_collections
```

### Health Check Script

```bash
#!/bin/bash

# Check MongoDB status
echo "Checking MongoDB status..."
mongo --eval "db.adminCommand('ping')"

# Check replica set status
echo "Checking replica set status..."
mongo --eval "rs.status()"

# Check database stats
echo "Checking database stats..."
mongo --eval "db.stats()"

# Check collection stats
echo "Checking collection stats..."
mongo --eval "db.users.stats()"

# Check index usage
echo "Checking index usage..."
mongo --eval "db.users.aggregate([{\$indexStats: {}}])"
```

## 🎯 Best Practices

### 1. Document Design Patterns

```mermaid
graph LR
    subgraph "Embedded Pattern"
        A[User Document] --> B[Address Object]
        A --> C[Phone Array]
        A --> D[Preferences Object]
    end
    
    subgraph "Referenced Pattern"
        E[User Document] --> F[User ID Reference]
        F --> G[Order Document]
        F --> H[Payment Document]
    end
    
    subgraph "Hybrid Pattern"
        I[Product Document] --> J[Basic Info Embedded]
        I --> K[Reviews Array]
        I --> L[Category Reference]
    end
```

### 2. Indexing Strategy

```java
@Component
public class IndexingStrategy {
    
    private final MongoTemplate mongoTemplate;
    
    public void createOptimalIndexes() {
        // Single field indexes
        createSingleFieldIndexes();
        
        // Compound indexes
        createCompoundIndexes();
        
        // Text indexes
        createTextIndexes();
        
        // Geospatial indexes
        createGeospatialIndexes();
        
        // TTL indexes
        createTTLIndexes();
    }
    
    private void createSingleFieldIndexes() {
        // Users collection
        mongoTemplate.indexOps(User.class)
            .ensureIndex(new Index().on("email", Sort.Direction.ASC).unique());
        
        mongoTemplate.indexOps(User.class)
            .ensureIndex(new Index().on("createdAt", Sort.Direction.DESC));
        
        // Products collection
        mongoTemplate.indexOps(Product.class)
            .ensureIndex(new Index().on("category", Sort.Direction.ASC));
        
        mongoTemplate.indexOps(Product.class)
            .ensureIndex(new Index().on("price", Sort.Direction.ASC));
    }
    
    private void createCompoundIndexes() {
        // Compound index for orders
        mongoTemplate.indexOps(Order.class)
            .ensureIndex(new Index()
                .on("userId", Sort.Direction.ASC)
                .on("status", Sort.Direction.ASC)
                .on("createdAt", Sort.Direction.DESC));
        
        // Compound index for products
        mongoTemplate.indexOps(Product.class)
            .ensureIndex(new Index()
                .on("category", Sort.Direction.ASC)
                .on("price", Sort.Direction.ASC)
                .on("stock", Sort.Direction.DESC));
    }
    
    private void createTextIndexes() {
        // Text index for product search
        mongoTemplate.indexOps(Product.class)
            .ensureIndex(new Index()
                .on("name", Sort.Direction.ASC)
                .on("description", Sort.Direction.ASC)
                .named("product_text_search"));
    }
    
    private void createGeospatialIndexes() {
        // 2dsphere index for location-based queries
        mongoTemplate.indexOps(User.class)
            .ensureIndex(new Index()
                .on("location", Sort.Direction.ASC)
                .named("location_2dsphere"));
    }
    
    private void createTTLIndexes() {
        // TTL index for session cleanup
        mongoTemplate.indexOps(Session.class)
            .ensureIndex(new Index()
                .on("expiresAt", Sort.Direction.ASC)
                .expire(0));
    }
}
```

### 3. Connection Pooling

```java
@Configuration
public class MongoDBConnectionConfig {
    
    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017");
        
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applyToConnectionPoolSettings(builder -> 
                    builder.maxSize(100)
                           .minSize(5)
                           .maxWaitTime(30000, TimeUnit.MILLISECONDS)
                           .maxConnectionLifeTime(300000, TimeUnit.MILLISECONDS))
                .applyToServerSettings(builder -> 
                    builder.heartbeatFrequency(10000, TimeUnit.MILLISECONDS))
                .retryWrites(true)
                .retryReads(true)
                .build();
        
        return MongoClients.create(settings);
    }
}
```

### 4. Error Handling

```java
@Component
public class MongoDBErrorHandler {
    
    private static final Logger log = LoggerFactory.getLogger(MongoDBErrorHandler.class);
    
    public <T> T handleMongoException(Supplier<T> operation, String operationName) {
        try {
            return operation.get();
        } catch (MongoTimeoutException e) {
            log.error("MongoDB timeout during {}: {}", operationName, e.getMessage());
            throw new ServiceException("Database timeout", e);
        } catch (MongoWriteException e) {
            log.error("MongoDB write error during {}: {}", operationName, e.getMessage());
            if (e.getError().getCode() == 11000) {
                throw new DuplicateResourceException("Resource already exists", e);
            }
            throw new ServiceException("Database write error", e);
        } catch (MongoException e) {
            log.error("MongoDB error during {}: {}", operationName, e.getMessage());
            throw new ServiceException("Database error", e);
        }
    }
    
    public void handleMongoException(Runnable operation, String operationName) {
        handleMongoException(() -> {
            operation.run();
            return null;
        }, operationName);
    }
}
```

## 🌍 Real-world Use Cases

### 1. Content Management System

```mermaid
graph LR
    subgraph "CMS Collections"
        A[Articles] --> B[Categories]
        A --> C[Tags]
        A --> D[Authors]
        A --> E[Comments]
    end
    
    subgraph "Article Document"
        F[Article] --> G[Title]
        F --> H[Content]
        F --> I[Author Reference]
        F --> J[Category Reference]
        F --> K[Tags Array]
        F --> L[Comments Array]
    end
```

### 2. E-commerce Platform

```mermaid
graph TB
    subgraph "Product Catalog"
        P1[Products] --> P2[Categories]
        P1 --> P3[Brands]
        P1 --> P4[Reviews]
    end
    
    subgraph "Order Management"
        O1[Orders] --> O2[Order Items]
        O1 --> O3[Shipping Info]
        O1 --> O4[Payment Info]
    end
    
    subgraph "User Management"
        U1[Users] --> U2[Profiles]
        U1 --> U3[Addresses]
        U1 --> U4[Preferences]
    end
```

### 3. IoT Data Platform

```mermaid
graph LR
    subgraph "Device Management"
        D1[Devices] --> D2[Device Types]
        D1 --> D3[Locations]
        D1 --> D4[Owners]
    end
    
    subgraph "Time Series Data"
        T1[Sensor Data] --> T2[Temperature]
        T1 --> T3[Humidity]
        T1 --> T4[Pressure]
        T1 --> T5[Timestamp]
    end
    
    subgraph "Analytics"
        A1[Aggregated Data] --> A2[Hourly Stats]
        A1 --> A3[Daily Stats]
        A1 --> A4[Monthly Stats]
    end
```

## 📈 Performance Tuning

### Query Optimization

```java
@Service
public class QueryOptimizationService {
    
    private final MongoTemplate mongoTemplate;
    
    // Use projection to limit fields
    public List<UserSummary> findUserSummaries() {
        Query query = new Query();
        query.fields().include("id", "firstName", "lastName", "email");
        return mongoTemplate.find(query, UserSummary.class, "users");
    }
    
    // Use aggregation for complex queries
    public List<ProductStats> getProductStats() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.group("category")
                .count().as("count")
                .avg("price").as("avgPrice")
                .sum("stock").as("totalStock"),
            Aggregation.sort(Sort.Direction.DESC, "count")
        );
        
        return mongoTemplate.aggregate(aggregation, "products", ProductStats.class)
                .getMappedResults();
    }
    
    // Use bulk operations
    public void bulkUpdateProductPrices(Map<String, BigDecimal> priceUpdates) {
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Product.class);
        
        priceUpdates.forEach((productId, newPrice) -> {
            Query query = new Query(Criteria.where("id").is(productId));
            Update update = new Update().set("price", newPrice);
            bulkOps.updateOne(query, update);
        });
        
        bulkOps.execute();
    }
}
```

### Index Optimization

```properties
# MongoDB configuration
# Enable query profiler
operationProfiling:
  mode: slowOp
  slowOpThresholdMs: 100

# Memory settings
storage:
  wiredTiger:
    engineConfig:
      cacheSizeGB: 4
    collectionConfig:
      blockCompressor: snappy

# Network settings
net:
  maxInMemoryConnections: 1000
  maxConnections: 1000
```

## 🔍 Troubleshooting

### Common Issues & Solutions

| Issue | Symptoms | Solution |
|-------|----------|----------|
| **Slow Queries** | High latency, timeouts | Create proper indexes, optimize queries |
| **Memory Pressure** | High memory usage, OOM | Increase memory, optimize indexes |
| **Connection Exhaustion** | Connection refused errors | Increase connection pool, check for leaks |
| **Disk Space** | Write failures, slow performance | Add storage, enable compression |
| **Replication Lag** | Data inconsistency | Check network, optimize writes |

### Diagnostic Commands

```bash
# Check database status
mongo --eval "db.adminCommand('serverStatus')"

# Check slow queries
mongo --eval "db.setProfilingLevel(1, 100)"

# Check index usage
mongo --eval "db.users.aggregate([{\$indexStats: {}}])"

# Check collection stats
mongo --eval "db.users.stats()"

# Check replication status
mongo --eval "rs.status()"

# Check connection count
mongo --eval "db.serverStatus().connections"
```

## 🎓 Conclusion

MongoDB has revolutionized how we store and manage data by providing a flexible, scalable, and developer-friendly document database. Its evolution from rigid relational databases addresses critical limitations in schema flexibility, horizontal scaling, and development speed.

**Key Takeaways:**
- MongoDB excels in flexible schema design and rapid development
- Proper indexing strategy is crucial for performance
- Document design patterns impact query efficiency
- Monitoring and optimization are essential for production use
- Connection pooling and error handling improve reliability

**Next Steps:**
1. Start with simple document models
2. Implement proper indexing strategy
3. Add monitoring and alerting
4. Optimize queries based on usage patterns
5. Consider sharding for horizontal scaling

---

*This blog is part of a series on modern backend technologies. Stay tuned for the next installment on Cassandra!* 