package com.BookMyShow.demo.controller;



import com.BookMyShow.demo.dto.LoginRequest;
import com.BookMyShow.demo.dto.LoginResponse;
import com.BookMyShow.demo.dto.RegisterDto;
import com.BookMyShow.demo.entities.ForgotPassword;
import com.BookMyShow.demo.entities.User;
import com.BookMyShow.demo.repository.UserRepository;
import com.BookMyShow.demo.repository.repoImpl.UserRepositoryImpl;
import com.BookMyShow.demo.security.jwt.JwtUtils;
import com.BookMyShow.demo.security.services.UserDetailsImpl;
import com.BookMyShow.demo.service.AuthenticationService;
import com.BookMyShow.demo.service.UserService;
import com.BookMyShow.demo.service.serviceImpl.OTPServiceImpl;
import com.BookMyShow.demo.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OTPServiceImpl otpService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws Exception {

        Optional<LoginResponse> loginResponse = authService.login(loginRequest);

        if (loginResponse.isPresent()) {
            return CommonUtil.createBuildResponse(loginResponse.get(), HttpStatus.OK);
        }else{
            return CommonUtil.createErrorResponseMessage("Login Failed", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDto user) throws Exception {
        boolean register = authService.register(user);
        if (register) {
            return CommonUtil.createBuildResponse("Successfully registered", HttpStatus.OK);
        }
        return CommonUtil.createErrorResponseMessage("Already registered", HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ForgotPassword fr = otpService.saveOTPForUser(user);
            otpService.sendOTPEmail(user,fr.getId());
            return CommonUtil.createBuildResponseMessage("OTP has been sent to your email", HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestParam String email, @RequestParam Integer otp) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (otpService.verifyOTP(user, otp)) {
                String token = jwtUtils.generateToken((UserDetailsImpl.build(user)));
                Map<String, String> response = new HashMap<>();
                response.put("message", "OTP verified successfully");
                response.put("token", token);

                return CommonUtil.createBuildResponse(response, HttpStatus.OK);
            } else {
                return CommonUtil.createErrorResponseMessage("Invalid OTP", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            if (jwtUtils.validateJwtToken(token)) {
                User user = userRepository.findByEmail(jwtUtils.getEmailFromJwtToken(token))
                        .orElseThrow(() -> new RuntimeException("User not found"));

                userRepositoryImpl.updatePassword(user.getEmail(), passwordEncoder.encode(newPassword));

                return CommonUtil.createBuildResponseMessage("Password updated successfully", HttpStatus.OK);
            } else {
                return CommonUtil.createErrorResponseMessage("Token did not match", HttpStatus.BAD_REQUEST);
            }
        } catch(Exception e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/verifyPasswordResetLink")
    public ResponseEntity<?> verifyPasswordResetLink(@RequestParam String userId, @RequestParam String ResetCode)
            throws Exception {
        if (userService.verifyPasswordResetLink(userId, ResetCode)) {
            return CommonUtil.createBuildResponseMessage("verification success", HttpStatus.OK);
        }
        return CommonUtil.createBuildResponseMessage("Verification Failed", HttpStatus.BAD_REQUEST);
    }
}
