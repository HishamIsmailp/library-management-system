-- Database Verification Script
-- Run these queries to verify the database setup

-- 1. Check if all tables are created
SELECT 
    table_name,
    table_type
FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;

-- 2. Check table row counts
SELECT 
    'users' as table_name, COUNT(*) as row_count FROM users
UNION ALL
SELECT 'authors' as table_name, COUNT(*) as row_count FROM authors
UNION ALL
SELECT 'genres' as table_name, COUNT(*) as row_count FROM genres
UNION ALL
SELECT 'publishers' as table_name, COUNT(*) as row_count FROM publishers
UNION ALL
SELECT 'books' as table_name, COUNT(*) as row_count FROM books
UNION ALL
SELECT 'book_copies' as table_name, COUNT(*) as row_count FROM book_copies
UNION ALL
SELECT 'book_transactions' as table_name, COUNT(*) as row_count FROM book_transactions
UNION ALL
SELECT 'book_reservations' as table_name, COUNT(*) as row_count FROM book_reservations
UNION ALL
SELECT 'fines' as table_name, COUNT(*) as row_count FROM fines
UNION ALL
SELECT 'activity_logs' as table_name, COUNT(*) as row_count FROM activity_logs
ORDER BY table_name;

-- 3. Check indexes
SELECT 
    schemaname,
    tablename,
    indexname,
    indexdef
FROM pg_indexes 
WHERE schemaname = 'public'
ORDER BY tablename, indexname;

-- 4. Check triggers
SELECT 
    event_object_table as table_name,
    trigger_name,
    event_manipulation,
    action_timing
FROM information_schema.triggers
WHERE trigger_schema = 'public'
ORDER BY event_object_table, trigger_name;

-- 5. Check views
SELECT 
    table_name as view_name,
    view_definition
FROM information_schema.views
WHERE table_schema = 'public'
ORDER BY table_name;

-- 6. Check foreign key constraints
SELECT
    tc.table_name,
    tc.constraint_name,
    tc.constraint_type,
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM information_schema.table_constraints AS tc
JOIN information_schema.key_column_usage AS kcu
    ON tc.constraint_name = kcu.constraint_name
    AND tc.table_schema = kcu.table_schema
JOIN information_schema.constraint_column_usage AS ccu
    ON ccu.constraint_name = tc.constraint_name
    AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY' 
    AND tc.table_schema = 'public'
ORDER BY tc.table_name, tc.constraint_name;

-- 7. Verify sample data
SELECT 'Sample Genres:' as description;
SELECT id, name, description FROM genres ORDER BY id LIMIT 5;

SELECT 'Sample Authors:' as description;
SELECT id, first_name, last_name, nationality FROM authors ORDER BY id LIMIT 5;

SELECT 'Sample Publishers:' as description;
SELECT id, name, established_year FROM publishers ORDER BY id LIMIT 3;

SELECT 'Sample Books:' as description;
SELECT 
    b.id,
    b.title,
    CONCAT(a.first_name, ' ', a.last_name) as author,
    g.name as genre,
    b.total_copies,
    b.available_copies
FROM books b
JOIN authors a ON b.author_id = a.id
JOIN genres g ON b.genre_id = g.id
ORDER BY b.id LIMIT 5;

-- 8. Test book statistics view
SELECT 'Book Statistics View Test:' as description;
SELECT 
    title,
    author_name,
    genre,
    total_copies,
    available_copies,
    total_issues
FROM book_statistics
ORDER BY title LIMIT 5;

-- 9. Check if triggers are working (test available_copies calculation)
SELECT 'Available Copies Verification:' as description;
SELECT 
    b.title,
    b.total_copies,
    b.available_copies,
    COUNT(bc.id) as actual_copies,
    COUNT(CASE WHEN bc.status = 'AVAILABLE' THEN 1 END) as actual_available
FROM books b
LEFT JOIN book_copies bc ON b.id = bc.book_id AND bc.is_active = TRUE
GROUP BY b.id, b.title, b.total_copies, b.available_copies
ORDER BY b.title;

-- 10. Database size and performance info
SELECT 
    pg_size_pretty(pg_database_size('library_management_db')) as database_size;

SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as table_size
FROM pg_tables 
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;