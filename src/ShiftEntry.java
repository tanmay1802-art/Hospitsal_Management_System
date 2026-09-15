package hms;

/**
 * One row of a doctor's operational shift roster, set by their Medical
 * Manager (e.g. Dr. Wei Ming Lee | Monday | Morning 8:00-14:00).
 */
public class ShiftEntry implements Persistable {

    private String shiftId;
    private String doctorId;
    private String dayOfWeek;   // Monday..Sunday
    private String shiftLabel;  // e.g. "Morning (8:00-14:00)"

    public ShiftEntry(String shiftId, String doctorId, String dayOfWeek, String shiftLabel) {
        this.shiftId = shiftId;
        this.doctorId = doctorId;
        this.dayOfWeek = dayOfWeek;
        this.shiftLabel = shiftLabel;
    }

    public String getShiftId() { return shiftId; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public String getShiftLabel() { return shiftLabel; }
    public void setShiftLabel(String shiftLabel) { this.shiftLabel = shiftLabel; }

    public String toFileLine() {
        return shiftId + "|" + doctorId + "|" + dayOfWeek + "|" + shiftLabel;
    }

    /** Returns null (instead of throwing) if the line is malformed, so a single
     *  corrupted row doesn't crash whichever dashboard loads this file. */
    public static ShiftEntry fromFileLine(String line) {
        try {
            String[] p = line.split("\\|", -1);
            if (p.length < 3) return null;
            return new ShiftEntry(p[0], p[1], p[2], p.length > 3 ? p[3] : "");
        } catch (RuntimeException e) {
            return null;
        }
    }
}
