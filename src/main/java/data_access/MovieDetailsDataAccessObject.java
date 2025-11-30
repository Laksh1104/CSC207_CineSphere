package data_access;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import entity.MovieDetails;
import entity.MovieReview;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import use_case.movie_details.MovieDetailsDataAccessInterface;

public class MovieDetailsDataAccessObject implements MovieDetailsDataAccessInterface {

    private static final Dotenv DOTENV = Dotenv.load();
    private static final String API_KEY = DOTENV.get("TMDB_API_KEY");
    private static final String BASE = "https://api.themoviedb.org/3";
    private static final String IMG_BASE = "https://image.tmdb.org/t/p/w500";
    private static final OkHttpClient HTTP = new OkHttpClient.Builder().build();

    /**
     * Initializes the DAO and validates the API key.
     */
    public MovieDetailsDataAccessObject() {
        validateApiKey();
    }

    /**
     * Retrieves movie details from TMDB API.
     *
     * @param filmId the film ID to retrieve
     * @return the movie details
     * @throws RuntimeException if the API call fails or response is unsuccessful
     */
    public MovieDetails getMovieDetails(final int filmId) {
        final Request request = buildGetMovieRequest(filmId);

        try (Response response = HTTP.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to get movie details. TMDB returned error code: "
                        + response.code());
            }

            return parseResponse(response);
        }
        catch (final IOException exception) {
            throw new RuntimeException("Failed to get movie details with error: " + exception.getMessage(),
                exception);
        }
    }

    private void validateApiKey() {
        if (API_KEY == null || API_KEY.isBlank()) {
            throw new IllegalStateException(
                    "TMDB_API_KEY is not set in environment variables. Please follow steps in README."
            );
        }
    }

    private Request buildGetMovieRequest(final int filmId) {
        final HttpUrl url = HttpUrl
                .parse(BASE + "/movie/" + filmId)
                .newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .addQueryParameter("language", "en-US")
                .addQueryParameter("append_to_response", "credits,reviews")
                .build();

        return new Request
                .Builder()
                .url(url)
                .get()
                .build();
    }

    private MovieDetails parseResponse(final Response response) throws IOException {
        final JSONObject json = new JSONObject(response.body().string());

        return new MovieDetails(
                json.getInt("id"),
                json.getString("title"),
                parseDirector(json),
                json.getString("release_date"),
                parseRatingOutOf10(json),
                parseGenres(json),
                json.getString("overview"),
                parseReviews(json),
                parsePosterPath(json)
        );
    }

    private String parseDirector(final JSONObject json) {
        final JSONObject credits = json.optJSONObject("credits");

        if (credits == null) {
            return null;
        }

        final JSONArray crew = credits.optJSONArray("crew");
        if (crew == null) {
            return null;
        }

        for (int i = 0; i < crew.length(); i++) {
            final JSONObject member = crew.getJSONObject(i);
            if ("Director".equalsIgnoreCase(member.optString("job"))) {
                return member.optString("name", "");
            }
        }

        return null;
    }

    private double parseRatingOutOf10(final JSONObject json) {
        double voteOutOf10 = json.optDouble("vote_average", 0.0);
        final double maxRating = 10.0;
        final double minRating = 0.0;
        final double scalingFactor = 10.0;

        // clamp + round to 1 decimal to keep UI friendly
        voteOutOf10 = Math.max(minRating, Math.min(maxRating, voteOutOf10));
        return Math.round(voteOutOf10 * scalingFactor) / scalingFactor;
    }

    private List<String> parseGenres(final JSONObject json) {
        final JSONArray jsonArray = json.optJSONArray("genres");
        final List<String> genres = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                genres.add(jsonArray.getJSONObject(i).optString("name", ""));
            }
        }

        return genres;
    }

    private List<MovieReview> parseReviews(final JSONObject json) {
        final JSONArray jsonArray;
        if (json.has("reviews")) {
            jsonArray = json.getJSONObject("reviews").getJSONArray("results");
        }
        else {
            jsonArray = new JSONArray();
        }

        final List<MovieReview> reviews = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject review = jsonArray.getJSONObject(i);

                reviews.add(new MovieReview(
                        review.optString("author", "Anonymous"),
                        review.optString("content", "No content")
                ));
            }
        }

        return reviews;
    }

    private @Nullable String parsePosterPath(final JSONObject json) {
        if (json.has("poster_path")) {
            return IMG_BASE + json.getString("poster_path");
        }
        return null;
    }
}
