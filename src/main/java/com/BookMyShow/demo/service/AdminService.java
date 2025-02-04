package com.BookMyShow.demo.service;


import com.BookMyShow.demo.dto.ShowRequest;
import com.BookMyShow.demo.dto.TheaterRequest;
import com.BookMyShow.demo.entities.City;
import com.BookMyShow.demo.entities.Screen;
import com.BookMyShow.demo.entities.Show;
import com.BookMyShow.demo.entities.Theater;
import com.BookMyShow.demo.enums.ScreenType;
import com.BookMyShow.demo.exception.ResourceNotFoundException;

import java.util.List;

public interface AdminService {

    City addCity(String cityName);
    List<City> getCities();
    void deleteCity(String cityId);



    Theater addTheater(TheaterRequest request) throws ResourceNotFoundException;
    List<Theater> getTheaters(String cityId) throws ResourceNotFoundException;
//    List<Theater> getTheatersByName(String name) throws ResourceNotFoundException;
    void deleteTheater(String theaterId);


    Screen addScreen(String theaterId, String name, ScreenType type);
    Screen updateScreen(String screenId, String name, ScreenType type);
    List<Screen> getScreens(String theaterId);
    public void deleteScreen(String screenId);


    Show addShow(String screenId, ShowRequest request);
    Show updateShow(String showId, ShowRequest request);
    List<Show> getShows(String screenId);
    void deleteShow(String showId);

}





