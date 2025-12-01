package data_access;

import entity.MovieTicket;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.bookings.BookingsDataAccessInterface;
import use_case.watchlist.WatchlistDataAccessInterface;

import java.io.*;
import java.util.*;

/**
 * Stores per-user watchlists and bookings in a JSON file.
 * Structure:
 * {
 *   "username": {
 *     "watchlist": [ "posterUrl1", "posterUrl2", ... ],
 *     "bookings": [
 *       {
 *         "movieName": "...",
 *         "cinemaName": "...",
 *         "date": "...",
 *         "startTime": "...",
 *         "endTime": "...",
 *         "seats": ["A1", "A2"],
 *         "cost": 40
 *       },
 *       ...
 *     ]
 *   },
 *   ...
 * }
 */
public class UserProfileJsonDataAccessObject
        implements WatchlistDataAccessInterface, BookingsDataAccessInterface {

    private final File file;

    public UserProfileJsonDataAccessObject(String filePath) {
        this.file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("{}");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize user profile file: " + filePath, e);
        }
    }

    // ===== WatchlistDataAccessInterface =====

    @Override
    public synchronized boolean isInWatchlist(String username, String posterUrl) {
        if (username == null || username.isBlank() ||
                posterUrl == null || posterUrl.isBlank()) {
            return false;
        }

        JSONObject root = readRoot();
        JSONObject userObj = getOrCreateUserObject(root, username);
        JSONArray watchlist = userObj.optJSONArray("watchlist");
        if (watchlist == null) {
            return false;
        }

        for (int i = 0; i < watchlist.length(); i++) {
            if (posterUrl.equals(watchlist.optString(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized void addToWatchlist(String username, String posterUrl) {
        if (username == null || username.isBlank() ||
                posterUrl == null || posterUrl.isBlank()) {
            return;
        }

        JSONObject root = readRoot();
        JSONObject userObj = getOrCreateUserObject(root, username);
        JSONArray watchlist = userObj.optJSONArray("watchlist");
        if (watchlist == null) {
            watchlist = new JSONArray();
            userObj.put("watchlist", watchlist);
        }

        // Avoid duplicates
        Set<String> existing = new HashSet<>();
        for (int i = 0; i < watchlist.length(); i++) {
            existing.add(watchlist.optString(i));
        }
        if (!existing.contains(posterUrl)) {
            watchlist.put(posterUrl);
        }

        root.put(username, userObj);
        writeRoot(root);
    }

    @Override
    public synchronized void removeFromWatchlist(String username, String posterUrl) {
        if (username == null || username.isBlank() ||
                posterUrl == null || posterUrl.isBlank()) {
            return;
        }

        JSONObject root = readRoot();
        JSONObject userObj = getOrCreateUserObject(root, username);
        JSONArray watchlist = userObj.optJSONArray("watchlist");
        if (watchlist == null) {
            return;
        }

        JSONArray updated = new JSONArray();
        for (int i = 0; i < watchlist.length(); i++) {
            String url = watchlist.optString(i);
            if (!posterUrl.equals(url)) {
                updated.put(url);
            }
        }
        userObj.put("watchlist", updated);
        root.put(username, userObj);
        writeRoot(root);
    }

    @Override
    public synchronized List<String> getWatchlist(String username) {
        List<String> result = new ArrayList<>();
        if (username == null || username.isBlank()) {
            return result;
        }

        JSONObject root = readRoot();
        JSONObject userObj = getOrCreateUserObject(root, username);
        JSONArray watchlist = userObj.optJSONArray("watchlist");
        if (watchlist == null) {
            return result;
        }

        for (int i = 0; i < watchlist.length(); i++) {
            result.add(watchlist.optString(i));
        }
        return result;
    }

    // ===== Booking persistence API (used by PersistentTicketDataAccessObject) =====

    public synchronized void addBooking(String username, MovieTicket ticket) {
        if (username == null || username.isBlank() || ticket == null) {
            return;
        }

        JSONObject root = readRoot();
        JSONObject userObj = getOrCreateUserObject(root, username);

        JSONArray bookings = userObj.optJSONArray("bookings");
        if (bookings == null) {
            bookings = new JSONArray();
            userObj.put("bookings", bookings);
        }

        JSONObject bookingJson = getJsonObject(ticket);

        JSONArray seatsArray = new JSONArray();
        for (String seat : ticket.getSeats()) {
            seatsArray.put(seat);
        }
        bookingJson.put("seats", seatsArray);

        bookings.put(bookingJson);
        userObj.put("bookings", bookings);
        root.put(username, userObj);
        writeRoot(root);
    }

    @NotNull
    private static JSONObject getJsonObject(MovieTicket ticket) {
        JSONObject bookingJson = new JSONObject();
        bookingJson.put("movieName", ticket.getMovieName());
        bookingJson.put("cinemaName", ticket.getCinemaName());
        bookingJson.put("date", ticket.getDate());
        bookingJson.put("startTime", ticket.getStartTime());
        bookingJson.put("endTime", ticket.getEndTime());
        bookingJson.put("cost", ticket.getCost());
        return bookingJson;
    }

    // ===== BookingsDataAccessInterface implementation =====

    @Override
    public synchronized List<MovieTicket> getBookings(String username) {
        List<MovieTicket> result = new ArrayList<>();
        if (username == null || username.isBlank()) {
            return result;
        }

        JSONObject root = readRoot();
        JSONObject userObj = getOrCreateUserObject(root, username);
        JSONArray bookings = userObj.optJSONArray("bookings");
        if (bookings == null) {
            return result;
        }

        for (int i = 0; i < bookings.length(); i++) {
            JSONObject b = bookings.optJSONObject(i);
            if (b == null) continue;

            String movieName = b.optString("movieName", "");
            String cinemaName = b.optString("cinemaName", "");
            String date = b.optString("date", "");
            String startTime = b.optString("startTime", "");
            String endTime = b.optString("endTime", "");
            int cost = b.optInt("cost", 0);

            Set<String> seats = new HashSet<>();
            JSONArray seatsArray = b.optJSONArray("seats");
            if (seatsArray != null) {
                for (int j = 0; j < seatsArray.length(); j++) {
                    String s = seatsArray.optString(j, null);
                    if (s != null && !s.isBlank()) {
                        seats.add(s);
                    }
                }
            }

            MovieTicket ticket = new MovieTicket(
                    movieName,
                    cinemaName,
                    date,
                    startTime,
                    endTime,
                    seats,
                    cost
            );
            result.add(ticket);
        }

        return result;
    }

    // ===== Internal helpers =====

    private synchronized JSONObject readRoot() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String row;
            while ((row = reader.readLine()) != null) {
                sb.append(row);
            }
            String json = sb.toString().trim();
            if (json.isEmpty()) {
                return new JSONObject();
            }
            return new JSONObject(json);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read user profile file: " + file.getPath(), e);
        }
    }

    private synchronized void writeRoot(JSONObject root) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(root.toString(2));
        } catch (IOException e) {
            throw new RuntimeException("Unable to write user profile file: " + file.getPath(), e);
        }
    }

    private JSONObject getOrCreateUserObject(JSONObject root, String username) {
        if (root.has(username)) {
            return root.getJSONObject(username);
        }

        JSONObject userObj = new JSONObject();
        userObj.put("watchlist", new JSONArray());
        userObj.put("bookings", new JSONArray());
        root.put(username, userObj);
        return userObj;
    }
}
