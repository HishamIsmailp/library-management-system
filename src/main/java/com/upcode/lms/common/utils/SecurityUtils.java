package com.upcode.lms.common.utils;

import com.upcode.lms.auth.entity.Role;
import com.upcode.lms.auth.entity.User;
import com.upcode.lms.common.exception.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Optional;

public class SecurityUtils {
    
    private SecurityUtils() {
        // Utility class
    }
    
    /**
     * Get the current authentication object
     */
    public static Optional<Authentication> getCurrentAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }
    
    /**
     * Get the current authenticated user
     */
    public static Optional<User> getCurrentUser() {
        return getCurrentAuthentication()
                .filter(auth -> auth.getPrincipal() instanceof User)
                .map(auth -> (User) auth.getPrincipal());
    }
    
    /**
     * Get the current authenticated user or throw exception
     */
    public static User getCurrentUserOrThrow() {
        return getCurrentUser()
                .orElseThrow(() -> AuthenticationException.invalidToken());
    }
    
    /**
     * Get current user's username
     */
    public static Optional<String> getCurrentUsername() {
        return getCurrentAuthentication()
                .map(Authentication::getName);
    }
    
    /**
     * Get current user's username or throw exception
     */
    public static String getCurrentUsernameOrThrow() {
        return getCurrentUsername()
                .orElseThrow(() -> AuthenticationException.invalidToken());
    }
    
    /**
     * Get current user's ID
     */
    public static User getCurrentUserId() {
        return getCurrentUser()
                .map(User::getId);
    }
    
    /**
     * Get current user's ID or throw exception
     */
    public static Long getCurrentUserIdOrThrow() {
        return getCurrentUserId()
                .orElseThrow(() -> AuthenticationException.invalidToken());
    }
    
    /**
     * Get current user's role
     */
    public static Optional<Role> getCurrentUserRole() {
        return getCurrentUser()
                .map(User::getRole);
    }
    
    /**
     * Get current user's role or throw exception
     */
    public static Role getCurrentUserRoleOrThrow() {
        return getCurrentUserRole()
                .orElseThrow(() -> AuthenticationException.invalidToken());
    }
    
    /**
     * Get current user's authorities
     */
    public static Collection<? extends GrantedAuthority> getCurrentAuthorities() {
        return getCurrentAuthentication()
                .map(Authentication::getAuthorities)
                .orElse(null);
    }
    
    /**
     * Check if current user is authenticated
     */
    public static boolean isAuthenticated() {
        return getCurrentAuthentication()
                .map(Authentication::isAuthenticated)
                .orElse(false);
    }
    
    /**
     * Check if current user is anonymous
     */
    public static boolean isAnonymous() {
        return getCurrentAuthentication()
                .map(auth -> "anonymousUser".equals(auth.getPrincipal().toString()))
                .orElse(true);
    }
    
    /**
     * Check if current user has specific role
     */
    public static boolean hasRole(Role role) {
        return getCurrentUserRole()
                .map(userRole -> userRole == role)
                .orElse(false);
    }
    
    /**
     * Check if current user has any of the specified roles
     */
    public static boolean hasAnyRole(Role... roles) {
        Optional<Role> currentRole = getCurrentUserRole();
        if (currentRole.isEmpty()) {
            return false;
        }
        
        for (Role role : roles) {
            if (currentRole.get() == role) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if current user is admin
     */
    public static boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }
    
    /**
     * Check if current user is librarian
     */
    public static boolean isLibrarian() {
        return hasRole(Role.LIBRARIAN);
    }
    
    /**
     * Check if current user is student
     */
    public static boolean isStudent() {
        return hasRole(Role.STUDENT);
    }
    
    /**
     * Check if current user is admin or librarian (staff)
     */
    public static boolean isStaff() {
        return hasAnyRole(Role.ADMIN, Role.LIBRARIAN);
    }
    
    /**
     * Check if current user has permission
     */
    public static boolean hasPermission(String permission) {
        return getCurrentUser()
                .map(user -> user.hasPermission(permission))
                .orElse(false);
    }
    
    /**
     * Check if current user can access resource (either own resource or staff)
     */
    public static boolean canAccessUserResource(Long userId) {
        if (isStaff()) {
            return true;
        }
        
        return getCurrentUserId()
                .map(currentUserId -> currentUserId.equals(userId))
                .orElse(false);
    }
    
    /**
     * Ensure current user can access resource or throw exception
     */
    public static void ensureCanAccessUserResource(Long userId) {
        if (!canAccessUserResource(userId)) {
            throw AuthenticationException.insufficientPermissions();
        }
    }
    
    /**
     * Ensure current user has specific role or throw exception
     */
    public static void ensureRole(Role role) {
        if (!hasRole(role)) {
            throw AuthenticationException.insufficientPermissions();
        }
    }
    
    /**
     * Ensure current user has any of the specified roles or throw exception
     */
    public static void ensureAnyRole(Role... roles) {
        if (!hasAnyRole(roles)) {
            throw AuthenticationException.insufficientPermissions();
        }
    }
    
    /**
     * Ensure current user is staff (admin or librarian) or throw exception
     */
    public static void ensureStaff() {
        if (!isStaff()) {
            throw AuthenticationException.insufficientPermissions();
        }
    }
    
    /**
     * Get current user's full name
     */
    public static Optional<String> getCurrentUserFullName() {
        return getCurrentUser()
                .map(User::getFullName);
    }
    
    /**
     * Get current user's email
     */
    public static Optional<String> getCurrentUserEmail() {
        return getCurrentUser()
                .map(User::getEmail);
    }
    
    /**
     * Get current user's student ID (if student)
     */
    public static Optional<String> getCurrentUserStudentId() {
        return getCurrentUser()
                .map(User::getStudentId);
    }
    
    /**
     * Check if current user is the owner of the resource
     */
    public static boolean isResourceOwner(Long resourceOwnerId) {
        return getCurrentUserId()
                .map(currentUserId -> currentUserId.equals(resourceOwnerId))
                .orElse(false);
    }
    
    /**
     * Check if current user can modify resource (owner or staff)
     */
    public static boolean canModifyResource(Long resourceOwnerId) {
        return isStaff() || isResourceOwner(resourceOwnerId);
    }
    
    /**
     * Ensure current user can modify resource or throw exception
     */
    public static void ensureCanModifyResource(Long resourceOwnerId) {
        if (!canModifyResource(resourceOwnerId)) {
            throw AuthenticationException.insufficientPermissions();
        }
    }
}