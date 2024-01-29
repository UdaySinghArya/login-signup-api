package com.example.registerapp.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import com.example.registerapp.dto.LoginDto;
import com.example.registerapp.dto.RegisterDto;
import com.example.registerapp.entity.User;
import com.example.registerapp.repository.UserRepository;
import com.example.registerapp.util.EmailUtil;
import com.example.registerapp.util.OtpUtil;
import jakarta.mail.MessagingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private OtpUtil otpUtil;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private UserRepository userRepository;

    public String register(RegisterDto registerDto) {
        // Validate input details
        if (StringUtils.isAnyBlank(registerDto.getName(), registerDto.getEmail(), registerDto.getPassword())) {
            return "Please provide all details (Name, Email, Password)";
        }

        String otp = otpUtil.generateOtp();

        try {
            emailUtil.sendOtpEmail(registerDto.getEmail(), otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send OTP. Please try again.");
        }

        User user = new User();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(registerDto.getPassword());
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());

        try {
            userRepository.save(user);
            return "User registration successful";
        } catch (DataIntegrityViolationException e) {
            return "Email or password should be unique";
        }
    }



    public String verifyAccount(String email, String otp) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));

            if (user.isActive()) {
                return "Account already verified. You can log in.";
            }

            String storedOtp = user.getOtp();
            LocalDateTime otpGeneratedTime = user.getOtpGeneratedTime();
            LocalDateTime currentTime = LocalDateTime.now();

            if (storedOtp.equals(otp) && Duration.between(otpGeneratedTime, currentTime).getSeconds() < (2 * 60)) {
                user.setActive(true);
                userRepository.save(user);
                return "OTP verified. You can log in.";
            } else {
                return "Incorrect OTP or OTP has expired. Please regenerate OTP and try again.";
            }
        } catch (NoSuchElementException e) {
            return "User not found with the provided email: " + email;
        } catch (Exception e) {
            return "Error verifying account: " + e.getMessage();
        }
    }


    public String regenerateOtp(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NoSuchElementException("User not found with this email: " + email));

            String otp = otpUtil.generateOtp();

            try {
                emailUtil.sendOtpEmail(email, otp);
            } catch (MessagingException e) {
                throw new RuntimeException("Unable to send OTP. Please try again.");
            }

            user.setOtp(otp);
            user.setOtpGeneratedTime(LocalDateTime.now());
            userRepository.save(user);

            return "Email sent... please verify account within 2 minutes";
        } catch (NoSuchElementException e) {
            return "User not found with the provided email: " + email;
        }
    }

    public String login(LoginDto loginDto) {
        try {
            User user = userRepository.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new NoSuchElementException("User not found with this email: " + loginDto.getEmail()));

            if (!loginDto.getPassword().equals(user.getPassword())) {
                return "Password is incorrect";
            } else if (!user.isActive()) {
                return "Your account is not verified";
            }

            return "Login successful";
        } catch (NoSuchElementException e) {
            return "User not found with the provided email: " + loginDto.getEmail();
        } catch (Exception e) {
            return "Error during login: " + e.getMessage();
        }
    }

    public String forgotPassword(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NoSuchElementException("User not found with this email: " + email));

            if (user.getPassword() == null) {
                // Handle the case where the user has no password set
                return "Password not set for the user with email: " + email;
            }

            try {
                emailUtil.sendOtpEmailPassword(email, user.getPassword());
                // Return a success message or code
                return "Password reset email sent successfully to: " + email;
            } catch (MessagingException e) {
                throw new RuntimeException("Unable to send OTP. Please try again.", e);
            }
        } catch (NoSuchElementException e) {
            return "User not found with the provided email: " + email;
        }
    }
}
