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
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final int SESSION_TIMEOUT_SECONDS = 60;

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
    @Autowired
    private final UserBookingSessionRepository sessionRepository;


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
//
//    private void checkValidity(String showId, User user, List<Seat> seats) {
//        List<Seat> availableSeats = getAvailableSeats(showId);
//
//        if (availableSeats.isEmpty()) {
//            notifyUser(showId, user);
//            throw new RuntimeException("No seats available. You will be notified if seats become available.");
//        }
//
//        for (Seat seat : seats) {
//            if (seat.getStatus() != SeatStatus.AVAILABLE) {
//                throw new RuntimeException("Seat already booked");
//            }
//        }
//    }

    @Transactional
    @Override
    public UserBookingSession createBookingSession(String userId, String showId, List<String> seatIds) throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Show> showOpt = showRepository.findById(showId);
        if (userOpt.isEmpty() || showOpt.isEmpty()) throw new RuntimeException("Invalid booking details");

        User user = userOpt.get();
        Show show = showOpt.get();


        List<Seat> seats = seatRepository.findAllById(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new RuntimeException("Some seats not found.");
        }

        for (Seat seat : seats) {
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new RuntimeException("Seat " + seat.getId() + " is not available.");
            }
        }

        seats.forEach(seat -> {
            seat.setStatus(SeatStatus.TEMPERORY_UNAVAILABLE);
            seatRepository.save(seat);
        });


        String sessionId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(SESSION_TIMEOUT_SECONDS);
        UserBookingSession session = UserBookingSession.builder()
                .sessionId(sessionId)
                .user(user)
                .show(show)
                .seats(seats)
                .status(SessionStatus.ACTIVE)
                .createdAt(now)
                .expiresAt(expiresAt)
                .build();

        sessionRepository.save(session);


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Check if the session is still active.
                Optional<UserBookingSession> sessionOpt = sessionRepository.findById(sessionId);
                if (sessionOpt.isPresent() && sessionOpt.get().getStatus() == SessionStatus.ACTIVE) {
                    cancelSession(sessionOpt.get());
                }
            }
        }, SESSION_TIMEOUT_SECONDS * 1000L);

        return session;
    }


//    @Transactional
//    public synchronized Booking bookTickets(String userId, String showId, List<String> seatIds, PaymentType payment) throws Exception {
//        synchronized (this) {
//            Optional<User> userOpt = userRepository.findById(userId);
//            Optional<Show> showOpt = showRepository.findById(showId);
//            List<Seat> seats = seatRepository.findAllById(seatIds);
//
//            if (userOpt.isEmpty() || showOpt.isEmpty()) throw new RuntimeException("Invalid booking details");
//
//            User user = userOpt.get();
//            Show show = showOpt.get();
//
//            checkValidity(showId, user, seats);
//
//            seats.forEach(seat -> seat.setStatus(SeatStatus.BOOKED));
//            seatRepository.saveAll(seats);
//
//            double amount = seats.stream().mapToDouble(Seat::getPrice).sum();
//            paymentManager.pay(payment, amount);
//
//
//            Booking booking = Booking.builder()
//                    .user(user)
//                    .show(show)
//                    .seats(seats)
//                    .payment(payment)
//                    .status(BookingStatus.BOOKED)
//                    .build();
//
//            return bookingRepository.save(booking);
//        }
//    }


    @Transactional
    @Override
    public Booking completeBooking(String sessionId, PaymentType payment) throws Exception {
        Optional<UserBookingSession> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) throw new RuntimeException("Session not found or expired");

        UserBookingSession session = sessionOpt.get();
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new RuntimeException("Session is no longer active.");
        }

        double amount = session.getSeats().stream().mapToDouble(Seat::getPrice).sum();
        boolean paymentSuccess = paymentManager.pay(payment, amount);
        if (!paymentSuccess) {

            releaseSeats(session);
            session.setStatus(SessionStatus.CANCELLED);
            sessionRepository.save(session);
            throw new RuntimeException("Payment failed.");
        }


        session.getSeats().forEach(seat -> {
            seat.setStatus(SeatStatus.BOOKED);
            seatRepository.save(seat);
        });

        session.setStatus(SessionStatus.COMPLETED);
        sessionRepository.save(session);

        Booking booking = Booking.builder()
                .user(session.getUser())
                .show(session.getShow())
                .seats(session.getSeats())
                .payment(payment)
                .status(BookingStatus.BOOKED)
                .build();

        return bookingRepository.save(booking);
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

    private void cancelSession(UserBookingSession session) {
        if (session.getStatus() == SessionStatus.ACTIVE) {
            System.out.println("Session " + session.getSessionId() + " timed out. Releasing seats.");
            releaseSeats(session);
            session.setStatus(SessionStatus.TIMED_OUT);
            sessionRepository.save(session);
        }
    }


    private void releaseSeats(UserBookingSession session) {
        session.getSeats().forEach(seat -> {
            seat.setStatus(SeatStatus.AVAILABLE);
            seatRepository.save(seat);
        });
    }

    public List<Booking> getBookingsByUser(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        return bookingRepository.findByUser(userOpt.get());
    }
}
