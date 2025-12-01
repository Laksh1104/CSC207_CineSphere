package app;

import javax.swing.*;
import java.awt.*;

// Data Access and Entity Factories
import data_access.*;
import entity.CinemaFactory;
import entity.MovieFactory;
import interface_adapter.BookMovie.BookMovieController;
import interface_adapter.BookMovie.BookMoviePresenter;
import interface_adapter.SearchFilm.SearchFilmController;
import interface_adapter.SearchFilm.SearchFilmPresenter;
import interface_adapter.SearchFilm.SearchFilmViewModel;
import interface_adapter.popular_movies.PopularMoviesController;
import interface_adapter.popular_movies.PopularMoviesPresenter;
import interface_adapter.popular_movies.PopularMoviesViewModel;
import use_case.book_movie.*;

// Views
import use_case.popular_movies.PopularMoviesDataAccessInterface;
import use_case.popular_movies.PopularMoviesInputBoundary;
import use_case.popular_movies.PopularMoviesInteractor;
import use_case.popular_movies.PopularMoviesOutputBoundary;
import use_case.search_film.SearchFilmDataAccessInterface;
import use_case.search_film.SearchFilmInputBoundary;
import use_case.search_film.SearchFilmInteractor;
import use_case.search_film.SearchFilmOutputBoundary;
import view.LoggedInView;
import view.BookingView;
import view.ScreenSwitchListener; // Assuming this interface is in the 'view' package

// Interface Adapters
import interface_adapter.BookMovie.BookMovieViewModel;
import interface_adapter.BookingQuery;


public class MainAppFrame extends JFrame implements ScreenSwitchListener {

    // --- DEPENDENCIES: Correctly Initialized and Singular ---

    // View Models (Only need one for Booking in this example)
    private final BookMovieViewModel vm = new BookMovieViewModel();
    private final PopularMoviesViewModel pmv = new PopularMoviesViewModel();
    private final SearchFilmViewModel svm = new SearchFilmViewModel();

    private final BookingMovieDataAccessObject movieDAO =
            new BookingMovieDataAccessObject(new MovieFactory());
    private final CinemaDataAccessObject cinemaDAO =
            new CinemaDataAccessObject(new CinemaFactory());
    private final BookingQuery query = new BookingQuery(movieDAO, cinemaDAO);
    private final BookTicketDataAccessInterface ticketDAO = new InMemoryTicketDataAccessObject();



    private final JPanel cards;
    private final CardLayout cardLayout;

    // View Names (Keys for CardLayout)
    public static final String HOME_VIEW = "Home";
    public static final String BOOKING_VIEW = "Booking";
    public static final String WATCHLIST_VIEW = "Watchlist";


    public MainAppFrame() {
        super("CineSphere Application");

        // --- Setup Frame ---
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);

        // --- Setup CardLayout ---
        this.cardLayout = new CardLayout();
        this.cards = new JPanel(cardLayout);
        add(cards);

        // --- Instantiate Views ---
        LoggedInView loggedInView = new LoggedInView();
        BookingView bookingView = new BookingView(vm, query);
        JPanel watchlistView = new JPanel();
        watchlistView.add(new JLabel("Watchlist View Placeholder"));
        MovieDataAccessInterface bookMovieDAO = this.movieDAO;
        BookMovieOutputBoundary bookMoviePresenter = new BookMoviePresenter(vm);
        BookMovieInputBoundary bookMovieInteractor = new BookMovieInteractor(ticketDAO, bookMoviePresenter);
        BookMovieController bookMovieController = new BookMovieController(bookMovieInteractor);
        bookingView.setBookMovieController(bookMovieController);

        PopularMoviesDataAccessInterface popularMoviesDAO = new PopularMoviesDataAccessObject("Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmYjQ3NTdjZWNmMTdjNDQyMDcyM2M0NTdhYWNkNjFlNiIsIm5iZiI6MTc2Mjc5NDA2My4xNjMsInN1YiI6IjY5MTIxYTRmMGZmMTVkYTY4NDlhYzQ3YyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.bUPbgDcky9nR63moe3ftxhKkuEQPJ-bB0F5qmL2AUfo");
        PopularMoviesOutputBoundary popularMoviesPresenter = new PopularMoviesPresenter(pmv);
        PopularMoviesInputBoundary popularMoviesInteractor = new PopularMoviesInteractor(popularMoviesDAO, popularMoviesPresenter);
        PopularMoviesController popularMoviesController = new PopularMoviesController(popularMoviesInteractor);
        loggedInView.setPopularMoviesDependencies(popularMoviesController, pmv);

        SearchFilmDataAccessInterface searchFilmDAO = new SearchFilmDataAccessObject();
        SearchFilmOutputBoundary searchFilmPresenter = new SearchFilmPresenter(svm);
        SearchFilmInputBoundary searchFilmInteractor = new SearchFilmInteractor(searchFilmDAO, searchFilmPresenter);
        SearchFilmController searchFilmController = new SearchFilmController(searchFilmInteractor);
        loggedInView.setSearchDependencies(searchFilmController, svm);

        loggedInView.setMovieDetailsDependencies();



        // --- Essential Step: Inject the Listener ---
        // 'this' is the MainAppFrame, which implements ScreenSwitchListener.
        loggedInView.setScreenSwitchListener(this);
        bookingView.setScreenSwitchListener(this);


        // --- Add Views to the CardLayout ---
        cards.add(loggedInView, HOME_VIEW);
        cards.add(bookingView, BOOKING_VIEW);
        cards.add(watchlistView, WATCHLIST_VIEW);

        // Show the initial screen
        cardLayout.show(cards, HOME_VIEW);

        setVisible(true);
    }

    // --- Implementation of ScreenSwitchListener ---
    @Override
    public void onSwitchScreen(String screenName) {
        if (screenName != null) {
            System.out.println("Switching to screen: " + screenName);
            cardLayout.show(cards, screenName);
            this.revalidate();
            this.repaint();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainAppFrame::new);
    }
}