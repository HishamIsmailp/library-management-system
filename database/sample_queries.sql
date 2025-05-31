-- Library Management System - Sample Queries
-- Useful queries for common operations and reports

-- ============================================================================
-- USER MANAGEMENT QUERIES
-- ============================================================================

-- 1. Find all active users by role
SELECT 
    u.id,
    u.username,
    u.first_name,
    u.last_name,
    u.email,
    u.student_id,
    u.created_at
FROM users u
WHERE u.role = 'STUDENT' 
    AND u.is_active = TRUE 
    AND u.is_enabled = TRUE
ORDER BY u.last_name, u.first_name;

-- 2. Search users by name or email
SELECT 
    u.id,
    u.username,
    CONCAT(u.first_name, ' ', u.last_name) as full_name,
    u.email,
    u.role,
    u.last_login
FROM users u
WHERE (
    LOWER(u.first_name) LIKE LOWER('%john%') OR
    LOWER(u.last_name) LIKE LOWER('%john%') OR
    LOWER(u.email) LIKE LOWER('%john%')
) AND u.is_active = TRUE
ORDER BY u.last_name, u.first_name;

-- 3. Users with pending email verification
SELECT 
    u.username,
    u.email,
    u.created_at,
    CURRENT_DATE - u.created_at::date as days_since_registration
FROM users u
WHERE u.is_email_verified = FALSE 
    AND u.email_verification_token IS NOT NULL
ORDER BY u.created_at DESC;

-- ============================================================================
-- BOOK MANAGEMENT QUERIES
-- ============================================================================

-- 4. Most popular books (by issue count)
SELECT 
    b.title,
    CONCAT(a.first_name, ' ', a.last_name) as author,
    g.name as genre,
    COUNT(bt.id) as total_issues,
    b.available_copies,
    b.total_copies
FROM books b
JOIN authors a ON b.author_id = a.id
LEFT JOIN genres g ON b.genre_id = g.id
LEFT JOIN book_copies bc ON b.id = bc.book_id
LEFT JOIN book_transactions bt ON bc.id = bt.book_copy_id
WHERE b.is_active = TRUE
GROUP BY b.id, b.title, a.first_name, a.last_name, g.name, b.available_copies, b.total_copies
ORDER BY total_issues DESC, b.title
LIMIT 20;

-- 5. Books with low availability (less than 2 copies available)
SELECT 
    b.title,
    CONCAT(a.first_name, ' ', a.last_name) as author,
    b.total_copies,
    b.available_copies,
    (b.total_copies - b.available_copies) as issued_copies
FROM books b
JOIN authors a ON b.author_id = a.id
WHERE b.available_copies < 2 
    AND b.is_active = TRUE
ORDER BY b.available_copies ASC, b.title;

-- 6. Search books by multiple criteria
SELECT 
    b.id,
    b.title,
    CONCAT(a.first_name, ' ', a.last_name) as author,
    g.name as genre,
    p.name as publisher,
    b.isbn,
    b.publication_date,
    b.available_copies
FROM books b
JOIN authors a ON b.author_id = a.id
LEFT JOIN genres g ON b.genre_id = g.id
LEFT JOIN publishers p ON b.publisher_id = p.id
WHERE (
    LOWER(b.title) LIKE LOWER('%harry%') OR
    LOWER(a.first_name) LIKE LOWER('%harry%') OR
    LOWER(a.last_name) LIKE LOWER('%harry%') OR
    LOWER(g.name) LIKE LOWER('%fantasy%')
) AND b.is_active = TRUE
ORDER BY b.title;

-- 7. Books by genre with statistics
SELECT 
    g.name as genre,
    COUNT(b.id) as total_books,
    SUM(b.total_copies) as total_copies,
    SUM(b.available_copies) as available_copies,
    ROUND(AVG(b.pages), 0) as avg_pages
FROM genres g
LEFT JOIN books b ON g.id = b.genre_id AND b.is_active = TRUE
WHERE g.is_active = TRUE
GROUP BY g.id, g.name
ORDER BY total_books DESC;

-- ============================================================================
-- TRANSACTION QUERIES
-- ============================================================================

-- 8. Currently issued books
SELECT 
    CONCAT(u.first_name, ' ', u.last_name) as user_name,
    u.student_id,
    u.email,
    b.title,
    bt.issue_date,
    bt.due_date,
    CURRENT_DATE - bt.due_date as days_overdue,
    CASE 
        WHEN bt.due_date < CURRENT_DATE THEN 'OVERDUE'
        WHEN bt.due_date = CURRENT_DATE THEN 'DUE TODAY'
        ELSE 'ACTIVE'
    END as status
