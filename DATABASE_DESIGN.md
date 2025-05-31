# Library Management System - Database Design

## 📊 Database Overview

**Database Name**: `library_management_db`  
**Database Engine**: PostgreSQL 12+  
**Character Set**: UTF-8  
**Collation**: en_US.UTF-8

## 🏗️ Entity Relationship Diagram (ERD)

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│     USERS       │    │     AUTHORS     │    │    GENRES       │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ id (PK)         │    │ id (PK)         │    │ id (PK)         │
│ username        │    │ first_name      │    │ name            │
│ email           │    │ last_name       │    │ description     │
│ password        │    │ biography       │    │ created_at      │
│ first_name      │    │ birth_date      │    │ updated_at      │
│ last_name       │    │ death_date      │    │ is_active       │
│ phone_number    │    │ nationality     │    └─────────────────┘
│ date_of_birth   │    │ created_at      │             │
│ address         │    │ updated_at      │             │
│ student_id      │    │ is_active       │             │
│ role            │    └─────────────────┘             │
│ is_enabled      │             │                      │
│ created_at      │             │                      │
│ updated_at      │             │                      │
│ is_active       │             │                      │
└─────────────────┘             │                      │
         │                      │                      │
         │    ┌─────────────────┐│                      │
         │    │   PUBLISHERS    ││                      │
         │    ├─────────────────┤│                      │
         │    │ id (PK)         ││                      │
         │    │ name            ││                      │
         │    │ address         ││                      │
         │    │ contact_email   ││                      │
         │    │ contact_phone   ││                      │
         │    │ website         ││                      │
         │    │ created_at      ││                      │
         │    │ updated_at      ││                      │
         │    │ is_active       ││                      │
         │    └─────────────────┘│                      │
         │                       │                      │
         │                       │                      │
         │    ┌─────────────────────────────────────────┴┐
         │    │                 BOOKS                    │
         │    ├──────────────────────────────────────────┤
         │    │ id (PK)                                  │
         │    │ title                                    │
         │    │ isbn                                     │
         │    │ description                              │
         │    │ publication_date                         │
         │    │ pages                                    │
         │    │ language                                 │
         │    │ edition                                  │
         │    │ cover_image_url                          │
         │    │ total_copies                             │
         │    │ available_copies                         │
         │    │ author_id (FK)                           │
         │    │ publisher_id (FK)                        │
         │    │ genre_id (FK)                            │
         │    │ created_at                               │
         │    │ updated_at                               │
         │    │ is_active                                │
         │    └──────────────────────────────────────────┘
         │                       │
         │                       │
         │    ┌─────────────────────────────────────────┐
         │    │             BOOK_COPIES                 │
         │    ├─────────────────────────────────────────┤
         │    │ id (PK)                                 │
         │    │ book_id (FK)                            │
         │    │ copy_number                             │
         │    │ condition                               │
         │    │ location                                │
         │    │ status                                  │
         │    │ acquisition_date                        │
         │    │ created_at                              │
         │    │ updated_at                              │
         │    │ is_active                               │
         │    └─────────────────────────────────────────┘
         │                       │
         │                       │
         │                       │
┌────────┴────────┐             │
│ BOOK_TRANSACTIONS│             │
├─────────────────┤             │
│ id (PK)         │             │
│ user_id (FK)    │─────────────┘
│ book_copy_id(FK)│
│ issue_date      │
│ due_date        │
│ return_date     │
│ renewal_count   │
│ status          │
│ issued_by (FK)  │
│ returned_to (FK)│
│ notes           │
│ created_at      │
│ updated_at      │
│ is_active       │
└─────────────────┘
         │
         │
┌────────┴────────┐
│ BOOK_RESERVATIONS│
├─────────────────┤
│ id (PK)         │
│ user_id (FK)    │
│ book_id (FK)    │
│ reservation_date│
│ expiry_date     │
│ status          │
│ notified        │
│ created_at      │
│ updated_at      │
│ is_active       │
└─────────────────┘
         │
         │
