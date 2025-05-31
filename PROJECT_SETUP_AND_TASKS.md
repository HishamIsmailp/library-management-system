# Library Management System - Project Setup & Task Distribution

## 📋 Project Overview

**Project Name**: Library Management System  
**Group ID**: `com.upcode.lms`  
**Artifact ID**: `library-management-system`  
**Technology Stack**: Spring Boot 3.2.5, PostgreSQL, JWT, Swagger/OpenAPI, Gradle, Java 17

## 🏗️ Project Structure

```
src/main/java/com/upcode/lms/
├── LibraryManagementSystemApplication.java
├── common/                    # Shared utilities and DTOs
│   ├── entity/
│   │   └── BaseEntity.java
│   ├── exception/             # Custom exceptions
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── LibraryBusinessException.java
│   │   └── AuthenticationException.java
│   ├── dto/                   # Common DTOs
│   │   ├── ApiResponse.java
│   │   ├── ErrorResponse.java
│   │   ├── PageRequest.java
│   │   └── PageResponse.java
│   └── utils/                 # Utility classes
│       ├── DateUtils.java
│       └── StringUtils.java
├── config/                    # Configuration classes
│   ├── JpaConfig.java
│   ├── SwaggerConfig.java
│   ├── PasswordConfig.java
│   ├── ModelMapperConfig.java
│   └── DataInitializer.java
├── auth/                      # Authentication & User Management
│   ├── entity/
│   │   ├── User.java
│   │   └── Role.java
│   ├── repository/
│   │   └── UserRepository.java
│   ├── service/
│   ├── controller/
│   │   └── UserContextController.java
│   └── dto/
├── book/                      # Book Management
│   ├── entity/
│   ├── repository/
│   ├── service/
│   ├── controller/
│   └── dto/
├── transaction/               # Issue/Return System
│   ├── entity/
│   ├── repository/
│   ├── service/
│   ├── controller/
│   └── dto/
├── reservation/               # Reservation System
│   ├── entity/
│   ├── repository/
│   ├── service/
│   ├── controller/
│   └── dto/
├── fine/                      # Fine Management
│   ├── entity/
│   ├── repository/
│   ├── service/
│   ├── controller/
│   └── dto/
├── report/                    # Reports & Analytics
│   ├── entity/
│   ├── repository/
│   ├── service/
│   ├── controller/
│   └── dto/
└── category/                  # Sample Entity (Reference Implementation)
    ├── entity/
    │   └── Category.java
    ├── repository/
    │   └── CategoryRepository.java
    ├── service/
    │   └── CategoryService.java
    ├── controller/
    │   └── CategoryController.java
    └── dto/
        ├── CategoryDto.java
        ├── CategoryCreateDto.java
        └── CategoryUpdateDto.java
```

## 👥 Team Task Distribution

### 🎯 **RASHID** - Authentication & User Management Module
**Priority**: HIGH (Foundation for all other modules)  
**Estimated Time**: 1-2 weeks

#### Tasks:
1. **User Repository & Service**
   - Create `UserRepository` interface with custom queries
   - Implement `UserService` with CRUD operations
   - Add user search and filtering functionality

2. **Authentication Service**
   - JWT token generation and validation
   - Login/logout functionality
   - Password reset with email/OTP integration
   - Account lockout mechanism

3. **Security Configuration**
   - Spring Security configuration
   - JWT authentication filter
   - Role-based access control
   - CORS configuration

4. **User Controller & DTOs**
   - Registration endpoint
   - Login/logout endpoints
   - Profile management endpoints
   - Password reset endpoints
   - Create DTOs: `UserRegistrationDto`, `LoginRequestDto`, `UserProfileDto`

4. **Email Service (Optional)**
   - Email verification
   - Password reset emails
   - OTP generation and validation

5. **SecurityUtils Integration**
   - Use `SecurityUtils.getCurrentUser()` for user context
   - Use `SecurityUtils.ensureStaff()` for permission checks
   - Use `SecurityUtils.hasRole(Role.ADMIN)` for role validation

#### Files to Create:
- `auth/repository/UserRepository.java` ✅ **CREATED**
- `auth/service/UserService.java`
- `auth/service/AuthenticationService.java`
- `auth/service/EmailService.java`
- `auth/controller/AuthController.java`
- `auth/controller/UserController.java`
- `auth/dto/UserRegistrationDto.java`
- `auth/dto/LoginRequestDto.java`
- `auth/dto/UserProfileDto.java`
- `config/SecurityConfig.java`
- `config/JwtConfig.java`

---

### 📚 **SINAN** - Book Management Module
**Priority**: HIGH (Core business logic)  
**Estimated Time**: 1-2 weeks

