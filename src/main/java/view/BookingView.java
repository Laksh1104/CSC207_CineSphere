package view;

import interface_adapter.BookMovie.*;
import entity.*;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import com.toedter.calendar.JDateChooser;
import interface_adapter.BookingQuery;
import view.components.HeaderPanel;
import view.components.SeatSelectionPanel;

public class BookingView extends JPanel implements PropertyChangeListener {

    private final BookMovieViewModel viewModel;
    private BookMovieController controller;
    private final BookingQuery bookingQuery;


    // UI Components
    private JComboBox<String> movieDropdown;
    private JComboBox<String> cinemaDropdown;
    private JComboBox<String> timeDropdown;

    private JPanel seatPanelWrapper;
    private SeatSelectionPanel seatPanel;


    private String selectedMovieName;
    private Integer selectedMovieId;
    private String selectedCinemaName;
    private String selectedShowtimeDisplay;
    private String selectedShowtimeStart;
    private String selectedShowtimeEnd;
    private ScreenSwitchListener listener;

    private final Map<String, ShowTime> showtimeMap = new HashMap<>();
    private String selectedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    private static final Color COLOR = new Color(255, 255, 224);
    private static final int HEIGHT = 25;

    public BookingView(BookMovieViewModel vm, BookingQuery bookingQuery) {
        this.viewModel = vm;
        this.bookingQuery = bookingQuery;
        vm.addPropertyChangeListener(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(COLOR);

        add(Box.createVerticalStrut(10));
        HeaderPanel headerPanel = new HeaderPanel();
        headerPanel.setHomeAction(() -> {
            if (listener != null) {
                listener.onSwitchScreen("Home");
            }
        });
        headerPanel.setBookAction(() -> {
            if (listener != null) {
                listener.onSwitchScreen("Booking");
            }
        });
        add(headerPanel, BorderLayout.NORTH);

        setupSelectionPanel();
        setupSeatPanelWrapper();
        setupBookButton();
    }

    public void setScreenSwitchListener(ScreenSwitchListener listener) {
        this.listener = listener;
    }

    public void setBookMovieController(BookMovieController controller) {
        this.controller = controller;
    }


    // UI Setup

    private void setupSeatPanelWrapper() {
        seatPanelWrapper = new JPanel(new FlowLayout());
        seatPanelWrapper.setBackground(COLOR);
        add(seatPanelWrapper);
    }

    private void setupSelectionPanel() {

        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        selectionPanel.setBackground(COLOR);

        // Movie Dropdown
        movieDropdown = createDropdown(300);
        populateMovies();

        movieDropdown.addActionListener(e -> {
            clearSeatGrid();

            String movieName = (String) movieDropdown.getSelectedItem();
            if (isNullOrPlaceholder(movieName, "Select Movie")) return;
            selectedMovieName = movieName;

            Movie movie = getMovie(movieName);
            selectedMovieId = movie.getId();
            populateCinemas(selectedMovieId, selectedDate);
        });

        selectionPanel.add(labeled("Movie:", movieDropdown));


        // Date Picker
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(150, HEIGHT));
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setMinSelectableDate(new Date());
        dateChooser.setDate(new Date());

        dateChooser.addPropertyChangeListener("date", evt -> {
            clearSeatGrid();

            Date date = dateChooser.getDate();
            if (date == null) return;

            selectedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            if (selectedMovieId != null)
                populateCinemas(selectedMovieId, selectedDate);
        });

        selectionPanel.add(labeled("Date:", dateChooser));


        // Cinema Dropdown
        cinemaDropdown = createDropdown(300);
        cinemaDropdown.addItem("Select Cinema");

        cinemaDropdown.addActionListener(e -> {
            clearSeatGrid();

            String cinemaName = (String) cinemaDropdown.getSelectedItem();
            if (isNullOrPlaceholder(cinemaName, "Select Cinema")) return;


            if (cinemaName.equals("This film is not playing on this date.")) {
                populateShowTimes(null);
                return;
            }

            selectedCinemaName = cinemaName;
            Cinema cinema = getCinema(cinemaName, selectedMovieId, selectedDate);

            populateShowTimes(cinema);
        });

        selectionPanel.add(labeled("Cinema:", cinemaDropdown));


        // Time Dropdown
        timeDropdown = createDropdown(250);
        timeDropdown.addItem("Select Time");

        timeDropdown.addActionListener(e -> {
            clearSeatGrid();

        });

        selectionPanel.add(labeled("Time:", timeDropdown));


        // Select Button
        JButton select = new JButton("Select");
        select.addActionListener(e -> handleSelect());
        selectionPanel.add(select);

        add(selectionPanel);
    }


