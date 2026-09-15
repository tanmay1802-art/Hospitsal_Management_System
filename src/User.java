package hms;

/**
 * Base class for every person who can log into the HMS.
 * Fields are private and exposed only through getters/setters.
 */
public abstract class User implements Persistable {

    private String userId;
    private String name;
    private String username;
    private String password;

    public User(String userId, String name, String username, String password) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    /** Role label used e.g. when the admin table prints a mixed list of users. */
    public abstract String getRole();

    /** Converts the object into one pipe-delimited line for the .txt file. */
    public String toFileLine() {
        return userId + "|" + name + "|" + username + "|" + password + "|" + getRole();
    }

    @Override
    public String toString() {
        return getRole() + " | " + userId + " | " + name + " | " + username;
    }
}
