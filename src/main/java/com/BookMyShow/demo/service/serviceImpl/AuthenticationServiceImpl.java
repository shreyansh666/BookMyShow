package com.BookMyShow.demo.service.serviceImpl;



import com.BookMyShow.demo.dto.LoginRequest;
import com.BookMyShow.demo.dto.LoginResponse;
import com.BookMyShow.demo.dto.RegisterDto;
import com.BookMyShow.demo.entities.User;
import com.BookMyShow.demo.exception.UnauthorizedException;
import com.BookMyShow.demo.repository.UserRepository;
import com.BookMyShow.demo.security.jwt.JwtUtils;
import com.BookMyShow.demo.security.services.UserDetailsImpl;
import com.BookMyShow.demo.service.AuthenticationService;
import com.BookMyShow.demo.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import java.security.SecureRandom;
import java.util.*;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public boolean register(RegisterDto user) throws UnauthorizedException {

        if (!ObjectUtils.isEmpty(userRepository.findByEmail(user.getEmail()))) {
            return false;
        }

        User user1 = new User(user.getUsername(), user.getEmail(), passwordEncoder.encode(user.getPassword()));
        userRepository.save(user1);
        return true;

    }

    public Optional<LoginResponse> login(LoginRequest loginRequest) throws UnauthorizedException {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String jwtToken = jwtUtils.generateToken(userDetails);

            UserDetailsImpl logedInUser = CommonUtil.getLoggedInUser();

            LoginResponse response = new LoginResponse(userDetails.getUsername(), jwtToken);
            return Optional.of(response);

        }
        return Optional.empty();
    }

}



