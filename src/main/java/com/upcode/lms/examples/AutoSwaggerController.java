package com.upcode.lms.examples;

import com.upcode.lms.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * This controller demonstrates SpringDoc's AUTO-DETECTION capabilities.
 * NO manual Swagger annotations needed - everything is generated automatically!
 * 
 * SpringDoc automatically detects:
 * - HTTP methods (GET, POST, PUT, DELETE)
 * - Request/Response types
 * - Path parameters
 * - Query parameters
 * - Request bodies
 * - Response codes
 * - Basic documentation from method names
 */
@Slf4j
@RestController
@RequestMapping("/auto-swagger-demo")
public class AutoSwaggerController {

    // ============================================================================
    // AUTO-DETECTED ENDPOINTS (No Swagger annotations needed!)
    // ============================================================================

    /**
     * SpringDoc automatically detects:
     * - GET method
     * - Returns ApiResponse<String>
     * - Generates 200 response documentation
     */
    @GetMapping("/basic")
    public ResponseEntity<ApiResponse<String>> getBasicInfo() {
        return ResponseEntity.ok(ApiResponse.success("Auto-detected endpoint!"));
    }

    /**
     * SpringDoc automatically detects:
     * - Path parameter {id}
     * - Parameter type (Long)
     * - Return type
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserById(@PathVariable Long id) {
        Map<String, Object> user = Map.of(
                "id", id,
                "name", "Auto-detected User",
                "timestamp", LocalDateTime.now()
        );
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * SpringDoc automatically detects:
     * - Query parameters
     * - Parameter types
     * - Default values
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<String>>> searchItems(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<String> results = List.of(
                "Result 1 for: " + query,
                "Result 2 for: " + query,
                "Page: " + page + ", Size: " + size
        );
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    /**
     * SpringDoc automatically detects:
     * - POST method
     * - Request body type (Map)
     * - 201 status code from ResponseEntity.status()
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createItem(@RequestBody Map<String, Object> data) {
        Map<String, Object> created = Map.of(
                "id", 123,
                "data", data,
                "created", LocalDateTime.now()
        );
        return ResponseEntity.status(201).body(ApiResponse.success(created, "Item created"));
    }

    /**
     * SpringDoc automatically detects:
     * - PUT method
     * - Path parameter + Request body
     * - Return type
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<String>> updateItem(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates
    ) {
        return ResponseEntity.ok(ApiResponse.success("Updated item " + id + " with " + updates.size() + " fields"));
    }

    /**
     * SpringDoc automatically detects:
     * - DELETE method
     * - 204 No Content response
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        log.info("Deleting item with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * SpringDoc automatically detects:
     * - Multiple query parameters
     * - Optional parameters
     * - Complex return types
     */
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Map<String, Object>>> filterItems(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        Map<String, Object> filters = Map.of(
                "category", category != null ? category : "all",
                "status", status != null ? status : "active",
                "sortBy", sortBy,
                "sortOrder", sortOrder,
                "resultsCount", 42
        );
        return ResponseEntity.ok(ApiResponse.success(filters));
    }

    // ============================================================================
    // WHAT SPRINGDOC AUTO-GENERATES:
    // ============================================================================
    
    /*
    For the above endpoints, SpringDoc automatically creates:
    
    1. OpenAPI JSON/YAML documentation
    2. Swagger UI with interactive testing
    3. Request/Response schemas
    4. Parameter documentation
    5. HTTP status codes
    6. Content types (application/json)
    7. Basic operation summaries from method names
    
    Available at:
    - Swagger UI: http://localhost:8080/api/swagger-ui.html
    - OpenAPI JSON: http://localhost:8080/api/api-docs
    - OpenAPI YAML: http://localhost:8080/api/api-docs.yaml
    
    All WITHOUT writing a single Swagger annotation!
    */

    // ============================================================================
    // OPTIONAL: MINIMAL ENHANCEMENTS (Just for better descriptions)
    // ============================================================================
    
    /**
     * You can add minimal JavaDoc comments for better descriptions.
     * SpringDoc will use these automatically!
     */
    @GetMapping("/enhanced")
    public ResponseEntity<ApiResponse<String>> getEnhancedEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("Enhanced with JavaDoc only!"));
    }

    /**
     * Get system statistics.
     * This method returns comprehensive system statistics including performance metrics.
     * 
     * @return System statistics data
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemStats() {
        Map<String, Object> stats = Map.of(
                "uptime", "2 hours",
                "requests", 1500,
                "memory", "256MB",
                "cpu", "12%"
        );
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}