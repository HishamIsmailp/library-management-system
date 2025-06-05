package com.upcode.lms.auth.controller;

import com.upcode.lms.auth.entity.Role;
import com.upcode.lms.auth.entity.User;
import com.upcode.lms.common.dto.ApiResponse;
import com.upcode.lms.common.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth/user-context")
@RequiredArgsConstructor
@Tag(name = "User Context", description = "APIs for getting current user information")
public class UserContextController {

    @Operation(
        summary = "Get current user information",
        description = "Retrieve information about the currently authenticated user",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Current user information retrieved successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - No valid authentication token"
        )
    })
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        log.info("GET /auth/user-context/me - User: {}", SecurityUtils.getCurrentUsername().orElse("unknown"));
        
        User currentUser = SecurityUtils.getCurrentUserOrThrow();
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", currentUser.getId());
        userInfo.put("username", currentUser.getUsername());
        userInfo.put("email", currentUser.getEmail());
        userInfo.put("fullName", currentUser.getFullName());
        userInfo.put("firstName", currentUser.getFirstName());
        userInfo.put("lastName", currentUser.getLastName());
        userInfo.put("role", currentUser.getRole());
        userInfo.put("studentId", currentUser.getStudentId());
        userInfo.put("phoneNumber", currentUser.getPhoneNumber());
        userInfo.put("isEmailVerified", currentUser.getEmailVerified());
        userInfo.put("lastLogin", currentUser.getLastLogin());
        userInfo.put("createdAt", currentUser.getCreatedAt());
        
        return ResponseEntity.ok(ApiResponse.success(userInfo, "Current user information retrieved successfully"));
    }

    @Operation(
        summary = "Get current user's role and permissions",
        description = "Retrieve role information and permission summary for the current user",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/role-info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUserRoleInfo() {
        log.info("GET /auth/user-context/role-info - User: {}", SecurityUtils.getCurrentUsername().orElse("unknown"));
        
        Role currentRole = SecurityUtils.getCurrentUserRoleOrThrow();
        
        Map<String, Object> roleInfo = new HashMap<>();
        roleInfo.put("role", currentRole);
        roleInfo.put("roleCode", currentRole.getCode());
        roleInfo.put("roleDescription", currentRole.getDescription());
        roleInfo.put("authority", currentRole.getAuthority());
        
        // Permission checks
        Map<String, Boolean> permissions = new HashMap<>();
        permissions.put("isAdmin", SecurityUtils.isAdmin());
        permissions.put("isLibrarian", SecurityUtils.isLibrarian());
        permissions.put("isStudent", SecurityUtils.isStudent());
        permissions.put("isStaff", SecurityUtils.isStaff());
        
        // Sample permission checks
        permissions.put("canManageBooks", SecurityUtils.hasPermission("BOOK_MANAGE"));
        permissions.put("canViewReports", SecurityUtils.hasPermission("REPORT_VIEW"));
        permissions.put("canManageUsers", SecurityUtils.hasPermission("USER_MANAGE"));
        permissions.put("canIssueBooks", SecurityUtils.hasPermission("BOOK_ISSUE"));
        
        roleInfo.put("permissions", permissions);
        
        return ResponseEntity.ok(ApiResponse.success(roleInfo, "Role information retrieved successfully"));
    }

    @Operation(
        summary = "Get current user's basic profile",
        description = "Retrieve basic profile information for the current user",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUserProfile() {
        log.info("GET /auth/user-context/profile - User: {}", SecurityUtils.getCurrentUsername().orElse("unknown"));
        
        User currentUser = SecurityUtils.getCurrentUserOrThrow();
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", currentUser.getId());
        profile.put("username", currentUser.getUsername());
        profile.put("email", SecurityUtils.getCurrentUserEmail().orElse(null));
        profile.put("fullName", SecurityUtils.getCurrentUserFullName().orElse(null));
        profile.put("studentId", SecurityUtils.getCurrentUserStudentId().orElse(null));
        profile.put("role", SecurityUtils.getCurrentUserRole().orElse(null));
        profile.put("dateOfBirth", currentUser.getDateOfBirth());
        profile.put("address", currentUser.getAddress());
        profile.put("profileImageUrl", currentUser.getProfileImageUrl());
        
        return ResponseEntity.ok(ApiResponse.success(profile, "User profile retrieved successfully"));
    }

    @Operation(
        summary = "Check if current user can access resource",
        description = "Check if the current user can access a specific user's resource",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/can-access/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkResourceAccess(
            @PathVariable Long userId
    ) {
        log.info("GET /auth/user-context/can-access/{} - Current User: {}", 
                userId, SecurityUtils.getCurrentUsername().orElse("unknown"));
        
        Map<String, Object> accessInfo = new HashMap<>();
        accessInfo.put("userId", userId);
        accessInfo.put("currentUserId", SecurityUtils.getCurrentUserId());
        accessInfo.put("canAccess", SecurityUtils.canAccessUserResource(userId));
        accessInfo.put("isOwner", SecurityUtils.isResourceOwner(userId));
        accessInfo.put("canModify", SecurityUtils.canModifyResource(userId));
        accessInfo.put("isStaff", SecurityUtils.isStaff());
        
        return ResponseEntity.ok(ApiResponse.success(accessInfo, "Resource access information retrieved"));
    }

    @Operation(
        summary = "Admin only endpoint",
        description = "Endpoint accessible only by administrators",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> adminOnlyEndpoint() {
        log.info("GET /auth/user-context/admin-only - Admin User: {}", 
                SecurityUtils.getCurrentUsername().orElse("unknown"));
        
        SecurityUtils.ensureRole(Role.ADMIN);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Hello Admin! You have access to this endpoint.", 
                "Admin endpoint accessed successfully"));
    }

    @Operation(
        summary = "Staff only endpoint",
        description = "Endpoint accessible by administrators and librarians",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/staff-only")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<String>> staffOnlyEndpoint() {
        log.info("GET /auth/user-context/staff-only - Staff User: {}", 
                SecurityUtils.getCurrentUsername().orElse("unknown"));
        
        SecurityUtils.ensureStaff();
        
        String role = SecurityUtils.getCurrentUserRole().map(Role::getDescription).orElse("Unknown");
        
        return ResponseEntity.ok(ApiResponse.success(
                "Hello " + role + "! You have staff access to this endpoint.", 
                "Staff endpoint accessed successfully"));
    }

    @Operation(
        summary = "Student only endpoint",
        description = "Endpoint accessible only by students",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/student-only")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<String>> studentOnlyEndpoint() {
        log.info("GET /auth/user-context/student-only - Student User: {}", 
                SecurityUtils.getCurrentUsername().orElse("unknown"));
        
        SecurityUtils.ensureRole(Role.STUDENT);
        
        String studentId = SecurityUtils.getCurrentUserStudentId().orElse("N/A");
        
        return ResponseEntity.ok(ApiResponse.success(
                "Hello Student! Your student ID is: " + studentId, 
                "Student endpoint accessed successfully"));
    }

    @Operation(
        summary = "Get authentication status",
        description = "Check the current authentication status and basic info"
    )
    @GetMapping("/auth-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAuthStatus() {
        log.info("GET /auth/user-context/auth-status");
        
        Map<String, Object> authStatus = new HashMap<>();
        authStatus.put("isAuthenticated", SecurityUtils.isAuthenticated());
        authStatus.put("isAnonymous", SecurityUtils.isAnonymous());
        
        if (SecurityUtils.isAuthenticated()) {
            authStatus.put("username", SecurityUtils.getCurrentUsername().orElse(null));
            authStatus.put("userId", SecurityUtils.getCurrentUserId());
            authStatus.put("role", SecurityUtils.getCurrentUserRole().orElse(null));
        }
        
        return ResponseEntity.ok(ApiResponse.success(authStatus, "Authentication status retrieved"));
    }
}