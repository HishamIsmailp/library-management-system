package com.upcode.lms.auth.entity;

import com.upcode.lms.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_username", columnList = "username"),
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_student_id", columnList = "student_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Size(max = 15, message = "Phone number cannot exceed 15 characters")
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "student_id", unique = true, length = 20)
    private String studentId;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Builder.Default
    @Column(name = "is_account_non_expired")
    private Boolean accountNonExpired = true;

    @Builder.Default
    @Column(name = "is_account_non_locked")
    private Boolean accountNonLocked = true;

    @Builder.Default
    @Column(name = "is_credentials_non_expired")
    private Boolean credentialsNonExpired = true;

    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    @Builder.Default
    @Column(name = "is_email_verified")
    private Boolean emailVerified = false;

    @Column(name = "email_verification_token")
    private String emailVerificationToken;

    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_token_expiry")
    private LocalDateTime passwordResetTokenExpiry;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Builder.Default
    @Column(name = "login_attempts")
    private Integer loginAttempts = 0;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getAuthority()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired != null ? accountNonExpired : true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked != null ? accountNonLocked : true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired != null ? credentialsNonExpired : true;
    }

    @Override
    public boolean isEnabled() {
        return enabled != null ? enabled : true;
    }

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isLibrarian() {
        return role == Role.LIBRARIAN;
    }

    public boolean isStudent() {
        return role == Role.STUDENT;
    }

    public boolean hasRole(Role targetRole) {
        return this.role == targetRole;
    }

    public boolean hasPermission(String permission) {
        return role.hasPermission(permission);
    }

    public void incrementLoginAttempts() {
        this.loginAttempts = (this.loginAttempts == null) ? 1 : this.loginAttempts + 1;
    }

    public void resetLoginAttempts() {
        this.loginAttempts = 0;
    }

    public boolean isAccountLocked() {
        return !isAccountNonLocked();
    }

    public void lockAccount() {
        this.accountNonLocked = false;
    }

    public void unlockAccount() {
        this.accountNonLocked = true;
        resetLoginAttempts();
    }

    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    public boolean isPasswordResetTokenValid() {
        return passwordResetToken != null && 
               passwordResetTokenExpiry != null && 
               passwordResetTokenExpiry.isAfter(LocalDateTime.now());
    }

    public void clearPasswordResetToken() {
        this.passwordResetToken = null;
        this.passwordResetTokenExpiry = null;
    }

    public void verifyEmail() {
        this.emailVerified = true;
        this.emailVerificationToken = null;
    }

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (loginAttempts == null) {
            loginAttempts = 0;
        }
        if (accountNonExpired == null) {
            accountNonExpired = true;
        }
        if (accountNonLocked == null) {
            accountNonLocked = true;
        }
        if (credentialsNonExpired == null) {
            credentialsNonExpired = true;
        }
        if (enabled == null) {
            enabled = true;
        }
        if (emailVerified == null) {
            emailVerified = false;
        }
    }
}