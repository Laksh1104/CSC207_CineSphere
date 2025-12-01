package app;

import javax.swing.*;
import java.awt.*;

import data_access.*;
import entity.CinemaFactory;
import entity.MovieFactory;
import interface_adapter.BookMovie.BookMovieController;
import interface_adapter.BookMovie.BookMoviePresenter;
import interface_adapter.BookMovie.BookMovieViewModel;
import interface_adapter.BookingQuery;
import use_case.book_movie.*;

import interface_adapter.popular_movies.PopularMoviesController;
import interface_adapter.popular_movies.PopularMoviesPresenter;
import interface_adapter.popular_movies.PopularMoviesViewModel;
import use_case.popular_movies.PopularMoviesDataAccessInterface;
import use_case.popular_movies.PopularMoviesInputBoundary;
import use_case.popular_movies.PopularMoviesInteractor;
import use_case.popular_movies.PopularMoviesOutputBoundary;
import interface_adapter.SearchFilm.SearchFilmController;
import interface_adapter.SearchFilm.SearchFilmPresenter;
import interface_adapter.SearchFilm.SearchFilmViewModel;
import use_case.search_film.SearchFilmDataAccessInterface;
import use_case.search_film.SearchFilmInputBoundary;
import use_case.search_film.SearchFilmInteractor;
import use_case.search_film.SearchFilmOutputBoundary;

import interface_adapter.filter_movies.FilterMoviesController;
import interface_adapter.filter_movies.FilterMoviesPresenter;
import interface_adapter.filter_movies.FilterMoviesViewModel;
import use_case.movie_filter.FilterMoviesDataAccessInterface;
import use_case.movie_filter.FilterMoviesInputBoundary;
import use_case.movie_filter.FilterMoviesInteractor;
import use_case.movie_filter.FilterMoviesOutputBoundary;

import view.BookingView;
import view.FilteredView;
import view.LoggedInView;
import view.LoginView;
import view.ScreenSwitchListener;

public class MainAppFrame extends JFrame implements ScreenSwitchListener {


    private static final String TMDB_V3_API_KEY =
            "6289d1f5d1b8e2d2a78614fc9e48742b";

    private static final String TMDB_BEARER =
            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmYjQ3NTdjZWNmMTdjNDQyMDcyM2M0NTdhYWNkNjFlNiIsIm5iZiI6MTc2Mjc5NDA2My4xNjMsInN1YiI6IjY5MTIxYTRmMGZmMTVkYTY4NDlhYzQ3YyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.bUPbgDcky9nR63moe3ftxhKkuEQPJ-bB0F5qmL2AUfo";

    private final BookMovieViewModel bookingVM = new BookMovieViewModel();
    private final PopularMoviesViewModel popularVM = new PopularMoviesViewModel();
    private final SearchFilmViewModel searchVM = new SearchFilmViewModel();

    private final BookingMovieDataAccessObject movieDAO =
            new BookingMovieDataAccessObject(new MovieFactory());
    private final CinemaDataAccessObject cinemaDAO =
            new CinemaDataAccessObject(new CinemaFactory());
    private final BookingQuery query = new BookingQuery(movieDAO, cinemaDAO);
    private final BookTicketDataAccessInterface ticketDAO = new InMemoryTicketDataAccessObject();

    private final JPanel cards;
    private final CardLayout cardLayout;

    public static final String LOGIN_VIEW = "Login";
    public static final String HOME_VIEW = "Home";
    public static final String FILTERED_VIEW = "Filtered";
    public static final String BOOKING_VIEW = "Booking";
    public static final String WATCHLIST_VIEW = "Watchlist";

    public MainAppFrame(LoginView loginView) {
        super("CineSphere Application");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        this.cardLayout = new CardLayout();
        this.cards = new JPanel(cardLayout);
        add(cards);

        LoggedInView loggedInView = new LoggedInView();

        PopularMoviesDataAccessInterface popularMoviesDAO =
                new PopularMoviesDataAccessObject(TMDB_BEARER);
        PopularMoviesOutputBoundary popularPresenter =
                new PopularMoviesPresenter(popularVM);
        PopularMoviesInputBoundary popularInteractor =
                new PopularMoviesInteractor(popularMoviesDAO, popularPresenter);
        PopularMoviesController popularController =
                new PopularMoviesController(popularInteractor);
        loggedInView.setPopularMoviesDependencies(popularController, popularVM);

        SearchFilmDataAccessInterface searchDAO = new SearchFilmDataAccessObject();
        SearchFilmOutputBoundary searchPresenter = new SearchFilmPresenter(searchVM);
        SearchFilmInputBoundary searchInteractor = new SearchFilmInteractor(searchDAO, searchPresenter);
        SearchFilmController searchController = new SearchFilmController(searchInteractor);
        loggedInView.setSearchDependencies(searchController, searchVM);

        loggedInView.setMovieDetailsDependencies();

        BookingView bookingView = new BookingView(bookingVM, query);
        BookMovieOutputBoundary bookMoviePresenter = new BookMoviePresenter(bookingVM);
        BookMovieInputBoundary bookMovieInteractor = new BookMovieInteractor(ticketDAO, bookMoviePresenter);
        BookMovieController bookMovieController = new BookMovieController(bookMovieInteractor);
        bookingView.setBookMovieController(bookMovieController);

        FilterMoviesViewModel filterVM = new FilterMoviesViewModel();
        FilterMoviesOutputBoundary filterPresenter = new FilterMoviesPresenter(filterVM);

        FilterMoviesDataAccessInterface filterDAO =
                new TmdbMovieDataAccessObject(TMDB_V3_API_KEY);

        FilterMoviesInputBoundary filterInteractor =
                new FilterMoviesInteractor(filterDAO, filterPresenter);
        FilterMoviesController filterController =
                new FilterMoviesController(filterInteractor);

        FilteredView filteredView = new FilteredView(filterController, filterVM);

        JPanel watchlistView = new JPanel();
        watchlistView.add(new JLabel("Watchlist View Placeholder"));

        loggedInView.setScreenSwitchListener(this);
        bookingView.setScreenSwitchListener(this);
        filteredView.setScreenSwitchListener(this);

        cards.add(loginView, LOGIN_VIEW);
        cards.add(loggedInView, HOME_VIEW);
        cards.add(filteredView, FILTERED_VIEW);
        cards.add(bookingView, BOOKING_VIEW);
        cards.add(watchlistView, WATCHLIST_VIEW);

        cardLayout.show(cards, LOGIN_VIEW);
        setVisible(true);
    }

    @Override
    public void onSwitchScreen(String screenName) {
        if (screenName != null) {
            cardLayout.show(cards, screenName);
            revalidate();
            repaint();
        }
    }
}
