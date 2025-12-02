package app;

import data_access.BookingMovieDataAccessObject;
import data_access.CinemaDataAccessObject;
import data_access.FileUserDataAccessObject;
import data_access.InMemoryTicketDataAccessObject;
import data_access.PersistentTicketDataAccessObject;
import data_access.PopularMoviesDataAccessObject;
import data_access.SearchFilmDataAccessObject;
import data_access.TmdbMovieDataAccessObject;
import data_access.UserProfileJsonDataAccessObject;

import entity.CinemaFactory;
import entity.MovieFactory;
import entity.UserFactory;

import interface_adapter.BookMovie.BookMovieController;
import interface_adapter.BookMovie.BookMoviePresenter;
import interface_adapter.BookMovie.BookMovieViewModel;
import interface_adapter.BookingQuery;
import interface_adapter.SearchFilm.SearchFilmController;
import interface_adapter.SearchFilm.SearchFilmPresenter;
import interface_adapter.SearchFilm.SearchFilmViewModel;
import interface_adapter.bookings.BookingsController;
import interface_adapter.filter_movies.FilterMoviesController;
import interface_adapter.filter_movies.FilterMoviesPresenter;
import interface_adapter.filter_movies.FilterMoviesViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import interface_adapter.popular_movies.PopularMoviesController;
import interface_adapter.popular_movies.PopularMoviesPresenter;
import interface_adapter.popular_movies.PopularMoviesViewModel;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import interface_adapter.watchlist.WatchlistController;

import io.github.cdimascio.dotenv.Dotenv;
import use_case.book_movie.BookMovieInputBoundary;
import use_case.book_movie.BookMovieInteractor;
import use_case.book_movie.BookMovieOutputBoundary;
import use_case.book_movie.BookTicketDataAccessInterface;
import use_case.bookings.BookingsDataAccessInterface;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;
import use_case.logout.LogoutUserDataAccessInterface;
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
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import use_case.watchlist.WatchlistDataAccessInterface;

import view.BookingView;
import view.FilteredView;
import view.LoggedInView;
import view.LoginView;
import view.MyBookingsView;
import view.WatchlistView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * Builder class for constructing the CineSphere application.
 * Uses the Builder pattern to allow flexible configuration of views and use cases.
 */
public class AppBuilder {

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    // ===== CONSTANTS =====
    private static final String TMDB_V3_API_KEY = dotenv.get("TMDB_API_KEY");
    private static final String TMDB_BEARER_TOKEN = dotenv.get("TMDB_BEARER_TOKEN");

    public static final String LOGIN_VIEW = "Login";
    public static final String HOME_VIEW = "Home";
    public static final String FILTERED_VIEW = "Filtered";
    public static final String BOOKING_VIEW = "Booking";
    public static final String WATCHLIST_VIEW = "Watchlist";
    public static final String MY_BOOKINGS_VIEW = "MyBookings";

    // ===== CARD LAYOUT COMPONENTS =====
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();

    // ===== FACTORIES =====
    private final UserFactory userFactory = new UserFactory();
    private final MovieFactory movieFactory = new MovieFactory();
    private final CinemaFactory cinemaFactory = new CinemaFactory();

    // ===== DATA ACCESS OBJECTS =====
    private final FileUserDataAccessObject userDataAccessObject;
    private final UserProfileJsonDataAccessObject userProfileDataAccessObject;
    private final PopularMoviesDataAccessObject popularMoviesDataAccessObject;
    private final SearchFilmDataAccessObject searchFilmDataAccessObject;
    private final TmdbMovieDataAccessObject filterMoviesDataAccessObject;
    private final BookingMovieDataAccessObject bookingMovieDataAccessObject;
    private final CinemaDataAccessObject cinemaDataAccessObject;
    private BookTicketDataAccessInterface ticketDataAccessObject;

    // ===== VIEW MODELS =====
    private LoginViewModel loginViewModel;
    private SignupViewModel signupViewModel;
    private PopularMoviesViewModel popularMoviesViewModel;
    private SearchFilmViewModel searchFilmViewModel;
    private FilterMoviesViewModel filterMoviesViewModel;
    private BookMovieViewModel bookMovieViewModel;

