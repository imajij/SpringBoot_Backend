package com.financetracker.service.impl;

import com.financetracker.dto.auth.AuthResponse;
import com.financetracker.dto.auth.LoginRequest;
import com.financetracker.dto.auth.RegisterRequest;
import com.financetracker.entity.User;
import com.financetracker.exception.BadRequestException;
import com.financetracker.exception.DuplicateResourceException;
import com.financetracker.repository.UserRepository;
import com.financetracker.security.JwtTokenProvider;
import com.financetracker.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtTokenProvider jwtTokenProvider;
        private final AuthenticationManager authenticationManager;
        private final com.financetracker.service.EmailService emailService;

        @Override
        public AuthResponse register(RegisterRequest request) {
                log.info("Registering new user with email: {}", request.getEmail());

                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new DuplicateResourceException("User", "email", request.getEmail());
                }

                User user = User.builder()
                                .email(request.getEmail().toLowerCase())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .firstName(request.getFirstName())
                                .lastName(request.getLastName())
                                .roles(List.of("USER"))
                                .active(true)
                                .build();

                User savedUser = userRepository.save(user);
                log.info("User registered successfully with ID: {}", savedUser.getId());

                // Send welcome email
                String subject = "Welcome to Finance Tracker!";
                String body = String.format("Hello %s,\n\nThank you for registering at Finance Tracker. We're excited to have you on board!\n\nBest regards,\nFinance Tracker Team", savedUser.getFirstName());
                emailService.sendEmail(savedUser.getEmail(), subject, body);

                // Authenticate and generate token
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
                );

                String token = jwtTokenProvider.generateToken(authentication);

                return buildAuthResponse(token, savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt for email: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        String token = jwtTokenProvider.generateToken(authentication);
        log.info("User logged in successfully: {}", request.getEmail());

        return buildAuthResponse(token, user);
    }

    private AuthResponse buildAuthResponse(String token, User user) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(AuthResponse.UserDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .build())
                .build();
    }
}
