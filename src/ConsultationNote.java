package hms;

/** Vitals + free-text consultation notes a Doctor records for one appointment. */
public class ConsultationNote implements Persistable {

    private String noteId;
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String date;
    private String bloodPressure;
    private String temperature;
    private String pulse;
    private String notes;

    public ConsultationNote(String noteId, String appointmentId, String patientId, String doctorId,
                             String date, String bloodPressure, String temperature, String pulse, String notes) {
        this.noteId = noteId;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
        this.pulse = pulse;
        this.notes = notes;
    }

    public String getNoteId() { return noteId; }
    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getDate() { return date; }
    public String getBloodPressure() { return bloodPressure; }
    public String getTemperature() { return temperature; }
    public String getPulse() { return pulse; }
    public String getNotes() { return notes; }

    /** Normal / Elevated / Critical, worked out from the three vitals above. Not stored - recomputed each time. */
    public String getVitalsGrade() {
        return VitalsGrader.overallGrade(bloodPressure, temperature, pulse);
    }

    public String toFileLine() {
        return noteId + "|" + appointmentId + "|" + patientId + "|" + doctorId + "|" + date + "|"
                + bloodPressure + "|" + temperature + "|" + pulse + "|" + FileManager.escape(notes);
    }

    /** Returns null (instead of throwing) if the line is malformed, so a single
     *  corrupted row doesn't crash whichever dashboard loads this file. */
    public static ConsultationNote fromFileLine(String line) {
        try {
            String[] p = line.split("\\|", -1);
            if (p.length < 8) return null;
            return new ConsultationNote(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7],
                    p.length > 8 ? FileManager.unescape(p[8]) : "");
        } catch (RuntimeException e) {
            return null;
        }
    }
}
