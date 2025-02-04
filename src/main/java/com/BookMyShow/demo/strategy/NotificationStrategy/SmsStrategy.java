package com.BookMyShow.demo.strategy.NotificationStrategy;


import com.BookMyShow.demo.entities.User;
import com.BookMyShow.demo.security.services.UserDetailsImpl;
import org.springframework.stereotype.Component;

@Component("SMS")
public class SmsStrategy implements NotificationStrategy {

    @Override
    public void sendNotification(UserDetailsImpl user, String message) throws Exception {
        System.out.println("Sending SMS to " + user.getUsername() + ": " + message);

    }
}

