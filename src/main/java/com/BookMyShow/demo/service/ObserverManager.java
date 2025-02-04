package com.BookMyShow.demo.service;

import com.BookMyShow.demo.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
public interface ObserverManager {

        public  void addObserver(String showId, User observer) ;

        public void notifyObservers(String showId) throws Exception;


}
