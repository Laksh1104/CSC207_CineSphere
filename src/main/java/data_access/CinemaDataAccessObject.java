package data_access;

import entity.Cinema;
import entity.CinemaFactory;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.book_movie.CinemaDataAccessInterface;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CinemaDataAccessObject implements CinemaDataAccessInterface {

    private static final String CINEMAS_FOR_FILM_URL =
            "https://api-gate2.movieglu.com/filmShowTimes/?film_id=";

    private static final String NUMBER_OF_CINEMAS = "25";
    private static final String CONTENT_TYPE_JSON = "application/json";

    // Load environment variables
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./")
            .ignoreIfMissing()
            .load();

    // MovieGlu API headers from .env
    private static final String API_VERSION = dotenv.get("MOVIEGLU_API_VERSION");
    private static final String AUTHORIZATION = dotenv.get("MOVIEGLU_AUTHORIZATION");
    private static final String CLIENT = dotenv.get("MOVIEGLU_CLIENT");
    private static final String X_API_KEY = dotenv.get("MOVIEGLU_X_API_KEY");
    private static final String TERRITORY = dotenv.get("MOVIEGLU_TERRITORY");

    private static final String DEVICE_DATETIME = ZonedDateTime.now(java.time.ZoneOffset.UTC)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

    private static final String TEST_GEOLOCATION = " -22.0;14.0";
    private static final String GEOAPIFY_IP_URL =
            "https://api.geoapify.com/v1/ipinfo?&apiKey=9451237170dd4a23b9b8ec87d5532b9c";

    private final OkHttpClient client;
    private final CinemaFactory cinemaFactory;

    public CinemaDataAccessObject(CinemaFactory cinemaFactory) {
        this.client = new OkHttpClient.Builder().build();
        this.cinemaFactory = cinemaFactory;
    }

    @Override
    public List<Cinema> getCinemasForFilm(int filmId, String date) {

        Request request = new Request.Builder()
                .url(CINEMAS_FOR_FILM_URL + filmId + "&date=" + date + "&n=" + NUMBER_OF_CINEMAS)
                .headers(buildHeaders())
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) return Collections.emptyList();

            String bodyString = response.body().string();
            if (bodyString.isBlank()) return Collections.emptyList();

            JSONArray cinemaArray = new JSONObject(bodyString).getJSONArray("cinemas");
            return parseCinemas(cinemaArray);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch cinemas: " + e.getMessage(), e);
        }
    }

    private Headers buildHeaders() {
        return new Headers.Builder()
                .add("api-version", API_VERSION)
                .add("Authorization", AUTHORIZATION)
                .add("client", CLIENT)
                .add("x-api-key", X_API_KEY)
                .add("device-datetime", DEVICE_DATETIME)
                .add("territory", TERRITORY)
                .add("geolocation", get_geolocation())
                .add(CONTENT_TYPE_JSON, CONTENT_TYPE_JSON)
                .build();
    }

    private List<Cinema> parseCinemas(JSONArray cinemaArray) {
        List<Cinema> cinemas = new ArrayList<>();

        for (int i = 0; i < cinemaArray.length(); i++) {
            JSONObject cinemaJson = cinemaArray.getJSONObject(i);

            Cinema cinema = cinemaFactory.create(
                    cinemaJson.getInt("cinema_id"),
                    cinemaJson.getString("cinema_name")
            );

            if (cinemaJson.has("showings")) {
                parseShowings(cinema, cinemaJson.getJSONObject("showings"));
            }

            cinemas.add(cinema);
        }
        return cinemas;
    }

    private void parseShowings(Cinema cinema, JSONObject showings) {
        for (String versionType : showings.keySet()) {
            JSONArray times = showings.getJSONObject(versionType).getJSONArray("times");

            for (int j = 0; j < times.length(); j++) {
                JSONObject t = times.getJSONObject(j);
                cinema.addShowTime(
                        versionType,
                        t.getString("start_time"),
                        t.getString("end_time")
                );
            }
        }
    }

    public String get_geolocation() {
        Request request = new Request.Builder()
                .url(GEOAPIFY_IP_URL)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) return "";

            JSONObject json = new JSONObject(response.body().string());
            JSONObject loc = json.getJSONObject("location");

            return loc.getDouble("latitude") + ";" + loc.getDouble("longitude");

        } catch (Exception e) {
            throw new RuntimeException("Failed to resolve geolocation: " + e.getMessage(), e);
        }
    }
}
