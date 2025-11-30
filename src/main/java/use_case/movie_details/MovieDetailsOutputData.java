package use_case.movie_details;

import java.util.List;

public record MovieDetailsOutputData(
    int filmId,
    String filmName,
    String director,
    String releaseDate,
    double ratingOutOf5,
    List<String> genres,
    String description,
    List<MovieReviewData> reviews,
    String posterUrl
) {
    /**
     * Inner record for movie review data.
     *
     * @param author the author of the review
     * @param content the content of the review
     */
    public record MovieReviewData(String author, String content) {
    }
}
