package com.BookMyShow.demo.service;

import com.BookMyShow.demo.dto.EmailRequest;
import com.BookMyShow.demo.entities.ForgotPassword;
import com.BookMyShow.demo.entities.User;

public interface OTPService {
    public Integer generateOTP();
   public ForgotPassword saveOTPForUser(User user);
   public boolean verifyOTP(User user, Integer otp);
   public void sendOTPEmail(User user, String id) throws Exception;
}