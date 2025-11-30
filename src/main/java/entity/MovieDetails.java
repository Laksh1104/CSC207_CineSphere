package entity;

import java.util.List;

/**
 * Record representing movie details with all necessary information.
 *
 * @param filmId the film ID
 * @param filmName the film name
 * @param director the director name
 * @param releaseDate the release date
 * @param ratingOutOf5 the rating out of 5
 * @param genres the list of genres
 * @param description the description
 * @param reviews the movie reviews
 * @param posterUrl the URL of the movie poster
 */
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
