package hms;

/** Medical Manager role, associated with a department. */
public class MedicalManager extends User {

    private String department;

    public MedicalManager(String userId, String name, String username, String password, String department) {
        super(userId, name, username, password);
        this.department = department;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String getRole() {
        return "MedicalManager";
    }

    @Override
    public String toFileLine() {
        return super.toFileLine() + "|" + department;
    }
}
