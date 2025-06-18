# 🚀 Monitoring, Testing & Architecture Evolution

> **Hướng dẫn sâu về evolution của monitoring, testing và architecture patterns**

---

## 📋 Mục lục

1. [Monitoring Evolution](#1-monitoring-evolution)
2. [Testing Evolution](#2-testing-evolution)
3. [Architecture Evolution](#3-architecture-evolution)
4. [Cloud Evolution](#4-cloud-evolution)

---

## 1. Monitoring Evolution

### 1.1 Từ Manual Logs đến Observability

**Manual Logging (1990s - 2000s):**
```java
// Manual logging - No structure
public class ProductService {
    public Product createProduct(ProductRequest request) {
        System.out.println("Creating product: " + request.getName());
        
        try {
            Product product = productRepository.save(mapToEntity(request));
            System.out.println("Product created successfully: " + product.getId());
            return mapToResponse(product);
        } catch (Exception e) {
            System.err.println("Error creating product: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
```

**Vấn đề với Manual Logging:**
- **No Structure**: Unstructured log messages
- **No Centralization**: Logs scattered across servers
- **No Search**: Difficult to find specific logs
- **No Analysis**: No insights from logs
- **No Alerting**: No automatic notifications

**Structured Logging (2000s - 2010s):**
```java
// Structured logging with SLF4J + Logback
@Slf4j
public class ProductService {
    public Product createProduct(ProductRequest request) {
        log.info("Creating product - name: {}, price: {}", 
                request.getName(), request.getPrice());
        
        try {
            Product product = productRepository.save(mapToEntity(request));
            log.info("Product created successfully - id: {}, name: {}", 
                    product.getId(), product.getName());
            return mapToResponse(product);
        } catch (Exception e) {
            log.error("Error creating product - name: {}, error: {}", 
                     request.getName(), e.getMessage(), e);
            throw e;
        }
    }
}
```

**Log Aggregation với ELK Stack (2010s):**
```yaml
# logback-spring.xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <timestamp/>
                <logLevel/>
                <loggerName/>
                <message/>
                <mdc/>
                <stackTrace/>
            </providers>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

**ELK Stack Configuration:**
```yaml
# docker-compose.yml
version: '3.8'
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.8.0
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
    ports:
      - "9200:9200"
    volumes:
      - elasticsearch_data:/usr/share/elasticsearch/data

  logstash:
    image: docker.elastic.co/logstash/logstash:8.8.0
    ports:
      - "5000:5000"
    volumes:
      - ./logstash/pipeline:/usr/share/logstash/pipeline
    depends_on:
      - elasticsearch

  kibana:
    image: docker.elastic.co/kibana/kibana:8.8.0
    ports:
      - "5601:5601"
    environment:
      - ELASTICSEARCH_HOSTS=http://elasticsearch:9200
    depends_on:
      - elasticsearch
```

**Logstash Pipeline:**
```ruby
# logstash/pipeline/logstash.conf
input {
  tcp {
    port => 5000
    codec => json
  }
}

filter {
  if [logger_name] == "com.example.springboot" {
    mutate {
      add_field => { "application" => "product-service" }
    }
  }
  
  if [level] == "ERROR" {
    mutate {
      add_tag => ["error"]
    }
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "logs-%{+YYYY.MM.dd}"
  }
}
```

**Observability với OpenTelemetry (2020s):**
```java
// OpenTelemetry Integration
@Configuration
public class ObservabilityConfig {
    
    @Bean
    public OpenTelemetry openTelemetry() {
        return OpenTelemetrySdk.builder()
                .setTracerProvider(SdkTracerProvider.builder()
                        .addSpanProcessor(BatchSpanProcessor.builder(
                                OtlpGrpcSpanExporter.builder()
                                        .setEndpoint("http://jaeger:4317")
                                        .build())
                                .build())
                        .build())
                .setMeterProvider(SdkMeterProvider.builder()
                        .addMetricReader(PeriodicMetricReader.builder(
                                OtlpGrpcMetricExporter.builder()
                                        .setEndpoint("http://prometheus:4317")
                                        .build())
                                .build())
                        .build())
                .build();
    }
}

@Service
@Slf4j
public class ProductService {
    
    private final Tracer tracer;
    private final Meter meter;
    private final Counter productCreatedCounter;
    
    public ProductService(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer("product-service");
        this.meter = openTelemetry.getMeter("product-service");
        this.productCreatedCounter = meter.counterBuilder("product.created")
                .setDescription("Number of products created")
                .build();
    }
    
    public Product createProduct(ProductRequest request) {
        Span span = tracer.spanBuilder("createProduct")
                .setAttribute("product.name", request.getName())
                .setAttribute("product.price", request.getPrice())
                .startSpan();
        
        try (var scope = span.makeCurrent()) {
            log.info("Creating product - name: {}", request.getName());
            
            Product product = productRepository.save(mapToEntity(request));
            
            productCreatedCounter.add(1);
            span.setAttribute("product.id", product.getId());
            
            log.info("Product created successfully - id: {}", product.getId());
            return mapToResponse(product);
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}
```

**Distributed Tracing với Jaeger:**
```yaml
# jaeger.yaml
apiVersion: jaegertracing.io/v1
kind: Jaeger
metadata:
  name: jaeger
spec:
  strategy: allInOne
  storage:
    type: elasticsearch
    options:
      es:
        server-urls: http://elasticsearch:9200
  ingress:
    enabled: true
    hosts:
      - jaeger.local
```

**Metrics với Prometheus:**
```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'spring-boot-app'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
```

**Grafana Dashboard:**
```json
{
  "dashboard": {
    "title": "Product Service Dashboard",
    "panels": [
      {
        "title": "Request Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_server_requests_total[5m])",
            "legendFormat": "{{method}} {{uri}}"
          }
        ]
      },
      {
        "title": "Response Time",
        "type": "graph",
        "targets": [
          {
            "expr": "histogram_quantile(0.95, rate(http_server_requests_duration_seconds_bucket[5m]))",
            "legendFormat": "95th percentile"
          }
        ]
      },
      {
        "title": "Error Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_server_requests_total{status=~\"5..\"}[5m])",
            "legendFormat": "5xx errors"
          }
        ]
      }
    ]
  }
}
```

---

## 2. Testing Evolution

### 2.1 Từ Manual Testing đến AI-Powered Testing

**Manual Testing (1990s - 2000s):**
```java
// Manual testing - No automation
public class ManualTestExample {
    public static void main(String[] args) {
        // Manual test execution
        ProductService service = new ProductService();
        
        // Test case 1: Create product
        ProductRequest request = new ProductRequest("Test Product", 99.99);
        Product product = service.createProduct(request);
        
        if (product.getName().equals("Test Product")) {
            System.out.println("✓ Test passed: Product name is correct");
        } else {
            System.out.println("✗ Test failed: Product name is incorrect");
        }
        
        // Test case 2: Invalid price
        try {
            ProductRequest invalidRequest = new ProductRequest("Test", -10.0);
            service.createProduct(invalidRequest);
            System.out.println("✗ Test failed: Should have thrown exception");
        } catch (Exception e) {
            System.out.println("✓ Test passed: Exception thrown for invalid price");
        }
    }
}
```

**Vấn đề với Manual Testing:**
- **Time-consuming**: Takes hours to run tests
- **Error-prone**: Human errors in test execution
- **Not repeatable**: Inconsistent results
- **No regression**: Can't catch all regressions
- **Expensive**: High cost for manual testers

**Unit Testing với JUnit (2000s):**
```java
// JUnit 4 - Basic unit testing
public class ProductServiceTest {
    
    private ProductService productService;
    private ProductRepository mockRepository;
    
    @Before
    public void setUp() {
        mockRepository = mock(ProductRepository.class);
        productService = new ProductService(mockRepository);
    }
    
    @Test
    public void testCreateProduct_WithValidRequest_ShouldCreateProduct() {
        // Given
        ProductRequest request = new ProductRequest("Test Product", 99.99);
        Product savedProduct = new Product(1L, "Test Product", 99.99);
        
        when(mockRepository.save(any(Product.class))).thenReturn(savedProduct);
        
        // When
        ProductResponse result = productService.createProduct(request);
        
        // Then
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(99.99, result.getPrice(), 0.01);
        verify(mockRepository).save(any(Product.class));
    }
    
    @Test(expected = ValidationException.class)
    public void testCreateProduct_WithInvalidPrice_ShouldThrowException() {
        // Given
        ProductRequest request = new ProductRequest("Test Product", -10.0);
        
        // When & Then
        productService.createProduct(request);
    }
}
```

**JUnit 5 với Modern Features:**
```java
// JUnit 5 - Modern testing
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private MetricsService metricsService;
    
    @InjectMocks
    private ProductService productService;
    
    @Test
    @DisplayName("Should create product when request is valid")
    void createProduct_WithValidRequest_ShouldCreateProduct() {
        // Given
        ProductRequest request = new ProductRequest("Test Product", 99.99);
        Product savedProduct = new Product(1L, "Test Product", 99.99, true);
        
        given(productRepository.save(any(Product.class))).willReturn(savedProduct);
        
        // When
        ProductResponse result = productService.createProduct(request);
        
        // Then
        assertThat(result)
                .isNotNull()
                .satisfies(product -> {
                    assertThat(product.getName()).isEqualTo("Test Product");
                    assertThat(product.getPrice()).isEqualTo(99.99);
                });
        
        then(productRepository).should().save(any(Product.class));
        then(metricsService).should().incrementProductCreated();
    }
    
    @ParameterizedTest
    @ValueSource(doubles = {-10.0, 0.0, -1.0})
    @DisplayName("Should throw exception for invalid prices")
    void createProduct_WithInvalidPrice_ShouldThrowException(double invalidPrice) {
        // Given
        ProductRequest request = new ProductRequest("Test Product", invalidPrice);
        
        // When & Then
        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Price must be positive");
        
        then(productRepository).shouldHaveNoInteractions();
    }
    
    @Test
    @DisplayName("Should handle database errors gracefully")
    void createProduct_WhenDatabaseError_ShouldThrowServiceException() {
        // Given
        ProductRequest request = new ProductRequest("Test Product", 99.99);
        given(productRepository.save(any(Product.class)))
                .willThrow(new DataAccessException("Database error") {});
        
        // When & Then
        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("Failed to create product");
    }
}
```

**Integration Testing với Spring Boot Test:**
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ProductControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Test
    void getProducts_ShouldReturnProducts() {
        // Given
        Product product = new Product("Test Product", "Description", 99.99);
        productRepository.save(product);
        
        // When
        ResponseEntity<ApiResponse> response = restTemplate
                .withBasicAuth("user", "user123")
                .getForEntity("/api/v1/products", ApiResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("success");
    }
    
    @Test
    void createProduct_WithValidRequest_ShouldCreateProduct() {
        // Given
        ProductRequest request = new ProductRequest("New Product", "Description", 149.99);
        
        // When
        ResponseEntity<ApiResponse> response = restTemplate
                .withBasicAuth("admin", "admin123")
                .postForEntity("/api/v1/products", request, ApiResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("success");
        
        // Verify in database
        List<Product> products = productRepository.findByName("New Product");
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getPrice()).isEqualTo(149.99);
    }
}
```

**Test Containers cho Database Testing:**
```java
@Testcontainers
class ProductRepositoryTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Container
    static RedisContainer<?> redis = new RedisContainer<>("redis:7");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }
    
    @Autowired
    private ProductRepository productRepository;
    
    @Test
    void saveProduct_ShouldPersistToDatabase() {
        // Given
        Product product = new Product("Test Product", "Description", 99.99);
        
        // When
        Product savedProduct = productRepository.save(product);
        
        // Then
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Test Product");
        
        // Verify in database
        Product foundProduct = productRepository.findById(savedProduct.getId()).orElse(null);
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("Test Product");
    }
}
```

**Performance Testing với JMeter:**
```xml
<!-- jmeter-test-plan.jmx -->
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2" properties="5.0">
  <hashTree>
    <TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="Product API Performance Test">
      <elementProp name="TestPlan.arguments" elementType="Arguments">
        <collectionProp name="Arguments.arguments"/>
      </elementProp>
      <boolProp name="TestPlan.functional_mode">false</boolProp>
      <stringProp name="TestPlan.user_define_classpath"></stringProp>
    </TestPlan>
    <hashTree>
      <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="Product API Load Test">
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController">
          <boolProp name="LoopController.continue_forever">false</boolProp>
          <stringProp name="LoopController.loops">100</stringProp>
        </elementProp>
        <stringProp name="ThreadGroup.num_threads">10</stringProp>
        <stringProp name="ThreadGroup.ramp_time">10</stringProp>
        <boolProp name="ThreadGroup.scheduler">false</boolProp>
        <stringProp name="ThreadGroup.duration"></stringProp>
        <stringProp name="ThreadGroup.delay"></stringProp>
      </ThreadGroup>
      <hashTree>
        <HTTPSamplerProxy guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="Get Products">
          <elementProp name="HTTPsampler.Arguments" elementType="Arguments">
            <collectionProp name="Arguments.arguments"/>
          </elementProp>
          <stringProp name="HTTPSampler.domain">localhost</stringProp>
          <stringProp name="HTTPSampler.port">8080</stringProp>
          <stringProp name="HTTPSampler.protocol">http</stringProp>
          <stringProp name="HTTPSampler.path">/api/v1/products</stringProp>
          <stringProp name="HTTPSampler.method">GET</stringProp>
          <boolProp name="HTTPSampler.follow_redirects">true</boolProp>
          <boolProp name="HTTPSampler.auto_redirects">false</boolProp>
          <boolProp name="HTTPSampler.use_keepalive">true</boolProp>
          <boolProp name="HTTPSampler.DO_MULTIPART_POST">false</boolProp>
        </HTTPSamplerProxy>
        <hashTree>
          <HeaderManager guiclass="HeaderPanel" testclass="HeaderManager" testname="HTTP Header Manager">
            <collectionProp name="HeaderManager.headers">
              <elementProp name="" elementType="Header">
                <stringProp name="Header.name">Authorization</stringProp>
                <stringProp name="Header.value">Bearer ${__P(jwt_token)}</stringProp>
              </elementProp>
            </collectionProp>
          </HeaderManager>
          <hashTree/>
        </hashTree>
      </hashTree>
    </hashTree>
  </hashTree>
</jmeterTestPlan>
```

**AI-Powered Testing (2020s):**
```java
// AI-powered test generation
@AI_Test
public class AIGeneratedProductServiceTest {
    
    @Test
    @AI_Generated
    public void testEdgeCases() {
        // AI automatically generates edge case tests
        // - Null values
        // - Boundary conditions
        // - Invalid inputs
        // - Race conditions
    }
    
    @Test
    @AI_Mutation
    public void testMutationScenarios() {
        // AI generates mutation tests
        // - Changes in business logic
        // - Different data types
        // - Altered conditions
    }
}
```

---

## 3. Architecture Evolution

### 3.1 Từ Monolith đến Event-Driven Architecture

**Monolithic Architecture (1990s - 2000s):**
```java
// Monolithic application - Everything in one
@SpringBootApplication
public class MonolithicApplication {
    // User management
    // Product management
    // Order management
    // Payment processing
    // Inventory management
    // All in one application
}

@Service
public class OrderService {
    public Order createOrder(OrderRequest request) {
        // Direct method calls - Tight coupling
        User user = userService.getUser(request.getUserId());
        Product product = productService.getProduct(request.getProductId());
        
        // Business logic
        Order order = new Order(user, product, request.getQuantity());
        
        // Direct database calls
        Order savedOrder = orderRepository.save(order);
        
        // Direct external service calls
        paymentService.processPayment(savedOrder);
        inventoryService.updateStock(product.getId(), request.getQuantity());
        emailService.sendOrderConfirmation(savedOrder);
        
        return savedOrder;
    }
}
```

**Vấn đề với Monolith:**
- **Deployment Risk**: One change affects entire system
- **Technology Lock-in**: Hard to change technology stack
- **Scaling Issues**: Must scale entire application
- **Team Coordination**: Multiple teams on same codebase
- **Single Point of Failure**: One failure brings down everything

**Microservices Architecture (2010s):**
```java
// User Service
@SpringBootApplication
public class UserServiceApplication {
    // Only user management
}

@Service
public class UserService {
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}

// Product Service
@SpringBootApplication
public class ProductServiceApplication {
    // Only product management
}

@Service
public class ProductService {
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}

// Order Service
@SpringBootApplication
public class OrderServiceApplication {
    // Only order management
}

@Service
public class OrderService {
    public Order createOrder(OrderRequest request) {
        // Service calls via HTTP/REST
        User user = userClient.getUser(request.getUserId());
        Product product = productClient.getProduct(request.getProductId());
        
        Order order = new Order(user, product, request.getQuantity());
        return orderRepository.save(order);
    }
}
```

**Service Communication Patterns:**
```java
// Synchronous Communication
@Service
public class OrderService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public Order createOrder(OrderRequest request) {
        // Synchronous HTTP calls
        ResponseEntity<User> userResponse = restTemplate.getForEntity(
            "http://user-service/api/users/" + request.getUserId(), 
            User.class
        );
        
        ResponseEntity<Product> productResponse = restTemplate.getForEntity(
            "http://product-service/api/products/" + request.getProductId(), 
            Product.class
        );
        
        User user = userResponse.getBody();
        Product product = productResponse.getBody();
        
        Order order = new Order(user, product, request.getQuantity());
        return orderRepository.save(order);
    }
}

// Asynchronous Communication with Message Queue
@Service
public class OrderService {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public Order createOrder(OrderRequest request) {
        Order order = new Order(request.getUserId(), request.getProductId(), request.getQuantity());
        Order savedOrder = orderRepository.save(order);
        
        // Publish event asynchronously
        OrderCreatedEvent event = new OrderCreatedEvent(savedOrder.getId(), savedOrder.getUserId());
        rabbitTemplate.convertAndSend("order.exchange", "order.created", event);
        
        return savedOrder;
    }
}

@Component
public class OrderEventHandler {
    
    @RabbitListener(queues = "inventory.queue")
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Update inventory asynchronously
        inventoryService.updateStock(event.getProductId(), event.getQuantity());
    }
    
    @RabbitListener(queues = "payment.queue")
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Process payment asynchronously
        paymentService.processPayment(event.getOrderId());
    }
}
```

**Event-Driven Architecture (2020s):**
```java
// Event Sourcing
@Entity
public class OrderEvent {
    @Id
    private String eventId;
    private String orderId;
    private String eventType;
    private String eventData;
    private LocalDateTime timestamp;
    private Long version;
}

@Service
public class OrderEventStore {
    
    public void saveEvent(String orderId, String eventType, String eventData) {
        OrderEvent event = new OrderEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setOrderId(orderId);
        event.setEventType(eventType);
        event.setEventData(eventData);
        event.setTimestamp(LocalDateTime.now());
        event.setVersion(getNextVersion(orderId));
        
        eventRepository.save(event);
        
        // Publish to event bus
        eventBus.publish(new EventPublishedEvent(event));
    }
    
    public List<OrderEvent> getEvents(String orderId) {
        return eventRepository.findByOrderIdOrderByVersion(orderId);
    }
    
    public Order reconstructOrder(String orderId) {
        List<OrderEvent> events = getEvents(orderId);
        Order order = new Order();
        
        for (OrderEvent event : events) {
            applyEvent(order, event);
        }
        
        return order;
    }
}

// CQRS (Command Query Responsibility Segregation)
@Service
public class OrderCommandService {
    
    public String createOrder(CreateOrderCommand command) {
        String orderId = UUID.randomUUID().toString();
        
        OrderCreatedEvent event = new OrderCreatedEvent(
            orderId, 
            command.getUserId(), 
            command.getProductId(), 
            command.getQuantity()
        );
        
        eventStore.saveEvent(orderId, "OrderCreated", serialize(event));
        return orderId;
    }
    
    public void updateOrderStatus(String orderId, UpdateOrderStatusCommand command) {
        OrderStatusUpdatedEvent event = new OrderStatusUpdatedEvent(
            orderId, 
            command.getStatus()
        );
        
        eventStore.saveEvent(orderId, "OrderStatusUpdated", serialize(event));
    }
}

@Service
public class OrderQueryService {
    
    public OrderDTO getOrder(String orderId) {
        return orderReadRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
    
    public List<OrderDTO> getOrdersByUser(String userId) {
        return orderReadRepository.findByUserId(userId);
    }
    
    public OrderStatistics getOrderStatistics() {
        return orderReadRepository.getStatistics();
    }
}

// Event Handlers for Read Model
@Component
public class OrderEventHandler {
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(event.getOrderId());
        orderDTO.setUserId(event.getUserId());
        orderDTO.setProductId(event.getProductId());
        orderDTO.setQuantity(event.getQuantity());
        orderDTO.setStatus("CREATED");
        orderDTO.setCreatedAt(LocalDateTime.now());
        
        orderReadRepository.save(orderDTO);
    }
    
    @EventListener
    public void handleOrderStatusUpdated(OrderStatusUpdatedEvent event) {
        OrderDTO orderDTO = orderReadRepository.findById(event.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(event.getOrderId()));
        
        orderDTO.setStatus(event.getStatus());
        orderDTO.setUpdatedAt(LocalDateTime.now());
        
        orderReadRepository.save(orderDTO);
    }
}
```

**Serverless Architecture (2015 - Present):**
```java
// AWS Lambda Function
public class OrderFunction {
    
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request) {
        try {
            String orderId = request.getPathParameters().get("orderId");
            
            switch (request.getHttpMethod()) {
                case "GET":
                    return getOrder(orderId);
                case "POST":
                    return createOrder(request.getBody());
                case "PUT":
                    return updateOrder(orderId, request.getBody());
                case "DELETE":
                    return deleteOrder(orderId);
                default:
                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(405)
                            .withBody("Method not allowed");
            }
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody("Internal server error: " + e.getMessage());
        }
    }
    
    private APIGatewayProxyResponseEvent getOrder(String orderId) {
        Order order = orderService.getOrder(orderId);
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(JsonUtils.toJson(order));
    }
    
    private APIGatewayProxyResponseEvent createOrder(String body) {
        CreateOrderRequest request = JsonUtils.fromJson(body, CreateOrderRequest.class);
        Order order = orderService.createOrder(request);
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(201)
                .withBody(JsonUtils.toJson(order));
    }
}
```

---

## 4. Cloud Evolution

### 4.1 Từ On-premise đến Multi-cloud

**On-premise Infrastructure (1990s - 2000s):**
```bash
# Manual server setup
# Physical hardware management
sudo apt-get update
sudo apt-get install openjdk-11-jdk
sudo apt-get install postgresql
sudo apt-get install nginx

# Manual configuration
sudo nano /etc/postgresql/15/main/postgresql.conf
sudo nano /etc/nginx/sites-available/myapp

# Manual deployment
scp target/myapp.jar server:/opt/myapp/
ssh server "sudo systemctl restart myapp"

# Manual monitoring
tail -f /var/log/myapp/application.log
htop
df -h
```

**Vấn đề với On-premise:**
- **High Capital Cost**: Expensive hardware
- **Manual Management**: Time-consuming maintenance
- **Limited Scalability**: Physical constraints
- **Disaster Recovery**: Complex backup strategies
- **Security**: Manual security updates

**IaaS (Infrastructure as a Service) - 2006:**
```yaml
# AWS EC2 with CloudFormation
AWSTemplateFormatVersion: '2010-09-09'
Description: 'Product Service Infrastructure'

Resources:
  EC2Instance:
    Type: AWS::EC2::Instance
    Properties:
      ImageId: ami-0c02fb55956c7d316
      InstanceType: t3.medium
      KeyName: my-key-pair
      SecurityGroups:
        - !Ref SecurityGroup
      UserData:
        Fn::Base64: !Sub |
          #!/bin/bash
          yum update -y
          yum install -y java-11-openjdk
          aws s3 cp s3://my-bucket/myapp.jar /opt/myapp.jar
          java -jar /opt/myapp.jar

  SecurityGroup:
    Type: AWS::EC2::SecurityGroup
    Properties:
      GroupDescription: Allow HTTP and SSH
      SecurityGroupIngress:
        - IpProtocol: tcp
          FromPort: 22
          ToPort: 22
          CidrIp: 0.0.0.0/0
        - IpProtocol: tcp
          FromPort: 8080
          ToPort: 8080
          CidrIp: 0.0.0.0/0

  RDSInstance:
    Type: AWS::RDS::DBInstance
    Properties:
      DBInstanceClass: db.t3.micro
      Engine: postgres
      EngineVersion: '15.4'
      AllocatedStorage: '20'
      DBName: myappdb
      MasterUsername: admin
      MasterUserPassword: !Ref DBPassword
      VPCSecurityGroups:
        - !Ref DBSecurityGroup

Outputs:
  InstancePublicDNS:
    Description: Public DNSName of the instance
    Value: !GetAtt EC2Instance.PublicDnsName
```

**PaaS (Platform as a Service) - 2011:**
```yaml
# Heroku app.json
{
  "name": "product-service",
  "description": "Product management service",
  "repository": "https://github.com/mycompany/product-service",
  "logo": "https://node-js-sample.herokuapp.com/node.png",
  "keywords": ["java", "spring-boot"],
  "addons": [
    "heroku-postgresql:mini",
    "heroku-redis:mini"
  ],
  "env": {
    "SPRING_PROFILES_ACTIVE": {
      "value": "production"
    },
    "JWT_SECRET": {
      "generator": "secret"
    }
  },
  "formation": {
    "web": {
      "quantity": 1,
      "size": "basic"
    }
  },
  "buildpacks": [
    {
      "url": "heroku/java"
    }
  ]
}
```

**FaaS (Function as a Service) - 2014:**
```java
// AWS Lambda with Spring Cloud Function
@SpringBootApplication
public class ProductFunctionApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ProductFunctionApplication.class, args);
    }
    
    @Bean
    public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> getProduct() {
        return request -> {
            String productId = request.getPathParameters().get("productId");
            Product product = productService.getProduct(productId);
            
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(JsonUtils.toJson(product));
        };
    }
    
    @Bean
    public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> createProduct() {
        return request -> {
            CreateProductRequest createRequest = JsonUtils.fromJson(
                request.getBody(), 
                CreateProductRequest.class
            );
            Product product = productService.createProduct(createRequest);
            
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(201)
                    .withBody(JsonUtils.toJson(product));
        };
    }
}
```

**Multi-cloud Strategy (2020s):**
```yaml
# Terraform multi-cloud configuration
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    google = {
      source  = "hashicorp/google"
      version = "~> 4.0"
    }
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.0"
    }
  }
}

# AWS Resources
provider "aws" {
  region = var.aws_region
}

resource "aws_eks_cluster" "main" {
  name     = "${var.project_name}-aws-cluster"
  role_arn = aws_iam_role.eks_cluster.arn
  version  = "1.28"
  
  vpc_config {
    subnet_ids = var.aws_subnet_ids
  }
}

# Google Cloud Resources
provider "google" {
  project = var.gcp_project
  region  = var.gcp_region
}

resource "google_container_cluster" "main" {
  name     = "${var.project_name}-gcp-cluster"
  location = var.gcp_region
  
  node_pool {
    name       = "default-pool"
    node_count = 3
    
    node_config {
      machine_type = "e2-medium"
      disk_size_gb = 20
    }
  }
}

# Azure Resources
provider "azurerm" {
  features {}
}

resource "azurerm_kubernetes_cluster" "main" {
  name                = "${var.project_name}-azure-cluster"
  location            = var.azure_location
  resource_group_name = azurerm_resource_group.main.name
  dns_prefix          = "${var.project_name}-cluster"
  
  default_node_pool {
    name       = "default"
    node_count = 3
    vm_size    = "Standard_D2s_v3"
  }
}
```

**Kubernetes Multi-cloud Deployment:**
```yaml
# Multi-cloud Kubernetes deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product-service
  labels:
    app: product-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: product-service
  template:
    metadata:
      labels:
        app: product-service
    spec:
      containers:
      - name: product-service
        image: myregistry.com/product-service:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: url
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
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

---
apiVersion: v1
kind: Service
metadata:
  name: product-service
spec:
  selector:
    app: product-service
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer

---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: product-service-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: product-service
  minReplicas: 3
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

---

## 🎯 Kết luận

### Evolution Summary:

| Technology | Past | Present | Future |
|------------|------|---------|--------|
| **Monitoring** | Manual Logs | ELK Stack | Observability |
| **Testing** | Manual Testing | Automated Testing | AI-Powered Testing |
| **Architecture** | Monolith | Microservices | Event-Driven |
| **Cloud** | On-premise | Multi-cloud | Edge Computing |

### Key Trends:

1. **Observability**: From logs to traces to metrics
2. **Automation**: From manual to automated to AI-powered
3. **Decentralization**: From centralized to distributed systems
4. **Cloud-native**: From on-premise to multi-cloud
5. **Event-driven**: From request-response to event-driven

### Tài liệu tham khảo:

1. **Monitoring**: 
   - [OpenTelemetry Documentation](https://opentelemetry.io/docs/)
   - [ELK Stack Guide](https://www.elastic.co/guide/)
   - [Prometheus Documentation](https://prometheus.io/docs/)

2. **Testing**:
   - [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
   - [TestContainers Documentation](https://www.testcontainers.org/)
   - [JMeter User Manual](https://jmeter.apache.org/usermanual/)

3. **Architecture**:
   - [Event Sourcing Pattern](https://martinfowler.com/eaaDev/EventSourcing.html)
   - [CQRS Pattern](https://martinfowler.com/bliki/CQRS.html)
   - [Serverless Architecture](https://aws.amazon.com/serverless/)

4. **Cloud**:
   - [AWS Documentation](https://docs.aws.amazon.com/)
   - [Google Cloud Documentation](https://cloud.google.com/docs/)
   - [Azure Documentation](https://docs.microsoft.com/azure/)

---

**Remember: Technology evolution is driven by real-world problems. Stay updated with the latest trends! 🚀** 