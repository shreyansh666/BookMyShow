package com.BookMyShow.demo.repository;

import com.BookMyShow.demo.entities.Theater;
import com.BookMyShow.demo.entities.UserBookingSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserBookingSessionRepository extends MongoRepository<UserBookingSession, String> {
}
