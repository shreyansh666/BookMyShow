package com.BookMyShow.demo.repository;

import com.BookMyShow.demo.entities.SeatTemplate;
import com.BookMyShow.demo.entities.ShowSeat;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SeatTemplateRepository extends MongoRepository<SeatTemplate, String> {
}
