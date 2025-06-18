# 🏢 Spring Boot Case Study: Thực tế, Tối ưu & Migration

> **Các case study thực tế, tối ưu hiệu năng, và migration từ legacy sang Spring Boot**

---

## 📋 Mục lục

1. [Case Study 1: E-commerce Platform](#1-case-study-1-e-commerce-platform)
2. [Case Study 2: Banking System](#2-case-study-2-banking-system)
3. [Performance Optimization](#3-performance-optimization)
4. [Migration từ Legacy](#4-migration-từ-legacy)
5. [Best Practices & Lessons Learned](#5-best-practices--lessons-learned)

---

## 1. Case Study 1: E-commerce Platform

### 1.1 Vấn đề ban đầu
**Legacy System:**
- Java EE (J2EE) với EJB 2.x
- Oracle Database với stored procedures
- WebLogic Server
- Monolithic architecture
- Manual deployment process
- Không có automated testing
- Performance issues: 5-10 seconds response time

**Challenges:**
- Khó scale khi traffic tăng
- Khó maintain và debug
- Khó deploy new features
- High operational costs
- Poor developer experience

### 1.2 Migration Strategy
```java
// Phase 1: Strangler Fig Pattern
// Giữ legacy system, tạo Spring Boot microservices cho từng module

@SpringBootApplication
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private LegacyProductClient legacyClient; // Gọi legacy system
    
    @Cacheable("products")
    public ProductDTO getProduct(Long productId) {
        // Try new system first
        try {
            return productRepository.findById(productId)
                    .map(this::mapToDTO)
                    .orElse(null);
        } catch (Exception e) {
            // Fallback to legacy system
            log.warn("New system failed, falling back to legacy", e);
            return legacyClient.getProduct(productId);
        }
    }
}

// API Gateway để route traffic
@Component
public class TrafficRouter {
    
    public boolean shouldUseNewSystem(String userId) {
        // Gradually increase traffic to new system
        return userHash(userId) % 100 < getNewSystemPercentage();
    }
}
```

### 1.3 Microservices Architecture
```yaml
# docker-compose.yml
version: '3.8'
services:
  product-service:
    image: ecommerce/product-service:latest
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - DATABASE_URL=jdbc:postgresql://postgres:5432/productdb
    ports:
      - "8081:8080"
    depends_on:
      - postgres
      - redis

  order-service:
    image: ecommerce/order-service:latest
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - DATABASE_URL=jdbc:postgresql://postgres:5432/orderdb
    ports:
      - "8082:8080"
    depends_on:
      - postgres
      - rabbitmq

  user-service:
    image: ecommerce/user-service:latest
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - DATABASE_URL=jdbc:postgresql://postgres:5432/userdb
    ports:
      - "8083:8080"
    depends_on:
      - postgres

  api-gateway:
    image: ecommerce/api-gateway:latest
    environment:
      - SPRING_PROFILES_ACTIVE=production
    ports:
      - "8080:8080"
    depends_on:
      - product-service
      - order-service
      - user-service

  postgres:
    image: postgres:15
    environment:
      POSTGRES_PASSWORD: password
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"

volumes:
  postgres_data:
```

### 1.4 Performance Improvements
```java
// Caching Strategy
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory())
                .cacheDefaults(defaultConfig())
                .withCacheConfiguration("products", productCacheConfig())
                .withCacheConfiguration("categories", categoryCacheConfig())
                .build();
        return cacheManager;
    }
    
    private RedisCacheConfiguration defaultConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
    
    private RedisCacheConfiguration productCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(2))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
}

// Async Processing
@Service
public class OrderService {
    
    @Async
    public CompletableFuture<OrderDTO> createOrderAsync(OrderRequest request) {
        // Process order asynchronously
        Order order = processOrder(request);
        return CompletableFuture.completedFuture(mapToDTO(order));
    }
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Send notifications asynchronously
        notificationService.sendOrderConfirmation(event.getOrderId());
        inventoryService.updateStock(event.getProductId(), event.getQuantity());
    }
}

// Database Optimization
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @QueryHints(@QueryHint(name = HINT_FETCH_SIZE, value = "50"))
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);
    
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                  @Param("maxPrice") BigDecimal maxPrice, 
                                  Pageable pageable);
}
```

### 1.5 Results
- **Response Time**: Giảm từ 5-10s xuống 200-500ms
- **Throughput**: Tăng 10x (từ 100 RPS lên 1000 RPS)
- **Deployment Time**: Giảm từ 2-4 hours xuống 5-10 minutes
- **Developer Productivity**: Tăng 3x
- **Operational Costs**: Giảm 40%

---

## 2. Case Study 2: Banking System

### 2.1 Vấn đề ban đầu
**Legacy System:**
- COBOL mainframe system
- Batch processing
- Manual reconciliation
- No real-time processing
- Poor user experience
- High maintenance costs

### 2.2 Modern Banking Architecture
```java
// Event-Driven Architecture
@Service
public class TransactionService {
    
    @Transactional
    public TransactionDTO processTransaction(TransactionRequest request) {
        // Validate transaction
        validateTransaction(request);
        
        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setFromAccount(request.getFromAccount());
        transaction.setToAccount(request.getToAccount());
        transaction.setType(request.getType());
        transaction.setStatus("PENDING");
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Publish event
        TransactionCreatedEvent event = new TransactionCreatedEvent(
            savedTransaction.getId(),
            savedTransaction.getAmount(),
            savedTransaction.getFromAccount(),
            savedTransaction.getToAccount()
        );
        
        eventPublisher.publish("transaction.created", event);
        
        return mapToDTO(savedTransaction);
    }
}

// Event Handlers
@Component
public class TransactionEventHandler {
    
    @EventListener
    public void handleTransactionCreated(TransactionCreatedEvent event) {
        // Update account balances
        accountService.debitAccount(event.getFromAccount(), event.getAmount());
        accountService.creditAccount(event.getToAccount(), event.getAmount());
        
        // Update transaction status
        transactionService.updateStatus(event.getTransactionId(), "COMPLETED");
        
        // Send notifications
        notificationService.sendTransactionNotification(event);
    }
    
    @EventListener
    public void handleTransactionFailed(TransactionFailedEvent event) {
        // Rollback transaction
        transactionService.rollbackTransaction(event.getTransactionId());
        
        // Send failure notification
        notificationService.sendFailureNotification(event);
    }
}

// Security Implementation
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/transactions/**").hasRole("USER")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}

// Audit Trail
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private BigDecimal amount;
    private String fromAccount;
    private String toAccount;
    private String type;
    private String status;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @CreatedBy
    private String createdBy;
    
    @LastModifiedBy
    private String updatedBy;
    
    // Getters and setters
}
```

### 2.3 Compliance & Monitoring
```java
// Audit Service
@Service
public class AuditService {
    
    @EventListener
    public void auditTransaction(TransactionCreatedEvent event) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("TRANSACTION_CREATED");
        auditLog.setEntityType("TRANSACTION");
        auditLog.setEntityId(event.getTransactionId().toString());
        auditLog.setUserId(SecurityContextHolder.getContext().getAuthentication().getName());
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setDetails(JsonUtils.toJson(event));
        
        auditRepository.save(auditLog);
    }
}

// Compliance Checker
@Component
public class ComplianceChecker {
    
    @EventListener
    public void checkCompliance(TransactionCreatedEvent event) {
        // Check for suspicious activity
        if (isSuspiciousTransaction(event)) {
            ComplianceAlert alert = new ComplianceAlert();
            alert.setTransactionId(event.getTransactionId());
            alert.setReason("SUSPICIOUS_AMOUNT");
            alert.setSeverity("HIGH");
            
            complianceService.createAlert(alert);
        }
        
        // Check for money laundering
        if (isMoneyLaunderingSuspicious(event)) {
            ComplianceAlert alert = new ComplianceAlert();
            alert.setTransactionId(event.getTransactionId());
            alert.setReason("MONEY_LAUNDERING_SUSPICION");
            alert.setSeverity("CRITICAL");
            
            complianceService.createAlert(alert);
        }
    }
    
    private boolean isSuspiciousTransaction(TransactionCreatedEvent event) {
        return event.getAmount().compareTo(new BigDecimal("10000")) > 0;
    }
    
    private boolean isMoneyLaunderingSuspicious(TransactionCreatedEvent event) {
        // Implement money laundering detection logic
        return false;
    }
}
```

---

## 3. Performance Optimization

### 3.1 Database Optimization
```java
// Connection Pool Configuration
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000

// Query Optimization
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Use indexes
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.active = true")
    List<Product> findActiveByCategory(@Param("categoryId") Long categoryId);
    
    // Pagination
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                  @Param("maxPrice") BigDecimal maxPrice, 
                                  Pageable pageable);
    
    // Projection for better performance
    @Query("SELECT new com.example.dto.ProductSummary(p.id, p.name, p.price) FROM Product p")
    List<ProductSummary> findAllProductSummaries();
}

// Batch Processing
@Service
public class BatchService {
    
    @Transactional
    public void processBatchTransactions(List<TransactionRequest> requests) {
        int batchSize = 100;
        
        for (int i = 0; i < requests.size(); i += batchSize) {
            List<TransactionRequest> batch = requests.subList(i, Math.min(i + batchSize, requests.size()));
            processBatch(batch);
        }
    }
    
    private void processBatch(List<TransactionRequest> batch) {
        List<Transaction> transactions = batch.stream()
                .map(this::mapToTransaction)
                .collect(Collectors.toList());
        
        transactionRepository.saveAll(transactions);
    }
}
```

### 3.2 Caching Strategy
```java
// Multi-level Caching
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    @Primary
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }
    
    @Bean
    public CacheManager redisCacheManager() {
        RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory())
                .cacheDefaults(defaultConfig())
                .build();
        return cacheManager;
    }
    
    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES));
        return cacheManager;
    }
}

// Cache Service
@Service
public class CacheService {
    
    @Autowired
    @Qualifier("caffeineCacheManager")
    private CacheManager localCache;
    
    @Autowired
    @Qualifier("redisCacheManager")
    private CacheManager distributedCache;
    
    public ProductDTO getProduct(Long productId) {
        // Try local cache first
        Cache cache = localCache.getCache("products");
        ProductDTO product = cache.get(productId, ProductDTO.class);
        
        if (product != null) {
            return product;
        }
        
        // Try distributed cache
        Cache redisCache = distributedCache.getCache("products");
        product = redisCache.get(productId, ProductDTO.class);
        
        if (product != null) {
            // Store in local cache
            cache.put(productId, product);
            return product;
        }
        
        // Load from database
        product = productService.getProduct(productId);
        
        if (product != null) {
            // Store in both caches
            cache.put(productId, product);
            redisCache.put(productId, product);
        }
        
        return product;
    }
}
```

### 3.3 Async Processing
```java
// Async Configuration
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
    
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}

// Async Service
@Service
public class NotificationService {
    
    @Async
    public CompletableFuture<Void> sendEmailAsync(String to, String subject, String content) {
        try {
            emailClient.sendEmail(to, subject, content);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Failed to send email", e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    @Async
    public CompletableFuture<Void> sendSMSAsync(String phone, String message) {
        try {
            smsClient.sendSMS(phone, message);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Failed to send SMS", e);
            return CompletableFuture.failedFuture(e);
        }
    }
}
```

---

## 4. Migration từ Legacy

### 4.1 Migration Strategy
```java
// Strangler Fig Pattern Implementation
@Component
public class MigrationRouter {
    
    @Autowired
    private FeatureToggleService featureToggle;
    
    public boolean shouldUseNewSystem(String userId, String feature) {
        // Check feature toggle
        if (!featureToggle.isEnabled(feature)) {
            return false;
        }
        
        // Check user migration status
        UserMigrationStatus status = getUserMigrationStatus(userId);
        
        switch (status) {
            case MIGRATED:
                return true;
            case IN_PROGRESS:
                return Math.random() < 0.5; // 50% traffic to new system
            case NOT_MIGRATED:
                return Math.random() < 0.1; // 10% traffic to new system
            default:
                return false;
        }
    }
}

// Data Migration Service
@Service
public class DataMigrationService {
    
    @Autowired
    private LegacyDataClient legacyClient;
    
    @Autowired
    private NewDataRepository newRepository;
    
    @Transactional
    public void migrateUserData(Long userId) {
        try {
            // Get data from legacy system
            LegacyUser legacyUser = legacyClient.getUser(userId);
            
            // Transform data
            User newUser = transformUser(legacyUser);
            
            // Save to new system
            newRepository.save(newUser);
            
            // Update migration status
            updateMigrationStatus(userId, MigrationStatus.MIGRATED);
            
        } catch (Exception e) {
            log.error("Failed to migrate user data", e);
            updateMigrationStatus(userId, MigrationStatus.FAILED);
            throw e;
        }
    }
    
    private User transformUser(LegacyUser legacyUser) {
        User user = new User();
        user.setId(legacyUser.getId());
        user.setName(legacyUser.getName());
        user.setEmail(legacyUser.getEmail());
        user.setStatus(transformStatus(legacyUser.getStatus()));
        return user;
    }
}
```

### 4.2 Blue-Green Deployment
```yaml
# Blue-Green Deployment with Kubernetes
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product-service-blue
  labels:
    app: product-service
    version: blue
spec:
  replicas: 3
  selector:
    matchLabels:
      app: product-service
      version: blue
  template:
    metadata:
      labels:
        app: product-service
        version: blue
    spec:
      containers:
      - name: product-service
        image: product-service:blue
        ports:
        - containerPort: 8080
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5

---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product-service-green
  labels:
    app: product-service
    version: green
spec:
  replicas: 0
  selector:
    matchLabels:
      app: product-service
      version: green
  template:
    metadata:
      labels:
        app: product-service
        version: green
    spec:
      containers:
      - name: product-service
        image: product-service:green
        ports:
        - containerPort: 8080
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5

---
apiVersion: v1
kind: Service
metadata:
  name: product-service
spec:
  selector:
    app: product-service
    version: blue  # Initially route to blue
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

---

## 5. Best Practices & Lessons Learned

### 5.1 Best Practices
1. **Start Small**: Migrate từng module một, không migrate toàn bộ cùng lúc
2. **Feature Toggles**: Sử dụng feature toggles để control traffic
3. **Monitoring**: Monitor cả old và new system trong quá trình migration
4. **Rollback Plan**: Luôn có plan rollback khi có vấn đề
5. **Data Consistency**: Đảm bảo data consistency giữa old và new system
6. **Testing**: Test kỹ trước khi deploy production
7. **Documentation**: Document tất cả changes và decisions

### 5.2 Common Pitfalls
1. **Big Bang Migration**: Migrate toàn bộ cùng lúc
2. **No Monitoring**: Không monitor performance và errors
3. **No Rollback Plan**: Không có plan rollback
4. **Data Inconsistency**: Data không sync giữa old và new system
5. **Poor Testing**: Test không đầy đủ
6. **No Documentation**: Không document process và decisions

### 5.3 Success Metrics
- **Performance**: Response time, throughput
- **Reliability**: Uptime, error rate
- **Cost**: Operational costs, development costs
- **Developer Productivity**: Time to market, deployment frequency
- **User Experience**: User satisfaction, feature adoption

---

**Spring Boot giúp modernize legacy systems một cách an toàn và hiệu quả, với performance tốt hơn, maintainability cao hơn, và developer experience tốt hơn! 🚀** 