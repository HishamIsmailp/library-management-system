# Swagger Integration & Security Utilities Guide

## 🔧 Swagger/OpenAPI Auto-Documentation

### ✅ Automatic Documentation (Like .NET Core)
SpringDoc automatically generates comprehensive API documentation **without manual annotations**. Just write clean controllers and DTOs!

#### What SpringDoc Auto-Detects:
- ✅ **HTTP Methods**: GET, POST, PUT, DELETE, PATCH
- ✅ **Request/Response Types**: From method signatures
- ✅ **Path & Query Parameters**: From @PathVariable and @RequestParam
- ✅ **Validation Rules**: From Bean Validation annotations
- ✅ **Security Requirements**: From @PreAuthorize annotations
- ✅ **HTTP Status Codes**: From ResponseEntity usage

#### Access URLs:
- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`
- **API Docs JSON**: `http://localhost:8080/api/api-docs`

#### Dependencies:
```gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0'
implementation 'io.swagger.core.v3:swagger-annotations:2.2.10'
```

## 🔐 Security Utilities

### SecurityUtils Class
Comprehensive utility class for getting current user context and enforcing security.

#### Key Features
- Get current authenticated user information
- Role-based access control
- Permission checking
- Resource ownership validation
- Security enforcement methods

### Core Methods

#### User Information
```java
// Get current user
Optional<User> getCurrentUser()
User getCurrentUserOrThrow()

// Get user details
Optional<String> getCurrentUsername()
Optional<Long> getCurrentUserId()
Optional<Role> getCurrentUserRole()
Optional<String> getCurrentUserEmail()
Optional<String> getCurrentUserFullName()
```

#### Authentication Status
```java
boolean isAuthenticated()
boolean isAnonymous()
```

#### Role Checking
```java
boolean hasRole(Role role)
boolean hasAnyRole(Role... roles)
boolean isAdmin()
boolean isLibrarian() 
boolean isStudent()
boolean isStaff() // Admin or Librarian
```

#### Permission Checking
```java
boolean hasPermission(String permission)
boolean canAccessUserResource(Long userId)
boolean canModifyResource(Long resourceOwnerId)
boolean isResourceOwner(Long resourceOwnerId)
```

#### Security Enforcement
```java
void ensureRole(Role role)
void ensureAnyRole(Role... roles)
void ensureStaff()
void ensureCanAccessUserResource(Long userId)
void ensureCanModifyResource(Long resourceOwnerId)
```

### Usage Examples

#### In Service Classes
```java
@Service
public class BookService {
    
    public BookDto createBook(BookCreateDto dto) {
        // Ensure user is staff (admin or librarian)
        SecurityUtils.ensureStaff();
        
        // Get current user for audit
        User currentUser = SecurityUtils.getCurrentUserOrThrow();
        
        // Create book logic...
    }
    
    public List<BookDto> getUserBooks(Long userId) {
        // Ensure user can access this resource
        SecurityUtils.ensureCanAccessUserResource(userId);
        
        // Get books logic...
    }
}
```

#### In Controllers
```java
@RestController
public class UserController {
    
    @GetMapping("/users/{id}/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long id) {
        // Check if current user can access this profile
        if (!SecurityUtils.canAccessUserResource(id)) {
            throw AuthenticationException.insufficientPermissions();
        }
        
        // Get profile logic...
    }
}
```

## 📋 Sample Entity Implementation

The project includes a complete **Category** entity as a reference implementation following all established patterns.

### Entity Structure
```
Category Entity
├── entity/Category.java          # JPA entity with relationships
├── repository/CategoryRepository.java # Comprehensive repository methods
├── service/CategoryService.java       # Business logic with security
├── controller/CategoryController.java # REST API with Swagger docs
└── dto/
    ├── CategoryDto.java              # Response DTO
    ├── CategoryCreateDto.java        # Creation request DTO
    └── CategoryUpdateDto.java        # Update request DTO
```

