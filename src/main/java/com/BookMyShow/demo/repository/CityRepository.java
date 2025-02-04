package com.BookMyShow.demo.repository;

import com.BookMyShow.demo.entities.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CityRepository extends MongoRepository<City, String> {

    Optional<City> findByName(String name);
}
