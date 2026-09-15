package hms;

/**
 * A specific type of clinical assessment / check-up the hospital offers
 * (e.g. "Annual Physical", "Child Growth Check"), scoped to one department,
 * with an expected duration and a base fee.
 *
 * This is the "design assessment/check-up types" feature from section 2.0
 * of the project brief that wasn't implemented before. It follows the exact
 * same CRUD pattern already used for Department/ConsultationConfig: a model
 * class with toFileLine()/fromFileLine(), a FileManager load/save pair, and
 * a CRUD panel (see MedicalManagerDashboard.buildAssessmentPanel()).
 */
public class AssessmentType implements Persistable {

    private String assessmentId;
    private String name;
    private String department;
    private int durationMinutes;
    private double baseFee;

    public AssessmentType(String assessmentId, String name, String department, int durationMinutes, double baseFee) {
        this.assessmentId = assessmentId;
        this.name = name;
        this.department = department;
        this.durationMinutes = durationMinutes;
        this.baseFee = baseFee;
    }

    public String getAssessmentId() { return assessmentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public double getBaseFee() { return baseFee; }
    public void setBaseFee(double baseFee) { this.baseFee = baseFee; }

    public String toFileLine() {
        return assessmentId + "|" + name + "|" + department + "|" + durationMinutes + "|" + baseFee;
    }

    /** Returns null (instead of throwing) if the line is malformed, so a single
     *  corrupted row doesn't crash whichever dashboard loads this file. */
    public static AssessmentType fromFileLine(String line) {
        try {
            String[] p = line.split("\\|", -1);
            if (p.length < 5) return null;
            return new AssessmentType(p[0], p[1], p[2], Integer.parseInt(p[3]), Double.parseDouble(p[4]));
        } catch (RuntimeException e) {
            return null;
        }
    }
}
