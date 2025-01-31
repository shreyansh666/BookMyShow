package com.BookMyShow.demo.strategy.NotificationStrategy;
import com.BookMyShow.demo.entities.User;
import com.BookMyShow.demo.security.services.UserDetailsImpl;


public interface NotificationStrategy {
    void sendNotification(UserDetailsImpl user, String message) throws Exception;
}