    // ===== VIEWS =====
    private LoginView loginView;
    private LoggedInView loggedInView;
    private FilteredView filteredView;
    private BookingView bookingView;
    private WatchlistView watchlistView;
    private MyBookingsView myBookingsView;

    // ===== CONTROLLERS =====
    private LoginController loginController;
    private SignupController signupController;
    private LogoutController logoutController;
    private PopularMoviesController popularMoviesController;
    private SearchFilmController searchFilmController;
    private FilterMoviesController filterMoviesController;
    private BookMovieController bookMovieController;
    private WatchlistController watchlistController;
    private BookingsController bookingsController;

    // ===== PRESENTERS (for wiring screen switches) =====
    private LoginPresenter loginPresenter;
    private LogoutPresenter logoutPresenter;

    // ===== HELPER OBJECTS =====
    private final BookingQuery bookingQuery;

    /**
     * Constructs a new AppBuilder with all necessary data access objects initialized.
     */
    public AppBuilder() {
        cardPanel.setLayout(cardLayout);

        // Initialize all DAOs
        this.userDataAccessObject = new FileUserDataAccessObject("users.txt", userFactory);
        this.userProfileDataAccessObject = new UserProfileJsonDataAccessObject("user_profiles.json");
        this.popularMoviesDataAccessObject = new PopularMoviesDataAccessObject(TMDB_BEARER_TOKEN);
        this.searchFilmDataAccessObject = new SearchFilmDataAccessObject();
        this.filterMoviesDataAccessObject = new TmdbMovieDataAccessObject(TMDB_V3_API_KEY);
        this.bookingMovieDataAccessObject = new BookingMovieDataAccessObject(movieFactory);
        this.cinemaDataAccessObject = new CinemaDataAccessObject(cinemaFactory);

        // Initialize ticket DAO with persistence
        this.ticketDataAccessObject = new PersistentTicketDataAccessObject(
                new InMemoryTicketDataAccessObject(),
                userDataAccessObject,
                userProfileDataAccessObject
        );

        // Initialize booking query helper
        this.bookingQuery = new BookingQuery(bookingMovieDataAccessObject, cinemaDataAccessObject);
    }

    // ===== VIEW ADDITION METHODS =====

    /**
     * Adds the Login View to the application.
     * @return this builder
     */
    public AppBuilder addLoginView() {
        loginViewModel = new LoginViewModel();
        signupViewModel = new SignupViewModel();
        // LoginView will be fully constructed after use cases are wired
        return this;
    }

    /**
     * Adds the Home (LoggedIn) View to the application.
     * @return this builder
     */
    public AppBuilder addLoggedInView() {
        popularMoviesViewModel = new PopularMoviesViewModel();
        searchFilmViewModel = new SearchFilmViewModel();
        loggedInView = new LoggedInView();
        cardPanel.add(loggedInView, HOME_VIEW);
        return this;
    }

    /**
     * Adds the Filtered Movies View to the application.
     * @return this builder
     */
    public AppBuilder addFilteredView() {
        filterMoviesViewModel = new FilterMoviesViewModel();
        // FilteredView will be fully constructed after use cases are wired
        return this;
    }

    /**
     * Adds the Booking View to the application.
     * @return this builder
     */
    public AppBuilder addBookingView() {
        bookMovieViewModel = new BookMovieViewModel();
        bookingView = new BookingView(bookMovieViewModel, bookingQuery);
        cardPanel.add(bookingView, BOOKING_VIEW);
        return this;
    }

    /**
     * Adds the Watchlist View to the application.
     * @return this builder
     */
    public AppBuilder addWatchlistView() {
        // WatchlistView will be fully constructed after watchlist controller is wired
        return this;
    }

    /**
     * Adds the My Bookings View to the application.
     * @return this builder
     */
    public AppBuilder addMyBookingsView() {
        // MyBookingsView will be fully constructed after bookings controller is wired
        return this;
    }

    // ===== USE CASE WIRING METHODS =====

    /**
     * Adds the Login Use Case to the application.
     * @return this builder
     */
    public AppBuilder addLoginUseCase() {
        loginPresenter = new LoginPresenter(loginViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, loginPresenter);
        loginController = new LoginController(loginInteractor);
        return this;
    }

