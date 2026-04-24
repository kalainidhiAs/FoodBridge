package com.foodbridge.foodbridge_backend.controller;

import com.foodbridge.foodbridge_backend.dto.*;
import com.foodbridge.foodbridge_backend.model.*;
import com.foodbridge.foodbridge_backend.repository.*;
import com.foodbridge.foodbridge_backend.security.JwtUtils;
import com.foodbridge.foodbridge_backend.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired AuthenticationManager authenticationManager;
    @Autowired UserRepository userRepository;
    @Autowired NgoProfileRepository ngoProfileRepository;
    @Autowired HomemakerProfileRepository homemakerProfileRepository;
    @Autowired PasswordEncoder encoder;
    @Autowired JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getMobileNumber(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), role));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        // Validation
        if (userRepository.findByMobileNumber(signUpRequest.getMobileNumber()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mobile number is already taken!"));
        }
        String mobile = signUpRequest.getMobileNumber();
        if (mobile == null || !mobile.matches("\\d{10}")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mobile number must be exactly 10 digits."));
        }
        String name = signUpRequest.getName();
        if (name == null || name.trim().length() < 2 || name.trim().length() > 20) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name must be 2-20 characters."));
        }
        if (!name.matches("[a-zA-Z ]+")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name must contain only alphabets and spaces."));
        }
        String pwd = signUpRequest.getPassword();
        if (pwd == null || pwd.length() < 8 || pwd.length() > 16) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password must be 8-16 characters."));
        }
        if (!pwd.matches(".*[A-Z].*") || !pwd.matches(".*[a-z].*") || !pwd.matches(".*\\d.*") || !pwd.matches(".*[@#$%^&*].*")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password must have uppercase, lowercase, number, and special character (@#$%^&*)."));
        }

        UserRole role = signUpRequest.getRole() != null ? signUpRequest.getRole() : UserRole.CUSTOMER;

        if (role == UserRole.NGO) {
            String pan = signUpRequest.getNgoPanNumber();
            if (pan == null || !pan.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid NGO PAN format."));
            }
            if (signUpRequest.getNgoDarpanUniqueId() == null || signUpRequest.getNgoDarpanUniqueId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "NGO Darpan ID is required."));
            }
            if (signUpRequest.getRegistrationCertificateUrl() == null || signUpRequest.getRegistrationCertificateUrl().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Registration Certificate is required."));
            }
        } else if (role == UserRole.HOMEMAKER) {
            if (signUpRequest.getIdProofUrl() == null || signUpRequest.getIdProofUrl().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID Proof document is required."));
            }
        }

        User user = new User();
        user.setName(signUpRequest.getName().trim());
        user.setMobileNumber(signUpRequest.getMobileNumber());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setAddress(signUpRequest.getAddress());

        user.setRole(role);
        user.setStatus(role == UserRole.NGO || role == UserRole.HOMEMAKER ? "PENDING" : "APPROVED");
        userRepository.save(user);

        if (role == UserRole.NGO) {
            NgoProfile ngo = new NgoProfile();
            ngo.setUser(user);
            ngo.setRegisteredAddress(signUpRequest.getRegisteredAddress());
            ngo.setContactPersonName(signUpRequest.getContactPersonName());
            ngo.setContactMobileNumber(signUpRequest.getContactMobileNumber());
            ngo.setNgoPanNumber(signUpRequest.getNgoPanNumber());
            ngo.setNgoDarpanUniqueId(signUpRequest.getNgoDarpanUniqueId());
            ngo.setRegistrationCertificateUrl(signUpRequest.getRegistrationCertificateUrl());
            ngoProfileRepository.save(ngo);
        } else if (role == UserRole.HOMEMAKER) {
            HomemakerProfile hm = new HomemakerProfile();
            hm.setUser(user);
            hm.setCuisinesOffered(signUpRequest.getCuisinesOffered());
            hm.setExperience(signUpRequest.getExperience());
            hm.setSpecialization(signUpRequest.getSpecialization());
            hm.setIdProofUrl(signUpRequest.getIdProofUrl());
            hm.setRating(0.0);
            homemakerProfileRepository.save(hm);
        }
        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication auth) {
        return userRepository.findByMobileNumber(auth.getName())
                .map(u -> ResponseEntity.ok((Object) u))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> body, Authentication auth) {
        return userRepository.findByMobileNumber(auth.getName()).map(user -> {
            String name = body.get("name");
            String address = body.get("address");
            if (name != null) {
                if (name.trim().length() < 2 || name.trim().length() > 20 || !name.matches("[a-zA-Z ]+")) {
                    return ResponseEntity.badRequest().body((Object) Map.of("error", "Invalid name."));
                }
                user.setName(name.trim());
            }
            if (address != null) user.setAddress(address);
            userRepository.save(user);
            return ResponseEntity.ok((Object) Map.of("message", "Profile updated successfully."));
        }).orElse(ResponseEntity.notFound().build());
    }
}
