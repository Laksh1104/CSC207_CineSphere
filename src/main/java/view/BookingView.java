package view;

import data_access.CinemaDataAccessObject;
import data_access.BookingMovieDataAccessObject;
import entity.*;
import interface_adapter.BookMovie.*;


import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import com.toedter.calendar.JDateChooser;


public class BookingView extends JPanel implements PropertyChangeListener {

    private final BookMovieViewModel bookMovieViewModel;
    private BookMovieController bookMovieController;

    private JComboBox<String> movieDropdown;
    private JComboBox<String> theaterDropdown;
    private JComboBox<String> timeDropdown;
    private SeatSelectionPanel seatPanel;

    private Movie selectedMovie = null;
    private Cinema selectedCinema = null;
    private ShowTime selectedShowtime = null;
    private final Map<String, ShowTime> showtimeMap = new HashMap<>();
    private String selectedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    private final Color COLOR = new Color(255, 255, 224);
    private final int HEIGHT = 25;

    public BookingView(BookMovieViewModel bookMovieViewModel) {
        this.bookMovieViewModel = bookMovieViewModel;
        this.bookMovieViewModel.addPropertyChangeListener(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(COLOR);
        add(Box.createVerticalStrut(10));
        add(new HeaderPanel(), BorderLayout.NORTH);
        setupSelectionPanel();

        setupSeatPanel();
        setupBookButton();
    }

    public void setBookMovieController(BookMovieController controller) {
        this.bookMovieController = controller;
    }

    private void setupHeader() {
        add(Box.createVerticalStrut(10)); // 10px gap

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        headerPanel.setBackground(COLOR);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10000));

        JLabel title = new JLabel("CineSphere");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JButton homeButton = new JButton("Home");
        JButton watchlistButton = new JButton("Watchlist");
        JButton bookButton = new JButton("Booking");
        JButton logoutButton = new JButton("Logout");

        headerPanel.add(title);
        headerPanel.add(homeButton);
        headerPanel.add(watchlistButton);
        headerPanel.add(bookButton);
        headerPanel.add(logoutButton);

