package com.BookMyShow.demo.service.serviceImpl;

import com.BookMyShow.demo.entities.Booking;
import com.BookMyShow.demo.entities.Show;
import com.BookMyShow.demo.entities.ShowSeat;
import com.BookMyShow.demo.entities.User;
import com.BookMyShow.demo.entities.UserBookingSession;
import com.BookMyShow.demo.enums.BookingStatus;
import com.BookMyShow.demo.enums.PaymentType;
import com.BookMyShow.demo.enums.SeatStatus;
import com.BookMyShow.demo.enums.SessionStatus;
import com.BookMyShow.demo.repository.BookingRepository;
import com.BookMyShow.demo.repository.ShowRepository;
import com.BookMyShow.demo.repository.ShowSeatRepository;
import com.BookMyShow.demo.repository.UserBookingSessionRepository;
import com.BookMyShow.demo.repository.UserRepository;
import com.BookMyShow.demo.service.BookingService;
import com.BookMyShow.demo.service.ObserverManager;
import com.BookMyShow.demo.strategy.NotificationStrategy.strategyImpl.NotificationManager;
import com.BookMyShow.demo.strategy.PaymentStrategy.strategyImpl.PaymentManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final ShowSeatRepository showSeatRepository;

    @Autowired
    private final PaymentManager paymentManager;

    @Autowired
    private final NotificationManager notificationManager;

    @Autowired
    private final ObserverManager observerManager;

    @Autowired
    private final UserBookingSessionRepository sessionRepository;


    public List<ShowSeat> getAvailableSeats(String showId) {
        List<ShowSeat> seats = showSeatRepository.findByShowId(showId);
        if (seats == null || seats.isEmpty()) {
            throw new RuntimeException("Show not found or no seats available");
        }
        return seats.stream()
                .filter(seat -> SeatStatus.AVAILABLE.equals(seat.getStatus()))
                .collect(Collectors.toList());
    }

    public void notifyUser(String showId, User user) {
        observerManager.addObserver(showId, user);
    }


    @Transactional
    @Override
    public UserBookingSession createBookingSession(String userId, String showId, List<String> seatIds) throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Show> showOpt = showRepository.findById(showId);
        if (userOpt.isEmpty() || showOpt.isEmpty()) {
            throw new RuntimeException("Invalid booking details");
        }
        User user = userOpt.get();
        Show show = showOpt.get();


        List<ShowSeat> seats = showSeatRepository.findAllById(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new RuntimeException("Some seats not found.");
        }
        for (ShowSeat seat : seats) {
            if (!SeatStatus.AVAILABLE.equals(seat.getStatus())) {
                throw new RuntimeException("Seat " + seat.getId() + " is not available.");
            }
        }

        seats.forEach(seat -> {
            seat.setStatus(SeatStatus.TEMPERORY_UNAVAILABLE);
            showSeatRepository.save(seat);
        });

        String sessionId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(SESSION_TIMEOUT_SECONDS);
        UserBookingSession session = UserBookingSession.builder()
                .sessionId(sessionId)
                .user(user)
                .show(show)
                .seats(new ArrayList<>(seats))
                .status(SessionStatus.ACTIVE)
                .createdAt(now)
                .expiresAt(expiresAt)
                .build();
        sessionRepository.save(session);


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Optional<UserBookingSession> sessionOpt = sessionRepository.findById(sessionId);
                if (sessionOpt.isPresent() && sessionOpt.get().getStatus() == SessionStatus.ACTIVE) {
                    cancelSession(sessionOpt.get());
                }
            }
        }, SESSION_TIMEOUT_SECONDS * 1000L);

        return session;
    }


    @Transactional
    @Override
    public Booking completeBooking(String sessionId, PaymentType payment) throws Exception {
        Optional<UserBookingSession> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) throw new RuntimeException("Session not found or expired");

        UserBookingSession session = sessionOpt.get();
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new RuntimeException("Session is no longer active.");
        }

        double amount = session.getSeats().stream()
                .mapToDouble(ShowSeat::getPrice)
                .sum();

        boolean paymentSuccess = paymentManager.pay(payment, amount);
        if (!paymentSuccess) {
            releaseSeats(session);
            session.setStatus(SessionStatus.CANCELLED);
            sessionRepository.save(session);
            throw new RuntimeException("Payment failed.");
        }


        session.getSeats().forEach(seat -> {
            seat.setStatus(SeatStatus.BOOKED);
            showSeatRepository.save(seat);
        });
        session.setStatus(SessionStatus.COMPLETED);
        sessionRepository.save(session);

        Booking booking = Booking.builder()
                .user(session.getUser())
                .show(session.getShow())
                .seats(session.getSeats().stream().collect(Collectors.toList()))
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
        List<ShowSeat> seats = booking.getSeats();

        boolean wasFull = getAvailableSeats(show.getId()).isEmpty();

        // Mark seats as AVAILABLE.
        seats.forEach(seat -> {
            seat.setStatus(SeatStatus.AVAILABLE);
            showSeatRepository.save(seat);
        });

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
            showSeatRepository.save(seat);
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
