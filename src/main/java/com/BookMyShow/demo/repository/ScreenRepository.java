package com.BookMyShow.demo.repository;


import com.BookMyShow.demo.entities.Screen;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScreenRepository extends MongoRepository<Screen, String> {
    List<Screen> findByTheaterId(String theaterId);

    @Query("{'theaterId': ?0, 'name': ?1}")
    Optional<Screen> findByTheaterIdAndName(String theaterId, String name);
    void deleteByTheaterId(String theaterId);

}
