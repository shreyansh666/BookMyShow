package com.BookMyShow.demo.service;

import com.BookMyShow.demo.dto.PasswordChangeRequest;
import com.BookMyShow.demo.entities.User;
import com.BookMyShow.demo.enums.NotificationType;
import com.BookMyShow.demo.enums.UserRole;
import com.BookMyShow.demo.exception.ResourceNotFoundException;
import com.BookMyShow.demo.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;


@Service
public interface UserService {

    public boolean updateProfile(User user) ;

    public boolean changeUserRole(String email, UserRole newRole) throws UnauthorizedException;

    public boolean changePassword(PasswordChangeRequest passwordRequest);

    public boolean verifyPasswordResetLink(String userId, String resetCode);

    public void sendEmailPasswordReset(String email, HttpServletRequest request) throws Exception, ResourceNotFoundException;

     public boolean addSubscription(String userId, NotificationType type);

     public  User findUser(String userId);
}
