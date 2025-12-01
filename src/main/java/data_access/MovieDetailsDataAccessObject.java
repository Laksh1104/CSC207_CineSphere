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

    private static final double MAX_RATING = 10.0;
    private static final double MIN_RATING = 0.0;

    public MovieDetailsDataAccessObject() {
        validateApiKey();
    }

    @Override
    public MovieDetails getMovieDetails(final int filmId) {
        Request request = buildMovieRequest(filmId);

        try (Response response = HTTP.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("TMDB error code: " + response.code());
            }

            return parseMovieDetails(response);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to fetch movie details: " + e.getMessage(), e);
        }
    }

    private void validateApiKey() {
        if (API_KEY == null || API_KEY.isBlank()) {
            throw new IllegalStateException("TMDB_API_KEY missing. See README.");
        }
    }

    private Request buildMovieRequest(final int filmId) {
        HttpUrl url = HttpUrl.parse(BASE + "/movie/" + filmId)
                .newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .addQueryParameter("language", "en-US")
                .addQueryParameter("append_to_response", "credits,reviews")
                .build();

        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }

    // -----------------------------
    // Parsing
    // -----------------------------

    private MovieDetails parseMovieDetails(final Response response) throws IOException {
        assert response.body() != null;
        JSONObject json = new JSONObject(response.body().string());

        return new MovieDetails(
                json.getInt("id"),
                json.optString("title", ""),
                parseDirector(json),
                json.optString("release_date", ""),
                parseRating(json),
                parseGenres(json),
                json.optString("overview", ""),
                parseReviews(json),
                parsePosterPath(json)
        );
    }

    private @Nullable String parseDirector(JSONObject json) {
        JSONArray crew = json.optJSONObject("credits")
                .optJSONArray("crew");

        if (crew == null) return null;

        for (int i = 0; i < crew.length(); i++) {
            JSONObject member = crew.getJSONObject(i);
            if ("Director".equalsIgnoreCase(member.optString("job"))) {
                return member.optString("name", "");
            }
        }
        return null;
    }

    private double parseRating(JSONObject json) {
        double value = json.optDouble("vote_average", 0.0);
        value = Math.min(MAX_RATING, Math.max(MIN_RATING, value));
        return Math.round(value * 10.0) / 10.0;
    }

    private List<String> parseGenres(JSONObject json) {
        JSONArray arr = json.optJSONArray("genres");
        List<String> list = new ArrayList<>();

        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                list.add(arr.getJSONObject(i).optString("name", ""));
            }
        }
        return list;
    }

    private List<MovieReview> parseReviews(JSONObject json) {
        JSONArray arr = json
                .optJSONObject("reviews")
                .optJSONArray("results");

        List<MovieReview> reviews = new ArrayList<>();
        if (arr == null) return reviews;

        for (int i = 0; i < arr.length(); i++) {
            JSONObject r = arr.getJSONObject(i);
            reviews.add(new MovieReview(
                    r.optString("author", "Anonymous"),
                    r.optString("content", "No content")
            ));
        }
        return reviews;
    }

    private @Nullable String parsePosterPath(JSONObject json) {
        return json.has("poster_path")
                ? IMG_BASE + json.optString("poster_path")
                : null;
    }
}