### Key Features Demonstrated
- **Hierarchical Relationships** - Self-referencing parent-child
- **Comprehensive Repository** - 30+ query methods
- **Security Integration** - Role-based access control
- **Full Swagger Documentation** - All endpoints documented
- **Validation** - Bean validation throughout
- **Audit Trail** - BaseEntity with timestamps
- **Soft Delete** - Logical deletion with is_active flag
- **Business Logic** - Circular reference prevention
- **Error Handling** - Custom exceptions with proper HTTP codes

### Repository Pattern
```java
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Basic finders
    Optional<Category> findByNameAndIsActiveTrue(String name);
    
    // Hierarchical queries
    List<Category> findByParentCategoryIsNullAndIsActiveTrueOrderByDisplayOrderAscNameAsc();
    
    // Search functionality
    @Query("SELECT c FROM Category c WHERE ...")
    Page<Category> searchCategories(@Param("search") String search, Pageable pageable);
    
    // Custom update operations
    @Modifying
    @Query("UPDATE Category c SET c.displayOrder = :displayOrder WHERE c.id = :categoryId")
    void updateDisplayOrder(@Param("categoryId") Long categoryId, @Param("displayOrder") Integer displayOrder);
}
```

### Service Pattern
```java
@Service
@Transactional(readOnly = true)
public class CategoryService {
    
    @Transactional
    public CategoryDto createCategory(CategoryCreateDto createDto) {
        // Security check
        SecurityUtils.ensureStaff();
        
        // Business validation
        validateCategoryNameUniqueness(createDto.getName(), null);
        
        // Create and save
        Category category = Category.builder()
                .name(createDto.getName().trim())
                .description(createDto.getDescription())
                // ... other fields
                .build();
        
        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }
}
```

## 📖 API Documentation Standards

### Controller Annotations
```java
@Tag(name = "Category Management", description = "APIs for managing book categories")
@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Operation(
        summary = "Create new category",
        description = "Create a new category. Requires ADMIN or LIBRARIAN role.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(
            @Parameter(description = "Category creation data") 
            @Valid @RequestBody CategoryCreateDto createDto
    ) {
        // Implementation
    }
}
```

### DTO Documentation
```java
@Schema(description = "Category creation request")
public class CategoryCreateDto {
    
    @Schema(description = "Category name", example = "Science Fiction", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;
    
    @Schema(description = "Category description", example = "Books about futuristic and scientific themes")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}
```

## 🧪 Testing with Swagger UI

### Authentication Flow
1. **Access Swagger UI**: `http://localhost:8080/api/swagger-ui.html`
2. **Login** (if auth endpoints are implemented):
   ```json
   POST /auth/login
   {
     "username": "admin",
     "password": "admin123"
   }
   ```
3. **Copy JWT Token** from response
4. **Authorize in Swagger**: Click "Authorize" button, enter `Bearer YOUR_TOKEN`

### Test Endpoints
1. **Get Categories**: `GET /categories` (Public)
2. **Create Category**: `POST /categories` (Requires Auth)
3. **Update Category**: `PUT /categories/{id}` (Requires Auth)
4. **User Context**: `GET /auth/user-context/me` (Shows current user)

### Sample Test Data
```json
// Create Category
{
  "name": "Programming",
  "description": "Books about software development and programming languages",
  "colorCode": "#2196F3",
  "icon": "code",
  "isFeatured": true
}

// Update Category
{
  "name": "Software Development",
  "description": "Updated description for programming books",
  "colorCode": "#4CAF50",
  "icon": "laptop-code",
  "isFeatured": false
}
```

## 🔒 Security Configuration

### Role-Based Access
```java
// Method-level security
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
@PreAuthorize("isAuthenticated()")

// In service using SecurityUtils
SecurityUtils.ensureStaff(); // Admin or Librarian
SecurityUtils.ensureRole(Role.ADMIN); // Admin only
SecurityUtils.ensureCanAccessUserResource(userId); // Owner or Staff
```

