package interface_adapter.watchlist;

import java.util.ArrayList;
import java.util.List;

/**
 * UI state for the watchlist.
 */
public class WatchlistState {

    private List<String> posterUrls = new ArrayList<>();
    private String errorMessage;

    public List<String> getPosterUrls() {
        return posterUrls;
    }

    public void setPosterUrls(List<String> posterUrls) {
        this.posterUrls = (posterUrls == null)
                ? new ArrayList<>()
                : new ArrayList<>(posterUrls);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