┌────────┴────────┐
│     FINES       │
├─────────────────┤
│ id (PK)         │
│ user_id (FK)    │
│ transaction_id  │
│ fine_amount     │
│ fine_reason     │
│ fine_date       │
│ payment_date    │
│ payment_amount  │
│ payment_method  │
│ status          │
│ waived_by (FK)  │
│ waived_reason   │
│ created_at      │
│ updated_at      │
│ is_active       │
└─────────────────┘
         │
         │
┌────────┴────────┐
│ ACTIVITY_LOGS   │
├─────────────────┤
│ id (PK)         │
│ user_id (FK)    │
│ action          │
│ entity_type     │
│ entity_id       │
│ details         │
│ ip_address      │
│ user_agent      │
│ created_at      │
└─────────────────┘
```

## 📋 Table Specifications

### 1. USERS Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(15),
    date_of_birth DATE,
    address TEXT,
    student_id VARCHAR(20) UNIQUE,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'LIBRARIAN', 'STUDENT')),
    is_account_non_expired BOOLEAN DEFAULT TRUE,
    is_account_non_locked BOOLEAN DEFAULT TRUE,
    is_credentials_non_expired BOOLEAN DEFAULT TRUE,
    is_enabled BOOLEAN DEFAULT TRUE,
    is_email_verified BOOLEAN DEFAULT FALSE,
    email_verification_token VARCHAR(255),
    password_reset_token VARCHAR(255),
    password_reset_token_expiry TIMESTAMP,
    last_login TIMESTAMP,
    login_attempts INTEGER DEFAULT 0,
    profile_image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

### 2. AUTHORS Table
```sql
CREATE TABLE authors (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    biography TEXT,
    birth_date DATE,
    death_date DATE,
    nationality VARCHAR(50),
    website VARCHAR(255),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

### 3. GENRES Table
```sql
CREATE TABLE genres (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    parent_genre_id BIGINT REFERENCES genres(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

### 4. PUBLISHERS Table
```sql
CREATE TABLE publishers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    address TEXT,
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    website VARCHAR(255),
    established_year INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

### 5. BOOKS Table
```sql
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    description TEXT,
    publication_date DATE,
    pages INTEGER,
    language VARCHAR(50) DEFAULT 'English',
    edition VARCHAR(50),
    cover_image_url VARCHAR(500),
    total_copies INTEGER DEFAULT 0,
    available_copies INTEGER DEFAULT 0,
    author_id BIGINT NOT NULL REFERENCES authors(id),
    publisher_id BIGINT REFERENCES publishers(id),
    genre_id BIGINT REFERENCES genres(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    
    CONSTRAINT chk_copies CHECK (available_copies >= 0 AND available_copies <= total_copies)
);
```

### 6. BOOK_COPIES Table
```sql
CREATE TABLE book_copies (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL REFERENCES books(id),
    copy_number VARCHAR(20) NOT NULL,
    condition VARCHAR(20) DEFAULT 'GOOD' CHECK (condition IN ('EXCELLENT', 'GOOD', 'FAIR', 'POOR', 'DAMAGED')),
    location VARCHAR(100),
    status VARCHAR(20) DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ISSUED', 'RESERVED', 'MAINTENANCE', 'LOST')),
    acquisition_date DATE DEFAULT CURRENT_DATE,
    last_maintenance_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    
    UNIQUE(book_id, copy_number)
);
```

### 7. BOOK_TRANSACTIONS Table
```sql
CREATE TABLE book_transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    book_copy_id BIGINT NOT NULL REFERENCES book_copies(id),
    issue_date DATE NOT NULL DEFAULT CURRENT_DATE,
    due_date DATE NOT NULL,
    return_date DATE,
    renewal_count INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ISSUED' CHECK (status IN ('ISSUED', 'RETURNED', 'OVERDUE', 'LOST', 'RENEWED')),
    issued_by BIGINT REFERENCES users(id),
    returned_to BIGINT REFERENCES users(id),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

### 8. BOOK_RESERVATIONS Table
```sql
CREATE TABLE book_reservations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    book_id BIGINT NOT NULL REFERENCES books(id),
    reservation_date DATE NOT NULL DEFAULT CURRENT_DATE,
    expiry_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'FULFILLED', 'EXPIRED', 'CANCELLED')),
    notified BOOLEAN DEFAULT FALSE,
    queue_position INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

