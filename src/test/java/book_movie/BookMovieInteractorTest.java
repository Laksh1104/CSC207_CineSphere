package book_movie;

import org.junit.jupiter.api.Test;
import use_case.book_movie.BookMovieInputData;
import use_case.book_movie.BookMovieInteractor;
import use_case.book_movie.BookMovieOutputBoundary;
import use_case.book_movie.BookMovieOutputData;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookMovieInteractorTest {

    private BookMovieOutputBoundary successPresenter(
            String movieName, String cinemaName, String date,
            String start, String end, Set<String> seats, int cost)
    {
        return new BookMovieOutputBoundary() {
            @Override
            public void prepareSuccessView(BookMovieOutputData data) {
                assertEquals(movieName, data.getMovieName());
                assertEquals(cinemaName, data.getCinemaName());
                assertEquals(date, data.getDate());
                assertEquals(start, data.getStartTime());
                assertEquals(end, data.getEndTime());
                assertEquals(seats, data.getSeats());
                assertEquals(cost, data.getTotalCost());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Unexpected failure: " + error);
            }
        };
    }


    private BookMovieOutputBoundary failPresenter(String expectedError) {
        return new BookMovieOutputBoundary() {
            @Override
            public void prepareSuccessView(BookMovieOutputData data) {
                fail("Unexpected success");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals(expectedError, error);
            }
        };
    }

    // Tests
    @Test
    void successTest() {
        InMemoryTicketTestDAO dao = new InMemoryTicketTestDAO();

        BookMovieInputData input = new BookMovieInputData(
                "Stargate", "Cinema 2", "2025-11-28",
                "21:30 - 23:00", Set.of("A1", "A2")
        );

        BookMovieOutputBoundary presenter = successPresenter(
                "Stargate", "Cinema 2", "2025-11-28",
                "21:30", "23:00", Set.of("A1", "A2"), 40
        );

        BookMovieInteractor interactor = new BookMovieInteractor(dao, presenter);
        interactor.execute(input);

        assertTrue(dao.bookedSeats.contains("A1"));
        assertTrue(dao.bookedSeats.contains("A2"));
    }

    @Test
    void missingMovie() {
        InMemoryTicketTestDAO dao = new InMemoryTicketTestDAO();

        BookMovieInputData input = new BookMovieInputData(
                null, "Cinema 2", "2025-11-28", "21:30 - 23:00", Set.of("A1")
        );

        BookMovieInteractor interactor = new BookMovieInteractor(dao, failPresenter("Some booking details are missing."));
        interactor.execute(input);
    }

    @Test
    void missingCinema() {
        InMemoryTicketTestDAO dao = new InMemoryTicketTestDAO();

        BookMovieInputData input = new BookMovieInputData(
                "Stargate", null, "2025-11-28", "21:30 - 23:00", Set.of("A1")
        );

        BookMovieInteractor interactor = new BookMovieInteractor(dao, failPresenter("Some booking details are missing."));
        interactor.execute(input);
    }

    @Test
    void missingDate() {
        InMemoryTicketTestDAO dao = new InMemoryTicketTestDAO();

        BookMovieInputData input = new BookMovieInputData(
                "Stargate", "Cinema 2", null, "21:30 - 23:00", Set.of("A1")
        );

        BookMovieInteractor interactor =
                new BookMovieInteractor(dao, failPresenter("Some booking details are missing."));
        interactor.execute(input);
    }

    @Test
    void missingEndTime() {
        InMemoryTicketTestDAO dao = new InMemoryTicketTestDAO();

        BookMovieInputData input = new BookMovieInputData(
                "Stargate", "Cinema 2", "2025-11-28", "21:30 - 23:00", Set.of("A1")
        );
        // modify input to only provide startTime and no endTime
        input = new BookMovieInputData("Stargate", "Cinema 2", "2025-11-28", null, Set.of("A1"));

        BookMovieInteractor interactor =
                new BookMovieInteractor(dao, failPresenter("Some booking details are missing."));
        interactor.execute(input);
    }

    @Test
    void missingStartTime() {
        InMemoryTicketTestDAO dao = new InMemoryTicketTestDAO();

        BookMovieInputData input = new BookMovieInputData(
                "Stargate", "Cinema 2", "2025-11-28", null, Set.of("A1")
        );

        BookMovieInteractor interactor = new BookMovieInteractor(dao, failPresenter("Some booking details are missing."));
        interactor.execute(input);
    }


    @Test
    void emptySeatSelectionTest() {
        InMemoryTicketTestDAO dao = new InMemoryTicketTestDAO();

        BookMovieInputData input = new BookMovieInputData(
                "Dune", "Cinema 10", "2025-10-10",
                "18:00 - 20:00", Set.of()
        );

        BookMovieInteractor interactor = new BookMovieInteractor(dao, failPresenter("No seats were selected."));
        interactor.execute(input);
    }

    @Test
    void seatAlreadyBookedTest() {
        InMemoryTicketTestDAO dao = new InMemoryTicketTestDAO();
        dao.bookedSeats.add("A1"); // pre-booked

        BookMovieInputData input = new BookMovieInputData(
                "Matrix", "Cinema X", "2025-12-01",
                "19:00 - 21:00", Set.of("A1", "B1")
        );

        BookMovieOutputBoundary presenter = failPresenter("Seat A1 is already booked.");

        BookMovieInteractor interactor = new BookMovieInteractor(dao, presenter);
        interactor.execute(input);
    }

    @Test
    void multipleBookings() {
        InMemoryTicketTestDAO dao = new InMemoryTicketTestDAO();

        BookMovieInteractor interactor = new BookMovieInteractor(
                dao,
                successPresenter("Movie", "Cinema", "2025-01-01", "10:00", "12:00", Set.of("A1"), 20)
        );

        // First booking
        interactor.execute(new BookMovieInputData("Movie","Cinema","2025-01-01","10:00 - 12:00", Set.of("A1")));

        // Second booking
        BookMovieInteractor interactor2 = new BookMovieInteractor(dao,successPresenter("Movie", "Cinema", "2025-01-01", "10:00", "12:00", Set.of("A2"), 20)
        );

        interactor2.execute(new BookMovieInputData("Movie","Cinema","2025-01-01","10:00 - 12:00", Set.of("A2")));

        assertEquals(Set.of("A1","A2"), dao.bookedSeats);
    }

    @Test
    void loadSeatLayoutPassthroughTest() {
        InMemoryTicketTestDAO dao = new InMemoryTicketTestDAO();

        // DAO already has default seats: A1, A2, A3, B1
        BookMovieInteractor interactor = new BookMovieInteractor(dao, failPresenter(""));

        var result = interactor.loadSeatLayout(
                "Movie", "Cinema", "2025-01-01", "10:00", "12:00"
        );

        assertEquals(4, result.size());
        assertEquals("A1", result.get(0).getSeatName());
    }

    @Test
    void getBookedSeatsPassthroughTest() {
        InMemoryTicketTestDAO dao = new InMemoryTicketTestDAO();
        dao.bookedSeats.add("A1");

        BookMovieInteractor interactor = new BookMovieInteractor(dao, failPresenter(""));

        Set<String> result = interactor.getBookedSeats(
                "Movie", "Cinema", "2025-01-01", "10:00", "12:00"
        );

        assertEquals(Set.of("A1"), result);
    }
}
