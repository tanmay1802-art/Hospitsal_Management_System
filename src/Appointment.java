package hms;

/**
 * A booked consultation slot. Created by a Patient (browse/book), can be
 * rescheduled or cancelled by the Patient, and is what a Doctor works from
 * to log vitals/notes and mark the visit Completed.
 *
 * status: "Confirmed" | "Completed" | "Cancelled"
 */
public class Appointment implements Persistable {

    private String appointmentId;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String date;      // yyyy-MM-dd
    private String timeSlot;  // e.g. "09:00 - 09:30"
    private String status;
    private String reason;

    public Appointment(String appointmentId, String patientId, String patientName, String doctorId,
                        String doctorName, String date, String timeSlot, String status, String reason) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.date = date;
        this.timeSlot = timeSlot;
        this.status = status;
        this.reason = reason;
    }

    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String toFileLine() {
        return appointmentId + "|" + patientId + "|" + patientName + "|" + doctorId + "|" + doctorName
                + "|" + date + "|" + timeSlot + "|" + status + "|" + reason;
    }

    /** Returns null (instead of throwing) if the line is malformed, so a single
     *  corrupted row doesn't crash whichever dashboard loads this file. */
    public static Appointment fromFileLine(String line) {
        try {
            String[] p = line.split("\\|", -1);
            if (p.length < 8) return null;
            return new Appointment(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p.length > 8 ? p[8] : "");
        } catch (RuntimeException e) {
            return null;
        }
    }
}
