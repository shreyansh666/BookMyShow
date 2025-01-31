package com.BookMyShow.demo.service.serviceImpl;

import com.BookMyShow.demo.entities.*;
import com.BookMyShow.demo.enums.*;
import com.BookMyShow.demo.repository.*;
import com.BookMyShow.demo.service.BookingService;
import com.BookMyShow.demo.service.ObserverManager;
import com.BookMyShow.demo.strategy.NotificationStrategy.strategyImpl.NotificationManager;
import com.BookMyShow.demo.strategy.PaymentStrategy.strategyImpl.PaymentManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ShowRepository showRepository;
    @Autowired
    private final SeatRepository seatRepository;
    @Autowired
    private final PaymentManager paymentManager;
    @Autowired
    private final NotificationManager notificationManager;
    @Autowired
    private final ObserverManager observerManager;

    public List<Seat> getAvailableSeats(String showId) {
        Optional<Show> showOpt = showRepository.findById(showId);
        if (showOpt.isEmpty()) throw new RuntimeException("Show not found");

        Show show = showOpt.get();
        return show.getSeats().stream()
                .filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE)
                .collect(Collectors.toList());
    }

    public void notifyUser(String showId, User user) {
        observerManager.addObserver(showId, user);
    }

    @Transactional
    public synchronized Booking bookTickets(String userId, String showId, List<String> seatIds, PaymentType payment) throws Exception {
        synchronized (this) {
            Optional<User> userOpt = userRepository.findById(userId);
            Optional<Show> showOpt = showRepository.findById(showId);
            List<Seat> seats = seatRepository.findAllById(seatIds);

            if (userOpt.isEmpty() || showOpt.isEmpty()) throw new RuntimeException("Invalid booking details");

            User user = userOpt.get();
            Show show = showOpt.get();

            List<Seat> availableSeats = getAvailableSeats(showId);
            if (availableSeats.isEmpty()) {
                notifyUser(showId, user);
                throw new RuntimeException("No seats available. You will be notified if seats become available.");
            }

            for (Seat seat : seats) {
                if (seat.getStatus() != SeatStatus.AVAILABLE) throw new RuntimeException("Seat already booked");
            }

            double amount = seats.stream().mapToDouble(Seat::getPrice).sum();
            paymentManager.pay(payment, amount);

            seats.forEach(seat -> seat.setStatus(SeatStatus.BOOKED));
            seatRepository.saveAll(seats);

            Booking booking = Booking.builder()
                    .user(user)
                    .show(show)
                    .seats(seats)
                    .payment(payment)
                    .status(BookingStatus.BOOKED)
                    .build();

            return bookingRepository.save(booking);
        }
    }

    @Transactional
    public void cancelBooking(String bookingId) throws Exception {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) throw new RuntimeException("Booking not found");

        Booking booking = bookingOpt.get();
        Show show = booking.getShow();
        List<Seat> seats = booking.getSeats();

        boolean wasFull = getAvailableSeats(show.getId()).isEmpty();

        seats.forEach(seat -> seat.setStatus(SeatStatus.AVAILABLE));
        seatRepository.saveAll(seats);

        booking.setStatus(BookingStatus.NOT_BOOKED);
        bookingRepository.save(booking);

        if (wasFull) {
            observerManager.notifyObservers(show.getId());
        }
    }

    public List<Booking> getBookingsByUser(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        return bookingRepository.findByUser(userOpt.get());
    }
}