    /**
     * Adds the Signup Use Case to the application.
     * @return this builder
     */
    public AppBuilder addSignupUseCase() {
        final SignupOutputBoundary signupPresenter = new SignupPresenter(signupViewModel);
        final SignupInputBoundary signupInteractor = new SignupInteractor(
                userDataAccessObject, signupPresenter, userFactory);
        signupController = new SignupController(signupInteractor);
        return this;
    }

    /**
     * Adds the Logout Use Case to the application.
     * @return this builder
     */
    public AppBuilder addLogoutUseCase() {
        // LogoutPresenter will be set with screen switch listener during build
        return this;
    }

    /**
     * Adds the Popular Movies Use Case to the application.
     * @return this builder
     */
    public AppBuilder addPopularMoviesUseCase() {
        final PopularMoviesOutputBoundary popularPresenter =
                new PopularMoviesPresenter(popularMoviesViewModel);
        final PopularMoviesInputBoundary popularInteractor =
                new PopularMoviesInteractor(popularMoviesDataAccessObject, popularPresenter);
        popularMoviesController = new PopularMoviesController(popularInteractor);

        if (loggedInView != null) {
            loggedInView.setPopularMoviesDependencies(popularMoviesController, popularMoviesViewModel);
        }
        return this;
    }

    /**
     * Adds the Search Film Use Case to the application.
     * @return this builder
     */
    public AppBuilder addSearchFilmUseCase() {
        final SearchFilmOutputBoundary searchPresenter =
                new SearchFilmPresenter(searchFilmViewModel);
        final SearchFilmInputBoundary searchInteractor =
                new SearchFilmInteractor(searchFilmDataAccessObject, searchPresenter);
        searchFilmController = new SearchFilmController(searchInteractor);

        if (loggedInView != null) {
            loggedInView.setSearchDependencies(searchFilmController, searchFilmViewModel);
        }
        return this;
    }

    /**
     * Adds the Filter Movies Use Case to the application.
     * @return this builder
     */
    public AppBuilder addFilterMoviesUseCase() {
        final FilterMoviesOutputBoundary filterPresenter =
                new FilterMoviesPresenter(filterMoviesViewModel);
        final FilterMoviesInputBoundary filterInteractor =
                new FilterMoviesInteractor(filterMoviesDataAccessObject, filterPresenter);
        filterMoviesController = new FilterMoviesController(filterInteractor);

        // Preload genres into the home screen
        if (loggedInView != null) {
            Map<String, Integer> initialGenres = filterMoviesDataAccessObject.getMovieGenres();
            loggedInView.setGenres(new ArrayList<>(initialGenres.keySet()));
        }

        return this;
    }

    /**
     * Adds the Book Movie Use Case to the application.
     * @return this builder
     */
    public AppBuilder addBookMovieUseCase() {
        final BookMovieOutputBoundary bookMoviePresenter =
                new BookMoviePresenter(bookMovieViewModel);
        final BookMovieInputBoundary bookMovieInteractor =
                new BookMovieInteractor(ticketDataAccessObject, bookMoviePresenter);
        bookMovieController = new BookMovieController(bookMovieInteractor);

        if (bookingView != null) {
            bookingView.setBookMovieController(bookMovieController);
        }
        return this;
    }

    /**
     * Adds the Watchlist Use Case to the application.
     * @return this builder
     */
    public AppBuilder addWatchlistUseCase() {
        watchlistController = new WatchlistController(userProfileDataAccessObject, userDataAccessObject);

        if (loggedInView != null) {
            loggedInView.setMovieDetailsDependencies(watchlistController);
        }
        return this;
    }

    /**
     * Adds the Bookings Use Case to the application.
     * @return this builder
     */
    public AppBuilder addBookingsUseCase() {
        bookingsController = new BookingsController(userProfileDataAccessObject, userDataAccessObject);
        return this;
    }

