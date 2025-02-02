package com.BookMyShow.demo.service.serviceImpl;



import com.BookMyShow.demo.dto.EmailRequest;
import com.BookMyShow.demo.entities.ForgotPassword;
import com.BookMyShow.demo.entities.User;
import com.BookMyShow.demo.repository.ForgotPasswordRepo;
import com.BookMyShow.demo.repository.UserRepository;
import com.BookMyShow.demo.service.OTPService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class OTPServiceImpl implements OTPService {

        @Autowired
        private ForgotPasswordRepo forgotPasswordRepository;

        @Autowired
        private EmailServiceImpl emailService;

        @Autowired
        private UserRepository userRepository;

        public Integer generateOTP() {
            return 100000 + new Random().nextInt(900000);
        }

        public ForgotPassword saveOTPForUser(User user) {

            Date expirationTime = new Date(System.currentTimeMillis() + 120000);

           Optional<ForgotPassword> existingEntry =  forgotPasswordRepository.findByUser(user);
           if(existingEntry.isPresent()) {
               existingEntry.get().setOtp(generateOTP());
               existingEntry.get().setExpirationTime(expirationTime);
               return forgotPasswordRepository.save(existingEntry.get());
           }

            ForgotPassword forgotPassword = ForgotPassword.builder()
                    .otp(generateOTP())
                    .ExpirationTime(expirationTime)
                    .user(user)
                    .build();
           return forgotPasswordRepository.save(forgotPassword);
        }

        public boolean verifyOTP(User user, Integer otp) {
            ForgotPassword forgotPassword = forgotPasswordRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("No OTP request found"));

            if (new Date().after(forgotPassword.getExpirationTime())) {
                forgotPasswordRepository.delete(forgotPassword);
                throw new RuntimeException("OTP has expired");
            }
            return forgotPassword.getOtp().equals(otp);
        }


    public void sendOTPEmail(User user,String id) throws Exception {
        String message = "Hi <b>[[username]]</b>, "
                + "<br><p>You have requested to reset your password.</p>"
                + "<p>Your OTP is: <b>[[otp]]</b></p>"
                + "<p>This OTP will expire in 60 seconds.</p>"
                + "<p>If you didn't request this, please ignore this email.</p>"
                + "<br>Thanks";


        ForgotPassword forgotPassword = forgotPasswordRepository.findById(id);

        message = message.replace("[[username]]", user.getUsername());
        message = message.replace("[[otp]]", forgotPassword.getOtp().toString());

        EmailRequest emailRequest = EmailRequest.builder()
                .to(user.getEmail())
                .title("Password Reset OTP")
                .subject("Password Reset OTP")
                .message(message)
                .build();

        emailService.sendEmail(emailRequest);
    }
 }

