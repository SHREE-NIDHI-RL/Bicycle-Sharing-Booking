package com.campusbike.service;

import com.campusbike.model.User;
import com.campusbike.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Default admin credentials
    private static final String ADMIN1_EMAIL = "admin1@campus.edu";
    private static final String ADMIN1_PASSWORD = "admin123";
    private static final String ADMIN2_EMAIL = "admin2@campus.edu";
    private static final String ADMIN2_PASSWORD = "admin456";

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        // Restrict admin registration - only allow User role
        if ("Admin".equals(user.getRole())) {
            throw new RuntimeException("Admin registration is not allowed. Contact system administrator.");
        }
        
        // Force role to User for all registrations
        user.setRole("User");
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Integer userId, User userDetails) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User existingUser = user.get();
            if (userDetails.getName() != null) {
                existingUser.setName(userDetails.getName());
            }
            if (userDetails.getContact() != null) {
                existingUser.setContact(userDetails.getContact());
            }
            if (userDetails.getRole() != null) {
                existingUser.setRole(userDetails.getRole());
            }
            return userRepository.save(existingUser);
        }
        throw new RuntimeException("User not found");
    }

    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }

    public Optional<User> authenticateUser(String email, String password) {
        // Debug logging
        System.out.println("Login attempt - Email: " + email + ", Password: " + password);
        System.out.println("Expected Admin1: " + ADMIN1_EMAIL + " / " + ADMIN1_PASSWORD);
        System.out.println("Expected Admin2: " + ADMIN2_EMAIL + " / " + ADMIN2_PASSWORD);
        
        // Check for default admin accounts first
        if (ADMIN1_EMAIL.equals(email) && ADMIN1_PASSWORD.equals(password)) {
            System.out.println("Admin1 login successful");
            return createOrGetAdmin(ADMIN1_EMAIL, "Admin One");
        }
        if (ADMIN2_EMAIL.equals(email) && ADMIN2_PASSWORD.equals(password)) {
            System.out.println("Admin2 login successful");
            return createOrGetAdmin(ADMIN2_EMAIL, "Admin Two");
        }
        
        // Regular user authentication
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            User foundUser = user.get();
            // Block admin login for non-default admins
            if ("Admin".equals(foundUser.getRole()) && 
                !ADMIN1_EMAIL.equals(email) && !ADMIN2_EMAIL.equals(email)) {
                return Optional.empty();
            }
            
            // For existing users without password, allow any password temporarily
            if (foundUser.getPassword() == null || foundUser.getPassword().isEmpty()) {
                return user;
            }
            // For users with password, check it
            if (foundUser.getPassword().equals(password)) {
                return user;
            }
        }
        return Optional.empty();
    }
    
    private Optional<User> createOrGetAdmin(String email, String name) {
        Optional<User> existingAdmin = userRepository.findByEmail(email);
        if (existingAdmin.isPresent()) {
            return existingAdmin;
        }
        
        // Create default admin if doesn't exist
        User admin = new User();
        admin.setEmail(email);
        admin.setName(name);
        admin.setRole("Admin");
        admin.setPassword(email.equals(ADMIN1_EMAIL) ? ADMIN1_PASSWORD : ADMIN2_PASSWORD);
        admin.setContact("000-000-0000");
        
        return Optional.of(userRepository.save(admin));
    }

    // Method to set default password for existing users
    public void setDefaultPasswordForUser(String email, String defaultPassword) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            User existingUser = user.get();
            existingUser.setPassword(defaultPassword);
            userRepository.save(existingUser);
        }
    }
}

