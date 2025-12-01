package app;

import data_access.BookingMovieDataAccessObject;
import data_access.CinemaDataAccessObject;
import data_access.InMemoryTicketDataAccessObject;
import data_access.PopularMoviesDataAccessObject;
import data_access.SearchFilmDataAccessObject;
import data_access.TmdbMovieDataAccessObject;
import entity.CinemaFactory;
import entity.MovieFactory;
import interface_adapter.BookMovie.BookMovieController;
import interface_adapter.BookMovie.BookMoviePresenter;
import interface_adapter.BookMovie.BookMovieViewModel;
import interface_adapter.BookingQuery;
import interface_adapter.SearchFilm.SearchFilmController;
import interface_adapter.SearchFilm.SearchFilmPresenter;
import interface_adapter.SearchFilm.SearchFilmViewModel;
import interface_adapter.filter_movies.FilterMoviesController;
import interface_adapter.filter_movies.FilterMoviesPresenter;
import interface_adapter.filter_movies.FilterMoviesViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.popular_movies.PopularMoviesController;
import interface_adapter.popular_movies.PopularMoviesPresenter;
import interface_adapter.popular_movies.PopularMoviesViewModel;
import use_case.book_movie.BookMovieInputBoundary;
import use_case.book_movie.BookMovieInteractor;
import use_case.book_movie.BookMovieOutputBoundary;
import use_case.book_movie.BookTicketDataAccessInterface;
import use_case.movie_filter.FilterMoviesDataAccessInterface;
import use_case.movie_filter.FilterMoviesInputBoundary;
import use_case.movie_filter.FilterMoviesInteractor;
import use_case.movie_filter.FilterMoviesOutputBoundary;
import use_case.popular_movies.PopularMoviesDataAccessInterface;
import use_case.popular_movies.PopularMoviesInputBoundary;
import use_case.popular_movies.PopularMoviesInteractor;
import use_case.popular_movies.PopularMoviesOutputBoundary;
import use_case.search_film.SearchFilmDataAccessInterface;
import use_case.search_film.SearchFilmInputBoundary;
import use_case.search_film.SearchFilmInteractor;
import use_case.search_film.SearchFilmOutputBoundary;
import view.BookingView;
import view.FilteredView;
import view.LoggedInView;
import view.LoginView;
import view.ScreenSwitchListener;

import javax.swing.*;
import java.awt.*;

public class MainAppFrame extends JFrame implements ScreenSwitchListener {

    // Use the correct credential for each DAO:
    // - Filter (discover/search) uses v3 api_key query param
    // - Popular uses Bearer auth (your PopularMovies DAO expects it)
    private static final String TMDB_V3_API_KEY = "6289d1f5d1b8e2d2a78614fc9e48742b";
    private static final String TMDB_BEARER_TOKEN =  "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmYjQ3NTdjZWNmMTdjNDQyMDcyM2M0NTdhYWNkNjFlNiIsIm5iZiI6MTc2Mjc5NDA2My4xNjMsInN1YiI6IjY5MTIxYTRmMGZmMTVkYTY4NDlhYzQ3YyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.bUPbgDcky9nR63moe3ftxhKkuEQPJ-bB0F5qmL2AUfo";

    public static final String LOGIN_VIEW = "Login";
    public static final String HOME_VIEW = "Home";
    public static final String FILTERED_VIEW = "Filtered";
    public static final String BOOKING_VIEW = "Booking";
    public static final String WATCHLIST_VIEW = "Watchlist";

    private final CardLayout cardLayout;
    private final JPanel cards;

    private final LoginView loginView;

    private final LoggedInView loggedInView;
    private final FilteredView filteredView;
    private final BookingView bookingView;
    private final JPanel watchlistView;

    // set later by AppBuilder
    private LogoutController logoutController;

