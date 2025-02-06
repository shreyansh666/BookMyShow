package com.BookMyShow.demo.repository;

import com.BookMyShow.demo.entities.ShowSeat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ShowSeatRepository extends MongoRepository<ShowSeat, String> {
    void deleteByShowId(String showId);
    List<ShowSeat> findByShowId(String showId);
}
