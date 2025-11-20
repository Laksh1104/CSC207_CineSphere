    package interface_adapter.BookMovie;

    import interface_adapter.ViewModel;

    public class BookMovieViewModel extends ViewModel<BookMovieState>  {

        public BookMovieViewModel() {
            super("book movie");
            setState(new BookMovieState());
        }

        @Override
        public void setState(BookMovieState state) {
            super.setState(state);
            firePropertyChange();
        }
    }