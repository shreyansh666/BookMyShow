package com.BookMyShow.demo.repository;


import com.BookMyShow.demo.entities.Show;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowRepository extends MongoRepository<Show, String> {

    List<Show> findByScreenId(String screenId);

    List<Show> findByMovieId(String movieId);

    void deleteByScreenId(String screenId);

    @Query("{'screenId': ?0, 'startTime': {'$gte': ?1, '$lte': ?2}}")
    List<Show> findConflictingShows(String screenId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("{'movieId': ?0, 'startTime': {'$gte': ?1}}")
    List<Show> findUpcomingShowsByMovie(String movieId, LocalDateTime currentTime);
}

