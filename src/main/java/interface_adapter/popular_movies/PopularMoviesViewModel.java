package interface_adapter.popular_movies;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.ArrayList;

public class PopularMoviesViewModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private List<String> posterUrls = new ArrayList<>();
    private List<Integer> filmIds = new ArrayList<>();
    private String errorMessage;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public List<String> getPosterUrls() {

        return posterUrls;
    }

    public void setPosterUrls(List<String> posterUrls) {
        List<String> old = this.posterUrls;
        this.posterUrls = posterUrls;
        support.firePropertyChange("posterUrls", old, posterUrls);
    }

    public List<Integer> getFilmIds() {
        return filmIds;
    }

    public void setFilmIds(List<Integer> filmIds) {
        List<Integer> old = this.filmIds;
        this.filmIds = filmIds;
        support.firePropertyChange("filmIds", old, filmIds);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        String old = this.errorMessage;
        this.errorMessage = errorMessage;
        support.firePropertyChange("errorMessage", old, errorMessage);
    }
}
