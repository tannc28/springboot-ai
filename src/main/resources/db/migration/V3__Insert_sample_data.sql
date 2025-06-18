-- Insert sample products
INSERT INTO products (name, description, price, created_at, updated_at, is_active) VALUES
('iPhone 15 Pro', 'Latest iPhone with advanced camera system', 999.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
('MacBook Pro M3', 'Powerful laptop for professionals', 1999.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
('iPad Air', 'Versatile tablet for work and play', 599.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
('Apple Watch Series 9', 'Advanced health monitoring smartwatch', 399.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
('AirPods Pro', 'Premium wireless earbuds with noise cancellation', 249.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE);

-- Insert sample admin user (password: admin123)
INSERT INTO users (username, email, password, full_name, role, created_at, updated_at) VALUES
('admin', 'admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'System Administrator', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample regular user (password: user123)
INSERT INTO users (username, email, password, full_name, role, created_at, updated_at) VALUES
('user', 'user@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Regular User', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); 