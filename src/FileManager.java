package hms;

import java.io.*;
import java.util.*;

/*@author:(Tanmay)-Tp092959
 * Central place for all text-file reading/writing.
 * Requirement: "Students should use text files for storing and retrieving data." So in that case I used text files to store the data
 * All data lives under the /data folder next to the project.
 */
public class FileManager {

    private static final String DATA_DIR = "data";
    public static final String USERS_FILE = DATA_DIR + File.separator + "users.txt";
    public static final String ASSETS_FILE = DATA_DIR + File.separator + "assets.txt";
    public static final String CONFIG_FILE = DATA_DIR + File.separator + "config.txt";
    public static final String ASSIGN_FILE = DATA_DIR + File.separator + "assignments.txt";
    public static final String DEPT_FILE = DATA_DIR + File.separator + "departments.txt";
    public static final String ROSTER_FILE = DATA_DIR + File.separator + "shift_roster.txt";
    public static final String APPT_FILE = DATA_DIR + File.separator + "appointments.txt";
    public static final String NOTE_FILE = DATA_DIR + File.separator + "consult_notes.txt";
    public static final String RX_FILE = DATA_DIR + File.separator + "prescriptions.txt";
    public static final String LAB_FILE = DATA_DIR + File.separator + "lab_requests.txt";
    public static final String RATING_FILE = DATA_DIR + File.separator + "ratings.txt";
    public static final String BILL_FILE = DATA_DIR + File.separator + "bills.txt";
    public static final String ASSESSMENT_FILE = DATA_DIR + File.separator + "assessment_types.txt";

    /** Creates the data folder and seed files (with a default admin login) the first time the app runs. */
    public static void initialize() {
        new File(DATA_DIR).mkdirs();
        seedFileIfMissing(USERS_FILE, new String[] {
            "A001|System Admin(Tanmay_Sarkar)|admin|admin123|AdminStaff",
            "D001|Dr. Aisyah Rahman|doctor|doc123|Doctor|Cardiology|",
            "D002|Dr. Wei Ming Le|drwei|doc123|Doctor|Pediatrics|",
            "M001|Nurul Huda|nurulh|mgr123|MedicalManager|Cardiology",
            "P001|Ahmad Faiz|ahmadf|pat123|Patient|012-3456789"
        });
        seedFileIfMissing(ASSETS_FILE, new String[] {
            "P01|Consultation Room|Room 506|Level 5, Block A|Available",
            "P02|Ward|General Ward 5|Level 2, Block C|Available",
            "P03|Lab|Blood Test Lab|Level 4, Block B|Available",
            "P04|Imaging Room|X-Ray Room 2|Level 3, Block C|Available"
        });
        seedFileIfMissing(CONFIG_FILE, new String[] {
            "Cardiology|150.0|AIA,Allianz",
            "Pediatrics|100.0|AIA,Prudential,Allianz"
        });
        seedFileIfMissing(ASSIGN_FILE, new String[] {});
        seedFileIfMissing(DEPT_FILE, new String[] {
            "DPT001|Cardiology|M001|Heart and cardiovascular care",
            "DPT002|Pediatrics|M001|Child and adolescent care"
        });
        seedFileIfMissing(ROSTER_FILE, new String[] {
            "H01|P01|Monday|Morning (8:00 - 14:00)",
            "H02|P02|Wednesday|Afternoon (12:00 - 20:00)"
        });
        seedFileIfMissing(APPT_FILE, new String[] {});
        seedFileIfMissing(NOTE_FILE, new String[] {});
        seedFileIfMissing(RX_FILE, new String[] {});
        seedFileIfMissing(LAB_FILE, new String[] {});
        seedFileIfMissing(RATING_FILE, new String[] {});
        seedFileIfMissing(BILL_FILE, new String[] {});
        seedFileIfMissing(ASSESSMENT_FILE, new String[] {
            "AS001|Annual Physical|Cardiology|30|80.0",
            "AS002|Child Growth Check|Pediatrics|20|50.0"
        });
    }

    private static void seedFileIfMissing(String path, String[] lines) {
        File f = new File(path);
        if (!f.exists()) {
            writeAllLines(path, Arrays.asList(lines));
        }
    }

