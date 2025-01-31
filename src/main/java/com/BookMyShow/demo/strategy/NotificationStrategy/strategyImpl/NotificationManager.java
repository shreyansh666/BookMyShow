package com.BookMyShow.demo.strategy.NotificationStrategy.strategyImpl;



import com.BookMyShow.demo.enums.NotificationType;
import com.BookMyShow.demo.security.services.UserDetailsImpl;
import com.BookMyShow.demo.strategy.NotificationStrategy.NotificationStrategy;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

@Service
public class NotificationManager {

    private final Map<String, NotificationStrategy> strategies;

    @Autowired
    public NotificationManager(Map<String, NotificationStrategy> strategies) {
        this.strategies = strategies;
    }

    public void sendNotification(NotificationType notificationType, UserDetailsImpl user) throws Exception {
        NotificationStrategy strategy = strategies.get(notificationType.name());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported notification type: " + notificationType);
        }

        strategy.sendNotification(user, "Hello! Thank you Booking with BookMyShow");
    }
}