### 9. FINES Table
```sql
CREATE TABLE fines (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    transaction_id BIGINT REFERENCES book_transactions(id),
    fine_amount DECIMAL(10,2) NOT NULL,
    fine_reason VARCHAR(255) NOT NULL,
    fine_date DATE NOT NULL DEFAULT CURRENT_DATE,
    payment_date DATE,
    payment_amount DECIMAL(10,2),
    payment_method VARCHAR(50),
    payment_reference VARCHAR(100),
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PAID', 'WAIVED', 'PARTIAL')),
    waived_by BIGINT REFERENCES users(id),
    waived_reason TEXT,
    waived_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

### 10. ACTIVITY_LOGS Table
```sql
CREATE TABLE activity_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    details JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 📈 Indexes for Performance

```sql
-- User indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_student_id ON users(student_id);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_active ON users(is_active);

-- Book indexes
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_author ON books(author_id);
CREATE INDEX idx_books_genre ON books(genre_id);
CREATE INDEX idx_books_publisher ON books(publisher_id);
CREATE INDEX idx_books_active ON books(is_active);

-- Book copy indexes
CREATE INDEX idx_book_copies_book_id ON book_copies(book_id);
CREATE INDEX idx_book_copies_status ON book_copies(status);

-- Transaction indexes
CREATE INDEX idx_transactions_user ON book_transactions(user_id);
CREATE INDEX idx_transactions_book_copy ON book_transactions(book_copy_id);
CREATE INDEX idx_transactions_status ON book_transactions(status);
CREATE INDEX idx_transactions_due_date ON book_transactions(due_date);
CREATE INDEX idx_transactions_issue_date ON book_transactions(issue_date);

-- Reservation indexes
CREATE INDEX idx_reservations_user ON book_reservations(user_id);
CREATE INDEX idx_reservations_book ON book_reservations(book_id);
CREATE INDEX idx_reservations_status ON book_reservations(status);
CREATE INDEX idx_reservations_expiry ON book_reservations(expiry_date);

-- Fine indexes
CREATE INDEX idx_fines_user ON fines(user_id);
CREATE INDEX idx_fines_status ON fines(status);
CREATE INDEX idx_fines_date ON fines(fine_date);

-- Activity log indexes
CREATE INDEX idx_activity_logs_user ON activity_logs(user_id);
CREATE INDEX idx_activity_logs_action ON activity_logs(action);
CREATE INDEX idx_activity_logs_entity ON activity_logs(entity_type, entity_id);
CREATE INDEX idx_activity_logs_created ON activity_logs(created_at);
```

## 🔄 Database Triggers

### 1. Update available copies when book copy status changes
```sql
CREATE OR REPLACE FUNCTION update_available_copies()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'UPDATE' THEN
        -- Update available copies count
        UPDATE books 
        SET available_copies = (
            SELECT COUNT(*) 
            FROM book_copies 
            WHERE book_id = NEW.book_id 
            AND status = 'AVAILABLE' 
            AND is_active = TRUE
        )
        WHERE id = NEW.book_id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_available_copies
    AFTER UPDATE ON book_copies
    FOR EACH ROW
    EXECUTE FUNCTION update_available_copies();
```

### 2. Auto-calculate fine amount for overdue books
```sql
CREATE OR REPLACE FUNCTION calculate_overdue_fine()
RETURNS TRIGGER AS $$
DECLARE
    days_overdue INTEGER;
    fine_per_day DECIMAL(10,2) := 5.00;
BEGIN
    IF NEW.status = 'OVERDUE' AND OLD.status != 'OVERDUE' THEN
        days_overdue := CURRENT_DATE - NEW.due_date;
        
        IF days_overdue > 0 THEN
            INSERT INTO fines (user_id, transaction_id, fine_amount, fine_reason)
            VALUES (
                NEW.user_id,
                NEW.id,
                days_overdue * fine_per_day,
                'Overdue book return - ' || days_overdue || ' days late'
            );
        END IF;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_calculate_overdue_fine
    AFTER UPDATE ON book_transactions
    FOR EACH ROW
    EXECUTE FUNCTION calculate_overdue_fine();
```