    public static List<String> readAllLines(String path) {
        List<String> lines = new ArrayList<>();
        File f = new File(path);
        if (!f.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) lines.add(line);
            }
        } catch (IOException e) {
            throw new DataAccessException("Could not read data file: " + path, e);
        }
        return lines;
    }

    public static void writeAllLines(String path, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, false))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new DataAccessException("Could not write data file: " + path, e);
        }
    }

    public static void appendLine(String path, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            throw new DataAccessException("Could not append to data file: " + path, e);
        }
    }

    // ---------- Free-text escaping ----------
    // Any JTextArea (consultation notes, prescription instructions, rating
    // comments, lab request notes, ...) can contain '|' or a newline, and
    // every entity is stored as one pipe-delimited line. Without escaping,
    // a note like "BP normal | patient anxious" would silently shift every
    // column after it out of place, and a literal newline would split one
    // logical record into two lines in the .txt file. escape()/unescape()
    // are the fix - call escape() when building a toFileLine() string for a
    // free-text field, and unescape() when reading that field back out in
    // fromFileLine().

    public static String escape(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\")
                   .replace("|", "\\p")
                   .replace("\r\n", "\\n")
                   .replace("\n", "\\n")
                   .replace("\r", "\\n");
    }

    public static String unescape(String escaped) {
        if (escaped == null) return "";
        StringBuilder out = new StringBuilder(escaped.length());
        for (int i = 0; i < escaped.length(); i++) {
            char c = escaped.charAt(i);
            if (c == '\\' && i + 1 < escaped.length()) {
                char next = escaped.charAt(i + 1);
                if (next == 'n') { out.append('\n'); i++; continue; }
                if (next == 'p') { out.append('|'); i++; continue; }
                if (next == '\\') { out.append('\\'); i++; continue; }
            }
            out.append(c);
        }
        return out.toString();
    }

    // ---------- Generic save for anything that implements Persistable ----------
    // Every save*(list) method below used to repeat the same three lines
    // (build a List<String>, call toFileLine() on each item, writeAllLines).
    // Routing them all through this one generic method is what actually
    // makes the Persistable interface pull its weight, rather than existing
    // just to tick a box.

    public static <T extends Persistable> void saveEntities(String path, List<T> entities) {
        List<String> lines = new ArrayList<>();
        for (T entity : entities) lines.add(entity.toFileLine());
        writeAllLines(path, lines);
    }

    // ---------- User-specific loading (turns raw text lines into objects, using POLYMORPHISM) ----------polymorphism are 2 types . compile time(Static Polymorphism or Early Binding) 
    // and run time polymorphism.

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        for (String line : readAllLines(USERS_FILE)) {
            try {
                String[] p = line.split("\\|", -1);
                if (p.length < 5) {
                    System.err.println("Skipping malformed user line: " + line);
                    continue;
                }
                String role = p[4];
                switch (role) {
                    case "AdminStaff":
                        users.add(new AdminStaff(p[0], p[1], p[2], p[3]));
                        break;
                    case "Doctor": {
                        Doctor d = new Doctor(p[0], p[1], p[2], p[3], p.length > 5 ? p[5] : "");
                        if (p.length > 6) d.setAssignedManagerId(p[6]);
                        users.add(d);
                        break;
                    }
                    case "MedicalManager":
                        users.add(new MedicalManager(p[0], p[1], p[2], p[3], p.length > 5 ? p[5] : ""));
                        break;
                    case "Patient":
                        users.add(new Patient(p[0], p[1], p[2], p[3], p.length > 5 ? p[5] : ""));
                        break;
                    default:
                        System.err.println("Skipping user line with unknown role: " + line);
                }
            } catch (RuntimeException e) {
                // One bad row (missing column, etc.) shouldn't take down the login screen.
                System.err.println("Skipping malformed user line: " + line + " (" + e.getMessage() + ")");
            }
        }

        // One-time migration: older data files (or this project's seed data) may still have
        // plaintext passwords. Upgrade any that aren't already a SHA-256 hash, then persist
        // the upgrade so it only has to happen once.
        boolean migrated = false;
        for (User u : users) {
            if (!PasswordUtil.isHashed(u.getPassword())) {
                u.setPassword(PasswordUtil.hash(u.getPassword()));
                migrated = true;
            }
        }
        if (migrated) saveUsers(users);

        return users;
    }

    public static void saveUsers(List<User> users) {
        saveEntities(USERS_FILE, users);
    }

    /**
     * Generates the next sequential ID for a role prefix (e.g. prefix "P" -> P001, P002...).
     * Scans existing users instead of counting them, so IDs never collide even if an
     * account in the middle was deleted. Used by registration/creation screens.
     */
    public static String nextUserId(List<User> users, String prefix) {
        int max = 0;
        for (User u : users) {
            String id = u.getUserId();
            if (id != null && id.startsWith(prefix)) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(prefix.length())));
                } catch (NumberFormatException ignored) {
                    // non-numeric suffix - skip
                }
            }
        }
        return String.format("%s%03d", prefix, max + 1);
    }

    public static List<HospitalAsset> loadAssets() {
        return loadList(ASSETS_FILE, HospitalAsset::fromFileLine);
    }

    public static void saveAssets(List<HospitalAsset> assets) {
        saveEntities(ASSETS_FILE, assets);
    }

    public static List<ConsultationConfig> loadConfigs() {
        return loadList(CONFIG_FILE, ConsultationConfig::fromFileLine);
    }

    public static void saveConfigs(List<ConsultationConfig> configs) {
        saveEntities(CONFIG_FILE, configs);
    }

    // ---------- Departments (Medical Manager) ----------

    public static List<Department> loadDepartments() {
        return loadList(DEPT_FILE, Department::fromFileLine);
    }

    public static void saveDepartments(List<Department> list) {
        saveEntities(DEPT_FILE, list);
    }

    // ---------- Shift roster (Medical Manager) ----------

    public static List<ShiftEntry> loadRoster() {
        return loadList(ROSTER_FILE, ShiftEntry::fromFileLine);
    }

    public static void saveRoster(List<ShiftEntry> list) {
        saveEntities(ROSTER_FILE, list);
    }

    // ---------- Appointments (Patient books, Doctor works from) ----------

    public static List<Appointment> loadAppointments() {
        return loadList(APPT_FILE, Appointment::fromFileLine);
    }

    public static void saveAppointments(List<Appointment> list) {
        saveEntities(APPT_FILE, list);
    }

    // ---------- Consultation notes (Doctor) ----------

    public static List<ConsultationNote> loadNotes() {
        return loadList(NOTE_FILE, ConsultationNote::fromFileLine);
    }

    public static void saveNotes(List<ConsultationNote> list) {
        saveEntities(NOTE_FILE, list);
    }

    // ---------- Prescriptions (Doctor issues, Patient views) ----------

    public static List<Prescription> loadPrescriptions() {
        return loadList(RX_FILE, Prescription::fromFileLine);
    }

    public static void savePrescriptions(List<Prescription> list) {
        saveEntities(RX_FILE, list);
    }

    // ---------- Lab / imaging requests (Doctor -> Admin) ----------

    public static List<LabRequest> loadLabRequests() {
        return loadList(LAB_FILE, LabRequest::fromFileLine);
    }

    public static void saveLabRequests(List<LabRequest> list) {
        saveEntities(LAB_FILE, list);
    }

    // ---------- Ratings (Patient -> Doctor) ----------

    public static List<Rating> loadRatings() {
        return loadList(RATING_FILE, Rating::fromFileLine);
    }

    public static void saveRatings(List<Rating> list) {
        saveEntities(RATING_FILE, list);
    }

    // ---------- Bills (generated when a Doctor completes a visit) ----------

    public static List<Bill> loadBills() {
        return loadList(BILL_FILE, Bill::fromFileLine);
    }

    public static void saveBills(List<Bill> list) {
        saveEntities(BILL_FILE, list);
    }

    // ---------- Assessment / check-up types (Medical Manager) ----------

    public static List<AssessmentType> loadAssessmentTypes() {
        return loadList(ASSESSMENT_FILE, AssessmentType::fromFileLine);
    }

    public static void saveAssessmentTypes(List<AssessmentType> list) {
        saveEntities(ASSESSMENT_FILE, list);
    }

    /**
     * Shared loop for every "read the file, parse each line, skip bad ones" loader above.
     * Each entity's fromFileLine() now returns null (instead of throwing) on a malformed
     * line, so one corrupted row just gets logged and skipped instead of crashing whichever
     * dashboard triggered the load.
     */
    private static <T> List<T> loadList(String path, java.util.function.Function<String, T> parser) {
        List<T> list = new ArrayList<>();
        for (String line : readAllLines(path)) {
            T item = parser.apply(line);
            if (item != null) {
                list.add(item);
            } else {
                System.err.println("Skipping malformed line in " + path + ": " + line);
            }
        }
        return list;
    }

    /** Generic next-id helper for the new entity files (prefix -> PREFIX001, PREFIX002...). */
    public static String nextId(List<String> existingIds, String prefix) {
        return nextId(existingIds, prefix, 3);
    }

    /**
     * Overload of nextId() that lets the caller choose how many digits to zero-pad to,
     * e.g. nextId(ids, "INV", 5) -> "INV00001" for IDs that need more headroom than the
     * default 3-digit width.
     */
    public static String nextId(List<String> existingIds, String prefix, int padWidth) {
        int max = 0;
        for (String id : existingIds) {
            if (id != null && id.startsWith(prefix)) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(prefix.length())));
                } catch (NumberFormatException ignored) { }
            }
        }
        return String.format("%s%0" + padWidth + "d", prefix, max + 1);
    }
}
