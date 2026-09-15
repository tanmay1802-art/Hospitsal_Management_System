package hms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * Patient dashboard. Implements all 4 Patient functionalities from Table 1.0:
 *   1) Edit personal / individual profile
 *   2) Browse available slots of doctors' consultations and book/reschedule/cancel
 *   3) View personal medical history and prescriptions
 *   4) Submit ratings and comments to doctors and clinic visits
 */
public class PatientDashboard extends JFrame {

    private static final String[] TIME_SLOTS = {
            "09:00 - 09:30", "09:30 - 10:00", "10:00 - 10:30", "10:30 - 11:00",
            "14:00 - 14:30", "14:30 - 15:00", "15:00 - 15:30", "15:30 - 16:00"
    };

    private final Patient loggedInPatient;
    private List<User> userList;
    private List<Appointment> apptList;
    private List<ConsultationNote> noteList;
    private List<Prescription> rxList;
    private List<Rating> ratingList;
    private List<Bill> billList;

    // ---- Navigation ----
    private CardLayout cardLayout;
    private JPanel contentCards;
    private JLabel sectionTitleLabel;
    private NavButton navProfile, navBook, navHistory, navRating;

    // ---- Profile tab ----
    private JTextField profileNameField, profileContactField;
    private JPasswordField profilePasswordField;

    // ---- Book/Reschedule/Cancel tab ----
    private DefaultTableModel myApptTableModel;
    private JTable myApptTable;
    private JComboBox<String> bookDoctorCombo;
    private JTextField bookDateField;
    private JComboBox<String> bookSlotCombo;
    private JTextField bookReasonField;
    private String selectedApptIdForReschedule;

    // ---- History tab ----
    private DefaultTableModel historyApptModel;
    private DefaultTableModel historyRxModel;

    // ---- Ratings tab ----
    private JComboBox<String> ratingApptCombo;
    private JComboBox<Integer> ratingStarsCombo;
    private JTextArea ratingCommentArea;
    private DefaultTableModel ratingTableModel;

