package hms;

/** A Patient's star rating and comment for a completed doctor visit. */
public class Rating implements Persistable {

    private String ratingId;
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String doctorName;
    private int stars;      // 1-5
    private String comment;
    private String date;

    public Rating(String ratingId, String appointmentId, String patientId, String doctorId,
                   String doctorName, int stars, String comment, String date) {
        this.ratingId = ratingId;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.stars = stars;
        this.comment = comment;
        this.date = date;
    }

    public String getRatingId() { return ratingId; }
    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public int getStars() { return stars; }
    public String getComment() { return comment; }
    public String getDate() { return date; }

    public String toFileLine() {
        return ratingId + "|" + appointmentId + "|" + patientId + "|" + doctorId + "|" + doctorName
                + "|" + stars + "|" + FileManager.escape(comment) + "|" + date;
    }

    /** Returns null (instead of throwing) if the line is malformed, so a single
     *  corrupted row doesn't crash whichever dashboard loads this file. */
    public static Rating fromFileLine(String line) {
        try {
            String[] p = line.split("\\|", -1);
            if (p.length < 7) return null;
            return new Rating(p[0], p[1], p[2], p[3], p[4], Integer.parseInt(p[5]),
                    FileManager.unescape(p[6]), p.length > 7 ? p[7] : "");
        } catch (RuntimeException e) {
            return null;
        }
    }
}
