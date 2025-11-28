import data_access.InMemoryTicketDataAccessObject;
import entity.MovieFactory;
import interface_adapter.BookMovie.*;
import interface_adapter.ViewManagerModel;
import use_case.book_movie.*;
import view.BookingView;

import javax.swing.*;

public class TestBookingView {
    public static void main(String[] args) {

        BookMovieViewModel bookMovieViewModel = new BookMovieViewModel();

        BookingView bookingView = new BookingView(bookMovieViewModel);

        InMemoryTicketDataAccessObject inMemoryTicketDataAccessObject = new InMemoryTicketDataAccessObject();

        BookMoviePresenter presenter =
                new BookMoviePresenter(bookMovieViewModel);

        BookMovieInputBoundary interactor =
                new BookMovieInteractor(inMemoryTicketDataAccessObject, presenter);

        BookMovieController controller =
                new BookMovieController(interactor);

        bookingView.setBookMovieController(controller);

        // Create a simple JFrame to show the view
        JFrame frame = new JFrame("Booking Test Harness");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.add(bookingView);
        frame.setVisible(true);
    }
}
