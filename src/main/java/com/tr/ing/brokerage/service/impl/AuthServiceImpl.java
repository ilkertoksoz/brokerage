package com.tr.ing.brokerage.service.impl;

import com.tr.ing.brokerage.entity.Role;
import com.tr.ing.brokerage.entity.User;
import com.tr.ing.brokerage.enums.RoleUser;
import com.tr.ing.brokerage.exception.UserAlreadyExistException;
import com.tr.ing.brokerage.repository.RoleRepository;
import com.tr.ing.brokerage.repository.UserRepository;
import com.tr.ing.brokerage.request.RegisterRequest;
import com.tr.ing.brokerage.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ResponseEntity<?> registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistException("Username is already taken!");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        Set<Role> roles = resolveRoles(request.getRoles());
        user.setRoles(roles);

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    private Set<Role> resolveRoles(Set<String> requestedRoles) {
        Set<Role> roles = new HashSet<>();

        if (requestedRoles == null || requestedRoles.isEmpty()) {
            roles.add(getRole(RoleUser.ROLE_USER));
            return roles;
        }

        requestedRoles.forEach(role -> {
            switch (role.toUpperCase()) {
                case "ROLE_ADMIN":
                    roles.add(getRole(RoleUser.ROLE_ADMIN));
                    break;
                case "ROLE_MODERATOR":
                    roles.add(getRole(RoleUser.ROLE_MODERATOR));
                    break;
                default:
                    roles.add(getRole(RoleUser.ROLE_USER));
            }
        });

        return roles;
    }

    private Role getRole(RoleUser roleUser) {
        return roleRepository.findByRoleUser(roleUser)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleUser));
    }
}