FROM book_transactions bt
JOIN users u ON bt.user_id = u.id
JOIN book_copies bc ON bt.book_copy_id = bc.id
JOIN books b ON bc.book_id = b.id
WHERE bt.status = 'ISSUED'
ORDER BY bt.due_date ASC, u.last_name;

-- 9. Overdue books report
SELECT 
    CONCAT(u.first_name, ' ', u.last_name) as user_name,
    u.student_id,
    u.phone_number,
    u.email,
    b.title,
    bt.issue_date,
    bt.due_date,
    CURRENT_DATE - bt.due_date as days_overdue,
    (CURRENT_DATE - bt.due_date) * 5.00 as calculated_fine
FROM book_transactions bt
JOIN users u ON bt.user_id = u.id
JOIN book_copies bc ON bt.book_copy_id = bc.id
JOIN books b ON bc.book_id = b.id
WHERE bt.status = 'ISSUED' 
    AND bt.due_date < CURRENT_DATE
ORDER BY days_overdue DESC, u.last_name;

-- 10. User's borrowing history
SELECT 
    b.title,
    CONCAT(a.first_name, ' ', a.last_name) as author,
    bt.issue_date,
    bt.due_date,
    bt.return_date,
    bt.status,
    bt.renewal_count,
    CASE 
        WHEN bt.return_date IS NOT NULL THEN bt.return_date - bt.issue_date
        ELSE CURRENT_DATE - bt.issue_date
    END as days_borrowed
FROM book_transactions bt
JOIN book_copies bc ON bt.book_copy_id = bc.id
JOIN books b ON bc.book_id = b.id
JOIN authors a ON b.author_id = a.id
WHERE bt.user_id = 3  -- Replace with actual user ID
ORDER BY bt.issue_date DESC;

-- 11. Books due in next 7 days
SELECT 
    CONCAT(u.first_name, ' ', u.last_name) as user_name,
    u.email,
    b.title,
    bt.due_date,
    bt.due_date - CURRENT_DATE as days_until_due
FROM book_transactions bt
JOIN users u ON bt.user_id = u.id
JOIN book_copies bc ON bt.book_copy_id = bc.id
JOIN books b ON bc.book_id = b.id
WHERE bt.status = 'ISSUED'
    AND bt.due_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '7 days'
ORDER BY bt.due_date ASC;

-- ============================================================================
-- RESERVATION QUERIES
-- ============================================================================

-- 12. Active reservations queue
SELECT 
    br.queue_position,
    CONCAT(u.first_name, ' ', u.last_name) as user_name,
    u.student_id,
    b.title,
    br.reservation_date,
    br.expiry_date,
    CURRENT_DATE - br.reservation_date as days_waiting
FROM book_reservations br
JOIN users u ON br.user_id = u.id
JOIN books b ON br.book_id = b.id
WHERE br.status = 'ACTIVE'
ORDER BY b.title, br.queue_position;

-- 13. Expired reservations
SELECT 
    CONCAT(u.first_name, ' ', u.last_name) as user_name,
    u.email,
    b.title,
    br.reservation_date,
    br.expiry_date,
    CURRENT_DATE - br.expiry_date as days_expired
FROM book_reservations br
JOIN users u ON br.user_id = u.id
JOIN books b ON br.book_id = b.id
WHERE br.expiry_date < CURRENT_DATE 
    AND br.status = 'ACTIVE'
ORDER BY days_expired DESC;

-- ============================================================================
-- FINE MANAGEMENT QUERIES
-- ============================================================================

-- 14. Outstanding fines by user
SELECT 
    CONCAT(u.first_name, ' ', u.last_name) as user_name,
    u.student_id,
    u.email,
    COUNT(f.id) as total_fines,
    SUM(f.fine_amount) as total_amount,
    SUM(CASE WHEN f.status = 'PENDING' THEN f.fine_amount ELSE 0 END) as pending_amount,
    SUM(CASE WHEN f.status = 'PAID' THEN f.fine_amount ELSE 0 END) as paid_amount
FROM users u
LEFT JOIN fines f ON u.id = f.user_id AND f.is_active = TRUE
WHERE u.is_active = TRUE
GROUP BY u.id, u.first_name, u.last_name, u.student_id, u.email
HAVING SUM(CASE WHEN f.status = 'PENDING' THEN f.fine_amount ELSE 0 END) > 0
ORDER BY pending_amount DESC;

-- 15. Fine payment history
SELECT 
    CONCAT(u.first_name, ' ', u.last_name) as user_name,
    f.fine_reason,
    f.fine_amount,
    f.payment_date,
    f.payment_amount,
    f.payment_method,
    f.payment_reference
