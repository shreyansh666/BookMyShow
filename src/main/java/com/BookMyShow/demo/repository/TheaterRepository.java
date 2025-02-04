package com.BookMyShow.demo.repository;



import com.BookMyShow.demo.entities.Show;
import com.BookMyShow.demo.entities.Theater;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TheaterRepository extends MongoRepository<Theater, String> {
    List<Theater> findByCityId(String cityId);
    Optional<Theater> findByNameAndAddress(String name, String address);

    @Query("{'name': ?0, 'address': ?1}")
    boolean existsByNameAndAddress(String name, String address);
}
