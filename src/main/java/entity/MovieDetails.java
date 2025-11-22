package entity;

import java.util.List;

public record MovieDetails(
        int filmId,
        String filmName,
        String director,
        String releaseDate,
        double ratingOutOf5,
        List<String> genres,
        String description,
        List<MovieReview> reviews,
        String posterUrl
) {

}
