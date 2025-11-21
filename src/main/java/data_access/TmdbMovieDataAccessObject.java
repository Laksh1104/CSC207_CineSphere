package data_access;

import entity.Movie;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.movie_filter.FilterMoviesDataAccessInterface;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TmdbMovieDataAccessObject implements FilterMoviesDataAccessInterface {

    private final String API_KEY;
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    private final HttpClient client = HttpClient.newHttpClient();

    public TmdbMovieDataAccessObject(String apiKey) {
        this.API_KEY = apiKey;
    }

    @Override
    public List<Movie> getFilteredMovies(
            String year,
            String rating,
            String genreId,
            String search,
            int page
    ) {
        try {

            // default: use discover endpoint
            StringBuilder url = new StringBuilder(BASE_URL + "/discover/movie");
            url.append("?api_key=").append(API_KEY);
            url.append("&language=en-US");
            url.append("&include_adult=false");
            url.append("&page=").append(page);

            // YEAR
            if (year != null && !year.equals("All Years")) {
                url.append("&primary_release_year=").append(year);
            }

            // RATING
            if (rating != null && !rating.equals("All Ratings")) {
                String minRating = rating.replace("+", "");
                url.append("&vote_average.gte=").append(minRating);
            }

            // GENRE ID
            if (genreId != null && !genreId.equals("All Genres")) {
                url.append("&with_genres=").append(genreId);
            }

            // SEARCH OVERRIDES EVERYTHING
            if (search != null && !search.isEmpty()) {
                url = new StringBuilder(BASE_URL + "/search/movie");
                url.append("?api_key=").append(API_KEY);
                url.append("&query=").append(URLEncoder.encode(search, StandardCharsets.UTF_8));
                url.append("&page=").append(page);
            }

            // ---- SEND REQUEST ----
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url.toString()))
                    .header("accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("TMDB ERROR CODE: " + response.statusCode());
                return List.of();
            }

            JSONObject json = new JSONObject(response.body());
            JSONArray results = json.optJSONArray("results");

            List<Movie> movies = new ArrayList<>();

            if (results != null) {
                for (int i = 0; i < results.length(); i++) {
                    JSONObject obj = results.getJSONObject(i);

                    int id = obj.optInt("id");
                    String title = obj.optString("title");
                    String posterPath = obj.optString("poster_path", null);

                    movies.add(new Movie(id, title, posterPath));
                }
            }

            return movies;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public List<String> getPosterUrls(List<Movie> movies) {
        List<String> urls = new ArrayList<>();

        for (Movie m : movies) {
            if (m.getPosterPath() != null && !m.getPosterPath().isEmpty()) {
                urls.add(IMAGE_BASE_URL + m.getPosterPath());
            }
        }

        return urls;
    }
}
