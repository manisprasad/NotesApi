package org.manis.notes.services;

import org.manis.notes.models.AuthenticationResponse;
import org.manis.notes.models.User;
import org.manis.notes.repo.UserRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthenticationService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(User request) {
        // Check if the user already exists
        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            return new AuthenticationResponse("Username already exists", null);
        }

        // Create a new user object and set its fields
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedDate(new Date());
        // Save the new user to the database
        userRepo.save(user);

        // Generate a JWT token for the newly registered user
        String token = jwtService.generateToken(user);

        // Return an AuthenticationResponse with a success message and the token
        return new AuthenticationResponse("User registered successfully", token);
    }

    public AuthenticationResponse authenticate(User request) {
        // Authenticate the user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Retrieve the user from the database
        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        // Generate a JWT token for the authenticated user
        String token = jwtService.generateToken(user);

        // Return an AuthenticationResponse with a success message and the token
        return new AuthenticationResponse("User authenticated successfully", token);
    }
}
