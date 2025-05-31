package com.upcode.lms.auth.repository;

import com.upcode.lms.auth.entity.Role;
import com.upcode.lms.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Basic finders
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByStudentId(String studentId);
    
    Optional<User> findByEmailVerificationToken(String token);
    
    Optional<User> findByPasswordResetToken(String token);
    
    // Existence checks
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByStudentId(String studentId);
    
    // Role-based queries
    List<User> findByRole(Role role);
    
    Page<User> findByRole(Role role, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    List<User> findActiveUsersByRole(@Param("role") Role role);
    
    // Active users
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    Page<User> findAllActiveUsers(Pageable pageable);
    
    List<User> findByIsActiveTrue();
    
    // Search functionality
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.studentId) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "u.isActive = true")
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.studentId) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "u.role = :role AND u.isActive = true")
    Page<User> searchUsersByRole(@Param("search") String search, @Param("role") Role role, Pageable pageable);
    
    // Account status queries
    List<User> findByEnabledFalse();
    
    List<User> findByAccountNonLockedFalse();
    
    @Query("SELECT u FROM User u WHERE u.loginAttempts >= :maxAttempts")
    List<User> findUsersWithExcessiveLoginAttempts(@Param("maxAttempts") int maxAttempts);
    
    // Email verification
    List<User> findByEmailVerifiedFalse();
    
    @Query("SELECT u FROM User u WHERE u.emailVerificationToken IS NOT NULL AND u.emailVerified = false")
    List<User> findUsersWithPendingEmailVerification();
    
    // Password reset
    @Query("SELECT u FROM User u WHERE u.passwordResetToken IS NOT NULL AND u.passwordResetTokenExpiry > :now")
    List<User> findUsersWithValidPasswordResetToken(@Param("now") LocalDateTime now);
    
    @Query("SELECT u FROM User u WHERE u.passwordResetTokenExpiry < :now AND u.passwordResetToken IS NOT NULL")
    List<User> findUsersWithExpiredPasswordResetToken(@Param("now") LocalDateTime now);
    
    // Statistics
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
    long countActiveUsersByRole(@Param("role") Role role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :since")
    long countUsersRegisteredSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.lastLogin >= :since")
    long countUsersLoggedInSince(@Param("since") LocalDateTime since);
    
    // Update queries
    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :loginTime WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime);
    
    @Modifying
    @Query("UPDATE User u SET u.loginAttempts = :attempts WHERE u.id = :userId")
    void updateLoginAttempts(@Param("userId") Long userId, @Param("attempts") int attempts);
    
    @Modifying
    @Query("UPDATE User u SET u.accountNonLocked = :locked WHERE u.id = :userId")
    void updateAccountLockStatus(@Param("userId") Long userId, @Param("locked") boolean locked);
    
    @Modifying
    @Query("UPDATE User u SET u.emailVerified = true, u.emailVerificationToken = null WHERE u.id = :userId")
    void markEmailAsVerified(@Param("userId") Long userId);
    
    @Modifying
    @Query("UPDATE User u SET u.passwordResetToken = null, u.passwordResetTokenExpiry = null WHERE u.id = :userId")
    void clearPasswordResetToken(@Param("userId") Long userId);
    
    @Modifying
    @Query("UPDATE User u SET u.isActive = :active WHERE u.id = :userId")
    void updateActiveStatus(@Param("userId") Long userId, @Param("active") boolean active);
    
    // Cleanup queries
    @Modifying
    @Query("UPDATE User u SET u.passwordResetToken = null, u.passwordResetTokenExpiry = null WHERE u.passwordResetTokenExpiry < :now")
    void cleanupExpiredPasswordResetTokens(@Param("now") LocalDateTime now);
    
    @Modifying
    @Query("DELETE FROM User u WHERE u.emailVerified = false AND u.createdAt < :cutoffDate AND u.emailVerificationToken IS NOT NULL")
    void deleteUnverifiedUsersOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
}