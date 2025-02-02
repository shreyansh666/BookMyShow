package com.BookMyShow.demo.entities;

import com.BookMyShow.demo.enums.NotificationType;
import com.BookMyShow.demo.enums.UserRole;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users_records")
public class User {

    @Id
    private String id;

    private String username;

    private String email;

    private String passwordHash;

    private UserRole role = UserRole.ADMIN;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String ResetToken;

    private Set<NotificationType> notificationSubscriptions = new HashSet<>();

    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.notificationSubscriptions.add(NotificationType.EMAIL);
    }
}
