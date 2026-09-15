package hms;

/** A digital medication prescription a Doctor issues to a patient's record. */
public class Prescription implements Persistable {

    private String prescriptionId;
    private String patientId;
    private String doctorId;
    private String doctorName;
    private String medication;
    private String dosage;
    private String instructions;
    private String date;

    public Prescription(String prescriptionId, String patientId, String doctorId, String doctorName,
                         String medication, String dosage, String instructions, String date) {
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.medication = medication;
        this.dosage = dosage;
        this.instructions = instructions;
        this.date = date;
    }

    public String getPrescriptionId() { return prescriptionId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public String getMedication() { return medication; }
    public String getDosage() { return dosage; }
    public String getInstructions() { return instructions; }
    public String getDate() { return date; }

    public String toFileLine() {
        return prescriptionId + "|" + patientId + "|" + doctorId + "|" + doctorName + "|" + medication
                + "|" + dosage + "|" + FileManager.escape(instructions) + "|" + date;
    }

    /** Returns null (instead of throwing) if the line is malformed, so a single
     *  corrupted row doesn't crash whichever dashboard loads this file. */
    public static Prescription fromFileLine(String line) {
        try {
            String[] p = line.split("\\|", -1);
            if (p.length < 7) return null;
            return new Prescription(p[0], p[1], p[2], p[3], p[4], p[5], FileManager.unescape(p[6]),
                    p.length > 7 ? p[7] : "");
        } catch (RuntimeException e) {
            return null;
        }
    }
}
