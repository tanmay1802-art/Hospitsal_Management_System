package hms;

/** Patient role, holding the patient's contact number. */
public class Patient extends User {

    private String contactNumber;

    public Patient(String userId, String name, String username, String password, String contactNumber) {
        super(userId, name, username, password);
        this.contactNumber = contactNumber;
    }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    @Override
    public String getRole() {
        return "Patient";
    }

    @Override
    public String toFileLine() {
        return super.toFileLine() + "|" + contactNumber;
    }
}
