import data_access.TicketDataAccessObject;
import entity.MovieFactory;
import interface_adapter.BookMovie.*;
import interface_adapter.ViewManagerModel;
import use_case.book_movie.*;
import view.BookingView;

import javax.swing.*;

public class TestBookingView {
    public static void main(String[] args) {

        ViewManagerModel viewManagerModel = new ViewManagerModel();

        BookMovieViewModel bookMovieViewModel = new BookMovieViewModel();

        BookingView bookingView = new BookingView(bookMovieViewModel);

        MovieFactory movieFactory = new MovieFactory();

        TicketDataAccessObject ticketDataAccessObject = new TicketDataAccessObject();

        BookMoviePresenter presenter =
                new BookMoviePresenter(bookMovieViewModel);

        BookMovieInputBoundary interactor =
                new BookMovieInteractor(ticketDataAccessObject, presenter);

        BookMovieController controller =
                new BookMovieController(interactor);

        bookingView.setBookMovieController(controller);

        // Create a simple JFrame to show the view
        JFrame frame = new JFrame("Booking Test Harness");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.add(bookingView);
        frame.setVisible(true);
    }
}
