package com.BookMyShow.demo.strategy.NotificationStrategy;


import com.BookMyShow.demo.dto.EmailRequest;
import com.BookMyShow.demo.security.services.UserDetailsImpl;
import com.BookMyShow.demo.service.serviceImpl.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("EMAIL")
public class EmailStrategy implements NotificationStrategy {

    private final EmailServiceImpl emailService;

    @Autowired
    public EmailStrategy(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendNotification(UserDetailsImpl user, String message) throws Exception {
        EmailRequest emailRequest = EmailRequest.builder()
                .to(user.getEmail())
                .title("Book My Show")
                .subject("Booking Confirmation")
                .message(message)
                .build();

        System.out.println(emailRequest);

        emailService.sendEmail(emailRequest);
    }
}
