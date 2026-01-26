package com.financetracker.service;

import com.financetracker.dto.auth.AuthResponse;
import com.financetracker.dto.auth.LoginRequest;
import com.financetracker.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
