package hms;

/**
 * Thrown when something goes wrong reading or writing one of the .txt data
 * files (or another persistence-layer operation, e.g. hashing a password).
 *
 * This is deliberately an unchecked exception: FileManager is called from
 * dozens of places across every dashboard, and forcing a checked exception
 * onto every one of those call sites would be a huge, risky change for a
 * small project like this. Leaving it unchecked still lets any caller that
 * *does* want to react (see Main's uncaught-exception handler, which shows
 * a JOptionPane instead of the old behaviour of printing to stderr and
 * silently continuing) catch DataAccessException specifically.
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