    public MainAppFrame(LoginView loginView) {
        super("CineSphere Application");

        this.loginView = loginView;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        this.cardLayout = new CardLayout();
        this.cards = new JPanel(cardLayout);
        setContentPane(cards);

        // ===== ViewModels =====
        BookMovieViewModel bookingVM = new BookMovieViewModel();
        PopularMoviesViewModel popularVM = new PopularMoviesViewModel();
        SearchFilmViewModel searchVM = new SearchFilmViewModel();

        // ===== Booking DAOs / Query =====
        BookingMovieDataAccessObject movieDAO =
                new BookingMovieDataAccessObject(new MovieFactory());
        CinemaDataAccessObject cinemaDAO =
                new CinemaDataAccessObject(new CinemaFactory());
        BookingQuery query = new BookingQuery(movieDAO, cinemaDAO);
        BookTicketDataAccessInterface ticketDAO = new InMemoryTicketDataAccessObject();

        // ===== HOME VIEW (LoggedInView) =====
        loggedInView = new LoggedInView();
        loggedInView.setScreenSwitchListener(this);

        // Popular wiring
        PopularMoviesDataAccessInterface popularMoviesDAO =
                new PopularMoviesDataAccessObject(TMDB_BEARER_TOKEN);
        PopularMoviesOutputBoundary popularPresenter =
                new PopularMoviesPresenter(popularVM);
        PopularMoviesInputBoundary popularInteractor =
                new PopularMoviesInteractor(popularMoviesDAO, popularPresenter);
        PopularMoviesController popularController =
                new PopularMoviesController(popularInteractor);
        loggedInView.setPopularMoviesDependencies(popularController, popularVM);

        // Search wiring (home search bar)
        SearchFilmDataAccessInterface searchDAO = new SearchFilmDataAccessObject();
        SearchFilmOutputBoundary searchPresenter = new SearchFilmPresenter(searchVM);
        SearchFilmInputBoundary searchInteractor = new SearchFilmInteractor(searchDAO, searchPresenter);
        SearchFilmController searchController = new SearchFilmController(searchInteractor);
        loggedInView.setSearchDependencies(searchController, searchVM);

        // Movie details wiring (used when clicking posters / search results)
        loggedInView.setMovieDetailsDependencies();

        // ===== BOOKING VIEW =====
        bookingView = new BookingView(bookingVM, query);
        bookingView.setScreenSwitchListener(this);

        BookMovieOutputBoundary bookMoviePresenter = new BookMoviePresenter(bookingVM);
        BookMovieInputBoundary bookMovieInteractor = new BookMovieInteractor(ticketDAO, bookMoviePresenter);
        BookMovieController bookMovieController = new BookMovieController(bookMovieInteractor);
        bookingView.setBookMovieController(bookMovieController);

        // ===== FILTERED VIEW =====
        FilterMoviesViewModel filterVM = new FilterMoviesViewModel();
        FilterMoviesOutputBoundary filterPresenter = new FilterMoviesPresenter(filterVM);

        FilterMoviesDataAccessInterface filterDAO =
                new TmdbMovieDataAccessObject(TMDB_V3_API_KEY);

        FilterMoviesInputBoundary filterInteractor =
                new FilterMoviesInteractor(filterDAO, filterPresenter);
        FilterMoviesController filterController =
                new FilterMoviesController(filterInteractor);

        filteredView = new FilteredView(filterController, filterVM);
        filteredView.setScreenSwitchListener(this);

        // ===== WATCHLIST (placeholder) =====
        watchlistView = new JPanel();
        watchlistView.add(new JLabel("Watchlist View Placeholder"));

        // ===== Cards =====
        cards.add(loginView, LOGIN_VIEW);
        cards.add(loggedInView, HOME_VIEW);
        cards.add(filteredView, FILTERED_VIEW);
        cards.add(bookingView, BOOKING_VIEW);
        cards.add(watchlistView, WATCHLIST_VIEW);

        cardLayout.show(cards, LOGIN_VIEW);

        // if AppBuilder already set logout before showing, apply it
        wireLogoutIfAvailable();

        setVisible(true);
    }

    /**
     * AppBuilder calls this after constructing the frame.
     * We then pass it into whichever views support logout wiring.
     */
    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
        wireLogoutIfAvailable();
    }

    private void wireLogoutIfAvailable() {
        if (logoutController == null) return;

        // Use reflection so this file compiles even if a view doesn’t have the method yet.
        wireLogoutToViewIfMethodExists(loggedInView);
        wireLogoutToViewIfMethodExists(filteredView);
        wireLogoutToViewIfMethodExists(bookingView);
        wireLogoutToViewIfMethodExists(watchlistView);
    }

    private void wireLogoutToViewIfMethodExists(Object view) {
        if (view == null) return;

        try {
            view.getClass()
                    .getMethod("setLogoutDependencies", LogoutController.class)
                    .invoke(view, logoutController);
            return;
        } catch (Exception ignored) {
        }

        try {
            view.getClass()
                    .getMethod("setLogoutController", LogoutController.class)
                    .invoke(view, logoutController);
        } catch (Exception ignored) {
            // no-op: that view simply doesn't support logout wiring
        }
    }

    @Override
    public void onSwitchScreen(String screenName) {
        if (screenName == null) return;

        if (LOGIN_VIEW.equals(screenName)) {
            // Clear fields when returning to login (so username/password are empty after logout)
            try {
                loginView.clearFields();
            } catch (Exception ignored) {
                // If you haven’t added clearFields() yet, add it to LoginView.
            }
        }

        cardLayout.show(cards, screenName);
        revalidate();
        repaint();
    }
}
