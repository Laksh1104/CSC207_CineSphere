package interface_adapter;

import javax.swing.*;
import java.awt.*;

import java.util.ArrayList;
import java.util.List;

public class WatchlistController {
    private int currentpage = 0;
    private int moviesperpage = 12;
    private List<String> watchlistAPI;


    public List<String>  getWatchlistAPI() {
        return watchlistAPI;
    }
    
    public void add2watchlistactionlistener(String state) {
        List<String> watchlistAPI = getWatchlistAPI();
        watchlistAPI.add(state);
    }

    public ArrayList<String> loadpage() {
        String [] allPosters = watchlistAPI.toArray(new String[watchlistAPI.size()]);
        ArrayList <String> currentposters = new ArrayList<>();
        if ((watchlistAPI.size() - currentpage) < moviesperpage) {
            moviesperpage = (watchlistAPI.size() - currentpage);
        }
        for(int i = currentpage; i < (currentpage + moviesperpage); i++) {
            currentposters.add(allPosters[i]);
        }
        return currentposters;
    }

    public void forward () {
        if ((currentpage + 1) * moviesperpage < (watchlistAPI.size())) {
            currentpage++;
            loadpage();
        }
    }

    public void back () {
        if (currentpage > 0) {
            currentpage--;
            loadpage();
        }
    }

    public WatchlistController() {
        this.watchlistAPI = new ArrayList<>();
    }


}