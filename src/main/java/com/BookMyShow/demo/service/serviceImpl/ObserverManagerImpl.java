package com.BookMyShow.demo.service.serviceImpl;

import com.BookMyShow.demo.dto.EmailRequest;
import com.BookMyShow.demo.entities.User;
import com.BookMyShow.demo.service.ObserverManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class ObserverManagerImpl implements ObserverManager {

    @Autowired
    private final EmailServiceImpl emailService;

        private static final Map<String, List<User>> waitlists = new HashMap<>();

        public void addObserver(String showId, User observer) {
            waitlists.computeIfAbsent(showId, k -> new ArrayList<>()).add(observer);
        }

        public  void notifyObservers(String showId) throws Exception {
            if (waitlists.containsKey(showId)) {
                for (User observer : waitlists.get(showId)) {
                    sendMail(observer.getEmail(), "Book Available");
                }
                waitlists.remove(showId);
            }
        }

        private void sendMail(String email, String message) throws Exception {
            EmailRequest emailRequest = EmailRequest.builder().to(email)
                    .title("Book My Show").subject("Free Seats").message(message).build();

            emailService.sendEmail(emailRequest);
        }



}
