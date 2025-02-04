package com.BookMyShow.demo.repository;

import com.BookMyShow.demo.entities.Payment;
import com.BookMyShow.demo.entities.Seat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends MongoRepository<Seat, String> {

    Optional<Seat> findById(String Id);

    List<Seat> findByShowId(String showId);

    void deleteByShowId(String showId);

    @Query("{'showId': ?0, 'status': 'AVAILABLE'}")
    List<Seat> findAvailableSeats(String showId);

    @Query("{'showId': ?0, 'seatType': ?1}")
    List<Seat> findByShowIdAndSeatType(String showId, String seatType);
}
