package com.BookMyShow.demo.service;

import com.BookMyShow.demo.dto.EmailRequest;

public interface EmailService {
    public void sendEmail(EmailRequest emailReq) throws Exception;
}
