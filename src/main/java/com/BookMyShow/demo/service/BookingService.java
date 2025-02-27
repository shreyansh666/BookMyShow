package com.BookMyShow.demo.service;


import com.BookMyShow.demo.entities.Booking;
import com.BookMyShow.demo.entities.Seat;
import com.BookMyShow.demo.entities.ShowSeat;
import com.BookMyShow.demo.entities.UserBookingSession;
import com.BookMyShow.demo.enums.PaymentType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookingService {

    public List<ShowSeat> getAvailableSeats(String showId);

//    public Booking bookTickets(String userId, String showId, List<String> seatIds, PaymentType payment) throws Exception;

    public void cancelBooking(String bookingId) throws Exception;

    public List<Booking> getBookingsByUser(String userId);

    UserBookingSession createBookingSession(String userId, String showId, List<String> seatIds) throws Exception;

    Booking completeBooking(String sessionId, PaymentType payment) throws Exception;

}