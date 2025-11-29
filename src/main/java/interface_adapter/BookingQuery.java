package interface_adapter;

import data_access.*;
import entity.*;

import java.util.List;
import java.util.Map;

public class BookingQuery {

    private final BookingMovieDataAccessObject movieDAO;
    private final CinemaDataAccessObject cinemaDAO;

    public BookingQuery(BookingMovieDataAccessObject movieDAO, CinemaDataAccessObject cinemaDAO
    ) {
        this.movieDAO = movieDAO;
        this.cinemaDAO = cinemaDAO;
    }

    public List<Movie> getMovies() {
        return movieDAO.getNowShowingMovies();
    }

    public List<Cinema> getCinemas(int movieId, String date) {
        return cinemaDAO.getCinemasForFilm(movieId, date);
    }

    public Map<String, List<ShowTime>> getShowtimes(Cinema cinema) {
        return cinema.getAllShowTimesWithVersion();
    }
}
