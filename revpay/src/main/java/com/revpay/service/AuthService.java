package com.revpay.service;

import com.revpay.dto.request.LoginRequest;
import com.revpay.dto.request.RegisterRequest;
import com.revpay.dto.response.AuthResponse;
import com.revpay.entity.BusinessProfile;
import com.revpay.entity.NotificationPreference;
import com.revpay.entity.User;
import com.revpay.entity.Wallet;
import com.revpay.enums.Role;
import com.revpay.exception.DuplicateResourceException;
import com.revpay.exception.ResourceNotFoundException;
import com.revpay.exception.UnauthorizedAccessException;
import com.revpay.repository.BusinessProfileRepository;
import com.revpay.repository.UserRepository;
import com.revpay.repository.WalletRepository;
import com.revpay.security.JwtUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private BusinessProfileRepository businessProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        logger.info("Registering new user with email: {}", request.getEmail());

        // check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        // check if phone already exists
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Phone number already registered");
        }

        // create user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setSecurityQuestion(request.getSecurityQuestion());
        user.setSecurityAnswer(request.getSecurityAnswer());
        System.out.println("Business flag received: " + request.isBusiness());
        user.setRole(request.isBusiness() ? Role.BUSINESS : Role.PERSONAL);
        user.setVerified(true);
        user.setActive(true);

        userRepository.save(user);

        // create wallet for this user automatically
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        walletRepository.save(wallet);

        // create default notification preferences
        NotificationPreference preference = new NotificationPreference();
        preference.setUser(user);

        // if business registration save business profile too
        if (request.isBusiness()) {
            if (businessProfileRepository.existsByTaxId(request.getTaxId())) {
                throw new DuplicateResourceException("Tax ID already registered");
            }

            BusinessProfile profile = new BusinessProfile();
            profile.setUser(user);
            profile.setBusinessName(request.getBusinessName());
            profile.setBusinessType(request.getBusinessType());
            profile.setTaxId(request.getTaxId());
            profile.setBusinessAddress(request.getBusinessAddress());
            businessProfileRepository.save(profile);
        }

        // generate token and return
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        logger.info("User registered successfully: {}", user.getEmail());

        return new AuthResponse(user.getEmail(),user.getFullName(),user.getRole().name(),token );    }

    public AuthResponse login(LoginRequest request) {
        logger.info("Login attempt for: {}", request.getEmailOrPhone());

        // find user by email or phone
        User user = userRepository.findByEmail(request.getEmailOrPhone())
                .orElseGet(() -> userRepository.findByPhone(request.getEmailOrPhone())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found")));

        // check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedAccessException("Invalid password");
        }

        // check if account is active
        if (!user.isActive()) {
            throw new UnauthorizedAccessException("Account is deactivated");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        logger.info("User logged in successfully: {}", user.getEmail());

        return new AuthResponse(user.getEmail(),user.getFullName(),user.getRole().name(),token );
    }
}
