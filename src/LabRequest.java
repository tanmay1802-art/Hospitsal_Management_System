package hms;

/**
 * A Doctor's request to Admin for a lab test, X-ray, or specialized imaging
 * for a patient. status: "Pending" | "Completed".
 */
public class LabRequest implements Persistable {

    private String requestId;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String testType;
    private String notes;
    private String status;
    private String date;

    public LabRequest(String requestId, String patientId, String patientName, String doctorId,
                       String doctorName, String testType, String notes, String status, String date) {
        this.requestId = requestId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.testType = testType;
        this.notes = notes;
        this.status = status;
        this.date = date;
    }

    public String getRequestId() { return requestId; }
    public String getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public String getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public String getTestType() { return testType; }
    public String getNotes() { return notes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDate() { return date; }

    public String toFileLine() {
        return requestId + "|" + patientId + "|" + patientName + "|" + doctorId + "|" + doctorName
                + "|" + testType + "|" + FileManager.escape(notes) + "|" + status + "|" + date;
    }

    /** Returns null (instead of throwing) if the line is malformed, so a single
     *  corrupted row doesn't crash whichever dashboard loads this file. */
    public static LabRequest fromFileLine(String line) {
        try {
            String[] p = line.split("\\|", -1);
            if (p.length < 8) return null;
            return new LabRequest(p[0], p[1], p[2], p[3], p[4], p[5], FileManager.unescape(p[6]), p[7],
                    p.length > 8 ? p[8] : "");
        } catch (RuntimeException e) {
            return null;
        }
    }
}
