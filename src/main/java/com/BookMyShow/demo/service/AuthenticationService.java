package com.BookMyShow.demo.service;




import com.BookMyShow.demo.dto.LoginRequest;
import com.BookMyShow.demo.dto.LoginResponse;
import com.BookMyShow.demo.dto.RegisterDto;
import com.BookMyShow.demo.exception.UnauthorizedException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface AuthenticationService {

    public boolean register(RegisterDto user) throws UnauthorizedException;

    public Optional<LoginResponse> login(LoginRequest loginRequest) throws UnauthorizedException;


}
