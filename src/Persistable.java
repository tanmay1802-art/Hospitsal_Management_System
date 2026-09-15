package hms;

/**
 * Contract shared by every entity that can be written to / read from one of
 * the pipe-delimited .txt files under /data (Appointment, Bill, Rating, ...).
 * Pulling this out as an interface (rather than just relying on every class
 * happening to have a toFileLine() method) lets FileManager work against the
 * interface type instead of one bespoke save method per entity - see
 * FileManager.saveEntities(...).
 */
public interface Persistable {
    /** Converts this object into one pipe-delimited line for its .txt file. */
    String toFileLine();
}
