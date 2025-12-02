package app;

import javax.swing.SwingUtilities;

/**
 * Main entry point for the CineSphere application.
 * Uses AppBuilder to construct and launch the application.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AppBuilder()
                    // Add views
                    .addLoginView()
                    .addLoggedInView()
                    .addFilteredView()
                    .addBookingView()
                    .addWatchlistView()
                    .addMyBookingsView()
                    // Add use cases
                    .addLoginUseCase()
                    .addSignupUseCase()
                    .addLogoutUseCase()
                    .addPopularMoviesUseCase()
                    .addSearchFilmUseCase()
                    .addFilterMoviesUseCase()
                    .addBookMovieUseCase()
                    .addWatchlistUseCase()
                    .addMovieDetailsUseCase()
                    .addBookingsUseCase()
                    // Build and show
                    .build()
                    .setVisible(true);
        });
    }
}