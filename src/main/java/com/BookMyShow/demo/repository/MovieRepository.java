package com.BookMyShow.demo.repository;

import com.BookMyShow.demo.entities.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {


    List<Movie> findByGenre(String genre);
    Optional<Movie> findByTitle(String title);

    //    @Query("{'releaseDate': {'$gte': ?0}}")
    //    List<Movie> findUpcomingMovies(LocalDateTime currentDate);

    //    @Query("{'isActive': true}")
    //    List<Movie> findAllActiveMovies();


}

