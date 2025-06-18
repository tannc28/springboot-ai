// MongoDB initialization script
db = db.getSiblingDB('productdb');

// Create collections
db.createCollection('products');
db.createCollection('users');
db.createCollection('orders');
db.createCollection('categories');
db.createCollection('audit_logs');

// Create indexes for better performance
db.products.createIndex({ "name": 1 });
db.products.createIndex({ "category": 1 });
db.products.createIndex({ "price": 1 });
db.products.createIndex({ "createdAt": -1 });

db.users.createIndex({ "email": 1 }, { unique: true });
db.users.createIndex({ "username": 1 }, { unique: true });

db.orders.createIndex({ "userId": 1 });
db.orders.createIndex({ "status": 1 });
db.orders.createIndex({ "createdAt": -1 });

db.audit_logs.createIndex({ "timestamp": -1 });
db.audit_logs.createIndex({ "userId": 1 });
db.audit_logs.createIndex({ "action": 1 });

// Insert sample data
db.categories.insertMany([
    {
        _id: ObjectId(),
        name: "Electronics",
        description: "Electronic devices and gadgets",
        createdAt: new Date(),
        updatedAt: new Date()
    },
    {
        _id: ObjectId(),
        name: "Clothing",
        description: "Fashion and apparel",
        createdAt: new Date(),
        updatedAt: new Date()
    },
    {
        _id: ObjectId(),
        name: "Books",
        description: "Books and publications",
        createdAt: new Date(),
        updatedAt: new Date()
    }
]);

db.products.insertMany([
    {
        _id: ObjectId(),
        name: "iPhone 15 Pro",
        description: "Latest iPhone with advanced features",
        price: 999.99,
        category: "Electronics",
        stock: 50,
        createdAt: new Date(),
        updatedAt: new Date()
    },
    {
        _id: ObjectId(),
        name: "MacBook Pro M3",
        description: "Powerful laptop for professionals",
        price: 1999.99,
        category: "Electronics",
        stock: 25,
        createdAt: new Date(),
        updatedAt: new Date()
    },
    {
        _id: ObjectId(),
        name: "Nike Air Max",
        description: "Comfortable running shoes",
        price: 129.99,
        category: "Clothing",
        stock: 100,
        createdAt: new Date(),
        updatedAt: new Date()
    }
]);

print("MongoDB initialization completed successfully!"); 