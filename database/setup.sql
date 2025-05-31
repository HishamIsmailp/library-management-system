-- Library Management System Database Setup Script
-- PostgreSQL Database Setup

-- Create database and user (run as postgres superuser)
-- CREATE DATABASE library_management_db;
-- CREATE USER library_user WITH ENCRYPTED PASSWORD 'library_password';
-- GRANT ALL PRIVILEGES ON DATABASE library_management_db TO library_user;

-- Connect to library_management_db database before running the rest

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- ============================================================================
-- TABLE CREATION
-- ============================================================================

-- 1. USERS Table
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

-- 2. AUTHORS Table
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

-- 3. GENRES Table
CREATE TABLE genres (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    parent_genre_id BIGINT REFERENCES genres(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- 4. PUBLISHERS Table
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

-- 5. BOOKS Table
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

-- 6. BOOK_COPIES Table
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

-- 7. BOOK_TRANSACTIONS Table
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

-- 8. BOOK_RESERVATIONS Table
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

-- 9. FINES Table
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

-- 10. ACTIVITY_LOGS Table
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

-- ============================================================================
-- INDEXES FOR PERFORMANCE
-- ============================================================================

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

-- ============================================================================
-- TRIGGERS AND FUNCTIONS
-- ============================================================================

-- Function to update timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply timestamp triggers to all tables with updated_at column
CREATE TRIGGER trigger_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_books_updated_at BEFORE UPDATE ON books FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_authors_updated_at BEFORE UPDATE ON authors FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_genres_updated_at BEFORE UPDATE ON genres FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_publishers_updated_at BEFORE UPDATE ON publishers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_book_copies_updated_at BEFORE UPDATE ON book_copies FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_transactions_updated_at BEFORE UPDATE ON book_transactions FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_reservations_updated_at BEFORE UPDATE ON book_reservations FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER trigger_fines_updated_at BEFORE UPDATE ON fines FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Function to update available copies when book copy status changes
CREATE OR REPLACE FUNCTION update_available_copies()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'UPDATE' OR TG_OP = 'INSERT' THEN
        UPDATE books 
        SET available_copies = (
            SELECT COUNT(*) 
            FROM book_copies 
            WHERE book_id = NEW.book_id 
            AND status = 'AVAILABLE' 
            AND is_active = TRUE
        )
        WHERE id = NEW.book_id;
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE books 
        SET available_copies = (
            SELECT COUNT(*) 
            FROM book_copies 
            WHERE book_id = OLD.book_id 
            AND status = 'AVAILABLE' 
            AND is_active = TRUE
        )
        WHERE id = OLD.book_id;
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_available_copies
    AFTER INSERT OR UPDATE OR DELETE ON book_copies
    FOR EACH ROW
    EXECUTE FUNCTION update_available_copies();

-- ============================================================================
-- VIEWS FOR REPORTS
-- ============================================================================

-- User Statistics View
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

-- Book Statistics View
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
    COUNT(DISTINCT br.id) as current_reservations
FROM books b
LEFT JOIN authors a ON b.author_id = a.id
LEFT JOIN genres g ON b.genre_id = g.id
LEFT JOIN publishers p ON b.publisher_id = p.id
LEFT JOIN book_copies bc ON b.id = bc.book_id
LEFT JOIN book_transactions bt ON bc.id = bt.book_copy_id
LEFT JOIN book_reservations br ON b.id = br.book_id AND br.status = 'ACTIVE'
WHERE b.is_active = TRUE
GROUP BY b.id, b.title, b.isbn, a.first_name, a.last_name, g.name, p.name, b.total_copies, b.available_copies;

-- ============================================================================
-- SAMPLE DATA INSERTION
-- ============================================================================

-- Insert sample genres
INSERT INTO genres (name, description) VALUES
('Fiction', 'Fictional stories and novels'),
('Non-Fiction', 'Factual and informational books'),
('Science Fiction', 'Futuristic and scientific fiction'),
('Romance', 'Love and relationship stories'),
('Mystery', 'Detective and mystery stories'),
('Biography', 'Life stories of real people'),
('History', 'Historical accounts and events'),
('Technology', 'Books about technology and computing'),
('Literature', 'Classic and contemporary literature'),
('Self-Help', 'Personal development and improvement books');

-- Insert sample authors
INSERT INTO authors (first_name, last_name, biography, nationality, birth_date) VALUES
('George', 'Orwell', 'English novelist and essayist known for 1984 and Animal Farm', 'British', '1903-06-25'),
('Jane', 'Austen', 'English novelist known for romantic fiction', 'British', '1775-12-16'),
('Agatha', 'Christie', 'English writer known for detective novels', 'British', '1890-09-15'),
('Isaac', 'Asimov', 'American writer and professor of biochemistry', 'American', '1920-01-02'),
('J.K.', 'Rowling', 'British author, best known for Harry Potter series', 'British', '1965-07-31'),
('Stephen', 'King', 'American author of horror, supernatural fiction', 'American', '1947-09-21'),
('Mark', 'Twain', 'American writer, humorist, entrepreneur', 'American', '1835-11-30'),
('William', 'Shakespeare', 'English playwright, poet, and actor', 'British', '1564-04-26');

-- Insert sample publishers
INSERT INTO publishers (name, address, contact_email, established_year) VALUES
('Penguin Random House', '1745 Broadway, New York, NY 10019', 'contact@penguin.com', 1927),
('HarperCollins', '195 Broadway, New York, NY 10007', 'info@harpercollins.com', 1989),
('Simon & Schuster', '1230 Avenue of the Americas, New York, NY 10020', 'contact@simonandschuster.com', 1924),
('Macmillan Publishers', '120 Broadway, New York, NY 10271', 'info@macmillan.com', 1843),
('Scholastic Corporation', '557 Broadway, New York, NY 10012', 'contact@scholastic.com', 1920);

-- Insert sample books
INSERT INTO books (title, isbn, description, publication_date, pages, author_id, publisher_id, genre_id, total_copies, available_copies) VALUES
('1984', '978-0-452-28423-4', 'Dystopian social science fiction novel', '1949-06-08', 328, 1, 1, 1, 5, 5),
('Animal Farm', '978-0-452-28424-1', 'Allegorical novella about farm animals', '1945-08-17', 95, 1, 1, 1, 3, 3),
('Pride and Prejudice', '978-0-14-143951-8', 'Romantic novel of manners', '1813-01-28', 279, 2, 1, 4, 4, 4),
('Murder on the Orient Express', '978-0-06-207350-4', 'Detective novel featuring Hercule Poirot', '1934-01-01', 256, 3, 2, 5, 3, 3),
('Foundation', '978-0-553-29335-0', 'Science fiction novel about psychohistory', '1951-05-01', 244, 4, 3, 3, 2, 2),
('Harry Potter and the Philosopher''s Stone', '978-0-7475-3269-9', 'Fantasy novel about a young wizard', '1997-06-26', 223, 5, 4, 1, 6, 6),
('The Shining', '978-0-385-12167-5', 'Horror novel about isolation and madness', '1977-01-28', 447, 6, 1, 1, 2, 2),
('Adventures of Huckleberry Finn', '978-0-486-28061-4', 'Adventure novel about a boy''s journey', '1884-12-10', 366, 7, 2, 1, 3, 3);

-- Insert sample book copies
INSERT INTO book_copies (book_id, copy_number, condition, location, status) VALUES
(1, 'COPY-001', 'EXCELLENT', 'Section A-1', 'AVAILABLE'),
(1, 'COPY-002', 'GOOD', 'Section A-1', 'AVAILABLE'),
(1, 'COPY-003', 'GOOD', 'Section A-1', 'AVAILABLE'),
(1, 'COPY-004', 'FAIR', 'Section A-1', 'AVAILABLE'),
(1, 'COPY-005', 'EXCELLENT', 'Section A-1', 'AVAILABLE'),
(2, 'COPY-001', 'GOOD', 'Section A-1', 'AVAILABLE'),
(2, 'COPY-002', 'EXCELLENT', 'Section A-1', 'AVAILABLE'),
(2, 'COPY-003', 'GOOD', 'Section A-1', 'AVAILABLE'),
(3, 'COPY-001', 'EXCELLENT', 'Section B-2', 'AVAILABLE'),
(3, 'COPY-002', 'GOOD', 'Section B-2', 'AVAILABLE'),
(3, 'COPY-003', 'GOOD', 'Section B-2', 'AVAILABLE'),
(3, 'COPY-004', 'EXCELLENT', 'Section B-2', 'AVAILABLE'),
(4, 'COPY-001', 'GOOD', 'Section C-3', 'AVAILABLE'),
(4, 'COPY-002', 'EXCELLENT', 'Section C-3', 'AVAILABLE'),
(4, 'COPY-003', 'FAIR', 'Section C-3', 'AVAILABLE'),
(5, 'COPY-001', 'EXCELLENT', 'Section D-4', 'AVAILABLE'),
(5, 'COPY-002', 'GOOD', 'Section D-4', 'AVAILABLE'),
(6, 'COPY-001', 'EXCELLENT', 'Section E-5', 'AVAILABLE'),
(6, 'COPY-002', 'EXCELLENT', 'Section E-5', 'AVAILABLE'),
(6, 'COPY-003', 'GOOD', 'Section E-5', 'AVAILABLE'),
(6, 'COPY-004', 'GOOD', 'Section E-5', 'AVAILABLE'),
(6, 'COPY-005', 'EXCELLENT', 'Section E-5', 'AVAILABLE'),
(6, 'COPY-006', 'GOOD', 'Section E-5', 'AVAILABLE'),
(7, 'COPY-001', 'GOOD', 'Section F-6', 'AVAILABLE'),
(7, 'COPY-002', 'EXCELLENT', 'Section F-6', 'AVAILABLE'),
(8, 'COPY-001', 'GOOD', 'Section G-7', 'AVAILABLE'),
(8, 'COPY-002', 'EXCELLENT', 'Section G-7', 'AVAILABLE'),
(8, 'COPY-003', 'FAIR', 'Section G-7', 'AVAILABLE');

-- Grant all permissions to library_user
GRANT ALL ON ALL TABLES IN SCHEMA public TO library_user;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO library_user;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO library_user;

-- Print completion message
SELECT 'Database setup completed successfully!' as status;