FROM fines f
JOIN users u ON f.user_id = u.id
WHERE f.status = 'PAID'
    AND f.payment_date >= CURRENT_DATE - INTERVAL '30 days'
ORDER BY f.payment_date DESC;

-- ============================================================================
-- REPORTING QUERIES
-- ============================================================================

-- 16. Monthly circulation report
SELECT 
    DATE_TRUNC('month', bt.issue_date) as month,
    COUNT(*) as books_issued,
    COUNT(DISTINCT bt.user_id) as unique_users,
    COUNT(DISTINCT bc.book_id) as unique_books,
    COUNT(CASE WHEN bt.return_date IS NOT NULL THEN 1 END) as books_returned,
    COUNT(CASE WHEN bt.due_date < CURRENT_DATE AND bt.status = 'ISSUED' THEN 1 END) as overdue_count
FROM book_transactions bt
JOIN book_copies bc ON bt.book_copy_id = bc.id
WHERE bt.issue_date >= CURRENT_DATE - INTERVAL '12 months'
GROUP BY DATE_TRUNC('month', bt.issue_date)
ORDER BY month DESC;

-- 17. Author popularity report
SELECT 
    CONCAT(a.first_name, ' ', a.last_name) as author_name,
    COUNT(DISTINCT b.id) as books_in_library,
    SUM(b.total_copies) as total_copies,
    COUNT(bt.id) as total_issues,
    ROUND(COUNT(bt.id)::numeric / NULLIF(SUM(b.total_copies), 0), 2) as issues_per_copy
FROM authors a
JOIN books b ON a.id = b.author_id AND b.is_active = TRUE
LEFT JOIN book_copies bc ON b.id = bc.book_id
LEFT JOIN book_transactions bt ON bc.id = bt.book_copy_id
WHERE a.is_active = TRUE
GROUP BY a.id, a.first_name, a.last_name
ORDER BY total_issues DESC
LIMIT 15;

-- 18. Daily activity summary
SELECT 
    CURRENT_DATE as report_date,
    COUNT(CASE WHEN bt.issue_date = CURRENT_DATE THEN 1 END) as books_issued_today,
    COUNT(CASE WHEN bt.return_date = CURRENT_DATE THEN 1 END) as books_returned_today,
    COUNT(CASE WHEN br.reservation_date = CURRENT_DATE THEN 1 END) as reservations_made_today,
    COUNT(CASE WHEN u.created_at::date = CURRENT_DATE THEN 1 END) as new_users_today,
    COUNT(CASE WHEN f.payment_date = CURRENT_DATE THEN 1 END) as fines_paid_today,
    SUM(CASE WHEN f.payment_date = CURRENT_DATE THEN f.payment_amount ELSE 0 END) as fine_revenue_today
FROM book_transactions bt
CROSS JOIN book_reservations br
CROSS JOIN users u
CROSS JOIN fines f;

-- 19. Top 10 books this month
SELECT 
    b.title,
    CONCAT(a.first_name, ' ', a.last_name) as author,
    g.name as genre,
    COUNT(bt.id) as issues_this_month,
    b.available_copies
FROM books b
JOIN authors a ON b.author_id = a.id
LEFT JOIN genres g ON b.genre_id = g.id
LEFT JOIN book_copies bc ON b.id = bc.book_id
LEFT JOIN book_transactions bt ON bc.id = bt.book_copy_id 
    AND bt.issue_date >= DATE_TRUNC('month', CURRENT_DATE)
WHERE b.is_active = TRUE
GROUP BY b.id, b.title, a.first_name, a.last_name, g.name, b.available_copies
ORDER BY issues_this_month DESC, b.title
LIMIT 10;

-- 20. System health check
SELECT 
    'Total Users' as metric, COUNT(*)::text as value FROM users WHERE is_active = TRUE
UNION ALL
SELECT 'Total Books', COUNT(*)::text FROM books WHERE is_active = TRUE
UNION ALL
SELECT 'Total Book Copies', COUNT(*)::text FROM book_copies WHERE is_active = TRUE
UNION ALL
SELECT 'Currently Issued', COUNT(*)::text FROM book_transactions WHERE status = 'ISSUED'
UNION ALL
SELECT 'Overdue Books', COUNT(*)::text FROM book_transactions 
    WHERE status = 'ISSUED' AND due_date < CURRENT_DATE
UNION ALL
SELECT 'Active Reservations', COUNT(*)::text FROM book_reservations WHERE status = 'ACTIVE'
UNION ALL
SELECT 'Pending Fines', COUNT(*)::text FROM fines WHERE status = 'PENDING'
UNION ALL
SELECT 'Total Fine Amount', CONCAT('$', SUM(fine_amount)::text) FROM fines WHERE status = 'PENDING';