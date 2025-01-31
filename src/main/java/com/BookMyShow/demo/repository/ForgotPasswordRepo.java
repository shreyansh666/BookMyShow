package com.BookMyShow.demo.repository;


import com.BookMyShow.demo.entities.ForgotPassword;
import com.BookMyShow.demo.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ForgotPasswordRepo extends MongoRepository<ForgotPassword,Integer> {
    Optional<ForgotPassword> findByUser(User user);
    ForgotPassword findById(String Id);


}
