package data_access;

import entity.Movie;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class PopularMoviesDataAccessObject {

    private static final String TMDB_URL = "https://api.themoviedb.org/3/movie/popular";

    private static final String TMDB_POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500";

    private HttpClient client;
    private String bearerToken;

    public PopularMoviesDataAccessObject(String bearerToken) {
        this.bearerToken = bearerToken;
        this.client = HttpClient.newHttpClient();
    }


    // public
    public List<Movie> getPopularMovies() {
        String json = accessPopularMovieJson();

        if (json == null) {
            return List.of();
        }
        return extractPopularMovies(json);
    }

    public List<String> getPosterUrls(List<Movie> movies) {
        List<String> posterUrls = new ArrayList<>();

        for (Movie movie : movies) {
            String posterPath = movie.getPosterPath();
            if (posterPath != null) {
                String url = buildPosterUrl(posterPath);
                posterUrls.add(url);
            }
        }
        return posterUrls;
    }


    // private
    private String accessPopularMovieJson() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TMDB_URL))
                .header("accept", "application/json")
                .header("Authorization", bearerToken)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Movie> extractPopularMovies(String json) {
        List<Movie> movies = new ArrayList<>();

        try {
            JSONObject obj = new JSONObject(json);
            JSONArray results = obj.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);

                int id = item.getInt("id");
                String title = item.optString("title");
                String poster = item.optString("poster_path");

                Movie m = new Movie(id, title, poster);
                movies.add(m);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movies;
    }


    private String buildPosterUrl(String posterPath) {
        if (posterPath == null || posterPath.isEmpty()) {
            return null;
        }
        return TMDB_POSTER_BASE_URL + posterPath;
    }
}
