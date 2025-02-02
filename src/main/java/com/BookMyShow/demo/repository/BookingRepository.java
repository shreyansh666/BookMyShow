package com.BookMyShow.demo.repository;


import com.BookMyShow.demo.entities.Booking;
import com.BookMyShow.demo.entities.User;
import com.BookMyShow.demo.enums.BookingStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByUser(User user);
    List<Booking> findByStatus(BookingStatus status);
}

