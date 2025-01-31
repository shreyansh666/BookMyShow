package com.BookMyShow.demo.controller;

import com.BookMyShow.demo.entities.Booking;
import com.BookMyShow.demo.entities.Seat;
import com.BookMyShow.demo.entities.User;
import com.BookMyShow.demo.enums.NotificationType;
import com.BookMyShow.demo.enums.PaymentType;
import com.BookMyShow.demo.security.services.UserDetailsImpl;
import com.BookMyShow.demo.service.BookingService;
import com.BookMyShow.demo.service.UserService;
import com.BookMyShow.demo.strategy.NotificationStrategy.strategyImpl.NotificationManager;
import com.BookMyShow.demo.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final NotificationManager notificationManager;

    @Autowired
    private final UserService userService;



    @GetMapping("/available-seats")
    public ResponseEntity<List<Seat>> getAvailableSeats(@RequestParam String showId) {
        List<Seat> seats = bookingService.getAvailableSeats(showId);
        return ok(seats);
    }

    @PostMapping("/book")
    public ResponseEntity<String> bookTickets(@RequestParam String showId, @RequestParam List<String> seatIds,
                                              @RequestParam PaymentType payment) {

        UserDetailsImpl loggedInUser = CommonUtil.getLoggedInUser();

        try {
            Booking booking = bookingService.bookTickets(loggedInUser.getId(), showId, seatIds, payment);
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

