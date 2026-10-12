package exception;

// Custom exception: one simple class used for every booking problem
// (empty name, wrong ticket count, seat taken, invalid booking ID, DB failure).
public class BookingException extends Exception {

    public BookingException(String message) {
        super(message);              // super keyword: pass message to Exception
    }

    public BookingException(String message, Throwable cause) {
        super(message, cause);       // keeps the original error (e.g. SQLException)
    }
}
