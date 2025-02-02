package com.BookMyShow.demo.service.serviceImpl;

import com.BookMyShow.demo.dto.EmailRequest;
import com.BookMyShow.demo.dto.PasswordChangeRequest;
import com.BookMyShow.demo.entities.City;
import com.BookMyShow.demo.entities.Movie;
import com.BookMyShow.demo.entities.Show;
import com.BookMyShow.demo.entities.User;
import com.BookMyShow.demo.enums.NotificationType;
import com.BookMyShow.demo.enums.UserRole;
import com.BookMyShow.demo.exception.ResourceNotFoundException;
import com.BookMyShow.demo.exception.UnauthorizedException;
import com.BookMyShow.demo.repository.CityRepository;
import com.BookMyShow.demo.repository.UserRepository;
import com.BookMyShow.demo.security.services.UserDetailsImpl;
import com.BookMyShow.demo.service.UserService;
import com.BookMyShow.demo.util.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private final UserRepository userRepo;

    @Autowired
    private final CityRepository cityRepository;

    @Autowired
    private final EmailServiceImpl emailService;

    public boolean updateProfile(User user) {
        Optional<User> user1 = userRepository.findByEmail(user.getEmail());

        if(user1.isPresent()){
            if (user.getUsername() != null) user1.get().setUsername(user.getUsername());
            if (user.getEmail() != null) user1.get().setEmail(user.getEmail());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user1.get());
            return true;
        }
        return false;
    }


    public boolean changeUserRole(String email, UserRole newRole) throws UnauthorizedException {

        UserDetailsImpl loggedInUser =  CommonUtil.getLoggedInUser();

        Optional<User> targetUser = userRepository.findByEmail(email);
        if (targetUser.isPresent()) {
            targetUser.get().setRole(newRole);
            targetUser.get().setUpdatedAt(LocalDateTime.now());
            userRepository.save(targetUser.get());
            return true;
        }

        return false;
    }


    public boolean changePassword(PasswordChangeRequest passwordRequest) {

        UserDetailsImpl logedInUser = CommonUtil.getLoggedInUser();

        if (!passwordEncoder.matches(passwordRequest.getOldPassword(), logedInUser.getPassword())) {
            throw new IllegalArgumentException("Old Password is incorrect !!");
        }
        String encodePassword = passwordEncoder.encode(passwordRequest.getNewPassword());

        Optional<User> targetUser = userRepository.findByEmail(logedInUser.getEmail());
        if (targetUser.isPresent()) {
            targetUser.get().setPasswordHash(encodePassword);
            userRepo.save(targetUser.get());
            UserDetailsImpl updatedUserDetails = new UserDetailsImpl(
                    targetUser.get().getId(),
                    targetUser.get().getUsername(),
                    targetUser.get().getEmail(),
                    encodePassword,
                    logedInUser.getAuthorities()
            );
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updatedUserDetails,
                    null,
                    updatedUserDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
            return true;
        }
        return false;
    }


    public boolean verifyPasswordResetLink(String userId, String ResetCode)  {
        Optional<User> user = userRepo.findById(userId);
        if(user.isPresent()) {
            return verifyPasswordResetToken(user.get().getResetToken(), ResetCode);
        }
        return false;
    }

    private boolean verifyPasswordResetToken(String existToken, String reqToken) {

            if(!StringUtils.hasText(existToken)) throw new IllegalArgumentException("Already Password reset");
            if(!existToken.equals(reqToken)) throw new IllegalArgumentException("invalid url");
            return true;

    }

    private void sendEmailRequest(User user,String url) throws Exception {

        String message = "Hi <b>[[username]]</b> "
                +"<br><p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=[[url]]>Change my password</a></p>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p><br>"
                + "Thanks";


        message = message.replace("[[username]]", user.getUsername());
        message = message.replace("[[url]]", url + "/api/v1/auth/verifyPasswordResetLink?userId=" + user.getId() + "&&ResetCode="
                + user.getResetToken());

        EmailRequest emailRequest = EmailRequest.builder().to(user.getEmail())
                .title("Password Reset").subject("Password Reset link").message(message).build();


        emailService.sendEmail(emailRequest);
    }



    public void sendEmailPasswordReset(String email,HttpServletRequest request) throws Exception, ResourceNotFoundException {
        Optional<User> user = userRepo.findByEmail(email);

        if(!user.isPresent()) {
            throw new ResourceNotFoundException("invalid Email");
        }

        String passwordResetToken = UUID.randomUUID().toString();
        user.get().setResetToken(passwordResetToken);
        User updateUser = userRepo.save(user.get());
        String url = CommonUtil.getUrl(request);
        sendEmailRequest(updateUser,url);

    }

    @Override
    public boolean addSubscription(String userId, NotificationType type) {
        Optional<User> user = userRepo.findById(userId);
        if(user.isPresent()) {
            User existingUser = user.get();
            existingUser.getNotificationSubscriptions().add(type);
            userRepo.save(existingUser);
            return true;
        }
        return false;
    }

    @Override
    public User findUser(String userId){
        return userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }


    public List<Movie> getMoviesInCity(String cityId) {

        City city = cityRepository.findById(cityId)
                .orElseThrow(() ->  new RuntimeException("City not found"));

        return city.getTheaters().stream()
                .flatMap(theater -> theater.getScreens().stream())
                .flatMap(screen -> screen.getShows().stream())
                .map(Show::getMovie)
                .distinct()
                .collect(Collectors.toList());
    }


}

