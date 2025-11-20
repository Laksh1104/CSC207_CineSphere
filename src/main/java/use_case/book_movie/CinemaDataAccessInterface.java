package use_case.book_movie;

import entity.Cinema;

import java.util.List;

public interface CinemaDataAccessInterface {
    List<Cinema> getCinemasForFilm(int filmId, String date);
}