### Permission System
```java
// Role-based permissions (in Role enum)
public boolean hasPermission(String permission) {
    return switch (this) {
        case ADMIN -> true; // Admin has all permissions
        case LIBRARIAN -> permission.startsWith("BOOK_") || 
                         permission.startsWith("TRANSACTION_") || 
                         permission.startsWith("FINE_") ||
                         permission.startsWith("REPORT_");
        case STUDENT -> permission.startsWith("BOOK_READ") || 
                       permission.startsWith("RESERVATION_") ||
                       permission.startsWith("PROFILE_");
    };
}

// Usage
SecurityUtils.hasPermission("BOOK_MANAGE")
SecurityUtils.hasPermission("REPORT_VIEW")
```

### Resource Ownership
```java
// Check if user owns resource
boolean isOwner = SecurityUtils.isResourceOwner(resourceOwnerId);

// Allow access if owner or staff
boolean canAccess = SecurityUtils.canAccessUserResource(userId);

// Enforce access control
SecurityUtils.ensureCanModifyResource(resourceOwnerId);
```

## 🎯 Best Practices

### Service Layer Security
```java
@Service
@Transactional(readOnly = true)
public class ExampleService {
    
    public ExampleDto getExample(Long id) {
        // Always check permissions first
        SecurityUtils.ensureCanAccessUserResource(ownerId);
        
        // Then proceed with business logic
        Example example = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Example", "id", id));
            
        return convertToDto(example);
    }
    
    @Transactional
    public ExampleDto createExample(ExampleCreateDto dto) {
        // Enforce role requirements
        SecurityUtils.ensureStaff();
        
        // Log who performed the action
        String currentUser = SecurityUtils.getCurrentUsernameOrThrow();
        log.info("Creating example by user: {}", currentUser);
        
        // Business logic...
    }
}
```

### Controller Layer Security
```java
@RestController
public class ExampleController {
    
    @Operation(summary = "Get user-specific data")
    @GetMapping("/users/{userId}/examples")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ExampleDto>>> getUserExamples(
            @PathVariable Long userId) {
        
        // Additional security check beyond @PreAuthorize
        if (!SecurityUtils.canAccessUserResource(userId)) {
            throw AuthenticationException.insufficientPermissions();
        }
        
        // Service call...
    }
}
```

### Error Handling
```java
// Consistent error responses
@ExceptionHandler(AuthenticationException.class)
public ResponseEntity<ErrorResponse> handleAuthenticationException(
        AuthenticationException ex, WebRequest request) {
    
    HttpStatus status = determineAuthStatus(ex.getErrorCode());
    
    ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error("Authentication Error")
            .message(ex.getMessage())
            .errorCode(ex.getErrorCode())
            .module(ex.getModule())
            .build();
    
    return new ResponseEntity<>(errorResponse, status);
}
```

## 📋 Quick Reference

### SecurityUtils Cheat Sheet
```java
// Current user info
User user = SecurityUtils.getCurrentUserOrThrow();
String username = SecurityUtils.getCurrentUsernameOrThrow();
Long userId = SecurityUtils.getCurrentUserIdOrThrow();
Role role = SecurityUtils.getCurrentUserRoleOrThrow();

// Role checks
boolean isAdmin = SecurityUtils.isAdmin();
boolean isStaff = SecurityUtils.isStaff();
boolean hasRole = SecurityUtils.hasRole(Role.LIBRARIAN);

// Permission checks
boolean canManage = SecurityUtils.hasPermission("BOOK_MANAGE");
boolean canAccess = SecurityUtils.canAccessUserResource(userId);

// Enforcement (throws exceptions)
SecurityUtils.ensureStaff();
SecurityUtils.ensureRole(Role.ADMIN);
SecurityUtils.ensureCanAccessUserResource(userId);
```

### Swagger Annotation Quick Reference
```java
@Tag(name = "Module Name", description = "Description")
@Operation(summary = "Brief summary", description = "Detailed description")
@ApiResponse(responseCode = "200", description = "Success description")
@Parameter(description = "Parameter description")
@Schema(description = "Field description", example = "Example value")
@SecurityRequirement(name = "Bearer Authentication")
```

This comprehensive integration provides a solid foundation for secure, well-documented APIs with proper role-based access control and user context management.