#### Tasks:
1. **Book Entity & Related Entities**
   - Create `Book`, `Author`, `Genre`, `Publisher` entities
   - Establish proper relationships
   - Add book copy management

2. **Book Repository & Service**
   - CRUD operations for books
   - Search functionality (title, author, ISBN, genre)
   - Filter and sort capabilities
   - Book availability tracking

3. **Book Management Controller**
   - Admin/Librarian endpoints for book management
   - Public endpoints for book browsing
   - File upload for book covers
   - Bulk book import functionality

4. **DTOs and Validation**
   - Create comprehensive DTOs with validation
   - Search and filter DTOs
   - Book statistics DTOs

5. **Auto-Documentation**
   - Write clean controllers - SpringDoc handles documentation automatically
   - Add only `@Tag(name = "Module Name")` to controller class
   - Focus on functionality, not manual documentation

6. **SecurityUtils Integration**
   - Use `SecurityUtils.ensureStaff()` for admin/librarian operations
   - Use `SecurityUtils.getCurrentUserId()` for audit trails

#### Files to Create:
- `book/entity/Book.java`
- `book/entity/Author.java`
- `book/entity/Genre.java`
- `book/entity/Publisher.java`
- `book/entity/BookCopy.java`
- `book/repository/BookRepository.java`
- `book/repository/AuthorRepository.java`
- `book/repository/GenreRepository.java`
- `book/service/BookService.java`
- `book/service/FileUploadService.java`
- `book/controller/BookController.java`
- `book/controller/BookSearchController.java`
- `book/dto/BookDto.java`
- `book/dto/BookSearchDto.java`
- `book/dto/BookCreationDto.java`

---

### 🔄 **RILUWAN** - Transaction System (Issue/Return)
**Priority**: HIGH (Core functionality)  
**Estimated Time**: 1-2 weeks

#### Tasks:
1. **Transaction Entity**
   - Create `BookTransaction` entity
   - Transaction status enum
   - Due date calculation
   - Renewal tracking

2. **Transaction Service**
   - Book issue functionality
   - Book return processing
   - Renewal logic
   - Overdue calculation
   - Business rules validation

3. **Transaction Controller**
   - Issue book endpoints
   - Return book endpoints
   - Renewal endpoints
   - Transaction history

4. **Integration with Fine System**
   - Automatic fine calculation
   - Fine payment integration
   - Overdue notifications

5. **SecurityUtils Integration**
   - Use `SecurityUtils.canAccessUserResource(userId)` for user transactions
   - Use `SecurityUtils.ensureStaff()` for issue/return operations
   - Use `SecurityUtils.getCurrentUserId()` for transaction logging

6. **Auto-Documentation** 
   - Write clean, descriptive method names
   - Use proper DTOs with validation
   - SpringDoc auto-generates comprehensive docs

#### Files to Create:
- `transaction/entity/BookTransaction.java`
- `transaction/entity/TransactionStatus.java`
- `transaction/repository/TransactionRepository.java`
- `transaction/service/TransactionService.java`
- `transaction/service/DueDateService.java`
- `transaction/controller/TransactionController.java`
- `transaction/dto/IssueBookDto.java`
- `transaction/dto/ReturnBookDto.java`
- `transaction/dto/TransactionHistoryDto.java`

---

### 🎫 **MUHAMMED** - Reservation & Fine Management
**Priority**: MEDIUM (Enhancement features)  
**Estimated Time**: 1-2 weeks

#### Tasks:
1. **Reservation System**
   - Create `BookReservation` entity
   - Reservation queue management
   - Auto-assignment when book available
   - Reservation expiry handling

2. **Fine Management System**
   - Create `Fine` entity
   - Fine calculation service
   - Payment tracking
   - Fine reports

3. **Controllers and Services**
   - Reservation endpoints
   - Fine management endpoints
   - Payment processing

4. **Notification System**
   - Email notifications for reservations
   - Overdue reminders
   - Fine payment reminders

5. **SecurityUtils Integration**
   - Use `SecurityUtils.canAccessUserResource(userId)` for user reservations/fines
   - Use `SecurityUtils.ensureStaff()` for fine management
   - Use `SecurityUtils.getCurrentUserId()` for reservation ownership

6. **Auto-Documentation**
   - Follow the clean Category controller pattern
   - Use meaningful method and parameter names
   - SpringDoc handles all documentation automatically