    public PatientDashboard(Patient patient) {
        this.loggedInPatient = patient;
        this.userList = FileManager.loadUsers();
        this.apptList = FileManager.loadAppointments();
        this.noteList = FileManager.loadNotes();
        this.rxList = FileManager.loadPrescriptions();
        this.billList = FileManager.loadBills();
        this.ratingList = FileManager.loadRatings();

        setTitle("HMS - Patient Portal (" + patient.getName() + ")");
        setSize(1060, 680);
        setMinimumSize(new Dimension(900, 580));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BACKGROUND);
        setContentPane(root);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContentArea(), BorderLayout.CENTER);

        showSection("profile");
    }

    // ================= SHELL =================

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.PRIMARY_DARK);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 12, 16, 12));

        JLabel logo = new JLabel("Hospital_Management_System");
        logo.setFont(UITheme.FONT_LOGO);
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 6, 2, 0));

        JLabel role = new JLabel("Patient Portal");
        role.setFont(UITheme.FONT_SUBTITLE);
        role.setForeground(new Color(0xB9, 0xD6, 0xC6));
        role.setAlignmentX(Component.LEFT_ALIGNMENT);
        role.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(true);
        card.setBackground(UITheme.PRIMARY);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(206, 54));
        card.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JLabel nameLbl = new JLabel(loggedInPatient.getName());
        nameLbl.setFont(UITheme.FONT_NAV_BOLD);
        nameLbl.setForeground(Color.WHITE);
        JLabel idLbl = new JLabel(loggedInPatient.getUserId() + " - Patient");
        idLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        idLbl.setForeground(new Color(0xC9, 0xE0, 0xD3));
        card.add(nameLbl);
        card.add(idLbl);

        navProfile = new NavButton(NavButton.Icon.PROFILE, "My Profile");
        navBook = new NavButton(NavButton.Icon.CALENDAR, "Book Appointment");
        navHistory = new NavButton(NavButton.Icon.RECORDS, "Medical History");
        navRating = new NavButton(NavButton.Icon.STAR, "Rate a Visit");
        NavButton navLogout = new NavButton(NavButton.Icon.LOGOUT, "Log Out");

        for (NavButton nb : new NavButton[]{navProfile, navBook, navHistory, navRating, navLogout}) {
            nb.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        navProfile.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("profile"); }
        });
        navBook.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("book"); }
        });
        navHistory.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("history"); }
        });
        navRating.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("rating"); }
        });
        navLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(PatientDashboard.this,
                        "Log out and return to the sign-in screen?", "Confirm Log Out", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginFrame().setVisible(true);
                }
            }
        });

        sidebar.add(logo);
        sidebar.add(role);
        sidebar.add(Box.createRigidArea(new Dimension(0, 18)));
        sidebar.add(card);
        sidebar.add(Box.createRigidArea(new Dimension(0, 22)));
        sidebar.add(navProfile);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(navBook);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(navHistory);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(navRating);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(navLogout);
        return sidebar;
    }

    private JPanel buildContentArea() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BACKGROUND);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BACKGROUND);
        header.setBorder(BorderFactory.createEmptyBorder(20, 26, 6, 26));
        sectionTitleLabel = new JLabel("My Profile");
        sectionTitleLabel.setFont(UITheme.FONT_TITLE);
        sectionTitleLabel.setForeground(UITheme.PRIMARY_DARK);
        header.add(sectionTitleLabel, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setOpaque(false);
        contentCards.setBorder(BorderFactory.createEmptyBorder(10, 26, 20, 26));

        contentCards.add(buildProfilePanel(), "profile");
        contentCards.add(buildBookPanel(), "book");
        contentCards.add(buildHistoryPanel(), "history");
        contentCards.add(buildRatingPanel(), "rating");

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(contentCards, BorderLayout.CENTER);
        return wrapper;
    }

    private void showSection(String key) {
        cardLayout.show(contentCards, key);
        navProfile.setSelected("profile".equals(key));
        navBook.setSelected("book".equals(key));
        navHistory.setSelected("history".equals(key));
        navRating.setSelected("rating".equals(key));
        switch (key) {
            case "profile": sectionTitleLabel.setText("My Profile"); break;
            case "book": sectionTitleLabel.setText("Book / Reschedule / Cancel Appointment"); break;
            case "history": sectionTitleLabel.setText("Medical History & Prescriptions"); break;
            case "rating": sectionTitleLabel.setText("Rate a Doctor / Visit"); break;
        }
        if ("book".equals(key)) { refreshBookDoctorCombo(); refreshMyApptTable(); refreshAvailableSlots(); }
        if ("history".equals(key)) { refreshHistory(); }
        if ("rating".equals(key)) { refreshRatingApptCombo(); refreshRatingTable(); }
    }

    private JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_LABEL);
        l.setForeground(UITheme.TEXT_DARK);
        return l;
    }

    private List<Doctor> allDoctors() {
        List<Doctor> list = new ArrayList<>();
        for (User u : userList) if (u instanceof Doctor) list.add((Doctor) u);
        return list;
    }

    // ================= 1) EDIT PERSONAL PROFILE =================

    private JPanel buildProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        JPanel formCard = UITheme.card();
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
        formCard.setPreferredSize(new Dimension(480, 300));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 14));
        form.setOpaque(false);
        form.setBorder(UITheme.sectionTitle("Account Details"));

        profileNameField = new JTextField(loggedInPatient.getName());
        profilePasswordField = new JPasswordField(loggedInPatient.getPassword());
        profileContactField = new JTextField(loggedInPatient.getContactNumber());
        UITheme.styleTextField(profileNameField);
        UITheme.styleTextField(profilePasswordField);
        UITheme.styleTextField(profileContactField);

        form.add(formLabel("User ID:"));
        form.add(formLabel(loggedInPatient.getUserId()));
        form.add(formLabel("Username:"));
        form.add(formLabel(loggedInPatient.getUsername()));
        form.add(formLabel("Full Name:"));
        form.add(profileNameField);
        form.add(formLabel("Password:"));
        form.add(profilePasswordField);
        form.add(formLabel("Contact Number:"));
        form.add(profileContactField);

        JButton saveBtn = UITheme.primaryButton("Save Changes");
        saveBtn.addActionListener(e -> saveProfile());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.add(saveBtn);

        formCard.add(form, BorderLayout.CENTER);
        formCard.add(btnPanel, BorderLayout.SOUTH);
        panel.add(formCard);
        return panel;
    }

    private void saveProfile() {
        if (profileNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        loggedInPatient.setName(profileNameField.getText().trim());
        loggedInPatient.setPassword(PasswordUtil.resolvePassword(loggedInPatient.getPassword(), new String(profilePasswordField.getPassword())));
        loggedInPatient.setContactNumber(profileContactField.getText().trim());
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserId().equals(loggedInPatient.getUserId())) {
                userList.set(i, loggedInPatient);
                break;
            }
        }
        FileManager.saveUsers(userList);
        JOptionPane.showMessageDialog(this, "Profile updated.");
    }

    // ================= 2) BOOK / RESCHEDULE / CANCEL =================

    private JPanel buildBookPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        myApptTableModel = new DefaultTableModel(new String[]{"Appt ID", "Doctor", "Date", "Time", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        myApptTable = new JTable(myApptTableModel);
        UITheme.styleTable(myApptTable);
        myApptTable.getSelectionModel().addListSelectionListener(e -> loadSelectedApptForReschedule());

        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        tableCard.add(new JScrollPane(myApptTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(UITheme.sectionTitle("New Booking (select a row above, then Reschedule/Cancel instead of Book)"));

        bookDoctorCombo = new JComboBox<>();
        bookDateField = new JTextField(LocalDate.now().plusDays(1).toString());
        bookSlotCombo = new JComboBox<>();
        bookReasonField = new JTextField();
        UITheme.styleComboBox(bookDoctorCombo);
        UITheme.styleTextField(bookDateField);
        UITheme.styleComboBox(bookSlotCombo);
        UITheme.styleTextField(bookReasonField);

        bookDoctorCombo.addActionListener(e -> refreshAvailableSlots());
        bookDateField.addActionListener(e -> refreshAvailableSlots());

        form.add(formLabel("Doctor:"));
        form.add(bookDoctorCombo);
        form.add(formLabel("Date (YYYY-MM-DD):"));
        form.add(bookDateField);
        form.add(formLabel("Available Time Slot:"));
        form.add(bookSlotCombo);
        form.add(formLabel("Reason for Visit:"));
        form.add(bookReasonField);

        JButton bookBtn = UITheme.primaryButton("Book Appointment");
        JButton rescheduleBtn = UITheme.secondaryButton("Reschedule Selected");
        JButton cancelBtn = UITheme.dangerButton("Cancel Selected");
        JButton clearBtn = UITheme.ghostButton("Clear Form");
        bookBtn.addActionListener(e -> bookAppointment());
        rescheduleBtn.addActionListener(e -> rescheduleAppointment());
        cancelBtn.addActionListener(e -> cancelAppointment());
        clearBtn.addActionListener(e -> clearBookForm());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.add(bookBtn);
        btnPanel.add(rescheduleBtn);
        btnPanel.add(cancelBtn);
        btnPanel.add(clearBtn);

        JPanel formCard = UITheme.card();
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));
        formCard.add(form, BorderLayout.CENTER);
        formCard.add(btnPanel, BorderLayout.SOUTH);

        panel.add(tableCard, BorderLayout.CENTER);
        panel.add(formCard, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshBookDoctorCombo() {
        String current = (String) bookDoctorCombo.getSelectedItem();
        bookDoctorCombo.removeAllItems();
        for (Doctor d : allDoctors()) {
            bookDoctorCombo.addItem(d.getUserId() + " - Dr. " + d.getName() + " (" + d.getSpecialty() + ")");
        }
        if (current != null) bookDoctorCombo.setSelectedItem(current);
    }

    private void refreshMyApptTable() {
        apptList = FileManager.loadAppointments();
        myApptTableModel.setRowCount(0);
        for (Appointment a : apptList) {
            if (a.getPatientId().equals(loggedInPatient.getUserId())) {
                myApptTableModel.addRow(new Object[]{a.getAppointmentId(), a.getDoctorName(), a.getDate(), a.getTimeSlot(), a.getStatus()});
            }
        }
    }

    private String selectedBookDoctorId() {
        String sel = (String) bookDoctorCombo.getSelectedItem();
        if (sel == null) return null;
        return sel.split(" - ", 2)[0];
    }

    /** Refills the time-slot combo with only slots NOT already taken for the chosen doctor+date ("browse available slots"). */
    private void refreshAvailableSlots() {
        if (bookSlotCombo == null) return;
        bookSlotCombo.removeAllItems();
        String doctorId = selectedBookDoctorId();
        String date = bookDateField.getText().trim();
        if (doctorId == null || date.isEmpty()) return;
        List<String> taken = new ArrayList<>();
        for (Appointment a : apptList) {
            if (a.getDoctorId().equals(doctorId) && a.getDate().equals(date) && !"Cancelled".equals(a.getStatus())) {
                taken.add(a.getTimeSlot());
            }
        }
        for (String slot : TIME_SLOTS) {
            if (!taken.contains(slot)) bookSlotCombo.addItem(slot);
        }
        if (bookSlotCombo.getItemCount() == 0) bookSlotCombo.addItem("(fully booked that day)");
    }

    private void loadSelectedApptForReschedule() {
        int row = myApptTable.getSelectedRow();
        if (row < 0) return;
        selectedApptIdForReschedule = (String) myApptTableModel.getValueAt(row, 0);
        Appointment a = findAppt(selectedApptIdForReschedule);
        if (a == null) return;
        for (int i = 0; i < bookDoctorCombo.getItemCount(); i++) {
            if (((String) bookDoctorCombo.getItemAt(i)).startsWith(a.getDoctorId() + " - ")) {
                bookDoctorCombo.setSelectedIndex(i);
                break;
            }
        }
        bookDateField.setText(a.getDate());
        bookReasonField.setText(a.getReason());
        refreshAvailableSlots();
        bookSlotCombo.setSelectedItem(a.getTimeSlot());
    }

    private Appointment findAppt(String id) {
        for (Appointment a : apptList) if (a.getAppointmentId().equals(id)) return a;
        return null;
    }

    private void clearBookForm() {
        bookDateField.setText(LocalDate.now().plusDays(1).toString());
        bookReasonField.setText("");
        myApptTable.clearSelection();
        selectedApptIdForReschedule = null;
        refreshAvailableSlots();
    }

    private boolean validSlotSelected() {
        String slot = (String) bookSlotCombo.getSelectedItem();
        return slot != null && !slot.startsWith("(fully booked");
    }

    /** Validates that bookDateField holds a real, non-past date in yyyy-MM-dd format.
     *  Shows a message and returns false if not. */
    private boolean validBookingDate() {
        String text = bookDateField.getText().trim();
        LocalDate date;
        try {
            date = LocalDate.parse(text);
        } catch (java.time.format.DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid date in yyyy-MM-dd format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (date.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Date cannot be in the past.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void bookAppointment() {
        String doctorId = selectedBookDoctorId();
        if (doctorId == null) {
            JOptionPane.showMessageDialog(this, "No doctors available to book.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validBookingDate()) {
            return;
        }
        if (!validSlotSelected()) {
            JOptionPane.showMessageDialog(this, "That doctor is fully booked on this date. Pick another date.", "No Slots Available", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Doctor d = null;
        for (Doctor doc : allDoctors()) if (doc.getUserId().equals(doctorId)) d = doc;
        if (d == null) return;

        // === NEW: hard-guard against a slot getting taken between dropdown refresh and clicking Book ===
        String selectedSlot = (String) bookSlotCombo.getSelectedItem();
        for (Appointment a : apptList) {
            if (a.getDoctorId().equals(doctorId)
                    && a.getDate().equals(bookDateField.getText().trim())
                    && a.getTimeSlot().equals(selectedSlot)
                    && !"Cancelled".equals(a.getStatus())) {
                JOptionPane.showMessageDialog(this, "This slot just got taken. Pick another.", "Slot Unavailable", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        // === END NEW ===

        List<String> ids = new ArrayList<>();
        for (Appointment a : apptList) ids.add(a.getAppointmentId());
        String id = hms.FileManager.nextId(ids, "AP");
        Appointment appt = new Appointment(id, loggedInPatient.getUserId(), loggedInPatient.getName(), doctorId, d.getName(),
                bookDateField.getText().trim(), (String) bookSlotCombo.getSelectedItem(), "Confirmed", bookReasonField.getText().trim());
        apptList.add(appt);
        FileManager.saveAppointments(apptList);
        refreshMyApptTable();
        refreshAvailableSlots();
        clearBookForm();
        JOptionPane.showMessageDialog(this, "Appointment booked: " + id);
    }

    private void rescheduleAppointment() {
        if (selectedApptIdForReschedule == null) {
            JOptionPane.showMessageDialog(this, "Select an appointment from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Appointment a = findAppt(selectedApptIdForReschedule);
        if (a == null) return;
        if (!"Confirmed".equals(a.getStatus())) {
            JOptionPane.showMessageDialog(this, "Only upcoming (Confirmed) appointments can be rescheduled.", "Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validBookingDate()) {
            return;
        }
        if (!validSlotSelected()) {
            JOptionPane.showMessageDialog(this, "That doctor is fully booked on this date. Pick another date.", "No Slots Available", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // === NEW: hard-guard, skip the appointment being rescheduled itself ===
        String selectedSlot = (String) bookSlotCombo.getSelectedItem();
        for (Appointment other : apptList) {
            if (other.getAppointmentId().equals(a.getAppointmentId())) continue; // this is the one we're editing
            if (other.getDoctorId().equals(a.getDoctorId())
                    && other.getDate().equals(bookDateField.getText().trim())
                    && other.getTimeSlot().equals(selectedSlot)
                    && !"Cancelled".equals(other.getStatus())) {
                JOptionPane.showMessageDialog(this, "This slot just got taken. Pick another.", "Slot Unavailable", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        // === END NEW ===

        a.setDate(bookDateField.getText().trim());
        a.setTimeSlot((String) bookSlotCombo.getSelectedItem());
        a.setReason(bookReasonField.getText().trim());
        FileManager.saveAppointments(apptList);
        refreshMyApptTable();
        refreshAvailableSlots();
        clearBookForm();
        JOptionPane.showMessageDialog(this, "Appointment rescheduled.");
    }
    private void cancelAppointment() {
        if (selectedApptIdForReschedule == null) {
            JOptionPane.showMessageDialog(this, "Select an appointment from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Appointment a = findAppt(selectedApptIdForReschedule);
        if (a == null) return;
        if (!"Confirmed".equals(a.getStatus())) {
            JOptionPane.showMessageDialog(this, "Only upcoming (Confirmed) appointments can be cancelled.", "Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Cancel this appointment?", "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            a.setStatus("Cancelled");
            FileManager.saveAppointments(apptList);
            refreshMyApptTable();
            refreshAvailableSlots();
            clearBookForm();
        }
    }

    // ================= 3) MEDICAL HISTORY & PRESCRIPTIONS =================

    private JPanel buildHistoryPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 14));
        panel.setOpaque(false);

        historyApptModel = new DefaultTableModel(new String[]{"Date", "Doctor", "Status", "Blood Pressure", "Temp", "Pulse", "Grade", "Bill (RM)", "Notes"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable historyApptTable = new JTable(historyApptModel);
        UITheme.styleTable(historyApptTable);
        JPanel apptCard = UITheme.card();
        apptCard.setLayout(new BorderLayout());
        apptCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4), UITheme.sectionTitle("Visit History")));
        apptCard.add(new JScrollPane(historyApptTable), BorderLayout.CENTER);

        historyRxModel = new DefaultTableModel(new String[]{"Date", "Doctor", "Medication", "Dosage", "Instructions"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable historyRxTable = new JTable(historyRxModel);
        UITheme.styleTable(historyRxTable);
        JPanel rxCard = UITheme.card();
        rxCard.setLayout(new BorderLayout());
        rxCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4), UITheme.sectionTitle("My Prescriptions")));
        rxCard.add(new JScrollPane(historyRxTable), BorderLayout.CENTER);

        panel.add(apptCard);
        panel.add(rxCard);
        return panel;
    }

    private void refreshHistory() {
        apptList = FileManager.loadAppointments();
        noteList = FileManager.loadNotes();
        rxList = FileManager.loadPrescriptions();
        billList = FileManager.loadBills();

        historyApptModel.setRowCount(0);
        for (Appointment a : apptList) {
            if (!a.getPatientId().equals(loggedInPatient.getUserId())) continue;
            ConsultationNote note = null;
            for (ConsultationNote n : noteList) {
                if (n.getAppointmentId().equals(a.getAppointmentId())) { note = n; break; }
            }
            Bill bill = null;
            for (Bill b : billList) {
                if (b.getAppointmentId().equals(a.getAppointmentId())) { bill = b; break; }
            }
            historyApptModel.addRow(new Object[]{
                    a.getDate(), a.getDoctorName(), a.getStatus(),
                    note != null ? note.getBloodPressure() : "",
                    note != null ? note.getTemperature() : "",
                    note != null ? note.getPulse() : "",
                    note != null ? note.getVitalsGrade() : "",
                    bill != null ? String.format("%.2f", bill.getTotalAmount()) : "",
                    note != null ? note.getNotes() : ""
            });
        }

        historyRxModel.setRowCount(0);
        for (Prescription p : rxList) {
            if (p.getPatientId().equals(loggedInPatient.getUserId())) {
                historyRxModel.addRow(new Object[]{p.getDate(), p.getDoctorName(), p.getMedication(), p.getDosage(), p.getInstructions()});
            }
        }
    }

    // ================= 4) RATINGS =================

    private JPanel buildRatingPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        ratingTableModel = new DefaultTableModel(new String[]{"Doctor", "Stars", "Comment", "Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable ratingTable = new JTable(ratingTableModel);
        UITheme.styleTable(ratingTable);

        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        tableCard.add(new JScrollPane(ratingTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(UITheme.sectionTitle("Rate a Completed Visit"));

        ratingApptCombo = new JComboBox<>();
        ratingStarsCombo = new JComboBox<>(new Integer[]{5, 4, 3, 2, 1});
        ratingCommentArea = new JTextArea(3, 20);
        ratingCommentArea.setLineWrap(true);
        ratingCommentArea.setWrapStyleWord(true);
        UITheme.styleComboBox(ratingApptCombo);
        UITheme.styleComboBox(ratingStarsCombo);
        ratingCommentArea.setFont(UITheme.FONT_BODY);
        ratingCommentArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        form.add(formLabel("Completed Visit:"));
        form.add(ratingApptCombo);
        form.add(formLabel("Stars (1-5):"));
        form.add(ratingStarsCombo);

        JPanel commentPanel = new JPanel(new BorderLayout(0, 4));
        commentPanel.setOpaque(false);
        commentPanel.add(formLabel("Comment:"), BorderLayout.NORTH);
        commentPanel.add(new JScrollPane(ratingCommentArea), BorderLayout.CENTER);

        JPanel formWrap = new JPanel(new BorderLayout(0, 10));
        formWrap.setOpaque(false);
        formWrap.add(form, BorderLayout.CENTER);
        formWrap.add(commentPanel, BorderLayout.SOUTH);

        JButton submitBtn = UITheme.primaryButton("Submit Rating");
        submitBtn.addActionListener(e -> submitRating());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.add(submitBtn);

        JPanel formCard = UITheme.card();
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));
        formCard.add(formWrap, BorderLayout.CENTER);
        formCard.add(btnPanel, BorderLayout.SOUTH);

        panel.add(tableCard, BorderLayout.CENTER);
        panel.add(formCard, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshRatingApptCombo() {
        apptList = FileManager.loadAppointments();
        ratingList = FileManager.loadRatings();
        ratingApptCombo.removeAllItems();
        List<String> ratedApptIds = new ArrayList<>();
        for (Rating r : ratingList) ratedApptIds.add(r.getAppointmentId());
        for (Appointment a : apptList) {
            if (a.getPatientId().equals(loggedInPatient.getUserId()) && "Completed".equals(a.getStatus())
                    && !ratedApptIds.contains(a.getAppointmentId())) {
                ratingApptCombo.addItem(a.getAppointmentId() + " - Dr. " + a.getDoctorName() + " on " + a.getDate());
            }
        }
    }

    private void refreshRatingTable() {
        ratingTableModel.setRowCount(0);
        for (Rating r : ratingList) {
            if (r.getPatientId().equals(loggedInPatient.getUserId())) {
                ratingTableModel.addRow(new Object[]{r.getDoctorName(), r.getStars() + " \u2605", r.getComment(), r.getDate()});
            }
        }
    }

    private void submitRating() {
        String sel = (String) ratingApptCombo.getSelectedItem();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "No completed visits available to rate yet.", "Nothing to Rate", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String apptId = sel.split(" - ", 2)[0];
        Appointment a = findAppt(apptId);
        if (a == null) return;

        List<String> ids = new ArrayList<>();
        for (Rating r : ratingList) ids.add(r.getRatingId());
        String id = FileManager.nextId(ids, "RT");
        Rating rating = new Rating(id, apptId, loggedInPatient.getUserId(), a.getDoctorId(), a.getDoctorName(),
                (Integer) ratingStarsCombo.getSelectedItem(), ratingCommentArea.getText().trim(), LocalDate.now().toString());
        ratingList.add(rating);
        FileManager.saveRatings(ratingList);
        refreshRatingApptCombo();
        refreshRatingTable();
        ratingCommentArea.setText("");
        JOptionPane.showMessageDialog(this, "Thanks for your feedback!");
    }
}
