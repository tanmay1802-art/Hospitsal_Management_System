package hms;

/**
 * Concrete role: Doctor
 * fields/methods (consultation notes, prescriptions, etc). This version
 * only carries what the Admin module needs (specialty + which Medical
 * Manager the doctor reports to) so this part compiles and runs on its own.
 */
public class Doctor extends User {

    private String specialty;
    private String assignedManagerId; // userId of the Medical Manager this doctor reports to

    public Doctor(String userId, String name, String username, String password, String specialty) {
        super(userId, name, username, password);
        this.specialty = specialty;
        this.assignedManagerId = ""; // not assigned yet
    }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getAssignedManagerId() { return assignedManagerId; }
    public void setAssignedManagerId(String assignedManagerId) { this.assignedManagerId = assignedManagerId; }

    @Override
    public String getRole() {
        return "Doctor";
    }

    @Override
    public String toFileLine() {
        return super.toFileLine() + "|" + specialty + "|" + assignedManagerId;
    }
}