#### Files to Create:
- `reservation/entity/BookReservation.java`
- `reservation/entity/ReservationStatus.java`
- `reservation/repository/ReservationRepository.java`
- `reservation/service/ReservationService.java`
- `reservation/controller/ReservationController.java`
- `fine/entity/Fine.java`
- `fine/entity/FineStatus.java`
- `fine/repository/FineRepository.java`
- `fine/service/FineService.java`
- `fine/controller/FineController.java`
- `fine/dto/FineDto.java`
- `reservation/dto/ReservationDto.java`

---

## 🗄️ Database Design

### Database Schema Overview
The system uses **PostgreSQL** with a comprehensive relational design supporting:
- User management with role-based access
- Book catalog with authors, genres, and publishers
- Book copy tracking for individual items
- Transaction management for issue/return operations
- Reservation system with queue management
- Fine management with payment tracking
- Activity logging for audit trails

### Key Database Files:
- `database/setup.sql` - Complete database setup script
- `database/verify.sql` - Database verification queries
- `database/sample_queries.sql` - Useful query examples
- `DATABASE_DESIGN.md` - Comprehensive database documentation

### Entity Relationship Summary:
```
USERS ←→ BOOK_TRANSACTIONS ←→ BOOK_COPIES ←→ BOOKS
  ↓           ↓                                ↑
FINES    ACTIVITY_LOGS                   AUTHORS/GENRES/PUBLISHERS
  ↓
BOOK_RESERVATIONS
```

### Database Features:
- **Automated triggers** for available copy updates
- **Indexes** for optimal query performance  
- **Views** for common reporting needs
- **Constraints** for data integrity
- **Audit trails** for all critical operations
- **Sample data** with 8 books, 28 copies, multiple authors/genres

### Additional Resources Created:
- **Complete Category Entity** - Single, clean reference implementation
- **SecurityUtils Class** - Get current user context and enforce permissions
- **Auto-Documentation** - SpringDoc generates docs automatically
- **Sample Queries** - 20+ useful database query examples
- **Clean Architecture** - No confusing duplicates or complex annotations

## 🚀 Setup Instructions

### Prerequisites
- Java 17+
- PostgreSQL 12+
- Gradle 8+
- IDE (IntelliJ IDEA recommended)

### Database Setup
1. **Create PostgreSQL database:**
```sql
CREATE DATABASE library_management_db;
CREATE USER library_user WITH ENCRYPTED PASSWORD 'library_password';
GRANT ALL PRIVILEGES ON DATABASE library_management_db TO library_user;
```

2. **Run the database setup script:**
```bash
psql -U library_user -d library_management_db -f database/setup.sql
```

3. **Verify database setup:**
```bash
psql -U library_user -d library_management_db -f database/verify.sql
```

4. Update `application.yml` with your database credentials if needed

### Running the Application
1. Clone the repository
2. Navigate to project directory
3. Run: `./gradlew bootRun`
4. Application will start on: `http://localhost:8080/api`

### Testing Endpoints
- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html` ✅
- **Test Endpoint**: `GET /api/test/hello` (Basic test)
- **Categories API**: `GET /api/categories` (Public endpoint)
- **Current User**: `GET /api/test/current-user` (Authenticated)
- **User Context**: `GET /api/auth/user-context/me` (Authenticated)

### Default Test Users
- **Admin**: `admin` / `admin123`
- **Librarian**: `librarian` / `librarian123`
- **Student**: `student` / `student123`
- **Test Students**: `student2-5` / `password123`

## 📝 Development Guidelines

### Code Standards
1. Follow Java naming conventions
2. Use Lombok annotations to reduce boilerplate
3. Implement proper validation using Bean Validation
4. Write comprehensive JavaDoc comments
5. Create unit tests for services
6. Handle exceptions properly using custom exceptions

### Git Workflow
1. Create feature branches: `feature/module-name`
2. Make atomic commits with clear messages
3. Create pull requests for code review
4. Merge only after review approval

### API Standards
1. Use RESTful endpoints
2. Consistent response format using `ApiResponse<T>`
3. Proper HTTP status codes
4. Pagination for list endpoints
5. Input validation and error handling

## 🔐 Security Features
- **JWT-based authentication** with Swagger integration
- **Role-based access control** (ADMIN, LIBRARIAN, STUDENT)
- **SecurityUtils class** for easy user context access
- **Permission-based security** with method-level enforcement
- **Password encryption** using BCrypt
- **Account lockout** after failed attempts
- **Email verification** support
- **Resource ownership** validation
- **CORS configuration**

## 🎯 New Features Added

### ✅ Swagger/OpenAPI Auto-Documentation
- **Automatic API documentation** with interactive UI (like .NET Core)
- **JWT authentication** support in Swagger UI
- **Zero manual annotations required** - SpringDoc auto-detects everything
- **Clean, minimal code** - focus on functionality, not documentation
- **Professional documentation** generated from clean controllers

### ✅ SecurityUtils Utility Class
```java
// Get current user information
User currentUser = SecurityUtils.getCurrentUserOrThrow();
String username = SecurityUtils.getCurrentUsernameOrThrow();
Role userRole = SecurityUtils.getCurrentUserRoleOrThrow();

