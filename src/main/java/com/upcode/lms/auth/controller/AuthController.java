package com.upcode.lms.auth.controller;

import com.upcode.lms.auth.dto.JwtResponseDto;
import com.upcode.lms.auth.dto.LoginRequestDto;
import com.upcode.lms.auth.entity.User;
import com.upcode.lms.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth/user")
@RequiredArgsConstructor
class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<JwtResponseDto> registerUser(@RequestBody User user) {
        JwtResponseDto jwtResponse = authService.registerUser(user);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> loginUser(@RequestBody LoginRequestDto loginRequest) {
        JwtResponseDto jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("verifyMail/{email}")
    public ResponseEntity<String> verifyEMail(@PathVariable String email) {
        return ResponseEntity.ok(authService.verifyEmail(email));
    }


    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        try {
            String result = authService.verifyOtp(otp, email);
            return ResponseEntity.ok(result);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(e.getMessage());
        }
    }

}
