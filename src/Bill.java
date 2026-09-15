package hms;

/** One bill generated for a completed appointment - base department rate plus a small surcharge if the visit came back Elevated/Critical. */
public class Bill implements Persistable {

    private String billId;
    private String appointmentId;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String department;
    private String date;
    private double baseRate;
    private String grade;      // Normal / Elevated / Critical / Unknown - from VitalsGrader
    private double totalAmount;

    public Bill(String billId, String appointmentId, String patientId, String patientName, String doctorId,
                String doctorName, String department, String date, double baseRate, String grade, double totalAmount) {
        this.billId = billId;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.department = department;
        this.date = date;
        this.baseRate = baseRate;
        this.grade = grade;
        this.totalAmount = totalAmount;
    }

    public String getBillId() { return billId; }
    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public String getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public String getDepartment() { return department; }
    public String getDate() { return date; }
    public double getBaseRate() { return baseRate; }
    public String getGrade() { return grade; }
    public double getTotalAmount() { return totalAmount; }

    public String toFileLine() {
        return billId + "|" + appointmentId + "|" + patientId + "|" + patientName + "|" + doctorId + "|"
                + doctorName + "|" + department + "|" + date + "|" + baseRate + "|" + grade + "|" + totalAmount;
    }

    /** Returns null (instead of throwing) if the line is malformed, so a single
     *  corrupted row doesn't crash whichever dashboard loads this file. */
    public static Bill fromFileLine(String line) {
        try {
            String[] p = line.split("\\|", -1);
            if (p.length < 11) return null;
            return new Bill(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7],
                    Double.parseDouble(p[8]), p[9], Double.parseDouble(p[10]));
        } catch (RuntimeException e) {
            return null;
        }
    }
}
