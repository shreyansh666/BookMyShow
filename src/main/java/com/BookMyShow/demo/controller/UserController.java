package com.BookMyShow.demo.controller;


import com.BookMyShow.demo.dto.PasswordChangeRequest;
import com.BookMyShow.demo.entities.Payment;
import com.BookMyShow.demo.entities.User;
import com.BookMyShow.demo.enums.NotificationType;
import com.BookMyShow.demo.enums.PaymentType;
import com.BookMyShow.demo.enums.UserRole;
import com.BookMyShow.demo.exception.ResourceNotFoundException;
import com.BookMyShow.demo.security.services.UserDetailsImpl;
import com.BookMyShow.demo.service.UserService;
import com.BookMyShow.demo.util.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/changeRole")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> changeRole(@RequestParam String email, @RequestParam UserRole newRole) throws Exception {
        if(userService.changeUserRole(email, newRole)){
            return CommonUtil.createBuildResponseMessage("Role Changed", HttpStatus.OK);
        }
        return CommonUtil.createErrorResponseMessage("Not Allowed", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestParam User user) throws Exception {
        if(userService.updateProfile(user)){
            return CommonUtil.createBuildResponseMessage("User Updated", HttpStatus.OK);
        }
        return CommonUtil.createErrorResponseMessage("Could not Update", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/getUserId")
    public ResponseEntity<?> getLoggedInUserId() throws Exception {

        UserDetailsImpl logedInUser = CommonUtil.getLoggedInUser();

        if(logedInUser.getId() != null){
            return CommonUtil.createBuildResponse(logedInUser.getId(),HttpStatus.OK);
        }
        return CommonUtil.createErrorResponseMessage("No logged in user", HttpStatus.BAD_REQUEST);
    }


    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest passwordChangeRequest){
               if(userService.changePassword(passwordChangeRequest)){
                   return CommonUtil.createBuildResponseMessage("Password Changed", HttpStatus.OK);
               }
        return CommonUtil.createErrorResponseMessage("Could Not change password", HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/sendResetEmail")
    public ResponseEntity<?> sendEmailForPasswordReset(@RequestParam String email, HttpServletRequest request) throws Exception, ResourceNotFoundException, ResourceNotFoundException {
        userService.sendEmailPasswordReset(email, request);
        return CommonUtil.createBuildResponseMessage("heck Email Reset Password", HttpStatus.OK);
    }

    @GetMapping("/subscribe/{type}")
    public ResponseEntity<?> subsribe(@PathVariable NotificationType type) throws Exception, ResourceNotFoundException, ResourceNotFoundException {
        UserDetailsImpl logedInUser = CommonUtil.getLoggedInUser();

        if(userService.addSubscription(logedInUser.getId(),type)){
            return CommonUtil.createBuildResponseMessage("Added Subscription", HttpStatus.OK);
        }

        return CommonUtil.createBuildResponseMessage("Failed", HttpStatus.BAD_REQUEST);

    }







}
