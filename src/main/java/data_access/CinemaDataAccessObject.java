package data_access;

import entity.Cinema;
import entity.CinemaFactory;
import entity.Movie;
import entity.MovieFactory;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import use_case.book_movie.CinemaDataAccessInterface;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CinemaDataAccessObject implements CinemaDataAccessInterface {
    private static final String CINEMAS_NEARBY_URL = "https://api-gate2.movieglu.com/cinemasNearby/?n=";
    public static final String CINEMAS_FOR_FILM_URL = "https://api-gate2.movieglu.com/filmShowTimes/?film_id=";
    public static final String NUMBER_OF_CINEMAS = "25";
    private static final String CONTENT_TYPE_JSON = "application/json";

    // MovieGlu API headers (align with your Movie DAO)
    private static final String API_VERSION = "v201";
    private static final String AUTHORIZATION = "Basic VU5JVl84M19YWDpIbGtDWHRCeDAwZjk=";
    private static final String CLIENT = "UNIV_83";
    private static final String X_API_KEY = "3k2LXALJ12aNVHv5o0QOL4v63Q25zG7rasLjUTKb";
    private static final String TERRITORY = "XX";
    private static final String DEVICE_DATETIME = ZonedDateTime.now(java.time.ZoneOffset.UTC)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    public static final String TEST_GEOLOCATION = " -22.0;14.0";
    // Geoapify IP info
    private static final String GEOAPIFY_IP_URL = "https://api.geoapify.com/v1/ipinfo?&apiKey=9451237170dd4a23b9b8ec87d5532b9c";

    private final OkHttpClient client;
    private final CinemaFactory cinemaFactory;


    public CinemaDataAccessObject(CinemaFactory cinemaFactory) {
        this.client = new OkHttpClient.Builder().build();
        this.cinemaFactory = cinemaFactory;
    }


    @Override
    public List<Cinema> getCinemasForFilm(int filmId, String date) {

        String geolocation = get_geolocation();

        Request request = new Request.Builder()
                .url(CINEMAS_FOR_FILM_URL + filmId + "&date=" + date + "&n=" + NUMBER_OF_CINEMAS)
                .get()
                .addHeader("api-version", API_VERSION)
                .addHeader("Authorization", AUTHORIZATION)
                .addHeader("client", CLIENT)
                .addHeader("x-api-key", X_API_KEY)
                .addHeader("device-datetime", DEVICE_DATETIME)
                .addHeader("territory", TERRITORY)
                .addHeader("geolocation", TEST_GEOLOCATION)
                .addHeader(CONTENT_TYPE_JSON, CONTENT_TYPE_JSON)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                throw new RuntimeException("Empty response body from CINEMA FILM API");
            }
            String bodyString = response.body() != null ? response.body().string() : "";
            if (bodyString.isBlank()) return Collections.emptyList();

            JSONObject jsonResponse = new JSONObject(bodyString);
            JSONArray cinema_array = jsonResponse.getJSONArray("cinemas");

            List<Cinema> cinemas = new ArrayList<>();
            for (int i = 0; i < cinema_array.length(); i++) {
                JSONObject cinema = cinema_array.getJSONObject(i);
                int cinemaId = cinema.getInt("cinema_id");
                String cinemaName = cinema.getString("cinema_name");
                Cinema cinemaObject = cinemaFactory.create(cinemaId, cinemaName);

                JSONObject showings = cinema.getJSONObject("showings");

                if (showings != null) {
                    for (String versionType : showings.keySet()) {
                        JSONObject version = showings.getJSONObject(versionType);
                        JSONArray times = version.getJSONArray("times");
                        if (times != null) {
                            for (int j = 0; j < times.length(); j++) {
                                JSONObject timeObj = times.getJSONObject(j);
                                String startTime = timeObj.getString("start_time");
                                String endTime = timeObj.getString("end_time");

                                // Add showtime to the Cinema object
                                cinemaObject.addShowTime(versionType, startTime, endTime);
                            }
                        }
                    }
                }

                cinemas.add(cinemaObject);
            }
            return cinemas;

        } catch (IOException | JSONException e) {
            throw new RuntimeException("Failed to fetch cinemas: " + e.getMessage(), e);
        }
    }

    public String get_geolocation(){
        Request request = new Request.Builder()
                .url(GEOAPIFY_IP_URL)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                throw new RuntimeException("Empty response body from Geoapify");
            }
            JSONObject json = new JSONObject(response.body().string());
            JSONObject location = json.getJSONObject("location");

            String geolocation = location.getDouble("latitude") + ";" + location.getDouble("longitude");

            return geolocation;

        } catch (IOException | JSONException e) {
            throw new RuntimeException("Failed to resolve geolocation: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        CinemaFactory cinemaFactory = new CinemaFactory();
        MovieFactory movieFactory = new MovieFactory();
        CinemaDataAccessObject cinemaDataAccessObject = new CinemaDataAccessObject(cinemaFactory);
        BookingMovieDataAccessObject movieDataAccessObject = new BookingMovieDataAccessObject(movieFactory);
        List<Movie> movies = movieDataAccessObject.getNowShowingMovies();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
       for  (Movie movie : movies) {
           List<Cinema> cinemas = cinemaDataAccessObject.getCinemasForFilm(movie.getId(), date);
           for(Cinema cinema : cinemas) {
               System.out.println("Movie: " + movie.getTitle() + " at Cinema: " + cinema.getCinemaName());
           }
       }

    }
}