        add(headerPanel);

    }

    private void setupSelectionPanel() {
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        selectionPanel.setBackground(COLOR);

        // Movie Dropdown
        movieDropdown = new JComboBox<>(new String[]{"Select Movie"});
        movieDropdown.setPreferredSize(new Dimension(300, HEIGHT));
        populateMovies();

        movieDropdown.addActionListener(e -> {
            String movieName = (String) movieDropdown.getSelectedItem();
            if (movieName != null && !movieName.equals("Select Movie")) {
                selectedMovie = getMovieObject(movieName);

                populateCinemas(selectedMovie.getFilmId(), selectedDate);

                BookMovieState state = bookMovieViewModel.getState();
                state.setMovie(selectedMovie);
                bookMovieViewModel.setState(state);
            }
        });
        JPanel moviePanel = new JPanel(new BorderLayout());
        moviePanel.setBackground(COLOR);
        moviePanel.add(new JLabel("Movie: "), BorderLayout.WEST);
        moviePanel.add(movieDropdown, BorderLayout.CENTER);

        // Date Picker
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setBackground(COLOR);
        datePanel.add(new JLabel("Date: "), BorderLayout.WEST);

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(150, HEIGHT));
        dateChooser.setDateFormatString("yyyy-MM-dd");
        Date today = new Date();
        dateChooser.setMinSelectableDate(today);
        dateChooser.setDate(today);

        dateChooser.addPropertyChangeListener("date", evt -> {
            Date date = dateChooser.getDate();
            if (date != null) {
                selectedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

                populateCinemas(selectedMovie.getFilmId(), selectedDate);

                BookMovieState state = bookMovieViewModel.getState();
                state.setDate(selectedDate);
                bookMovieViewModel.setState(state);
            }
        });
        datePanel.add(dateChooser, BorderLayout.CENTER);

        // Cinema Dropdown
        theaterDropdown = new JComboBox<>(new String[]{"Select Cinema"});
        JPanel theaterPanel = new JPanel(new BorderLayout());
        theaterPanel.setBackground(COLOR);
        theaterPanel.setPreferredSize(new Dimension(300, HEIGHT));
        theaterPanel.add(new JLabel("Cinema: "), BorderLayout.WEST);
        theaterPanel.add(theaterDropdown, BorderLayout.CENTER);

        theaterDropdown.addActionListener(e -> {
            String cinemaName = (String) theaterDropdown.getSelectedItem();

            if (cinemaName != null && !cinemaName.equals("Select Cinema") && selectedMovie != null) {
                selectedCinema = getCinemaObject(cinemaName, selectedMovie.getFilmId(), selectedDate);

                populateShowTime(selectedCinema);

                BookMovieState state = bookMovieViewModel.getState();
                state.setCinema(selectedCinema);
                bookMovieViewModel.setState(state);
            }
        });

        // Time Dropdown
        timeDropdown = new JComboBox<>(new String[]{"Select Time"});
        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.setBackground(COLOR);
        timePanel.setPreferredSize(new Dimension(250, HEIGHT));
        timePanel.add(new JLabel("Time: "), BorderLayout.WEST);
        timePanel.add(timeDropdown, BorderLayout.CENTER);

        timeDropdown.addActionListener(e -> {
            selectedShowtime = getSelectedShowtime();
            updateSeatGridForCurrentSelection();
        });



        selectionPanel.add(moviePanel);
        selectionPanel.add(datePanel);
        selectionPanel.add(theaterPanel);
        selectionPanel.add(timePanel);
        add(selectionPanel);
    }

    private void setupSeatPanel() {

        JPanel seatPanelContainer = new JPanel();
        seatPanelContainer.setLayout(new BoxLayout(seatPanelContainer, BoxLayout.Y_AXIS));
        seatPanelContainer.setBackground(COLOR);

        // Add vertical spacing above
        seatPanelContainer.add(Box.createVerticalStrut(15));

        JLabel seatsLabel = new JLabel("Seating Arrangement:");
        seatsLabel.setFont(new Font("Arial", Font.BOLD, 18));

        seatPanel = new SeatSelectionPanel(Collections.emptySet());

        seatPanelContainer.add(seatsLabel);
        // Add vertical spacing below
        seatPanelContainer.add(Box.createVerticalStrut(15));

        seatPanelContainer.add(seatPanel);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setBackground(COLOR);
        wrapper.add(seatPanelContainer);

        add(wrapper);

    }

    private void setupBookButton() {
        JButton bookMovieButton = new JButton("Book Movie");
        bookMovieButton.setFont(new Font("Arial", Font.BOLD, 18));
        bookMovieButton.setForeground(Color.BLACK);
        bookMovieButton.setFocusPainted(false);

        bookMovieButton.addActionListener(e -> {
            if (bookMovieController != null) {
                selectedShowtime = getSelectedShowtime();

                BookMovieState state = bookMovieViewModel.getState();
                state.setShowtime(selectedShowtime);
                bookMovieViewModel.setState(state);

                if (selectedMovie != null && selectedCinema != null && selectedShowtime != null &&
                        !seatPanel.getSelectedSeats().isEmpty()) {
                    bookMovieController.execute(
                            selectedMovie,
                            selectedDate,
                            selectedCinema,
                            selectedShowtime,
                            new HashSet<>(seatPanel.getSelectedSeats())
                    );
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Please select movie, cinema, date, time, and seats.",
                            "Incomplete Booking", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        JPanel bookButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bookButtonPanel.setBackground(COLOR);
        bookButtonPanel.add(bookMovieButton);
        add(bookButtonPanel);
    }

    /** PropertyChangeListener updates UI when state changes */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        BookMovieState state = (BookMovieState) evt.getNewValue();

        // 1) Mark unavailable seats
        if (!state.getSeats().isEmpty()) {
            seatPanel.markSeatsAsUnavailable(state.getSeats());
        }

        // 2) Show error
        if (state.getBookingError() != null) {
            JOptionPane.showMessageDialog(this, state.getBookingError(),
                    "Booking Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3) Show success message
        if (state.getBookingSuccessMessage() != null) {
            JOptionPane.showMessageDialog(this,
                    state.getBookingSuccessMessage(),
                    "Booking Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);
            state.setBookingSuccessMessage(null);
        }
    }

    private void populateMovies() {
        MovieFactory movieFactory = new MovieFactory();
        BookingMovieDataAccessObject movieDAO = new BookingMovieDataAccessObject(movieFactory);
        List<Movie> movies = movieDAO.getNowShowingMovies();

        movies.sort(Comparator.comparing(Movie::getFilmName));

        for (Movie movie : movies) {
            movieDropdown.addItem(movie.getFilmName());
        }
    }

    private Movie getMovieObject(String film_name){
        MovieFactory movieFactory = new MovieFactory();
        BookingMovieDataAccessObject movieDAO = new BookingMovieDataAccessObject(movieFactory);
        List<Movie> movies = movieDAO.getNowShowingMovies();
        for  (Movie movie : movies) {
            if(movie.getFilmName().equals(film_name)) {
                return movie;
            }
        }
        return null;
    }

    private Cinema getCinemaObject(String cinema_name, int filmId, String date) {
        CinemaFactory cinemaFactory = new CinemaFactory();
        CinemaDataAccessObject cinemaDAO = new CinemaDataAccessObject(cinemaFactory);
        List<Cinema> cinemas = cinemaDAO.getCinemasForFilm(filmId, date);

        for (Cinema cinema : cinemas) {
            if (cinema.getCinemaName().equals(cinema_name)) {
                return cinema;
            }
        }
        return null;
    }

    public ShowTime getSelectedShowtime() {
        String selected = (String) timeDropdown.getSelectedItem();
        if (selected != null) {
            return showtimeMap.get(selected);
        }
        return null;
    }

    private void populateCinemas(int movie_Id, String date){
        CinemaFactory cinemaFactory = new CinemaFactory();
        CinemaDataAccessObject cinemaDAO = new CinemaDataAccessObject(cinemaFactory);
        List<Cinema> cinemas = cinemaDAO.getCinemasForFilm(movie_Id, date);
        cinemas.sort(Comparator.comparing(Cinema::getCinemaName));
        theaterDropdown.removeAllItems();

        for (Cinema cinema : cinemas) {
            theaterDropdown.addItem(cinema.getCinemaName());
        }
    }

    private void populateShowTime(Cinema cinema) {
        timeDropdown.removeAllItems();
        showtimeMap.clear(); // clear previous showtimes

        Map<String, List<ShowTime>> allShowTimes = cinema.getAllShowTimesWithVersion();

        for (Map.Entry<String, List<ShowTime>> entry : allShowTimes.entrySet()) {
            String version = entry.getKey();
            List<ShowTime> showTimes = entry.getValue();

            for (ShowTime st : showTimes) {
                String display = version + ": " + st.getStartTime() + " - " + st.getEndTime();
                timeDropdown.addItem(display);
                showtimeMap.put(display, st); // store mapping
            }
        }

    }

    private void refreshSeatPanel(SeatSelectionPanel newPanel) {
        Container parent = seatPanel.getParent();
        parent.remove(seatPanel);
        seatPanel = newPanel;
        parent.add(seatPanel);
        parent.revalidate();
        parent.repaint();
    }

    private void updateSeatGridForCurrentSelection() {
        if (selectedMovie == null || selectedCinema == null || selectedShowtime == null)
            return;

        Set<String> unavailable = bookMovieController.getBookedSeats(
                selectedMovie,
                selectedCinema,
                selectedDate,
                selectedShowtime
        );

        SeatSelectionPanel newPanel = new SeatSelectionPanel(unavailable);
        refreshSeatPanel(newPanel);
    }
}
