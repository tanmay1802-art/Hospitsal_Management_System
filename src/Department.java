package hms;

/**
 * A specialized clinical department (e.g. Cardiology, Pediatrics) that a
 * Medical Manager creates/updates. Separate from Doctor.specialty so a
 * manager can maintain department metadata independently of who is
 * currently assigned to it.
 */
public class Department implements Persistable {

    private String deptId;
    private String name;
    private String managerId;    // userId of the Medical Manager who owns this department
    private String description;

    public Department(String deptId, String name, String managerId, String description) {
        this.deptId = deptId;
        this.name = name;
        this.managerId = managerId;
        this.description = description;
    }

    public String getDeptId() { return deptId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String toFileLine() {
        return deptId + "|" + name + "|" + managerId + "|" + description;
    }

    /** Returns null (instead of throwing) if the line is malformed, so a single
     *  corrupted row doesn't crash whichever dashboard loads this file. */
    public static Department fromFileLine(String line) {
        try {
            String[] p = line.split("\\|", -1);
            if (p.length < 3) return null;
            return new Department(p[0], p[1], p[2], p.length > 3 ? p[3] : "");
        } catch (RuntimeException e) {
            return null;
        }
    }
}
