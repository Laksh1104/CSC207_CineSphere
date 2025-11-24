package interface_adapter.movie_details;

import use_case.movie_details.MovieDetailsOutputData.MovieReviewData;
import java.util.List;

public record MovieDetailsState(
    String filmName,
    String director,
    String releaseDate,
    double ratingOutOf5,
    List<String> genres,
    String description,
    List<MovieReviewData> reviews,
    String posterUrl
) {}