    /**
     * Builds and returns the complete CineSphere application JFrame.
     * This method finalizes all view construction and wiring.
     * @return the configured JFrame
     */
    public JFrame build() {
        final JFrame application = new JFrame("CineSphere Application");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.setSize(1000, 700);
        application.setLocationRelativeTo(null);

        // Create LoginView now that controllers are ready
        loginView = new LoginView(
                loginController,
                loginViewModel,
                signupController,
                signupViewModel
        );
        cardPanel.add(loginView, LOGIN_VIEW);

        // Create FilteredView now that controllers are ready
        filteredView = new FilteredView(filterMoviesController, filterMoviesViewModel, watchlistController);
        cardPanel.add(filteredView, FILTERED_VIEW);

        // Create WatchlistView now that controller is ready
        watchlistView = new WatchlistView(watchlistController);
        cardPanel.add(watchlistView, WATCHLIST_VIEW);

        // Create MyBookingsView now that controller is ready
        myBookingsView = new MyBookingsView(bookingsController);
        cardPanel.add(myBookingsView, MY_BOOKINGS_VIEW);

        // Create screen switch listener for navigation
        ScreenSwitchListenerImpl screenSwitchListener = new ScreenSwitchListenerImpl(
                cardLayout, cardPanel, loginView, watchlistView, myBookingsView);

        // Wire screen switch listeners to all views
        loggedInView.setScreenSwitchListener(screenSwitchListener);
        filteredView.setScreenSwitchListener(screenSwitchListener);
        bookingView.setScreenSwitchListener(screenSwitchListener);
        watchlistView.setScreenSwitchListener(screenSwitchListener);
        myBookingsView.setScreenSwitchListener(screenSwitchListener);

        // Wire login presenter to switch screens after successful login
        loginPresenter.setScreenSwitchListener(screenSwitchListener);

        // Create and wire logout use case
        logoutPresenter = new LogoutPresenter(screenSwitchListener);
        final LogoutInputBoundary logoutInteractor =
                new LogoutInteractor(userDataAccessObject, logoutPresenter);
        logoutController = new LogoutController(logoutInteractor);

        // Wire logout controller to all views that need it
        wireLogoutToViews();

        application.add(cardPanel);

        // Show login view initially
        cardLayout.show(cardPanel, LOGIN_VIEW);

        return application;
    }

    /**
     * Wires the logout controller to all views that support it.
     */
    private void wireLogoutToViews() {
        wireLogoutToViewIfMethodExists(loggedInView);
        wireLogoutToViewIfMethodExists(filteredView);
        wireLogoutToViewIfMethodExists(bookingView);
        wireLogoutToViewIfMethodExists(watchlistView);
        wireLogoutToViewIfMethodExists(myBookingsView);
    }

    /**
     * Attempts to wire logout controller to a view using reflection.
     * Tries both setLogoutDependencies and setLogoutController methods.
     * @param view the view to wire
     */
    private void wireLogoutToViewIfMethodExists(Object view) {
        if (view == null || logoutController == null) return;

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
        }
    }

    /**
     * Inner class implementing ScreenSwitchListener for navigation between views.
     */
    private static class ScreenSwitchListenerImpl implements view.ScreenSwitchListener {
        private final CardLayout cardLayout;
        private final JPanel cardPanel;
        private final LoginView loginView;
        private final WatchlistView watchlistView;
        private final MyBookingsView myBookingsView;

        public ScreenSwitchListenerImpl(CardLayout cardLayout, JPanel cardPanel,
                                        LoginView loginView, WatchlistView watchlistView,
                                        MyBookingsView myBookingsView) {
            this.cardLayout = cardLayout;
            this.cardPanel = cardPanel;
            this.loginView = loginView;
            this.watchlistView = watchlistView;
            this.myBookingsView = myBookingsView;
        }

        @Override
        public void onSwitchScreen(String screenName) {
            if (screenName == null) return;

            if (LOGIN_VIEW.equals(screenName)) {
                try {
                    loginView.clearFields();
                } catch (Exception ignored) {
                }
            }

            if (WATCHLIST_VIEW.equals(screenName)) {
                watchlistView.refresh();
            }

            if (MY_BOOKINGS_VIEW.equals(screenName)) {
                myBookingsView.refresh();
            }

            cardLayout.show(cardPanel, screenName);
            cardPanel.revalidate();
            cardPanel.repaint();
        }
    }
}