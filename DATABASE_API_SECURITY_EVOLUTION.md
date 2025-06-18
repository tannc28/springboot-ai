# 🚀 Database, API, Security & Deployment Evolution

> **Hướng dẫn sâu về evolution của các công nghệ cốt lõi trong enterprise development**

---

## 📋 Mục lục

1. [Database Evolution](#1-database-evolution)
2. [API Evolution](#2-api-evolution)
3. [Security Evolution](#3-security-evolution)
4. [Deployment Evolution](#4-deployment-evolution)

---

## 1. Database Evolution

### 1.1 Từ SQL đến NoSQL đến NewSQL

**Relational Databases (1970s - 2000s):**
```sql
-- Traditional SQL - ACID Properties
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    total_amount DECIMAL(10,2),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Complex JOINs
SELECT u.username, o.total_amount, o.status
FROM users u
JOIN orders o ON u.id = o.user_id
WHERE o.created_at > '2024-01-01'
ORDER BY o.total_amount DESC;
```

**Vấn đề với RDBMS:**
- **Scalability Issues**: Khó scale horizontally
- **Schema Rigidity**: Schema changes require migrations
- **Performance**: Complex JOINs become slow with large data
- **Cost**: Expensive for high-volume data

**NoSQL Revolution (2000s - 2010s):**

**Document Database (MongoDB):**
```javascript
// MongoDB - Flexible Schema
db.users.insertOne({
    _id: ObjectId("..."),
    username: "john_doe",
    email: "john@example.com",
    profile: {
        firstName: "John",
        lastName: "Doe",
        age: 30,
        interests: ["coding", "reading"]
    },
    orders: [
        {
            orderId: "ORD001",
            amount: 99.99,
            status: "completed",
            items: ["laptop", "mouse"]
        }
    ],
    createdAt: new Date()
});

// No JOINs needed - Data is denormalized
db.users.find({
    "orders.status": "completed",
    "orders.amount": { $gt: 50 }
});
```

**Key-Value Store (Redis):**
```java
// Redis - Ultra-fast caching
@Service
public class CacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    
    public void cacheUser(Long userId, User user) {
        redisTemplate.opsForValue().set("user:" + userId, user, Duration.ofHours(1));
    }
    
    public User getUser(Long userId) {
        return (User) redisTemplate.opsForValue().get("user:" + userId);
    }
    
    // Complex data structures
    public void addToLeaderboard(String game, String player, double score) {
        redisTemplate.opsForZSet().add("leaderboard:" + game, player, score);
    }
    
    public List<String> getTopPlayers(String game, int count) {
        return redisTemplate.opsForZSet().reverseRange("leaderboard:" + game, 0, count - 1);
    }
}
```

**Column-Family Store (Cassandra):**
```sql
-- Cassandra - Wide-column store
CREATE TABLE user_orders (
    user_id uuid,
    order_date date,
    order_id uuid,
    product_name text,
    quantity int,
    price decimal,
    PRIMARY KEY (user_id, order_date, order_id)
);

-- Efficient queries by partition key
SELECT * FROM user_orders 
WHERE user_id = 123e4567-e89b-12d3-a456-426614174000 
AND order_date = '2024-01-15';
```

**NewSQL (2010s - Present):**

**CockroachDB - Distributed SQL:**
```sql
-- CockroachDB - ACID + Scalability
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username STRING UNIQUE NOT NULL,
    email STRING UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT now()
);

-- Automatic sharding and replication
-- ACID transactions across distributed nodes
BEGIN;
INSERT INTO users (username, email) VALUES ('john', 'john@example.com');
INSERT INTO orders (user_id, amount) VALUES (last_insert_id(), 99.99);
COMMIT;
```

**TiDB - MySQL Compatible:**
```sql
-- TiDB - MySQL compatible + horizontal scaling
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) SHARD_ROW_ID_BITS = 4; -- Automatic sharding

-- MySQL compatible syntax
SELECT u.username, COUNT(o.id) as order_count
FROM users u
LEFT JOIN orders o ON u.id = o.user_id
GROUP BY u.id, u.username
HAVING order_count > 5;
```

### 1.2 Database Migration Evolution

**Trước Flyway - Manual Migrations:**
```bash
# Manual SQL execution - Error-prone
ssh production-server
psql -d myapp -c "ALTER TABLE users ADD COLUMN phone VARCHAR(20);"
psql -d myapp -c "UPDATE users SET phone = 'N/A' WHERE phone IS NULL;"
psql -d myapp -c "ALTER TABLE users ALTER COLUMN phone SET NOT NULL;"

# Vấn đề:
# - Không track được changes
# - Khó rollback
# - Environment differences
# - Manual process error-prone
```

**Với Flyway - Automated Migrations:**
```sql
-- V1__Create_users_table.sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- V2__Add_phone_to_users.sql
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
UPDATE users SET phone = 'N/A' WHERE phone IS NULL;
ALTER TABLE users ALTER COLUMN phone SET NOT NULL;

-- V3__Add_indexes.sql
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at);
```

**Liquibase - XML-based Migrations:**
```xml
<!-- changelog.xml -->
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog">
    <changeSet id="1" author="developer">
        <createTable tableName="users">
            <column name="id" type="bigint" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="username" type="varchar(255)">
                <constraints unique="true" nullable="false"/>
            </column>
            <column name="email" type="varchar(255)">
                <constraints unique="true" nullable="false"/>
            </column>
        </createTable>
    </changeSet>
    
    <changeSet id="2" author="developer">
        <addColumn tableName="users">
            <column name="phone" type="varchar(20)"/>
        </addColumn>
    </changeSet>
</databaseChangeLog>
```

---

## 2. API Evolution

### 2.1 Từ SOAP đến REST đến GraphQL

**SOAP (2000s) - XML-based:**
```xml
<!-- SOAP Request -->
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Body>
        <getUser xmlns="http://example.com/user">
            <userId>123</userId>
        </getUser>
    </soap:Body>
</soap:Envelope>

<!-- SOAP Response -->
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Body>
        <getUserResponse xmlns="http://example.com/user">
            <user>
                <id>123</id>
                <username>john_doe</username>
                <email>john@example.com</email>
                <profile>
                    <firstName>John</firstName>
                    <lastName>Doe</lastName>
                </profile>
            </user>
        </getUserResponse>
    </soap:Body>
</soap:Envelope>
```

**Vấn đề với SOAP:**
- **Verbose**: XML overhead
- **Complex**: WSDL, XSD schemas
- **Heavy**: Large payloads
- **Browser Support**: Limited browser support

**REST (2000s - Present) - HTTP-based:**
```java
// REST API with Spring Boot
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        return ResponseEntity.ok(mapToResponse(user));
    }
    
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                           .body(mapToResponse(user));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, 
                                                  @Valid @RequestBody UpdateUserRequest request) {
        User user = userService.updateUser(id, request);
        return ResponseEntity.ok(mapToResponse(user));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
```

**REST API Documentation với OpenAPI:**
```yaml
# openapi.yaml
openapi: 3.0.0
info:
  title: User Management API
  version: 1.0.0
  description: API for managing users

paths:
  /api/v1/users/{id}:
    get:
      summary: Get user by ID
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: User found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserResponse'
        '404':
          description: User not found

components:
  schemas:
    UserResponse:
      type: object
      properties:
        id:
          type: integer
        username:
          type: string
        email:
          type: string
```

**GraphQL (2015 - Present) - Query Language:**
```graphql
# GraphQL Schema
type User {
  id: ID!
  username: String!
  email: String!
  profile: Profile
  orders: [Order!]!
}

type Profile {
  firstName: String
  lastName: String
  age: Int
}

type Order {
  id: ID!
  amount: Float!
  status: String!
  items: [String!]!
}

type Query {
  user(id: ID!): User
  users(limit: Int, offset: Int): [User!]!
}

type Mutation {
  createUser(input: CreateUserInput!): User!
  updateUser(id: ID!, input: UpdateUserInput!): User!
  deleteUser(id: ID!): Boolean!
}
```

**GraphQL Implementation với Spring Boot:**
```java
@Component
public class UserResolver implements GraphQLResolver<User> {
    
    public List<Order> getOrders(User user, DataFetchingEnvironment env) {
        // Only fetch orders if requested
        if (env.getSelectionSet().contains("orders")) {
            return orderService.getOrdersByUserId(user.getId());
        }
        return Collections.emptyList();
    }
}

@Component
public class UserQuery implements GraphQLQueryResolver {
    
    public User getUser(String id) {
        return userService.getUser(Long.valueOf(id));
    }
    
    public List<User> getUsers(Integer limit, Integer offset) {
        return userService.getUsers(limit, offset);
    }
}

@Component
public class UserMutation implements GraphQLMutationResolver {
    
    public User createUser(CreateUserInput input) {
        return userService.createUser(input);
    }
    
    public User updateUser(String id, UpdateUserInput input) {
        return userService.updateUser(Long.valueOf(id), input);
    }
    
    public Boolean deleteUser(String id) {
        userService.deleteUser(Long.valueOf(id));
        return true;
    }
}
```

**gRPC (2015 - Present) - Protocol Buffers:**
```protobuf
// user.proto
syntax = "proto3";

package com.example.user;

service UserService {
  rpc GetUser(GetUserRequest) returns (UserResponse);
  rpc CreateUser(CreateUserRequest) returns (UserResponse);
  rpc UpdateUser(UpdateUserRequest) returns (UserResponse);
  rpc DeleteUser(DeleteUserRequest) returns (DeleteUserResponse);
  rpc StreamUsers(StreamUsersRequest) returns (stream UserResponse);
}

message GetUserRequest {
  int64 id = 1;
}

message UserResponse {
  int64 id = 1;
  string username = 2;
  string email = 3;
  Profile profile = 4;
}

message Profile {
  string first_name = 1;
  string last_name = 2;
  int32 age = 3;
}
```

**gRPC Implementation:**
```java
@Service
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {
    
    @Override
    public void getUser(GetUserRequest request, 
                       StreamObserver<UserResponse> responseObserver) {
        try {
            User user = userService.getUser(request.getId());
            UserResponse response = UserResponse.newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setProfile(Profile.newBuilder()
                    .setFirstName(user.getProfile().getFirstName())
                    .setLastName(user.getProfile().getLastName())
                    .setAge(user.getProfile().getAge())
                    .build())
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error fetching user")
                .withCause(e)
                .asRuntimeException());
        }
    }
    
    @Override
    public void streamUsers(StreamUsersRequest request, 
                           StreamObserver<UserResponse> responseObserver) {
        try {
            List<User> users = userService.getUsers(request.getLimit(), 0);
            for (User user : users) {
                UserResponse response = mapToUserResponse(user);
                responseObserver.onNext(response);
                Thread.sleep(100); // Simulate processing time
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                .withDescription("Error streaming users")
                .withCause(e)
                .asRuntimeException());
        }
    }
}
```

---

## 3. Security Evolution

### 3.1 Từ Basic Auth đến OAuth 2.0 đến OIDC

**Basic Authentication (1990s):**
```java
// Basic Auth - Username/Password in Header
@RestController
public class BasicAuthController {
    
    @GetMapping("/api/secure")
    public ResponseEntity<String> secureEndpoint(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String credentials = new String(Base64.getDecoder()
                .decode(authHeader.substring(6)));
            String[] parts = credentials.split(":");
            
            if (authenticateUser(parts[0], parts[1])) {
                return ResponseEntity.ok("Access granted");
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                           .header("WWW-Authenticate", "Basic realm=\"Secure Area\"")
                           .build();
    }
}
```

**Vấn đề với Basic Auth:**
- **Credentials in Header**: Password sent with every request
- **No Expiration**: Credentials never expire
- **No Scopes**: All-or-nothing access
- **No Third-party**: Can't delegate access

**Session-based Authentication (2000s):**
```java
// Session-based Auth
@RestController
public class SessionAuthController {
    
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request, 
                                       HttpSession session) {
        if (authenticateUser(request.getUsername(), request.getPassword())) {
            session.setAttribute("user", getUser(request.getUsername()));
            session.setAttribute("authenticated", true);
            return ResponseEntity.ok("Login successful");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    
    @GetMapping("/api/secure")
    public ResponseEntity<String> secureEndpoint(HttpSession session) {
        if (session.getAttribute("authenticated") != null) {
            User user = (User) session.getAttribute("user");
            return ResponseEntity.ok("Hello " + user.getUsername());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
```

**Vấn đề với Session-based Auth:**
- **Server-side Storage**: Sessions stored in server memory
- **Scalability Issues**: Doesn't work with multiple servers
- **Stateful**: Server maintains state
- **Mobile Issues**: Not suitable for mobile apps

**JWT (JSON Web Tokens) - 2015:**
```java
// JWT Implementation
@Service
public class JwtService {
    
    private final String secret = "your-secret-key";
    private final int expiration = 86400000; // 24 hours
    
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
```

**OAuth 2.0 (2012) - Authorization Framework:**
```java
// OAuth 2.0 Implementation
@Configuration
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {
    
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("web-app")
                .secret(passwordEncoder.encode("secret"))
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("read", "write")
                .accessTokenValiditySeconds(3600)
                .refreshTokenValiditySeconds(86400);
    }
    
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
    }
}

@RestController
public class OAuth2Controller {
    
    @PostMapping("/oauth/token")
    public ResponseEntity<OAuth2AccessToken> getToken(@RequestBody TokenRequest request) {
        // OAuth 2.0 token endpoint
        // Handles password grant, client credentials, etc.
    }
    
    @GetMapping("/api/secure")
    @PreAuthorize("hasScope('read')")
    public ResponseEntity<String> secureEndpoint() {
        return ResponseEntity.ok("OAuth 2.0 protected resource");
    }
}
```

**OpenID Connect (2014) - Identity Layer:**
```java
// OIDC Implementation
@Configuration
@EnableWebSecurity
public class OidcConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.oauth2Login()
                .userInfoEndpoint()
                .userService(oidcUserService())
                .and()
                .authorizationEndpoint()
                .authorizationRequestResolver(authorizationRequestResolver());
    }
    
    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        return userRequest -> {
            OidcUser oidcUser = OidcUserService.loadUser(userRequest);
            
            // Extract claims from ID token
            Map<String, Object> claims = oidcUser.getClaims();
            String email = (String) claims.get("email");
            String name = (String) claims.get("name");
            
            return oidcUser;
        };
    }
}

@RestController
public class OidcController {
    
    @GetMapping("/api/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal OidcUser user) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getSubject());
        profile.put("email", user.getEmail());
        profile.put("name", user.getName());
        profile.put("picture", user.getPicture());
        
        return ResponseEntity.ok(profile);
    }
}
```

### 3.2 Zero Trust Security

**Traditional Security Model:**
```java
// Trust-based security
@Component
public class TraditionalSecurity {
    
    public boolean isAuthorized(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        
        // Trust internal network
        if (isInternalNetwork(clientIp)) {
            return true; // Trusted by default
        }
        
        // Check credentials for external requests
        return validateCredentials(request);
    }
}
```

**Zero Trust Security Model:**
```java
// Zero Trust - Never trust, always verify
@Component
public class ZeroTrustSecurity {
    
    public boolean isAuthorized(HttpServletRequest request, 
                               Authentication authentication) {
        // Always verify identity
        if (!isIdentityValid(authentication)) {
            return false;
        }
        
        // Always verify device
        if (!isDeviceCompliant(request)) {
            return false;
        }
        
        // Always verify network
        if (!isNetworkSecure(request)) {
            return false;
        }
        
        // Always verify access
        if (!hasRequiredPermissions(authentication, request)) {
            return false;
        }
        
        // Always verify behavior
        if (!isBehaviorNormal(authentication, request)) {
            return false;
        }
        
        return true;
    }
    
    private boolean isBehaviorNormal(Authentication auth, HttpServletRequest request) {
        // AI/ML-based behavior analysis
        return behaviorAnalyzer.analyze(auth, request);
    }
}
```

---

## 4. Deployment Evolution

### 4.1 Từ Manual Deployment đến GitOps

**Manual Deployment (1990s - 2000s):**
```bash
# Manual deployment script
#!/bin/bash

echo "Starting deployment..."

# Stop application
ssh production-server "sudo systemctl stop myapp"

# Backup current version
ssh production-server "cp /opt/myapp/myapp.jar /opt/myapp/myapp.jar.backup.$(date +%Y%m%d_%H%M%S)"

# Upload new version
scp target/myapp.jar production-server:/opt/myapp/

# Update configuration
ssh production-server "cp config/production.properties /opt/myapp/"

# Start application
ssh production-server "sudo systemctl start myapp"

# Check if application is running
sleep 30
if curl -f http://production-server:8080/health; then
    echo "Deployment successful"
else
    echo "Deployment failed, rolling back..."
    ssh production-server "cp /opt/myapp/myapp.jar.backup.* /opt/myapp/myapp.jar"
    ssh production-server "sudo systemctl restart myapp"
fi
```

**Vấn đề với Manual Deployment:**
- **Error-prone**: Human errors
- **Inconsistent**: Different environments
- **Slow**: Time-consuming process
- **No Rollback**: Difficult to rollback
- **No Audit**: No deployment history

**CI/CD Pipeline (2010s):**
```yaml
# Jenkins Pipeline
pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        
        stage('SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar'
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t myapp:$BUILD_NUMBER .'
            }
        }
        
        stage('Push to Registry') {
            steps {
                sh 'docker push myregistry.com/myapp:$BUILD_NUMBER'
            }
        }
        
        stage('Deploy to Staging') {
            steps {
                sh 'kubectl set image deployment/myapp myapp=myregistry.com/myapp:$BUILD_NUMBER -n staging'
            }
        }
        
        stage('Integration Tests') {
            steps {
                sh 'mvn verify -Dspring.profiles.active=staging'
            }
        }
        
        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                sh 'kubectl set image deployment/myapp myapp=myregistry.com/myapp:$BUILD_NUMBER -n production'
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        failure {
            // Send notification
            emailext (
                subject: "Deployment Failed: ${env.JOB_NAME}",
                body: "Build ${env.BUILD_NUMBER} failed. Check console output.",
                to: 'team@company.com'
            )
        }
    }
}
```

**GitOps (2018 - Present):**
```yaml
# ArgoCD Application
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: myapp
  namespace: argocd
spec:
  project: default
  source:
    repoURL: https://github.com/mycompany/myapp-k8s
    targetRevision: HEAD
    path: k8s/production
  destination:
    server: https://kubernetes.default.svc
    namespace: production
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
    syncOptions:
    - CreateNamespace=true
    - PrunePropagationPolicy=foreground
    - PruneLast=true
```

**Kubernetes Manifests (Git Repository):**
```yaml
# k8s/production/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
  namespace: production
spec:
  replicas: 3
  selector:
    matchLabels:
      app: myapp
  template:
    metadata:
      labels:
        app: myapp
    spec:
      containers:
      - name: myapp
        image: myregistry.com/myapp:latest
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
  name: myapp-service
  namespace: production
spec:
  selector:
    app: myapp
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer

---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: myapp-hpa
  namespace: production
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: myapp
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

**Infrastructure as Code (Terraform):**
```hcl
# infrastructure/main.tf
terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# EKS Cluster
resource "aws_eks_cluster" "main" {
  name     = "${var.project_name}-cluster"
  role_arn = aws_iam_role.eks_cluster.arn
  version  = "1.28"

  vpc_config {
    subnet_ids = var.subnet_ids
  }

  depends_on = [
    aws_iam_role_policy_attachment.eks_cluster_policy
  ]
}

# EKS Node Group
resource "aws_eks_node_group" "main" {
  cluster_name    = aws_eks_cluster.main.name
  node_group_name = "${var.project_name}-node-group"
  node_role_arn   = aws_iam_role.eks_node_group.arn
  subnet_ids      = var.subnet_ids

  scaling_config {
    desired_size = 3
    max_size     = 5
    min_size     = 1
  }

  instance_types = ["t3.medium"]

  depends_on = [
    aws_iam_role_policy_attachment.eks_node_group_policy
  ]
}

# RDS Database
resource "aws_db_instance" "main" {
  identifier           = "${var.project_name}-db"
  engine               = "postgres"
  engine_version       = "15.4"
  instance_class       = "db.t3.micro"
  allocated_storage    = 20
  storage_encrypted    = true
  db_name              = "myappdb"
  username             = var.db_username
  password             = var.db_password
  skip_final_snapshot  = true

  vpc_security_group_ids = [aws_security_group.rds.id]
  db_subnet_group_name   = aws_db_subnet_group.main.name
}

# ElastiCache Redis
resource "aws_elasticache_cluster" "main" {
  cluster_id           = "${var.project_name}-redis"
  engine               = "redis"
  node_type            = "cache.t3.micro"
  num_cache_nodes      = 1
  parameter_group_name = "default.redis7"
  port                 = 6379
  security_group_ids   = [aws_security_group.redis.id]
  subnet_group_name    = aws_elasticache_subnet_group.main.name
}
```

---

## 🎯 Kết luận

### Evolution Summary:

| Technology | Past | Present | Future |
|------------|------|---------|--------|
| **Database** | RDBMS | NoSQL + NewSQL | Distributed SQL |
| **API** | SOAP | REST + GraphQL | gRPC + Async APIs |
| **Security** | Basic Auth | OAuth 2.0 + OIDC | Zero Trust |
| **Deployment** | Manual | CI/CD | GitOps + IaC |

### Key Trends:

1. **Decentralization**: From centralized to distributed systems
2. **Automation**: From manual to automated processes
3. **Security**: From perimeter-based to zero trust
4. **Scalability**: From vertical to horizontal scaling
5. **Developer Experience**: From complex to simple tools

### Tài liệu tham khảo:

1. **Database Evolution**: 
   - [MongoDB Documentation](https://docs.mongodb.com/)
   - [CockroachDB Documentation](https://www.cockroachlabs.com/docs/)
   - [TiDB Documentation](https://docs.pingcap.com/)

2. **API Evolution**:
   - [GraphQL Documentation](https://graphql.org/)
   - [gRPC Documentation](https://grpc.io/docs/)
   - [OpenAPI Specification](https://swagger.io/specification/)

3. **Security Evolution**:
   - [OAuth 2.0 RFC](https://tools.ietf.org/html/rfc6749)
   - [OpenID Connect](https://openid.net/connect/)
   - [Zero Trust Architecture](https://www.nist.gov/publications/zero-trust-architecture)

4. **Deployment Evolution**:
   - [GitOps Principles](https://www.gitops.tech/)
   - [ArgoCD Documentation](https://argo-cd.readthedocs.io/)
   - [Terraform Documentation](https://www.terraform.io/docs/)

---

**Remember: Technology evolution is driven by real-world problems. Stay updated with the latest trends! 🚀** 