# Library Management System - Setup Verification Checklist

## ✅ Prerequisites Verification

### 1. Java Development Kit
- [ ] Java 17 or higher installed
- [ ] `java -version` shows Java 17+
- [ ] `javac -version` shows Java 17+
- [ ] JAVA_HOME environment variable set correctly

### 2. Database Setup
- [ ] PostgreSQL 12+ installed and running
- [ ] Database `library_management_db` created
- [ ] User `library_user` created with password `library_password`
- [ ] User has all privileges on the database
- [ ] Can connect to database successfully

### 3. Development Environment
- [ ] IDE installed (IntelliJ IDEA recommended)
- [ ] Gradle wrapper working (`./gradlew --version`)
- [ ] Git configured with user name and email

## 🔧 Project Setup Verification

### 1. Project Structure
- [ ] All package directories created under `src/main/java/com/upcode/lms/`
- [ ] Common utilities and exceptions in place
- [ ] Base entity and configuration classes created
- [ ] User entity and Role enum implemented

### 2. Dependencies and Configuration
- [ ] `build.gradle` contains all required dependencies
- [ ] `application.yml` configured with database settings
- [ ] JWT secret configured (use environment variable in production)
- [ ] Email configuration present (optional for development)

### 3. Build Verification
- [ ] `./gradlew clean build -x test` runs successfully
- [ ] No compilation errors
- [ ] All dependencies resolved correctly

## 🚀 Application Startup

### 1. Database Connection
- [ ] Application starts without database connection errors
- [ ] Tables are created automatically (check logs)
- [ ] Sample users are created on first startup

### 2. Default Users Created
- [ ] Admin user: `admin` / `admin123`
- [ ] Librarian user: `librarian` / `librarian123`
- [ ] Student user: `student` / `student123`
- [ ] Additional test students: `student2-5` / `password123`

### 3. Application Health
- [ ] Application starts on `http://localhost:8080/api`
- [ ] No startup errors in console
- [ ] Application logs show "Started LibraryManagementSystemApplication"

## 🧪 Basic Functionality Tests

### 1. Database Verification
Connect to your PostgreSQL database and verify:
```sql
-- Check if tables are created
\dt

-- Check users table
SELECT username, email, role FROM users;

-- Verify sample data
SELECT COUNT(*) FROM users WHERE role = 'ADMIN';
SELECT COUNT(*) FROM users WHERE role = 'LIBRARIAN';
SELECT COUNT(*) FROM users WHERE role = 'STUDENT';
```

### 2. API Endpoints (When Implemented)
- [ ] Health check endpoint accessible
- [ ] Authentication endpoints respond
- [ ] Proper error handling for invalid requests

## 📝 Development Workflow Setup

### 1. Git Repository
- [ ] Repository cloned successfully
- [ ] Can create feature branches
- [ ] Can commit and push changes
- [ ] `.gitignore` excludes build files and IDE settings

### 2. Code Quality
- [ ] Lombok annotations working (no compilation errors)
- [ ] Bean validation annotations recognized
- [ ] Spring Boot auto-configuration working
- [ ] JPA repositories can be autowired

## 🎯 Team Task Preparation

### For Each Team Member:
- [ ] Assigned module directory structure is clear
- [ ] Understanding of the base classes and utilities
- [ ] Access to project documentation
- [ ] Knowledge of coding standards and conventions

### Rashid (Authentication Module):
- [ ] Understands User entity and Role enum structure
- [ ] UserRepository interface is available
- [ ] Security dependencies are in place
- [ ] Email configuration is ready (optional)

### Sinan (Book Management Module):
- [ ] Book module directory structure ready
- [ ] Understands relationship with User entity
- [ ] File upload dependencies available
- [ ] Image storage configuration planned

### Riluwan (Transaction System):
- [ ] Transaction module structure ready
- [ ] Understands integration with Book and User modules
- [ ] Date utilities available for due date calculations
- [ ] Fine calculation logic dependencies ready

### Muhammed (Reservation & Fine Management):
- [ ] Both module directories ready
- [ ] Understands notification requirements
- [ ] Email service dependencies available
- [ ] Integration points with Transaction module clear

## 🐛 Common Issues and Solutions

### Build Issues:
- **Issue**: Gradle daemon conflicts
  - **Solution**: `./gradlew --stop` then rebuild

- **Issue**: Java version mismatch
  - **Solution**: Ensure JAVA_HOME points to Java 17+

### Database Issues:
- **Issue**: Connection refused
  - **Solution**: Check PostgreSQL is running and credentials are correct

- **Issue**: Tables not created
  - **Solution**: Check `spring.jpa.hibernate.ddl-auto=update` in application.yml

### Application Issues:
- **Issue**: Port already in use
  - **Solution**: Change port in application.yml or kill process using port 8080

- **Issue**: Bean creation errors
  - **Solution**: Check for circular dependencies and missing configurations

## 📊 Success Criteria

### ✅ Setup Complete When:
- [ ] Application builds successfully
- [ ] Application starts without errors
- [ ] Database connection established
- [ ] Sample data loaded
- [ ] All team members can run the application locally
- [ ] Git workflow is established
- [ ] Task assignments are clear

### 📈 Ready for Development When:
- [ ] All verification steps pass
- [ ] Team understands the project structure
- [ ] Development environment is stable
- [ ] Communication channels established
- [ ] Code review process defined

## 🚨 Emergency Contacts

### If You Get Stuck:
1. Check the console logs for specific error messages
2. Verify database connection and credentials
3. Ensure Java 17+ is being used
4. Try rebuilding with `./gradlew clean build`
5. Ask team members for help
6. Check Spring Boot documentation

### Quick Commands Reference:
```bash
# Build project
./gradlew clean build

# Run application
./gradlew bootRun

# Check Java version
java -version

# Check Gradle version
./gradlew --version

# Stop Gradle daemon
./gradlew --stop
```

**Note**: Mark each checkbox as you complete the verification steps. All team members should complete this checklist before starting development work.