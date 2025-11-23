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
    public record MovieReviewData(String author, String content) {}
}
