package interface_adapter.movie_details;

import java.util.List;

import use_case.movie_details.MovieDetailsOutputData.MovieReviewData;

public record MovieDetailsState(
    String filmName,
    String director,
    String releaseDate,
    double ratingOutOf5,
    List<String> genres,
    String description,
    List<MovieReviewData> reviews,
    String posterUrl
) {
}