### 3. Update timestamps
```sql
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply to all tables with updated_at column
CREATE TRIGGER trigger_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_books_updated_at BEFORE UPDATE ON books FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_authors_updated_at BEFORE UPDATE ON authors FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_genres_updated_at BEFORE UPDATE ON genres FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_publishers_updated_at BEFORE UPDATE ON publishers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_book_copies_updated_at BEFORE UPDATE ON book_copies FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_transactions_updated_at BEFORE UPDATE ON book_transactions FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_reservations_updated_at BEFORE UPDATE ON book_reservations FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_fines_updated_at BEFORE UPDATE ON fines FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

## 📊 Views for Reports

### 1. User Statistics View
```sql
CREATE VIEW user_statistics AS
SELECT 
    u.id,
    u.username,
    u.first_name,
    u.last_name,
    u.role,
    COUNT(DISTINCT bt.id) as total_borrowed_books,
    COUNT(DISTINCT CASE WHEN bt.status = 'ISSUED' THEN bt.id END) as currently_borrowed,
    COUNT(DISTINCT f.id) as total_fines,
    COALESCE(SUM(CASE WHEN f.status = 'PENDING' THEN f.fine_amount ELSE 0 END), 0) as pending_fine_amount,
    u.created_at as member_since
FROM users u
LEFT JOIN book_transactions bt ON u.id = bt.user_id
LEFT JOIN fines f ON u.id = f.user_id
WHERE u.is_active = TRUE
GROUP BY u.id, u.username, u.first_name, u.last_name, u.role, u.created_at;
```

### 2. Book Statistics View
```sql
CREATE VIEW book_statistics AS
SELECT 
    b.id,
    b.title,
    b.isbn,
    CONCAT(a.first_name, ' ', a.last_name) as author_name,
    g.name as genre,
    p.name as publisher,
    b.total_copies,
    b.available_copies,
    COUNT(DISTINCT bt.id) as total_issues,
    COUNT(DISTINCT br.id) as current_reservations,
    AVG(CASE WHEN bt.return_date IS NOT NULL 
        THEN bt.return_date - bt.issue_date 
        ELSE NULL END) as avg_loan_duration
FROM books b
LEFT JOIN authors a ON b.author_id = a.id
LEFT JOIN genres g ON b.genre_id = g.id
LEFT JOIN publishers p ON b.publisher_id = p.id
LEFT JOIN book_copies bc ON b.id = bc.book_id
LEFT JOIN book_transactions bt ON bc.id = bt.book_copy_id
LEFT JOIN book_reservations br ON b.id = br.book_id AND br.status = 'ACTIVE'
WHERE b.is_active = TRUE
GROUP BY b.id, b.title, b.isbn, a.first_name, a.last_name, g.name, p.name, b.total_copies, b.available_copies;
```

### 3. Monthly Report View
```sql
CREATE VIEW monthly_report AS
SELECT 
    DATE_TRUNC('month', bt.issue_date) as month,
    COUNT(DISTINCT bt.id) as books_issued,
    COUNT(DISTINCT bt.user_id) as active_users,
    COUNT(DISTINCT CASE WHEN bt.return_date IS NOT NULL THEN bt.id END) as books_returned,
    COUNT(DISTINCT CASE WHEN bt.status = 'OVERDUE' THEN bt.id END) as overdue_books,
    COALESCE(SUM(f.fine_amount), 0) as total_fines_generated
FROM book_transactions bt
LEFT JOIN fines f ON bt.id = f.transaction_id 
    AND DATE_TRUNC('month', f.fine_date) = DATE_TRUNC('month', bt.issue_date)
GROUP BY DATE_TRUNC('month', bt.issue_date)
ORDER BY month DESC;
```

## 🔐 Security Considerations

### 1. Row Level Security (RLS)
```sql
-- Enable RLS on sensitive tables
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE book_transactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE fines ENABLE ROW LEVEL SECURITY;

-- Students can only see their own data
CREATE POLICY student_users_policy ON users
    FOR ALL TO student_role
    USING (id = current_setting('app.current_user_id')::BIGINT);

CREATE POLICY student_transactions_policy ON book_transactions
    FOR ALL TO student_role
    USING (user_id = current_setting('app.current_user_id')::BIGINT);
