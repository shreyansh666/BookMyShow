package com.BookMyShow.demo.service.serviceImpl;

import com.BookMyShow.demo.dto.ShowRequest;
import com.BookMyShow.demo.dto.TheaterRequest;
import com.BookMyShow.demo.dto.ShowSeatConfigRequest;
import com.BookMyShow.demo.entities.*;
import com.BookMyShow.demo.enums.ScreenType;
import com.BookMyShow.demo.enums.SeatStatus;
import com.BookMyShow.demo.enums.SeatType;
import com.BookMyShow.demo.exception.ResourceNotFoundException;
import com.BookMyShow.demo.repository.*;
import com.BookMyShow.demo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CityRepository cityRepository;
    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final MovieRepository movieRepository;

    @Transactional
    public City addCity(String cityName) {
        City city = City.builder()
                .name(cityName)
                .theaters(new ArrayList<>())
                .build();

        return cityRepository.save(city);
    }

//    @Transactional
//    public Theater addTheater(String cityId, String name, String address) {
//        City city = cityRepository.findById(cityId)
//                .orElseThrow(() -> new RuntimeException("City not found"));
//
//        boolean exists = city.getTheaters().stream()
//                .anyMatch(theater -> theater.getName().equalsIgnoreCase(name) && theater.getAddress().equalsIgnoreCase(address)
//                );
//
//        if (exists) {
//            throw new RuntimeException("Already Exists");
//        }
//
//        Theater theater = Theater.builder()
//                .name(name)
//                .address(address)
//                .screens(new ArrayList<>())
//                .build();
//
//        Theater savedTheater = theaterRepository.save(theater);
//        city.getTheaters().add(savedTheater);
//        cityRepository.save(city);
//
//        return savedTheater;
//    }

    @Transactional
    public Theater addTheater(TheaterRequest request) throws ResourceNotFoundException {

        if (request.getName() == null || request.getCity() == null || request.getCity().trim().isEmpty() ||
                request.getState() == null || request.getState().trim().isEmpty() ||
                request.getPinCode() == null || request.getPinCode().trim().isEmpty() ||
                request.getLocality() == null || request.getLocality().trim().isEmpty()) {
            throw new IllegalArgumentException("Data Not accurate");
        }


        String address = String.format("%s, %s, %s - %s", request.getLocality(), request.getCity(), request.getState(), request.getPinCode());


        Optional<City> city = cityRepository.findByName(request.getCity());
        if(!city.isPresent()) throw new ResourceNotFoundException("city not present");

        boolean exists = city.get().getTheaters().stream()
                .anyMatch(theater ->
                        theater.getName().equalsIgnoreCase(request.getName())
                                && theater.getAddress().equalsIgnoreCase(address)
                );

        if (exists) {
            throw new RuntimeException("Theater already exists in this city.");
        }


        Theater theater = Theater.builder()
                .name(request.getName())
                .address(address)
                .screens(new ArrayList<>())
                .build();

        Theater savedTheater = theaterRepository.save(theater);
        city.get().getTheaters().add(savedTheater);
        cityRepository.save(city.get());

        return savedTheater;
    }



    @Transactional
    public Screen addScreen(String theaterId, String name, ScreenType type) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("Theater not found"));

        Screen screen = Screen.builder()
                .name(name)
                .type(type)
                .shows(new ArrayList<>())
                .build();

        Screen savedScreen = screenRepository.save(screen);

        theater.getScreens().add(savedScreen);
        theaterRepository.save(theater);

        return savedScreen;
    }

    @Transactional
    public Show addShow(String screenId, ShowRequest request) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new RuntimeException("Screen not found"));

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));


        List<Seat> seats = createSeats(request.getSeatConfig());
        List<Seat> savedSeats = seatRepository.saveAll(seats);

        Show show = Show.builder()
                .movie(movie)
                .ScreenId(screenId)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .seats(savedSeats)
                .build();

        Show savedShow = showRepository.save(show);


        savedSeats.forEach(seat -> {
            seat.setShowId(savedShow.getId());
            seatRepository.save(seat);
        });

        screen.getShows().add(savedShow);
        screenRepository.save(screen);

        return savedShow;
    }

    private List<Seat> createSeats(ShowSeatConfigRequest config) {
        List<Seat> seats = new ArrayList<>();

        addSeatsOfType(seats, SeatType.REGULAR, config.getRegularCount(),
                config.getRegularPrice());

        addSeatsOfType(seats, SeatType.GOLD, config.getVipCount(),
                config.getVipPrice());


        addSeatsOfType(seats, SeatType.PLATINUM, config.getPremiumCount(),
                config.getPremiumPrice());



        return seats;
    }

    private void addSeatsOfType(List<Seat> seats, SeatType type, int count,
                                double price) {
        for (int i = 1; i <= count; i++) {
            String seatNumber = String.format("%s-%d", type.name().charAt(0), i);
            Seat seat = Seat.builder()
                    .seatNumber(seatNumber)
                    .seatType(type)
                    .price(price)
                    .status(SeatStatus.AVAILABLE)
                    .version(0)
                    .build();
            seats.add(seat);
        }
    }


    public void deleteShow(String showId) {
        if (!showRepository.existsById(showId)) {
            throw new RuntimeException("Show not found with ID: " + showId);
        }
        showRepository.deleteById(showId);
    }

    @Transactional
    public Show updateShow(String showId, ShowRequest request) {
        Show existingShow = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found with ID: " + showId));
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        existingShow.setMovie(movie);
        existingShow.setStartTime(request.getStartTime());
        existingShow.setEndTime(request.getEndTime());


        List<Seat> oldSeats = existingShow.getSeats();
        if (oldSeats != null && !oldSeats.isEmpty()) {
            seatRepository.deleteAll(oldSeats);
        }

        List<Seat> newSeats = createSeats(request.getSeatConfig());
        List<Seat> savedNewSeats = seatRepository.saveAll(newSeats);

//        savedNewSeats.forEach(seat -> {
//           seat.setShowId(existingShow.getId());
//            seatRepository.save(seat);
//        });

        existingShow.setSeats(savedNewSeats);

        return showRepository.save(existingShow);
    }


    @Transactional
    public Screen updateScreen(String screenId, String name, ScreenType type) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new RuntimeException("Screen not found with ID: " + screenId));
        screen.setName(name);
        screen.setType(type);
        return screenRepository.save(screen);
    }


    public List<Theater> getTheaters(String cityId) {
        return theaterRepository.findByCityId(cityId);
    }

    public List<Show> getShows(String screenId) {
        return showRepository.findByScreenId(screenId);
    }

    public List<City> getCities() {
        return cityRepository.findAll();
    }

    public List<Screen> getScreens(String theaterId) {
        return screenRepository.findByTheaterId(theaterId);
    }




    public void deleteCity(String cityId) {
        City city = cityRepository.findById(cityId)
                    .orElseThrow(() -> new RuntimeException("City not found with ID: " + cityId));

        List<Theater> theaters = city.getTheaters();

        for (Theater theater : theaters) {
            List<Screen> screens = screenRepository.findByTheaterId(theater.getId());
            for (Screen screen : screens) {
                List<Show> shows = showRepository.findByScreenId(screen.getId());
                for (Show show : shows) {
                    seatRepository.deleteByShowId(show.getId());
                }
                showRepository.deleteByScreenId(screen.getId());
             }
               screenRepository.deleteByTheaterId(theater.getId());
            }
            theaterRepository.deleteAll(theaters);
            cityRepository.deleteById(cityId);
        }


    public void deleteScreen(String screenId) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new RuntimeException("Screen not found with ID: " + screenId));

        List<Show> shows = showRepository.findByScreenId(screenId);

        for (Show show : shows) {
            seatRepository.deleteByShowId(show.getId());
        }

        showRepository.deleteByScreenId(screenId);
        screenRepository.deleteById(screenId);
    }



    public void deleteTheater(String theaterId) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("Theater not found with ID: " + theaterId));

        List<Screen> screens = screenRepository.findByTheaterId(theaterId);

        for (Screen screen : screens) {
            List<Show> shows = showRepository.findByScreenId(screen.getId());
            for (Show show : shows) {
                seatRepository.deleteByShowId(show.getId());
            }
            showRepository.deleteByScreenId(screen.getId());
        }

        screenRepository.deleteByTheaterId(theaterId);
        theaterRepository.deleteById(theaterId);
    }




}