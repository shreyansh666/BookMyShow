package com.BookMyShow.demo.service;


import com.BookMyShow.demo.dto.AddShowRequest;
import com.BookMyShow.demo.entities.City;
import com.BookMyShow.demo.entities.Screen;
import com.BookMyShow.demo.entities.Show;
import com.BookMyShow.demo.entities.Theater;
import com.BookMyShow.demo.enums.ScreenType;

public interface AdminService {

    City addCity(String cityName);

    Theater addTheater(String cityId, String name, String address);

    Screen addScreen(String theaterId, String name, ScreenType type);

    Show addShow(String screenId, AddShowRequest request);


}





