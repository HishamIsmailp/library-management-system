package com.upcode.lms.auth.service;

import com.upcode.lms.auth.dto.JwtResponseDto;
import com.upcode.lms.auth.dto.LoginRequestDto;
import com.upcode.lms.auth.dto.MailBodyDto;
import com.upcode.lms.auth.entity.ForgotPassword;
import com.upcode.lms.auth.entity.User;
import com.upcode.lms.auth.repository.ForgotPasswordRepository;
import com.upcode.lms.auth.repository.UserRepository;
import com.upcode.lms.common.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;

    int otp = otpGenerator();

    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }


    public JwtResponseDto registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);

        String jwtToken = jwtService.generateToken(savedUser);

        return new JwtResponseDto(
                "Bearer",
                jwtToken,
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    public JwtResponseDto authenticateUser(LoginRequestDto loginRequestDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getUsername(),
                            loginRequestDto.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password");
        }

        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwtToken = jwtService.generateToken(user);

        return new JwtResponseDto(
                "Bearer",
                jwtToken,
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    public String verifyEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("please provide an valid email..!!"));
        MailBodyDto mailBody = MailBodyDto.builder()
                .to(email)
                .text("This is the OTP for your Email Verification : " + otp)
                .subject("OTP for Email Verification")
                .build();

        Optional<ForgotPassword> existingOtp = forgotPasswordRepository.findByUser(user);

        if(existingOtp.isPresent()) {
            ForgotPassword oldOtp = existingOtp.get();
            if (oldOtp.getExpirationTime().before(new Date())) {
                forgotPasswordRepository.delete(oldOtp);
            } else {
                throw new RuntimeException("An OTP was already sent. Try again after expiry.");
            }
        }

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 70 * 1000 ))
                .user(user)
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(fp);

        return "OTP sent for verification to Mail !!";
    }

    public String verifyOtp(Integer otp, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide an valid email!"));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new RuntimeException("Invalid OTP for email: " + email));

        if (fp.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deleteById(fp.getId());
            throw new RuntimeException("OTP has expired!");
        }

        user.setEmailVerified(true);
        userRepository.save(user);

        return "OTP verified";
    }

}
