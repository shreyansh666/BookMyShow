package com.BookMyShow.demo.service.serviceImpl;

import com.BookMyShow.demo.dto.AddShowRequest;
import com.BookMyShow.demo.dto.ShowSeatConfigRequest;
import com.BookMyShow.demo.entities.*;
import com.BookMyShow.demo.enums.SeatStatus;
import com.BookMyShow.demo.enums.SeatType;
import com.BookMyShow.demo.repository.*;
import com.BookMyShow.demo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    public Theater addTheater(String cityId, String name,String address) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new RuntimeException("City not found"));

        Theater theater = Theater.builder()
                .name(name)
                .address(address)
                .screens(new ArrayList<>())
                .build();

        Theater savedTheater = theaterRepository.save(theater);

        city.getTheaters().add(savedTheater);
        cityRepository.save(city);

        return savedTheater;
    }

    @Transactional
    public Screen addScreen(String theaterId, String name) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("Theater not found"));

        Screen screen = Screen.builder()
                .name(name)
                .shows(new ArrayList<>())
                .build();

        Screen savedScreen = screenRepository.save(screen);

        theater.getScreens().add(savedScreen);
        theaterRepository.save(theater);

        return savedScreen;
    }

    @Transactional
    public Show addShow(String screenId, AddShowRequest request) {
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
}