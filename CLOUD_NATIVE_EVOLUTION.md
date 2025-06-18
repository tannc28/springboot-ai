# ☁️ Cloud Native Evolution

> **Hướng dẫn sâu về evolution của Cloud Native và Microservices architecture**

---

## 📋 Mục lục

1. [Cloud Native Evolution](#1-cloud-native-evolution)
2. [Microservices Evolution](#2-microservices-evolution)
3. [Service Mesh Evolution](#3-service-mesh-evolution)
4. [Event-Driven Architecture](#4-event-driven-architecture)

---

## 1. Cloud Native Evolution

### 1.1 Từ On-Premise đến Cloud Native

**On-Premise Infrastructure (1990s - 2000s):**
```bash
# Physical server management
# Server Room Setup
# - Power: UPS systems, backup generators
# - Cooling: HVAC systems
# - Security: Access control, CCTV
# - Network: Switches, routers, firewalls

# Manual server provisioning
sudo apt-get update
sudo apt-get install -y openjdk-11-jdk postgresql nginx
sudo systemctl enable postgresql
sudo systemctl start postgresql

# Manual scaling
# Add new server
sudo apt-get install -y openjdk-11-jdk
sudo cp /opt/myapp/myapp.jar /opt/myapp/
sudo systemctl start myapp

# Load balancer configuration
sudo nano /etc/nginx/sites-available/load-balancer
upstream backend {
    server 192.168.1.10:8080;
    server 192.168.1.11:8080;
    server 192.168.1.12:8080;
}
```

**Vấn đề với On-Premise:**
- **High Capital Cost**: Expensive hardware investment
- **Manual Management**: Time-consuming maintenance
- **Limited Scalability**: Physical constraints
- **Disaster Recovery**: Complex backup strategies
- **Security**: Manual security updates

**Virtualization (2000s - 2010s):**
```yaml
# VMware vSphere Configuration
# vsphere-config.yaml
apiVersion: v1
kind: VirtualMachine
metadata:
  name: product-service-vm
spec:
  template:
    metadata:
      labels:
        app: product-service
    spec:
      domain:
        resources:
          requests:
            memory: 4Gi
            cpu: 2
          limits:
            memory: 8Gi
            cpu: 4
        devices:
          disks:
          - name: disk0
            disk:
              bus: virtio
          interfaces:
          - name: default
            bridge: {}
      networks:
      - name: default
        pod: {}
      volumes:
      - name: disk0
        persistentVolumeClaim:
          claimName: product-service-pvc

# Hypervisor Management
# vcenter-management.sh
#!/bin/bash
# Create VM
govc vm.create -on=false -c=2 -m=4096 -disk.controller=pvscsi \
  -disk="product-service.vmdk" -net.adapter=vmxnet3 \
  -net.name="VM Network" product-service-vm

# Clone VM for scaling
govc vm.clone -vm=product-service-vm -on=false product-service-vm-2
govc vm.clone -vm=product-service-vm -on=false product-service-vm-3

# Power on VMs
govc vm.power -on=true product-service-vm
govc vm.power -on=true product-service-vm-2
govc vm.power -on=true product-service-vm-3
```

**Cloud Computing (2006 - Present):**
```yaml
# AWS CloudFormation Template
# cloudformation-template.yaml
AWSTemplateFormatVersion: '2010-09-09'
Description: 'Product Service Cloud Infrastructure'

Parameters:
  Environment:
    Type: String
    Default: production
    AllowedValues: [development, staging, production]
    Description: Environment name

Resources:
  # VPC
  VPC:
    Type: AWS::EC2::VPC
    Properties:
      CidrBlock: 10.0.0.0/16
      EnableDnsHostnames: true
      EnableDnsSupport: true
      Tags:
        - Key: Name
          Value: !Sub '${Environment}-vpc'

  # Public Subnets
  PublicSubnet1:
    Type: AWS::EC2::Subnet
    Properties:
      VpcId: !Ref VPC
      CidrBlock: 10.0.1.0/24
      AvailabilityZone: !Select [0, !GetAZs '']
      MapPublicIpOnLaunch: true
      Tags:
        - Key: Name
          Value: !Sub '${Environment}-public-subnet-1'

  PublicSubnet2:
    Type: AWS::EC2::Subnet
    Properties:
      VpcId: !Ref VPC
      CidrBlock: 10.0.2.0/24
      AvailabilityZone: !Select [1, !GetAZs '']
      MapPublicIpOnLaunch: true
      Tags:
        - Key: Name
          Value: !Sub '${Environment}-public-subnet-2'

  # Private Subnets
  PrivateSubnet1:
    Type: AWS::EC2::Subnet
    Properties:
      VpcId: !Ref VPC
      CidrBlock: 10.0.3.0/24
      AvailabilityZone: !Select [0, !GetAZs '']
      Tags:
        - Key: Name
          Value: !Sub '${Environment}-private-subnet-1'

  PrivateSubnet2:
    Type: AWS::EC2::Subnet
    Properties:
      VpcId: !Ref VPC
      CidrBlock: 10.0.4.0/24
      AvailabilityZone: !Select [1, !GetAZs '']
      Tags:
        - Key: Name
          Value: !Sub '${Environment}-private-subnet-2'

  # ECS Cluster
  ECSCluster:
    Type: AWS::ECS::Cluster
    Properties:
      ClusterName: !Sub '${Environment}-product-service-cluster'
      CapacityProviders:
        - FARGATE
        - FARGATE_SPOT
      DefaultCapacityProviderStrategy:
        - CapacityProvider: FARGATE
          Weight: 1

  # ECS Task Definition
  TaskDefinition:
    Type: AWS::ECS::TaskDefinition
    Properties:
      Family: product-service
      NetworkMode: awsvpc
      RequiresCompatibilities:
        - FARGATE
      Cpu: 256
      Memory: 512
      ExecutionRoleArn: !GetAtt ECSExecutionRole.Arn
      TaskRoleArn: !GetAtt ECSTaskRole.Arn
      ContainerDefinitions:
        - Name: product-service
          Image: !Sub '${AWS::AccountId}.dkr.ecr.${AWS::Region}.amazonaws.com/product-service:latest'
          PortMappings:
            - ContainerPort: 8080
              Protocol: tcp
          Environment:
            - Name: SPRING_PROFILES_ACTIVE
              Value: !Ref Environment
            - Name: DATABASE_URL
              Value: !Sub 'jdbc:postgresql://${RDSInstance.Endpoint.Address}:5432/productdb'
          LogConfiguration:
            LogDriver: awslogs
            Options:
              awslogs-group: !Ref CloudWatchLogGroup
              awslogs-region: !Ref AWS::Region
              awslogs-stream-prefix: ecs

  # ECS Service
  ECSService:
    Type: AWS::ECS::Service
    Properties:
      ServiceName: product-service
      Cluster: !Ref ECSCluster
      TaskDefinition: !Ref TaskDefinition
      DesiredCount: 3
      LaunchType: FARGATE
      NetworkConfiguration:
        AwsvpcConfiguration:
          AssignPublicIp: ENABLED
          SecurityGroups:
            - !Ref ECSSecurityGroup
          Subnets:
            - !Ref PublicSubnet1
            - !Ref PublicSubnet2
      LoadBalancers:
        - ContainerName: product-service
          ContainerPort: 8080
          TargetGroupArn: !Ref TargetGroup

  # Application Load Balancer
  LoadBalancer:
    Type: AWS::ElasticLoadBalancingV2::LoadBalancer
    Properties:
      Name: !Sub '${Environment}-product-service-alb'
      Scheme: internet-facing
      Type: application
      Subnets:
        - !Ref PublicSubnet1
        - !Ref PublicSubnet2
      SecurityGroups:
        - !Ref ALBSecurityGroup

  # Target Group
  TargetGroup:
    Type: AWS::ElasticLoadBalancingV2::TargetGroup
    Properties:
      Name: !Sub '${Environment}-product-service-tg'
      Port: 8080
      Protocol: HTTP
      TargetType: ip
      VpcId: !Ref VPC
      HealthCheckPath: /actuator/health
      HealthCheckIntervalSeconds: 30
      HealthCheckTimeoutSeconds: 5
      HealthyThresholdCount: 2
      UnhealthyThresholdCount: 3

  # RDS Database
  RDSInstance:
    Type: AWS::RDS::DBInstance
    Properties:
      DBInstanceIdentifier: !Sub '${Environment}-product-db'
      DBInstanceClass: db.t3.micro
      Engine: postgres
      EngineVersion: '15.4'
      AllocatedStorage: 20
      StorageType: gp2
      DBName: productdb
      MasterUsername: admin
      MasterUserPassword: !Ref DBPassword
      VPCSecurityGroups:
        - !Ref RDSSecurityGroup
      DBSubnetGroupName: !Ref DBSubnetGroup
      BackupRetentionPeriod: 7
      MultiAZ: true
      DeletionProtection: true

  # Auto Scaling
  AutoScalingTarget:
    Type: AWS::ApplicationAutoScaling::ScalableTarget
    Properties:
      MaxCapacity: 10
      MinCapacity: 3
      ResourceId: !Sub 'service/${ECSCluster.Name}/${ECSService.Name}'
      RoleARN: !Sub 'arn:aws:iam::${AWS::AccountId}:role/aws-service-role/ecs.application-autoscaling.amazonaws.com/AWSServiceRoleForApplicationAutoScaling_ECSService'
      ScalableDimension: ecs:service:DesiredCount
      ServiceNamespace: ecs

  CPUUtilizationTarget:
    Type: AWS::ApplicationAutoScaling::ScalingPolicy
    Properties:
      PolicyName: CPUTargetTrackingScaling
      PolicyType: TargetTrackingScaling
      ScalingTargetId: !Ref AutoScalingTarget
      TargetTrackingScalingPolicyConfiguration:
        PredefinedMetricSpecification:
          PredefinedMetricType: ECSServiceAverageCPUUtilization
        TargetValue: 70.0
        ScaleInCooldown: 300
        ScaleOutCooldown: 300

Outputs:
  LoadBalancerDNS:
    Description: DNS name of the load balancer
    Value: !GetAtt LoadBalancer.DNSName
    Export:
      Name: !Sub '${AWS::StackName}-LoadBalancerDNS'

  ECSClusterName:
    Description: Name of the ECS cluster
    Value: !Ref ECSCluster
    Export:
      Name: !Sub '${AWS::StackName}-ECSClusterName'
```

**Container Orchestration (2014 - Present):**
```yaml
# Kubernetes Deployment
# k8s/deployment.yaml
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
        image: ghcr.io/mycompany/product-service:latest
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
        - name: REDIS_URL
          valueFrom:
            secretKeyRef:
              name: redis-secret
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
        startupProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
          failureThreshold: 30

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
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80

---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: product-service-ingress
  annotations:
    kubernetes.io/ingress.class: nginx
    cert-manager.io/cluster-issuer: letsencrypt-prod
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/force-ssl-redirect: "true"
spec:
  tls:
  - hosts:
    - api.mycompany.com
    secretName: product-service-tls
  rules:
  - host: api.mycompany.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: product-service
            port:
              number: 80
```

---

## 2. Microservices Evolution

### 2.1 Từ Monolith đến Microservices

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

**Service-Oriented Architecture (SOA) - 2000s:**
```xml
<!-- SOAP Web Services -->
<!-- UserService.wsdl -->
<?xml version="1.0" encoding="UTF-8"?>
<wsdl:definitions xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
                  xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
                  xmlns:tns="http://example.com/user"
                  targetNamespace="http://example.com/user">
    
    <wsdl:types>
        <xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                     targetNamespace="http://example.com/user">
            <xsd:element name="GetUserRequest">
                <xsd:complexType>
                    <xsd:sequence>
                        <xsd:element name="userId" type="xsd:long"/>
                    </xsd:sequence>
                </xsd:complexType>
            </xsd:element>
            
            <xsd:element name="GetUserResponse">
                <xsd:complexType>
                    <xsd:sequence>
                        <xsd:element name="user" type="tns:User"/>
                    </xsd:sequence>
                </xsd:complexType>
            </xsd:element>
            
            <xsd:complexType name="User">
                <xsd:sequence>
                    <xsd:element name="id" type="xsd:long"/>
                    <xsd:element name="name" type="xsd:string"/>
                    <xsd:element name="email" type="xsd:string"/>
                </xsd:sequence>
            </xsd:complexType>
        </xsd:schema>
    </wsdl:types>
    
    <wsdl:message name="GetUserRequest">
        <wsdl:part name="parameters" element="tns:GetUserRequest"/>
    </wsdl:message>
    
    <wsdl:message name="GetUserResponse">
        <wsdl:part name="parameters" element="tns:GetUserResponse"/>
    </wsdl:message>
    
    <wsdl:portType name="UserService">
        <wsdl:operation name="getUser">
            <wsdl:input message="tns:GetUserRequest"/>
            <wsdl:output message="tns:GetUserResponse"/>
        </wsdl:operation>
    </wsdl:portType>
    
    <wsdl:binding name="UserServiceSoapBinding" type="tns:UserService">
        <soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
        <wsdl:operation name="getUser">
            <soap:operation soapAction="http://example.com/user/getUser"/>
            <wsdl:input>
                <soap:body use="literal"/>
            </wsdl:input>
            <wsdl:output>
                <soap:body use="literal"/>
            </wsdl:output>
        </wsdl:operation>
    </wsdl:binding>
    
    <wsdl:service name="UserService">
        <wsdl:port name="UserServicePort" binding="tns:UserServiceSoapBinding">
            <soap:address location="http://localhost:8080/soap/user"/>
        </wsdl:port>
    </wsdl:service>
</wsdl:definitions>

// SOAP Client
@Service
public class UserServiceClient {
    
    @Autowired
    private WebServiceTemplate webServiceTemplate;
    
    public User getUser(Long userId) {
        GetUserRequest request = new GetUserRequest();
        request.setUserId(userId);
        
        GetUserResponse response = (GetUserResponse) webServiceTemplate
            .marshalSendAndReceive("http://user-service:8080/soap/user", request);
        
        return response.getUser();
    }
}
```

**Microservices Architecture (2010s - Present):**
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

---

## 3. Service Mesh Evolution

### 3.1 Từ Direct Communication đến Service Mesh

**Direct Service Communication (2010s):**
```java
// Direct HTTP calls between services
@Service
public class OrderService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public Order createOrder(OrderRequest request) {
        try {
            // Direct call to User Service
            ResponseEntity<User> userResponse = restTemplate.getForEntity(
                "http://user-service:8080/api/users/" + request.getUserId(), 
                User.class
            );
            
            // Direct call to Product Service
            ResponseEntity<Product> productResponse = restTemplate.getForEntity(
                "http://product-service:8080/api/products/" + request.getProductId(), 
                Product.class
            );
            
            User user = userResponse.getBody();
            Product product = productResponse.getBody();
            
            Order order = new Order(user, product, request.getQuantity());
            return orderRepository.save(order);
            
        } catch (RestClientException e) {
            // Handle service communication errors
            throw new ServiceCommunicationException("Failed to communicate with external services", e);
        }
    }
}
```

**Vấn đề với Direct Communication:**
- **No Circuit Breaker**: Cascading failures
- **No Retry Logic**: Temporary failures cause permanent errors
- **No Load Balancing**: Manual load balancer configuration
- **No Observability**: Hard to trace requests across services
- **No Security**: No service-to-service authentication

**API Gateway Pattern (2015 - Present):**
```yaml
# Kong API Gateway Configuration
# kong.yml
_format_version: "2.1"

services:
  - name: user-service
    url: http://user-service:8080
    routes:
      - name: user-routes
        paths:
          - /api/users
        strip_path: false
    plugins:
      - name: rate-limiting
        config:
          minute: 100
          hour: 1000
      - name: key-auth
        config:
          key_names:
            - apikey
      - name: cors
        config:
          origins:
            - "*"
          methods:
            - GET
            - POST
            - PUT
            - DELETE
          headers:
            - Content-Type
            - Authorization

  - name: product-service
    url: http://product-service:8080
    routes:
      - name: product-routes
        paths:
          - /api/products
        strip_path: false
    plugins:
      - name: rate-limiting
        config:
          minute: 200
          hour: 2000
      - name: key-auth
        config:
          key_names:
            - apikey
      - name: cors
        config:
          origins:
            - "*"
          methods:
            - GET
            - POST
            - PUT
            - DELETE
          headers:
            - Content-Type
            - Authorization

  - name: order-service
    url: http://order-service:8080
    routes:
      - name: order-routes
        paths:
          - /api/orders
        strip_path: false
    plugins:
      - name: rate-limiting
        config:
          minute: 50
          hour: 500
      - name: key-auth
        config:
          key_names:
            - apikey
      - name: cors
        config:
          origins:
            - "*"
          methods:
            - GET
            - POST
            - PUT
            - DELETE
          headers:
            - Content-Type
            - Authorization

consumers:
  - username: mobile-app
    keyauth_credentials:
      - key: mobile-app-key-123
  - username: web-app
    keyauth_credentials:
      - key: web-app-key-456
  - username: admin
    keyauth_credentials:
      - key: admin-key-789
```

**Service Mesh với Istio (2017 - Present):**
```yaml
# Istio Service Mesh Configuration
# istio-config.yaml

# Virtual Service - Traffic routing
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: product-service
spec:
  hosts:
  - product-service
  - api.mycompany.com
  gateways:
  - product-gateway
  http:
  - match:
    - uri:
        prefix: /api/products
    route:
    - destination:
        host: product-service
        subset: v1
      weight: 90
    - destination:
        host: product-service
        subset: v2
      weight: 10
    retries:
      attempts: 3
      perTryTimeout: 2s
    timeout: 10s
    fault:
      delay:
        percentage:
          value: 5
        fixedDelay: 2s
      abort:
        percentage:
          value: 1
        httpStatus: 500

---
# Destination Rule - Load balancing and circuit breaker
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: product-service
spec:
  host: product-service
  trafficPolicy:
    loadBalancer:
      simple: ROUND_ROBIN
    connectionPool:
      tcp:
        maxConnections: 100
        connectTimeout: 30ms
      http:
        http2MaxRequests: 1000
        maxRequestsPerConnection: 10
        maxRetries: 3
    outlierDetection:
      consecutive5xxErrors: 5
      interval: 10s
      baseEjectionTime: 30s
      maxEjectionPercent: 10
  subsets:
  - name: v1
    labels:
      version: v1
  - name: v2
    labels:
      version: v2

---
# Gateway - Ingress traffic
apiVersion: networking.istio.io/v1alpha3
kind: Gateway
metadata:
  name: product-gateway
spec:
  selector:
    istio: ingressgateway
  servers:
  - port:
      number: 80
      name: http
      protocol: HTTP
    hosts:
    - api.mycompany.com
    tls:
      httpsRedirect: true
  - port:
      number: 443
      name: https
      protocol: HTTPS
    hosts:
    - api.mycompany.com
    tls:
      mode: SIMPLE
      credentialName: product-service-cert

---
# Authorization Policy - Security
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: product-service-auth
  namespace: default
spec:
  selector:
    matchLabels:
      app: product-service
  rules:
  - from:
    - source:
        principals: ["cluster.local/ns/default/sa/product-service"]
    to:
    - operation:
        methods: ["GET"]
        paths: ["/api/products/*"]
  - from:
    - source:
        namespaces: ["default"]
    to:
    - operation:
        methods: ["POST", "PUT", "DELETE"]
        paths: ["/api/products/*"]
    when:
    - key: request.auth.claims[role]
      values: ["admin"]

---
# Service Entry - External services
apiVersion: networking.istio.io/v1alpha3
kind: ServiceEntry
metadata:
  name: external-payment-service
spec:
  hosts:
  - payment.external.com
  ports:
  - number: 443
    name: https
    protocol: HTTPS
  resolution: DNS
  location: MESH_EXTERNAL

---
# Sidecar - Proxy configuration
apiVersion: networking.istio.io/v1alpha3
kind: Sidecar
metadata:
  name: product-service-sidecar
  namespace: default
spec:
  selector:
    matchLabels:
      app: product-service
  egress:
  - hosts:
    - "default/*"
    - "istio-system/*"
    - "external-payment-service/*"
  ingress:
  - port:
      number: 8080
      protocol: HTTP
      name: http
    defaultEndpoint: 127.0.0.1:8080
```

---

## 4. Event-Driven Architecture

### 4.1 Từ Request-Response đến Event-Driven

**Request-Response Pattern (Traditional):**
```java
// Synchronous request-response
@Service
public class OrderService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private EmailService emailService;
    
    @Transactional
    public Order createOrder(OrderRequest request) {
        // Synchronous calls - blocking
        User user = userService.getUser(request.getUserId());
        Product product = productService.getProduct(request.getProductId());
        
        // Validate inventory
        if (!inventoryService.checkAvailability(product.getId(), request.getQuantity())) {
            throw new InsufficientInventoryException("Product not available");
        }
        
        // Create order
        Order order = new Order(user, product, request.getQuantity());
        Order savedOrder = orderRepository.save(order);
        
        // Process payment - blocking
        PaymentResult paymentResult = paymentService.processPayment(savedOrder);
        if (!paymentResult.isSuccess()) {
            throw new PaymentFailedException("Payment processing failed");
        }
        
        // Update inventory - blocking
        inventoryService.updateStock(product.getId(), request.getQuantity());
        
        // Send confirmation email - blocking
        emailService.sendOrderConfirmation(savedOrder);
        
        return savedOrder;
    }
}
```

**Vấn đề với Request-Response:**
- **Tight Coupling**: Services depend on each other
- **Blocking Operations**: Slow response times
- **Cascading Failures**: One service failure affects others
- **Scalability Issues**: Hard to scale individual components
- **Complex Error Handling**: Difficult to handle partial failures

**Event-Driven Architecture (2015 - Present):**
```java
// Event-Driven Architecture
@Service
public class OrderService {
    
    @Autowired
    private EventPublisher eventPublisher;
    
    @Transactional
    public Order createOrder(OrderRequest request) {
        // Create order
        Order order = new Order(request.getUserId(), request.getProductId(), request.getQuantity());
        Order savedOrder = orderRepository.save(order);
        
        // Publish event asynchronously
        OrderCreatedEvent event = new OrderCreatedEvent(
            savedOrder.getId(),
            savedOrder.getUserId(),
            savedOrder.getProductId(),
            savedOrder.getQuantity(),
            savedOrder.getTotalAmount()
        );
        
        eventPublisher.publish("order.created", event);
        
        return savedOrder;
    }
}

// Event Handlers
@Component
public class OrderEventHandler {
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Validate user
        validateUser(event.getUserId());
        
        // Validate product
        validateProduct(event.getProductId());
        
        // Check inventory
        checkInventory(event.getProductId(), event.getQuantity());
    }
    
    @EventListener
    public void handleUserValidated(UserValidatedEvent event) {
        // Process payment
        processPayment(event.getOrderId(), event.getAmount());
    }
    
    @EventListener
    public void handleProductValidated(ProductValidatedEvent event) {
        // Update inventory
        updateInventory(event.getProductId(), event.getQuantity());
    }
    
    @EventListener
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        if (event.isSuccess()) {
            // Send confirmation email
            sendOrderConfirmation(event.getOrderId());
        } else {
            // Cancel order
            cancelOrder(event.getOrderId());
        }
    }
    
    @EventListener
    public void handleInventoryUpdated(InventoryUpdatedEvent event) {
        // Mark order as confirmed
        confirmOrder(event.getOrderId());
    }
}

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

---

## 🎯 Kết luận

### Evolution Summary:

| Technology | Past | Present | Future |
|------------|------|---------|--------|
| **Infrastructure** | On-Premise | Cloud Native | Edge Computing |
| **Architecture** | Monolith | Microservices | Event-Driven |
| **Communication** | Direct Calls | Service Mesh | Event Streaming |
| **Deployment** | Manual | Container Orchestration | Serverless |

### Key Trends:

1. **Decentralization**: From centralized to distributed systems
2. **Automation**: From manual to fully automated operations
3. **Observability**: From basic monitoring to full observability
4. **Resilience**: From fragile to fault-tolerant systems
5. **Scalability**: From fixed to auto-scaling infrastructure

### Tài liệu tham khảo:

1. **Cloud Native**: 
   - [Cloud Native Computing Foundation](https://www.cncf.io/)
   - [Kubernetes Documentation](https://kubernetes.io/docs/)
   - [Docker Documentation](https://docs.docker.com/)

2. **Microservices**:
   - [Microservices.io](https://microservices.io/)
   - [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
   - [Istio Documentation](https://istio.io/docs/)

3. **Service Mesh**:
   - [Istio Documentation](https://istio.io/docs/)
   - [Envoy Proxy Documentation](https://www.envoyproxy.io/docs/)
   - [Linkerd Documentation](https://linkerd.io/docs/)

4. **Event-Driven**:
   - [Event Sourcing Pattern](https://martinfowler.com/eaaDev/EventSourcing.html)
   - [CQRS Pattern](https://martinfowler.com/bliki/CQRS.html)
   - [Apache Kafka Documentation](https://kafka.apache.org/documentation/)

---

**Remember: Cloud Native is not just about containers, it's about building resilient, scalable, and maintainable systems! 🚀** 