```

### 2. Audit Trail
```sql
CREATE OR REPLACE FUNCTION audit_trigger()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO activity_logs (user_id, action, entity_type, entity_id, details)
    VALUES (
        current_setting('app.current_user_id', true)::BIGINT,
        TG_OP,
        TG_TABLE_NAME,
        COALESCE(NEW.id, OLD.id),
        jsonb_build_object(
            'old', to_jsonb(OLD),
            'new', to_jsonb(NEW)
        )
    );
    
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- Add audit triggers to important tables
CREATE TRIGGER audit_users AFTER INSERT OR UPDATE OR DELETE ON users FOR EACH ROW EXECUTE FUNCTION audit_trigger();
CREATE TRIGGER audit_books AFTER INSERT OR UPDATE OR DELETE ON books FOR EACH ROW EXECUTE FUNCTION audit_trigger();
CREATE TRIGGER audit_transactions AFTER INSERT OR UPDATE OR DELETE ON book_transactions FOR EACH ROW EXECUTE FUNCTION audit_trigger();
```

## 🚀 Database Setup Script

```sql
-- Create database and user
CREATE DATABASE library_management_db;
CREATE USER library_user WITH ENCRYPTED PASSWORD 'library_password';
GRANT ALL PRIVILEGES ON DATABASE library_management_db TO library_user;

-- Connect to the database and run the following:
\c library_management_db;

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Run all table creation scripts above
-- Run all index creation scripts above
-- Run all trigger creation scripts above
-- Run all view creation scripts above

-- Grant permissions
GRANT ALL ON ALL TABLES IN SCHEMA public TO library_user;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO library_user;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO library_user;
```

## 📊 Sample Data Insertion

```sql
-- Insert sample genres
INSERT INTO genres (name, description) VALUES
('Fiction', 'Fictional stories and novels'),
('Non-Fiction', 'Factual and informational books'),
('Science Fiction', 'Futuristic and scientific fiction'),
('Romance', 'Love and relationship stories'),
('Mystery', 'Detective and mystery stories'),
('Biography', 'Life stories of real people'),
('History', 'Historical accounts and events'),
('Technology', 'Books about technology and computing');

-- Insert sample authors
INSERT INTO authors (first_name, last_name, biography, nationality) VALUES
('George', 'Orwell', 'English novelist and essayist', 'British'),
('Jane', 'Austen', 'English novelist known for romantic fiction', 'British'),
('Agatha', 'Christie', 'English writer known for detective novels', 'British'),
('Isaac', 'Asimov', 'American writer and professor of biochemistry', 'American');

-- Insert sample publishers
INSERT INTO publishers (name, address, contact_email) VALUES
('Penguin Random House', '1745 Broadway, New York, NY', 'contact@penguin.com'),
('HarperCollins', '195 Broadway, New York, NY', 'info@harpercollins.com'),
('Simon & Schuster', '1230 Avenue of the Americas, New York, NY', 'contact@simonandschuster.com');
```

## 🔍 Query Examples

### Most Popular Books
```sql
SELECT 
    b.title,
    CONCAT(a.first_name, ' ', a.last_name) as author,
    COUNT(bt.id) as total_issues
FROM books b
JOIN authors a ON b.author_id = a.id
LEFT JOIN book_copies bc ON b.id = bc.book_id
LEFT JOIN book_transactions bt ON bc.id = bt.book_copy_id
GROUP BY b.id, b.title, a.first_name, a.last_name
ORDER BY total_issues DESC
LIMIT 10;
```

### Overdue Books Report
```sql
SELECT 
    CONCAT(u.first_name, ' ', u.last_name) as user_name,
    u.student_id,
    b.title as book_title,
    bt.due_date,
    CURRENT_DATE - bt.due_date as days_overdue,
    (CURRENT_DATE - bt.due_date) * 5.00 as calculated_fine
FROM book_transactions bt
JOIN users u ON bt.user_id = u.id
JOIN book_copies bc ON bt.book_copy_id = bc.id
JOIN books b ON bc.book_id = b.id
WHERE bt.status = 'ISSUED' 
AND bt.due_date < CURRENT_DATE
ORDER BY days_overdue DESC;
```

This comprehensive database design provides a solid foundation for the Library Management System with proper relationships, constraints, indexes, and security measures.