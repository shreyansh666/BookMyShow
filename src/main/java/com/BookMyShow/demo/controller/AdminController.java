package com.BookMyShow.demo.controller;

import com.BookMyShow.demo.dto.AddShowRequest;
import com.BookMyShow.demo.dto.ShowSeatConfigRequest; // Import ShowSeatConfigRequest
import com.BookMyShow.demo.entities.*;
import com.BookMyShow.demo.service.AdminService;
import com.BookMyShow.demo.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    private final AdminService adminService;

    @PostMapping("/addCity")
    public ResponseEntity<?> addCity(@RequestParam String cityName) {
        try {
            City city = adminService.addCity(cityName);
            return CommonUtil.createBuildResponse(city, HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/addTheater")
    public ResponseEntity<?> addTheater(@RequestParam String cityId, @RequestParam String name, @RequestParam String address) {
        try {
            Theater theater = adminService.addTheater(cityId, name, address);
            return CommonUtil.createBuildResponse(theater, HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/addScreen")
    public ResponseEntity<?> addScreen(@RequestParam String theaterId, @RequestParam String name) {
        try {
            Screen screen = adminService.addScreen(theaterId, name);
            return CommonUtil.createBuildResponse(screen, HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/addShow")
    public ResponseEntity<?> addShow(@RequestParam String screenId, @RequestBody AddShowRequest request) {
        try {
            Show show = adminService.addShow(screenId, request);
            return CommonUtil.createBuildResponse(show, HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}