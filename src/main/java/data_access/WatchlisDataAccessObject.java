package data_access;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.Watchlist;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;

public class WatchlisDataAccessObject {
    private final Gson gson = new Gson();
    private final String folder = "data/watchlist/";

    public Watchlist loadWatchlist(String username) {
        try {
            FileReader reader = new FileReader(folder + username + ".json");
            Type type = new TypeToken<Watchlist>() {
            }.getType();
            return gson.fromJson(reader, type);
        }catch (Exception e) {
            return new  Watchlist();
        }

    }

    public void saveWatchlist(String username, Watchlist watchlist) {
        try(FileWriter writer = new FileWriter(folder + username + ".json")) {
            gson.toJson(watchlist, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
