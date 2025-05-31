package com.upcode.lms.test;

import com.upcode.lms.common.dto.ApiResponse;
import com.upcode.lms.common.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/test")
@Tag(name = "Test API", description = "Simple test endpoints for verification")
public class TestController {

    @GetMapping("/hello")
    public ResponseEntity<ApiResponse<String>> hello() {
        log.info("GET /test/hello");
        return ResponseEntity.ok(ApiResponse.success("Hello from Library Management System!"));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatus() {
        log.info("GET /test/status");
        
        Map<String, Object> status = new HashMap<>();
        status.put("application", "Library Management System");
        status.put("status", "running");
        status.put("timestamp", LocalDateTime.now());
        status.put("version", "1.0.0");
        
        return ResponseEntity.ok(ApiResponse.success(status, "Application status retrieved"));
    }

    @GetMapping("/auth-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAuthStatus() {
        log.info("GET /test/auth-status");
        
        Map<String, Object> authStatus = new HashMap<>();
        authStatus.put("isAuthenticated", SecurityUtils.isAuthenticated());
        authStatus.put("isAnonymous", SecurityUtils.isAnonymous());
        authStatus.put("securityEnabled", true);
        authStatus.put("loginPageDisabled", true);
        
        if (SecurityUtils.isAuthenticated()) {
            authStatus.put("username", SecurityUtils.getCurrentUsername().orElse("unknown"));
            authStatus.put("role", SecurityUtils.getCurrentUserRole().orElse(null));
        }
        
        return ResponseEntity.ok(ApiResponse.success(authStatus, "Authentication status retrieved"));
    }

    @GetMapping("/current-user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        log.info("GET /test/current-user");
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", SecurityUtils.getCurrentUsername().orElse("unknown"));
        userInfo.put("userId", SecurityUtils.getCurrentUserId().orElse(null));
        userInfo.put("role", SecurityUtils.getCurrentUserRole().orElse(null));
        userInfo.put("isAdmin", SecurityUtils.isAdmin());
        userInfo.put("isLibrarian", SecurityUtils.isLibrarian());
        userInfo.put("isStudent", SecurityUtils.isStudent());
        userInfo.put("isStaff", SecurityUtils.isStaff());
        
        return ResponseEntity.ok(ApiResponse.success(userInfo, "Current user info retrieved"));
    }
}