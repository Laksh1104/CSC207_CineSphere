package data_access;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import entity.Movie;
import entity.MovieFactory;
import use_case.book_movie.MovieDataAccessInterface;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class    BookingMovieDataAccessObject implements MovieDataAccessInterface {
    private static final String BASE_URL = "https://api-gate2.movieglu.com/filmsNowShowing/?n=";
    public static final String NUMBER_OF_FILMS = "20";
    private static final String CONTENT_TYPE_JSON = "application/json";

    // MovieGlu API headers
    private static final String API_VERSION = "v201";
    private static final String AUTHORIZATION = "Basic VU5JVl84M19YWDpIbGtDWHRCeDAwZjk=";
    private static final String CLIENT = "UNIV_83";
    private static final String X_API_KEY = "3k2LXALJ12aNVHv5o0QOL4v63Q25zG7rasLjUTKb";
    private static final String TERRITORY = "XX";

    private final OkHttpClient client;
    private final MovieFactory movieFactory;

    public BookingMovieDataAccessObject(MovieFactory movieFactory) {
        this.client = new OkHttpClient().newBuilder().build();
        this.movieFactory = movieFactory;
    }

    @Override
    public List<Movie> getNowShowingMovies() {
        String deviceDatetime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));

        Request request = new Request.Builder()
                .url(BASE_URL + NUMBER_OF_FILMS)
                .get()
                .addHeader("api-version", API_VERSION)
                .addHeader("Authorization", AUTHORIZATION)
                .addHeader("client", CLIENT)
                .addHeader("x-api-key", X_API_KEY)
                .addHeader("device-datetime", deviceDatetime)
                .addHeader("territory", TERRITORY)
                .addHeader("Content-Type", CONTENT_TYPE_JSON)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                throw new RuntimeException("Empty response body from Movie API");
            }

            JSONObject jsonResponse = new JSONObject(response.body().string());
            JSONArray films = jsonResponse.getJSONArray("films");

            List<Movie> movies = new ArrayList<>();
            for (int i = 0; i < films.length(); i++) {
                JSONObject film = films.getJSONObject(i);
                int filmId = film.getInt("film_id");
                String filmName = film.getString("film_name");

                Movie movie = movieFactory.create(filmId, filmName);
                movies.add(movie);

            }

            return movies;
        } catch (IOException | JSONException e) {
            throw new RuntimeException("Failed to fetch movies: " + e.getMessage(), e);
        }
    }
}
