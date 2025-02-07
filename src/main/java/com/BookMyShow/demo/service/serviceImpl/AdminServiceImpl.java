package com.BookMyShow.demo.service.serviceImpl;

import com.BookMyShow.demo.dto.ShowRequest;
import com.BookMyShow.demo.dto.ScreenSeatTemplateConfigRequest;
import com.BookMyShow.demo.dto.TheaterRequest;
import com.BookMyShow.demo.entities.*;
import com.BookMyShow.demo.enums.ScreenType;
import com.BookMyShow.demo.enums.SeatStatus;
import com.BookMyShow.demo.enums.SeatType;
import com.BookMyShow.demo.exception.ResourceNotFoundException;
import com.BookMyShow.demo.repository.CityRepository;
import com.BookMyShow.demo.repository.MovieRepository;
import com.BookMyShow.demo.repository.ScreenRepository;
import com.BookMyShow.demo.repository.ShowRepository;
import com.BookMyShow.demo.repository.ShowSeatRepository;
import com.BookMyShow.demo.repository.SeatTemplateRepository;
import com.BookMyShow.demo.repository.TheaterRepository;
import com.BookMyShow.demo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    @Autowired
    private final CityRepository cityRepository;

    @Autowired
    private final TheaterRepository theaterRepository;

    @Autowired
    private final ScreenRepository screenRepository;

    @Autowired
    private final ShowRepository showRepository;

    // Using ShowSeatRepository for persisting ShowSeat entities.
    @Autowired
    private final ShowSeatRepository showSeatRepository;

    @Autowired
    private final MovieRepository movieRepository;

    // Repository for SeatTemplate so that orphaned templates can be deleted.
    @Autowired
    private final SeatTemplateRepository seatTemplateRepository;

    @Transactional
    public City addCity(String cityName) {
        City city = City.builder()
                .name(cityName)
//                .theaters(new ArrayList<>())
                .build();

        cityRepository.save(city);
        return city;
    }

    @Transactional
    public Theater addTheater(TheaterRequest request) throws ResourceNotFoundException {
        if (request.getName() == null || request.getCity() == null || request.getCity().trim().isEmpty() ||
                request.getState() == null || request.getState().trim().isEmpty() ||
                request.getPinCode() == null || request.getPinCode().trim().isEmpty() ||
                request.getLocality() == null || request.getLocality().trim().isEmpty()) {
            throw new IllegalArgumentException("Data not accurate");
        }

        String address = String.format("%s, %s, %s - %s",
                request.getLocality(), request.getCity(), request.getState(), request.getPinCode());

        Optional<City> city = cityRepository.findByName(request.getCity());
        if (!city.isPresent()) throw new ResourceNotFoundException("City not present");


        List<Theater> theaters =  theaterRepository.findByCityId(city.get().getId());

        boolean exists = theaters.stream()
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

        Theater savedTheater  = theaterRepository.save(theater);
//        city.get().getTheaters().add(savedTheater);
        cityRepository.save(city.get());




        return savedTheater;
    }

    @Transactional
    public Screen addScreen(String theaterId, String name, ScreenType type, ScreenSeatTemplateConfigRequest config) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("Theater not found"));

        List<SeatTemplate> seatTemplates = createSeatTemplates(config);

        Screen screen = Screen.builder()
                .name(name)
                .type(type)
                .seats(seatTemplates)
                .build();

        Screen savedScreen = screenRepository.save(screen);
        theater.getScreens().add(savedScreen);
        theaterRepository.save(theater);

        return savedScreen;
    }

    private List<SeatTemplate> createSeatTemplates(ScreenSeatTemplateConfigRequest config) {
        List<SeatTemplate> seatTemplates = new ArrayList<>();
        addSeatTemplatesOfType(seatTemplates, SeatType.REGULAR, config.getRegularCount(), config.getRegularPrice());
        addSeatTemplatesOfType(seatTemplates, SeatType.GOLD, config.getVipCount(), config.getVipPrice());
        addSeatTemplatesOfType(seatTemplates, SeatType.PLATINUM, config.getPremiumCount(), config.getPremiumPrice());
        return seatTemplates;
    }

    private void addSeatTemplatesOfType(List<SeatTemplate> seatTemplates, SeatType type, int count, double defaultPrice) {
        for (int i = 1; i <= count; i++) {
            String seatNumber = String.format("%s-%d", type.name().charAt(0), i);
            SeatTemplate template = SeatTemplate.builder()
                    .seatNumber(seatNumber)
                    .seatType(type)
                    .defaultPrice(defaultPrice)
                    .build();
            seatTemplates.add(template);
        }
    }


    @Transactional
    public Show addShow(String screenId, ShowRequest request) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new RuntimeException("Screen not found"));

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));


        Show show = Show.builder()
                .movie(movie)
                .ScreenId(screenId)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();
        Show savedShow = showRepository.save(show);

        List<ShowSeat> showSeats = createShowSeats(screen, request.getSeatConfig(), savedShow.getId());
        showSeatRepository.saveAll(showSeats);

        return savedShow;
    }

    private List<ShowSeat> createShowSeats(Screen screen, ScreenSeatTemplateConfigRequest config, String showId) {
        List<ShowSeat> showSeats = new ArrayList<>();
        for (SeatTemplate seatTemplate : screen.getSeats()) {
            double price = seatTemplate.getDefaultPrice();
            if (seatTemplate.getSeatType() == SeatType.REGULAR && config.getRegularPrice() > 0) {
                price = config.getRegularPrice();
            } else if (seatTemplate.getSeatType() == SeatType.GOLD && config.getVipPrice() > 0) {
                price = config.getVipPrice();
            } else if (seatTemplate.getSeatType() == SeatType.PLATINUM && config.getPremiumPrice() > 0) {
                price = config.getPremiumPrice();
            }

            ShowSeat showSeat = ShowSeat.builder()
                    .seatTemplate(seatTemplate)
                    .price(price)
                    .status(SeatStatus.AVAILABLE)
                    .showId(showId)
                    .build();
            showSeats.add(showSeat);
        }
        return showSeats;
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

    public List<Theater> getTheaters(String cityId) throws ResourceNotFoundException {
        Optional<City> city = cityRepository.findById(cityId);
        if (city.isPresent()) return theaterRepository.findByCityId(city.get().getId());
        throw new ResourceNotFoundException("City with this id does not exist");
    }

    public List<Show> getShows(String screenId) {
        return showRepository.findByScreenId(screenId);
    }

    public List<City> getCities() {
        return cityRepository.findAll();
    }

    public List<Screen> getScreens(String theaterId) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("Theater not found with ID: " + theaterId));
        return theater.getScreens();
    }



    public void deleteCity(String cityId) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new RuntimeException("City not found with ID: " + cityId));

        List<Theater> theaters =  theaterRepository.findByCityId(city.getId());
        for (Theater theater : theaters) {
            List<Screen> screens = theater.getScreens();
            for (Screen screen : screens) {
                List<Show> shows = showRepository.findByScreenId(screen.getId());
                for (Show show : shows) {
                    showSeatRepository.deleteByShowId(show.getId());
                }
                showRepository.deleteByScreenId(screen.getId());
                if (screen.getSeats() != null && !screen.getSeats().isEmpty()) {
                    seatTemplateRepository.deleteAll(screen.getSeats());
                }
                screenRepository.delete(screen);
            }
//            screenRepository.deleteByTheaterId(theater.getId());
        }
        theaterRepository.deleteAll(theaters);
        cityRepository.deleteById(cityId);
    }


    public void deleteScreen(String screenId) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new RuntimeException("Screen not found with ID: " + screenId));

        List<Show> shows = showRepository.findByScreenId(screenId);
        for (Show show : shows) {
            showSeatRepository.deleteByShowId(show.getId());
        }
        showRepository.deleteByScreenId(screenId);
        // Delete associated seat templates for this screen
        if (screen.getSeats() != null && !screen.getSeats().isEmpty()) {
            seatTemplateRepository.deleteAll(screen.getSeats());
        }
        screenRepository.deleteById(screenId);
    }

    @Transactional
    public void deleteShow(String showId) {
        if (!showRepository.existsById(showId)) {
            throw new RuntimeException("Show not found with ID: " + showId);
        }
        showSeatRepository.deleteByShowId(showId);
        showRepository.deleteById(showId);
    }



    public void deleteTheater(String theaterId) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("Theater not found with ID: " + theaterId));

        List<Screen> screens = theater.getScreens();
        for (Screen screen : screens) {
            List<Show> shows = showRepository.findByScreenId(screen.getId());
            for (Show show : shows) {
                showSeatRepository.deleteByShowId(show.getId());
            }
            showRepository.deleteByScreenId(screen.getId());

            if (screen.getSeats() != null && !screen.getSeats().isEmpty()) {
                seatTemplateRepository.deleteAll(screen.getSeats());
            }
            screenRepository.delete(screen);
        }
//        screenRepository.deleteByTheaterId(theaterId);
        theaterRepository.deleteById(theaterId);
    }
}
