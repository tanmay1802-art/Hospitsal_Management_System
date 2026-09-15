package hms;

/** Concrete role: Administrative Staff  */
public class AdminStaff extends User {

    public AdminStaff(String userId, String name, String username, String password) {
        super(userId, name, username, password);
    }

    @Override
    public String getRole() {
        return "AdminStaff";
    }
}
