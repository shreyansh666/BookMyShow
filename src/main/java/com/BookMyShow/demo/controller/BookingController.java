package com.BookMyShow.demo.controller;

import com.BookMyShow.demo.entities.*;
import com.BookMyShow.demo.enums.NotificationType;
import com.BookMyShow.demo.enums.PaymentType;
import com.BookMyShow.demo.security.services.UserDetailsImpl;
import com.BookMyShow.demo.service.BookingService;
import com.BookMyShow.demo.service.UserService;
import com.BookMyShow.demo.strategy.NotificationStrategy.strategyImpl.NotificationManager;
import com.BookMyShow.demo.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static org.springframework.http.ResponseEntity.ok;


@RequiredArgsConstructor
@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private final BookingService bookingService;

    @Autowired
    private static RedisTemplate redisTemplate;



    @Autowired
    private final NotificationManager notificationManager;

    @Autowired
    private final UserService userService;

    @GetMapping("/available-seats")
    public ResponseEntity<List<ShowSeat>> getAvailableSeats(@RequestParam String showId) {
        List<ShowSeat> seats = bookingService.getAvailableSeats(showId);
        return ResponseEntity.ok(seats);
    }

    @PostMapping("/create-session")
    public ResponseEntity<String> createBookingSession(@RequestParam String showId,
                                                       @RequestBody List<String> seatIds) {
        UserDetailsImpl loggedInUser = CommonUtil.getLoggedInUser();
        try {
            UserBookingSession session = bookingService.createBookingSession(loggedInUser.getId(), showId, seatIds);
            return ResponseEntity.ok("Session created. Your session ID is: " + session.getSessionId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating session: " + e.getMessage());
        }
    }

    @PostMapping("/book")
    public ResponseEntity<String> completeBooking(@RequestParam String sessionId,
                                              @RequestParam PaymentType payment) {

        UserDetailsImpl loggedInUser = CommonUtil.getLoggedInUser();

        try {
            Booking booking = bookingService.completeBooking(sessionId, payment);
            if (!ObjectUtils.isEmpty(booking)) {
                User user = userService.findUser(loggedInUser.getId());
                for (NotificationType type : user.getNotificationSubscriptions()) {
                    notificationManager.sendNotification(type, loggedInUser);
                }
                return ResponseEntity.ok("Booking Successful " + booking.getId());
            } else {
                return ResponseEntity.badRequest().body("Booking failed");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/cache")
    public ResponseEntity<String> cancelBooking() {
        redisTemplate.opsForValue().set("email", "shrey@gmail.com");
        Object email = redisTemplate.opsForValue().get("email");
        System.out.println(email);
        return ResponseEntity.ok("ok.");
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelBooking(@RequestParam String bookingId) {
        try {

            bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok("Booking canceled successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }



    @GetMapping("/getAllBooking")
    public ResponseEntity<?> getBookingsByUser() {
        UserDetailsImpl logedInUser = CommonUtil.getLoggedInUser();
        try {
            List<Booking> bookings = bookingService.getBookingsByUser(logedInUser.getId());
            return ResponseEntity.ok(bookings);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}

