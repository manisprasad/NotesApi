package org.manis.notes.controllers;

import org.manis.notes.models.User;
import org.manis.notes.models.UserDetailsResponse;
import org.manis.notes.repo.UserRepo;
import org.manis.notes.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5175/")
@RequestMapping("/user")
public class UserController {

    private final UserRepo userRepo;
    private final UserService userService;

    public UserController(UserRepo userRepo, UserService userService) {
        this.userRepo = userRepo;
        this.userService = userService;
    }

    @GetMapping("/all_user_details")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }


//    UserDetailsResponse
    @GetMapping("/user_details")
    public ResponseEntity<?> getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> user = userRepo.findByUsername(username);

        if (user.isPresent()) {
            // Return user details using the userService
            return ResponseEntity.ok(userService.getUserDetails(username));
        }

        // Return 404 Not Found if user is not present
        return ResponseEntity.notFound().build();
    }


}
