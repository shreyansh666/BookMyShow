package com.BookMyShow.demo.controller;

import com.BookMyShow.demo.dto.ScreenSeatTemplateConfigRequest;
import com.BookMyShow.demo.dto.ShowRequest;
import com.BookMyShow.demo.dto.TheaterRequest;
import com.BookMyShow.demo.entities.*;
import com.BookMyShow.demo.enums.ScreenType;
import com.BookMyShow.demo.exception.ResourceNotFoundException;
import com.BookMyShow.demo.service.AdminService;
import com.BookMyShow.demo.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> addTheater(@RequestBody TheaterRequest request) {
        try {
            Theater theater = adminService.addTheater(request);
            return CommonUtil.createBuildResponse(theater, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/addScreen")
    public ResponseEntity<?> addScreen(@RequestParam String theaterId,
                                       @RequestParam String name,
                                       @RequestParam ScreenType type,
                                       @RequestBody ScreenSeatTemplateConfigRequest config) {
        try {
            Screen screen = adminService.addScreen(theaterId, name, type, config);
            return CommonUtil.createBuildResponse(screen, HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/addShow")
    public ResponseEntity<?> addShow(@RequestParam String screenId, @RequestBody ShowRequest request) {
        try {
            Show show = adminService.addShow(screenId, request);
            return CommonUtil.createBuildResponse(show, HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/updateShow/{showId}")
    public ResponseEntity<?> updateShow(@PathVariable String showId, @RequestBody ShowRequest request) {
        try {
            Show updatedShow = adminService.updateShow(showId, request);
            return CommonUtil.createBuildResponse(updatedShow, HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/updateScreen/{screenId}")
    public ResponseEntity<?> updateScreen(@PathVariable String screenId, @RequestParam String name, @RequestParam ScreenType type) {
        try {
            Screen updatedScreen = adminService.updateScreen(screenId, name, type);
            return CommonUtil.createBuildResponse(updatedScreen, HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteCity/{cityId}")
    public ResponseEntity<?> deleteCity(@PathVariable String cityId) {
        try {
            adminService.deleteCity(cityId);
            return CommonUtil.createBuildResponse("City deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteTheater/{theaterId}")
    public ResponseEntity<?> deleteTheater(@PathVariable String theaterId) {
        try {
            adminService.deleteTheater(theaterId);
            return CommonUtil.createBuildResponse("Theater deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteScreen/{screenId}")
    public ResponseEntity<?> deleteScreen(@PathVariable String screenId) {
        try {
            adminService.deleteScreen(screenId);
            return CommonUtil.createBuildResponse("Screen deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteShow/{showId}")
    public ResponseEntity<?> deleteShow(@PathVariable String showId) {
        try {
            adminService.deleteShow(showId);
            return CommonUtil.createBuildResponse("Show deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getCities")
    public ResponseEntity<?> getCities() {
        try {
            List<City> cities = adminService.getCities();
            return CommonUtil.createBuildResponse(cities, HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getTheaters/{cityId}")
    public ResponseEntity<?> getTheaters(@PathVariable String cityId) {
        try {
            List<Theater> theaters = adminService.getTheaters(cityId);
            return CommonUtil.createBuildResponse(theaters, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getScreens/{theaterId}")
    public ResponseEntity<?> getScreens(@PathVariable String theaterId) {
        try {
            List<Screen> screens = adminService.getScreens(theaterId);
            return CommonUtil.createBuildResponse(screens, HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getShows/{screenId}")
    public ResponseEntity<?> getShows(@PathVariable String screenId) {
        try {
            List<Show> shows = adminService.getShows(screenId);
            return CommonUtil.createBuildResponse(shows, HttpStatus.OK);
        } catch (Exception e) {
            return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
