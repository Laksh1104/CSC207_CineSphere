package data_access;

import entity.Movie;
import entity.MovieFactory;
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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TmdbMovieDataAccessObject implements FilterMoviesDataAccessInterface {

    private final String API_KEY;
    private static final String BASE_URL = "https://api.themoviedb.org/3";

    private final HttpClient client = HttpClient.newHttpClient();
    private final MovieFactory movieFactory = new MovieFactory();

    private int lastTotalPages = 1;

    public TmdbMovieDataAccessObject(String apiKey) {
        this.API_KEY = apiKey;
    }

    public int getLastTotalPages() {
        return Math.max(1, lastTotalPages);
    }

    public Map<String, Integer> getMovieGenres() {
        try {
            String url = BASE_URL + "/genre/movie/list"
                    + "?api_key=" + API_KEY
                    + "&language=en";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("TMDB GENRE ERROR CODE: " + response.statusCode());
                return Map.of();
            }

            JSONObject json = new JSONObject(response.body());
            JSONArray genres = json.optJSONArray("genres");
            if (genres == null) return Map.of();

            List<Map.Entry<String, Integer>> entries = new ArrayList<>();
            for (int i = 0; i < genres.length(); i++) {
                JSONObject g = genres.getJSONObject(i);
                String name = g.optString("name", "").trim();
                int id = g.optInt("id", -1);
                if (!name.isEmpty() && id != -1) entries.add(Map.entry(name, id));
            }

            entries.sort(Comparator.comparing(e -> e.getKey().toLowerCase()));
            Map<String, Integer> genreMap = new LinkedHashMap<>();
            for (var e : entries) genreMap.put(e.getKey(), e.getValue());
            return genreMap;

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of();
        }
    }

    private static class RatingRange {
        final boolean any;
        final Double min10; // 0..10
        final Double max10; // 0..10

        RatingRange(boolean any, Double min10, Double max10) {
            this.any = any;
            this.min10 = min10;
            this.max10 = max10;
        }
    }

    /** Rating strings are TMDB scale (0..10)*/
    private RatingRange parseRating(String rating) {
        if (rating == null) return new RatingRange(true, null, null);

        String r = rating.trim();
        if (r.isEmpty() || r.equalsIgnoreCase("All Ratings")) {
            return new RatingRange(true, null, null);
        }

        try {
            if (r.endsWith("+")) {
                double min = Double.parseDouble(r.substring(0, r.length() - 1).trim());
                return new RatingRange(false, clamp(min, 0, 10), null);
            }

            if (r.contains("-")) {
                String[] parts = r.split("-");
                if (parts.length == 2) {
                    double a = Double.parseDouble(parts[0].trim());
                    double b = Double.parseDouble(parts[1].trim());

                    double min10 = clamp(a, 0, 10);
                    double max10 = clamp(b, 0, 10);

                    if (min10 > max10) {
                        double t = min10;
                        min10 = max10;
                        max10 = t;
                    }
                    return new RatingRange(false, min10, max10);
                }
            }

            double min = Double.parseDouble(r);
            return new RatingRange(false, clamp(min, 0, 10), null);

        } catch (Exception ignored) {
            // don't accidentally wipe results on parse error
            return new RatingRange(true, null, null);
        }
    }

    private double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    @Override
    public List<Movie> getFilteredMovies(String year, String rating, String genreId, String search, int page) {
        try {
            RatingRange range = parseRating(rating);
            boolean usingSearchEndpoint = (search != null && !search.trim().isEmpty());

            StringBuilder url;
            if (!usingSearchEndpoint) {
                url = new StringBuilder(BASE_URL + "/discover/movie");
                url.append("?api_key=").append(API_KEY);
                url.append("&language=en-US");
                url.append("&include_adult=false");
                url.append("&page=").append(page);

                // YEAR
                if (year != null && !year.equals("All Years")) {
                    url.append("&primary_release_year=").append(year);
                }

                // GENRE
                if (genreId != null && !genreId.equals("All Genres")) {
                    url.append("&with_genres=").append(genreId);
                }

                if (!range.any) {
                    if (range.min10 != null) url.append("&vote_average.gte=").append(range.min10);
                    if (range.max10 != null) url.append("&vote_average.lte=").append(range.max10);
                }

            } else {
                url = new StringBuilder(BASE_URL + "/search/movie");
                url.append("?api_key=").append(API_KEY);
                url.append("&query=").append(URLEncoder.encode(search.trim(), StandardCharsets.UTF_8));
                url.append("&page=").append(page);
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url.toString()))
                    .header("accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("TMDB ERROR CODE: " + response.statusCode());
                lastTotalPages = 1;
                return List.of();
            }

            JSONObject json = new JSONObject(response.body());
            lastTotalPages = Math.max(1, json.optInt("total_pages", 1));

            JSONArray results = json.optJSONArray("results");
            List<Movie> movies = new ArrayList<>();

            if (results != null) {
                for (int i = 0; i < results.length(); i++) {
                    JSONObject obj = results.getJSONObject(i);

                    if (!range.any) {
                        double voteAvg = obj.optDouble("vote_average", -1.0);
                        if (voteAvg < 0) continue;

                        if (range.min10 != null && voteAvg < range.min10) continue;
                        if (range.max10 != null && voteAvg > range.max10) continue;
                    }

                    int id = obj.optInt("id");
                    String title = obj.optString("title");
                    String posterPath = obj.optString("poster_path", null);

                    Movie movie = movieFactory.fromTMDB(id, title, posterPath);
                    movies.add(movie);
                }
            }

            return movies;

        } catch (Exception e) {
            e.printStackTrace();
            lastTotalPages = 1;
            return List.of();
        }
    }

    @Override
    public List<String> getPosterUrls(List<Movie> movies) {
        List<String> urls = new ArrayList<>();
        for (Movie m : movies) {
            if (m.getPosterPath() != null && !m.getPosterPath().isEmpty()) {
                urls.add(m.getPosterPath());
            }
        }
        return urls;
    }
}