    private JPanel labeled(String text, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR);
        panel.add(new JLabel(text), BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private JComboBox<String> createDropdown(int width) {
        JComboBox<String> cb = new JComboBox<>();
        cb.setPreferredSize(new Dimension(width, HEIGHT));
        return cb;
    }


    // Select Handler
    private void handleSelect() {
        String movieName = (String) movieDropdown.getSelectedItem();
        String cinemaName = (String) cinemaDropdown.getSelectedItem();
        String timeDisplay = (String) timeDropdown.getSelectedItem();

        if (isInvalidSelection(movieName, cinemaName, timeDisplay)) return;

        ShowTime st = showtimeMap.get(timeDisplay);

        if (st == null) {
            warn("Invalid showtime selected.");
            return;
        }

        selectedShowtimeDisplay = timeDisplay;
        selectedShowtimeStart = st.getStartTime();
        selectedShowtimeEnd = st.getEndTime();

        // Update state
        BookMovieState state = viewModel.getState();
        state.setMovieName(selectedMovieName);
        state.setCinemaName(selectedCinemaName);
        state.setDate(selectedDate);
        state.setStartTime(selectedShowtimeStart);
        state.setEndTime(selectedShowtimeEnd);
        viewModel.setState(state);

        buildSeatGrid();
    }


    // Book Button

    private void setupBookButton() {
        JButton bookBtn = new JButton("Book Movie");
        bookBtn.setFont(new Font("Arial", Font.BOLD, 18));
        bookBtn.setBackground(Color.WHITE);

        bookBtn.addActionListener(e -> {

            if (seatPanel == null) {
                warn("Please click Select to load the seat map before booking.");
                return;
            }

            controller.execute(
                    selectedMovieName,
                    selectedCinemaName,
                    selectedDate,
                    selectedShowtimeStart + " - " + selectedShowtimeEnd,
                    seatPanel.getSelectedSeats()
            );
        });

        JPanel wrapper = new JPanel(new FlowLayout());
        wrapper.add(bookBtn);
        wrapper.setBackground(COLOR);
        add(wrapper);
    }


    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }


    // Seat Grid Methods

    private void clearSeatGrid() {
        seatPanelWrapper.removeAll();
        seatPanelWrapper.revalidate();
        seatPanelWrapper.repaint();
        seatPanel = null;
    }

    private void refreshSeatPanel(SeatSelectionPanel newPanel) {
        seatPanelWrapper.removeAll();
        seatPanel = newPanel;
        seatPanelWrapper.add(seatPanel);
        seatPanelWrapper.revalidate();
        seatPanelWrapper.repaint();
    }

    private void buildSeatGrid() {
        if (selectedMovieName == null || selectedCinemaName == null || selectedShowtimeDisplay == null) {
            warn("Please choose movie, date, cinema, and showtime first.");
            return;
        }

        List<Seat> seats = controller.loadSeatLayout(
                selectedMovieName, selectedCinemaName, selectedDate, selectedShowtimeStart, selectedShowtimeEnd
        );

        Set<String> unavailable = new HashSet<>();
        for (Seat s : seats)
            if (s.isBooked()) unavailable.add(s.getSeatName());

        refreshSeatPanel(new SeatSelectionPanel(unavailable));
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        BookMovieState state = (BookMovieState) evt.getNewValue();

        if (state.getBookingError() != null) {
            JOptionPane.showMessageDialog(this, state.getBookingError(),
                    "Booking Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (seatPanel == null) return;

        if (state.getBookingSuccessMessage() != null) {
            JOptionPane.showMessageDialog(this,
                    state.getBookingSuccessMessage(),
                    "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);

            state.setBookingSuccessMessage(null);
        }

        Set<String> newlyBooked = state.getSeats();
        if (newlyBooked != null && !newlyBooked.isEmpty()) {
            seatPanel.markSeatsAsUnavailable(newlyBooked);
        }
    }


    // Data Access Helpers

    private void populateMovies() {
        List<Movie> movies = bookingQuery.getMovies();
        movies.sort(Comparator.comparing(Movie::getTitle));

        movieDropdown.removeAllItems();
        movieDropdown.addItem("Select Movie");
        for (Movie m : movies) {
            movieDropdown.addItem(m.getTitle());
        }
    }

    private Movie getMovie(String name) {
        return bookingQuery.getMovies().stream()
                .filter(m -> m.getTitle().equals(name))
                .findFirst()
                .orElse(null);
    }

    private Cinema getCinema(String name, int filmId, String date) {
        return bookingQuery.getCinemas(filmId, date).stream()
                .filter(c -> c.getCinemaName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void populateCinemas(int filmId, String date) {
        List<Cinema> cinemas = bookingQuery.getCinemas(filmId, date);

        cinemaDropdown.removeAllItems();

        if (cinemas.isEmpty()) {
            cinemaDropdown.addItem("This film is not playing on this date.");
            return;
        }

        cinemas.sort(Comparator.comparing(Cinema::getCinemaName));
        for (Cinema c : cinemas)
            cinemaDropdown.addItem(c.getCinemaName());
    }

    private void populateShowTimes(Cinema cinema) {
        timeDropdown.removeAllItems();
        showtimeMap.clear();

        if (cinema == null) {
            timeDropdown.addItem("No showtime is available.");
            return;
        }

        Map<String, List<ShowTime>> grouped = bookingQuery.getShowtimes(cinema);

        for (var entry : grouped.entrySet()) {
            String version = entry.getKey();
            for (ShowTime st : entry.getValue()) {
                String display = version + ": " + st.getStartTime() + " - " + st.getEndTime();
                timeDropdown.addItem(display);
                showtimeMap.put(display, st);
            }
        }

        if (timeDropdown.getItemCount() == 0)
            timeDropdown.addItem("No showtime is available.");
    }

    private boolean isNullOrPlaceholder(String s, String placeholder) {
        return s == null || s.equals(placeholder);
    }

    private boolean isInvalidSelection(String movieName, String cinemaName, String timeDisplay) {
        if (isNullOrPlaceholder(movieName, "Select Movie")) {
            warn("Please select a movie.");
            return true;
        }
        if (isNullOrPlaceholder(cinemaName, "Select Cinema") ||
                cinemaName.equals("This film is not playing on this date.")) {
            warn("Please select a valid cinema.");
            return true;
        }
        if (isNullOrPlaceholder(timeDisplay, "Select Time") ||
                timeDisplay.equals("No showtime is available.")) {
            warn("Please select a valid showtime.");
            return true;
        }
        return false;
    }
}