package app;

import javax.swing.*;
import java.awt.*;

// Data Access and Entity Factories
import data_access.BookingMovieDataAccessObject;
import data_access.CinemaDataAccessObject;
import entity.CinemaFactory;
import entity.MovieFactory;

// Views
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

    // 1. Instantiate the concrete DAOs and Factories
    private final BookingMovieDataAccessObject movieDAO =
            new BookingMovieDataAccessObject(new MovieFactory());
    private final CinemaDataAccessObject cinemaDAO =
            new CinemaDataAccessObject(new CinemaFactory());

    // 2. Pass the instantiated DAOs to the BookingQuery
    private final BookingQuery query = new BookingQuery(movieDAO, cinemaDAO);


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

        // Use the correctly initialized 'vm' and 'query'
        BookingView bookingView = new BookingView(vm, query);

        // Placeholder for WatchlistView (needs its own setup and listener injection)
        JPanel watchlistView = new JPanel();
        watchlistView.add(new JLabel("Watchlist View Placeholder"));


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