// Role and permission checking
boolean isAdmin = SecurityUtils.isAdmin();
boolean isStaff = SecurityUtils.isStaff();
boolean canAccess = SecurityUtils.canAccessUserResource(userId);

// Security enforcement
SecurityUtils.ensureStaff(); // Throws exception if not admin/librarian
SecurityUtils.ensureCanAccessUserResource(userId);
```

### ✅ Complete Sample Entity (Category)
- **Full CRUD operations** with validation
- **Hierarchical relationships** (parent-child categories)
- **30+ repository methods** demonstrating best practices
- **Comprehensive service layer** with business logic
- **Clean REST API** with auto-generated documentation
- **Security integration** with role-based access
- **Error handling** with custom exceptions

### 📊 Additional Documentation
- `SWAGGER_AND_SECURITY_GUIDE.md` - Guide for auto-documentation and SecurityUtils
- `database/setup.sql` - Complete database setup script
- `database/verify.sql` - Database verification queries
- `database/sample_queries.sql` - 20+ useful query examples

## 📊 API Endpoints Overview

### Authentication (`/api/auth`)
- `POST /register` - User registration
- `POST /login` - User login
- `POST /logout` - User logout
- `POST /forgot-password` - Password reset request
- `POST /reset-password` - Password reset confirmation

### Books (`/api/books`)
- `GET /` - List books (paginated, searchable)
- `GET /{id}` - Get book details
- `POST /` - Add new book (Admin/Librarian)
- `PUT /{id}` - Update book (Admin/Librarian)
- `DELETE /{id}` - Delete book (Admin)

### Transactions (`/api/transactions`)
- `POST /issue` - Issue book
- `POST /return` - Return book
- `POST /renew` - Renew book
- `GET /history` - Transaction history

### Reservations (`/api/reservations`)
- `POST /` - Make reservation
- `GET /my-reservations` - User's reservations
- `DELETE /{id}` - Cancel reservation

### Fines (`/api/fines`)
- `GET /my-fines` - User's fines
- `POST /pay` - Pay fine
- `GET /all` - All fines (Admin/Librarian)

## 🎯 Success Metrics
- ✅ **All CRUD operations** working with Category example
- ✅ **Authentication and authorization** implemented with SecurityUtils
- ✅ **Business rules** enforced with custom exceptions
- ✅ **Clean, maintainable code** following established patterns
- ✅ **Comprehensive error handling** with global exception handler
- ✅ **API documentation** with Swagger/OpenAPI integration
- ✅ **Database design** with complete ERD and sample data
- ✅ **Security utilities** for easy user context management
- ✅ **Reference implementation** with Category entity

## 📚 Learning Resources
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

## 🤝 Team Communication
- Daily standup meetings
- Code review requirements
- Help each other with blockers
- Share knowledge and best practices

## 🚀 Getting Started Guide

### 1. **For Rashid (Authentication)**
- Study `SecurityUtils` class and `UserRepository`
- Implement JWT authentication following Spring Security patterns
- Use clean controller patterns (no complex Swagger annotations needed)
- Integrate with existing User entity and role system

### 2. **For Sinan (Book Management)**
- Follow the `Category` entity pattern for Book entities
- Use `CategoryRepository` as template for BookRepository methods
- Study `CategoryService` for business logic patterns
- Copy `CategoryController` clean, minimal style (auto-documentation)

### 3. **For Riluwan (Transaction System)**
- Use `SecurityUtils.canAccessUserResource()` for user transaction validation
- Follow the service transaction patterns from CategoryService
- Integrate with the existing User and Book entities
- Use DateUtils for due date calculations

### 4. **For Muhammed (Reservation & Fine)**
- Study the Category hierarchical relationship for reservation queue
- Use SecurityUtils for user ownership validation
- Follow the pagination patterns from CategoryService
- Integrate email notifications using the existing email configuration

### 📚 Key Resources
- **Live Example**: Category module (`/api/categories`) - Clean, minimal annotations
- **Auto-Documentation**: `http://localhost:8080/api/swagger-ui.html`
- **SecurityUtils**: `com.upcode.lms.common.utils.SecurityUtils`
- **Database Setup**: `database/setup.sql`
- **Sample Queries**: `database/sample_queries.sql`
- **Clean Patterns**: Follow CategoryController for all modules

**Your foundation is solid - now build something amazing! 🚀💪**