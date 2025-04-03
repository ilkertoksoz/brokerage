package com.tr.ing.brokerage.service;

import com.tr.ing.brokerage.request.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> registerUser(RegisterRequest request);
}
