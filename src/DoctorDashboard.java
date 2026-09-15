package hms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * Doctor dashboard. Implements all 4 Doctor functionalities from assignment requirements
 *   1) Edit personal / individual profile
 *   2) Log patient vital signs and write consultation notes
 *   3) Issue digital medication prescriptions to a patient's record
 *   4) Issue requests to admin for lab test, X-rays or specialized imaging
 */
public class DoctorDashboard extends JFrame {

    private final Doctor loggedInDoctor;
    private List<User> userList;
    private List<Appointment> apptList;
    private List<ConsultationNote> noteList;
    private List<Prescription> rxList;
    private List<LabRequest> labList;

    // ---- Navigation ----
    private CardLayout cardLayout;
    private JPanel contentCards;
    private JLabel sectionTitleLabel;
    private NavButton navProfile, navConsult, navRx, navLab;

    // ---- Profile tab ----
    private JTextField profileNameField;
    private JPasswordField profilePasswordField;
    private JTextField profileSpecialtyField;

    // ---- Consultation tab ----
    private DefaultTableModel apptTableModel;
    private JTable apptTable;
    private JComboBox<String> apptFilterCombo;
    private JTextField bpField, tempField, pulseField;
    private JTextArea notesArea;
    private String selectedApptId;
    private JLabel apptContextLabel;
    private JLabel vitalsGradeLabel;
    private List<ConsultationConfig> configList;
    private List<Bill> billList;

    // ---- Prescriptions tab ----
    private JComboBox<String> rxPatientCombo;
    private JTextField rxMedField, rxDosageField;
    private JTextArea rxInstructionsArea;
    private DefaultTableModel rxTableModel;

    // ---- Lab requests tab ----
    private JComboBox<String> labPatientCombo;
    private JComboBox<String> labTypeCombo;
    private JTextArea labNotesArea;
    private DefaultTableModel labTableModel;

    public DoctorDashboard(Doctor doctor) {
        this.loggedInDoctor = doctor;
        this.userList = FileManager.loadUsers();
        this.apptList = FileManager.loadAppointments();
        this.noteList = FileManager.loadNotes();
        this.rxList = FileManager.loadPrescriptions();
        this.labList = FileManager.loadLabRequests();
        this.configList = FileManager.loadConfigs();
        this.billList = FileManager.loadBills();

        setTitle("Hospital_Management_System - Doctor Portal (Dr. " + doctor.getName() + ")");
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

        JLabel logo = new JLabel("Hospital Management System");
        logo.setFont(UITheme.FONT_LOGO);
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 6, 2, 0));

        JLabel role = new JLabel("Doctor Portal");
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
        JLabel nameLbl = new JLabel("Dr. " + loggedInDoctor.getName());
        nameLbl.setFont(UITheme.FONT_NAV_BOLD);
        nameLbl.setForeground(Color.WHITE);
        JLabel idLbl = new JLabel(loggedInDoctor.getUserId() + " - " + loggedInDoctor.getSpecialty());
        idLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        idLbl.setForeground(new Color(0xC9, 0xE0, 0xD3));
        card.add(nameLbl);
        card.add(idLbl);

        navProfile = new NavButton(NavButton.Icon.PROFILE, "My Profile");
        navConsult = new NavButton(NavButton.Icon.RECORDS, "Consultations");
        navRx = new NavButton(NavButton.Icon.RX, "Prescriptions");
        navLab = new NavButton(NavButton.Icon.LAB, "Lab / Imaging Requests");
        NavButton navLogout = new NavButton(NavButton.Icon.LOGOUT, "Log Out");

        for (NavButton nb : new NavButton[]{navProfile, navConsult, navRx, navLab, navLogout}) {
            nb.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        navProfile.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("profile"); }
        });
        navConsult.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("consult"); }
        });
        navRx.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("rx"); }
        });
        navLab.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("lab"); }
        });
        navLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(DoctorDashboard.this,
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
        sidebar.add(navConsult);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(navRx);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(navLab);
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
        contentCards.add(buildConsultPanel(), "consult");
        contentCards.add(buildRxPanel(), "rx");
        contentCards.add(buildLabPanel(), "lab");

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(contentCards, BorderLayout.CENTER);
        return wrapper;
    }

    private void showSection(String key) {
        cardLayout.show(contentCards, key);
        navProfile.setSelected("profile".equals(key));
        navConsult.setSelected("consult".equals(key));
        navRx.setSelected("rx".equals(key));
        navLab.setSelected("lab".equals(key));
        switch (key) {
            case "profile": sectionTitleLabel.setText("My Profile"); break;
            case "consult": sectionTitleLabel.setText("Consultations - Vitals & Notes"); break;
            case "rx": sectionTitleLabel.setText("Issue Prescription"); break;
            case "lab": sectionTitleLabel.setText("Lab / Imaging Requests to Admin"); break;
        }
        if ("consult".equals(key)) { refreshApptTable(); }
        if ("rx".equals(key)) { refreshRxPatientCombo(); refreshRxTable(); }
        if ("lab".equals(key)) { refreshLabPatientCombo(); refreshLabTable(); }
    }

    private JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_LABEL);
        l.setForeground(UITheme.TEXT_DARK);
        return l;
    }

    /** Patients this doctor has at least one appointment with (used to populate Rx / Lab pickers). */
    private List<String> myPatientOptions() {
        List<String> out = new ArrayList<>();
        List<String> seen = new ArrayList<>();
        for (Appointment a : apptList) {
            if (a.getDoctorId().equals(loggedInDoctor.getUserId()) && !seen.contains(a.getPatientId())) {
                seen.add(a.getPatientId());
                out.add(a.getPatientId() + " - " + a.getPatientName());
            }
        }
        return out;
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

        profileNameField = new JTextField(loggedInDoctor.getName());
        profilePasswordField = new JPasswordField(loggedInDoctor.getPassword());
        profileSpecialtyField = new JTextField(loggedInDoctor.getSpecialty());
        UITheme.styleTextField(profileNameField);
        UITheme.styleTextField(profilePasswordField);
        UITheme.styleTextField(profileSpecialtyField);

        form.add(formLabel("User ID:"));
        form.add(formLabel(loggedInDoctor.getUserId()));
        form.add(formLabel("Username:"));
        form.add(formLabel(loggedInDoctor.getUsername()));
        form.add(formLabel("Full Name:"));
        form.add(profileNameField);
        form.add(formLabel("Password:"));
        form.add(profilePasswordField);
        form.add(formLabel("Specialty:"));
        form.add(profileSpecialtyField);

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
        loggedInDoctor.setName(profileNameField.getText().trim());
        loggedInDoctor.setPassword(PasswordUtil.resolvePassword(loggedInDoctor.getPassword(), new String(profilePasswordField.getPassword())));
        loggedInDoctor.setSpecialty(profileSpecialtyField.getText().trim());
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserId().equals(loggedInDoctor.getUserId())) {
                userList.set(i, loggedInDoctor);
                break;
            }
        }
        FileManager.saveUsers(userList);
        JOptionPane.showMessageDialog(this, "Profile updated.");
    }

    // ================= 2) VITALS + CONSULTATION NOTES =================

    private JPanel buildConsultPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        apptTableModel = new DefaultTableModel(new String[]{"Appt ID", "Patient", "Date", "Time", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        apptTable = new JTable(apptTableModel);
        UITheme.styleTable(apptTable);
        apptTable.getSelectionModel().addListSelectionListener(e -> loadSelectedAppointment());

        apptFilterCombo = new JComboBox<>(new String[]{"Confirmed (upcoming)", "Completed", "All"});
        UITheme.styleComboBox(apptFilterCombo);
        apptFilterCombo.addActionListener(e -> refreshApptTable());
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        filterRow.setOpaque(false);
        filterRow.add(formLabel("Show:"));
        filterRow.add(apptFilterCombo);

        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        tableCard.add(filterRow, BorderLayout.NORTH);
        tableCard.add(new JScrollPane(apptTable), BorderLayout.CENTER);

        apptContextLabel = new JLabel("Select an appointment from the table to log vitals and notes.");
        apptContextLabel.setFont(UITheme.FONT_LABEL);
        apptContextLabel.setForeground(UITheme.TEXT_MUTED);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(UITheme.sectionTitle("Vitals & Notes"));

        bpField = new JTextField();
        tempField = new JTextField();
        pulseField = new JTextField();
        notesArea = new JTextArea(4, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        UITheme.styleTextField(bpField);
        UITheme.styleTextField(tempField);
        UITheme.styleTextField(pulseField);
        notesArea.setFont(UITheme.FONT_BODY);
        notesArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        form.add(formLabel("Blood Pressure (e.g. 120/80):"));
        form.add(bpField);
        form.add(formLabel("Temperature (\u00b0C):"));
        form.add(tempField);
        form.add(formLabel("Pulse (bpm):"));
        form.add(pulseField);

        vitalsGradeLabel = new JLabel("Grade: -");
        vitalsGradeLabel.setFont(UITheme.FONT_LABEL);
        javax.swing.event.DocumentListener gradeUpdater = new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { updateVitalsGradeLabel(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { updateVitalsGradeLabel(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { updateVitalsGradeLabel(); }
        };
        bpField.getDocument().addDocumentListener(gradeUpdater);
        tempField.getDocument().addDocumentListener(gradeUpdater);
        pulseField.getDocument().addDocumentListener(gradeUpdater);

        JPanel notesPanel = new JPanel(new BorderLayout(0, 4));
        notesPanel.setOpaque(false);
        notesPanel.add(formLabel("Consultation Notes:"), BorderLayout.NORTH);
        notesPanel.add(new JScrollPane(notesArea), BorderLayout.CENTER);

        JPanel formWrap = new JPanel(new BorderLayout(0, 10));
        formWrap.setOpaque(false);
        formWrap.add(apptContextLabel, BorderLayout.NORTH);
        formWrap.add(form, BorderLayout.CENTER);
        JPanel southStack = new JPanel(new BorderLayout(0, 6));
        southStack.setOpaque(false);
        southStack.add(vitalsGradeLabel, BorderLayout.NORTH);
        southStack.add(notesPanel, BorderLayout.CENTER);
        formWrap.add(southStack, BorderLayout.SOUTH);

        JButton saveBtn = UITheme.primaryButton("Save Vitals & Complete Visit");
        JButton clearBtn = UITheme.ghostButton("Clear Form");
        saveBtn.addActionListener(e -> saveConsultation());
        clearBtn.addActionListener(e -> clearConsultForm());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.add(saveBtn);
        btnPanel.add(clearBtn);

        JPanel formCard = UITheme.card();
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));
        formCard.add(formWrap, BorderLayout.CENTER);
        formCard.add(btnPanel, BorderLayout.SOUTH);

        panel.add(tableCard, BorderLayout.CENTER);
        panel.add(formCard, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshApptTable() {
        apptList = FileManager.loadAppointments();
        apptTableModel.setRowCount(0);
        String filter = (String) apptFilterCombo.getSelectedItem();
        for (Appointment a : apptList) {
            if (!a.getDoctorId().equals(loggedInDoctor.getUserId())) continue;
            boolean include;
            if (filter == null || filter.startsWith("Confirmed")) include = "Confirmed".equals(a.getStatus());
            else if (filter.startsWith("Completed")) include = "Completed".equals(a.getStatus());
            else include = true;
            if (include) {
                apptTableModel.addRow(new Object[]{a.getAppointmentId(), a.getPatientName(), a.getDate(), a.getTimeSlot(), a.getStatus()});
            }
        }
    }

    private void loadSelectedAppointment() {
        int row = apptTable.getSelectedRow();
        if (row < 0) return;
        selectedApptId = (String) apptTableModel.getValueAt(row, 0);
        Appointment a = findAppt(selectedApptId);
        if (a == null) return;
        apptContextLabel.setText("Logging visit for " + a.getPatientName() + " on " + a.getDate() + " (" + a.getTimeSlot() + ")");
        ConsultationNote existing = null;
        for (ConsultationNote n : noteList) {
            if (n.getAppointmentId().equals(selectedApptId)) { existing = n; break; }
        }
        if (existing != null) {
            bpField.setText(existing.getBloodPressure());
            tempField.setText(existing.getTemperature());
            pulseField.setText(existing.getPulse());
            notesArea.setText(existing.getNotes());
        } else {
            bpField.setText("");
            tempField.setText("");
            pulseField.setText("");
            notesArea.setText("");
        }
    }

    private Appointment findAppt(String id) {
        for (Appointment a : apptList) if (a.getAppointmentId().equals(id)) return a;
        return null;
    }

    /** Recomputes the Normal/Elevated/Critical grade from whatever is currently typed in the vitals fields. */
    private void updateVitalsGradeLabel() {
        String grade = VitalsGrader.overallGrade(bpField.getText(), tempField.getText(), pulseField.getText());
        vitalsGradeLabel.setText("Grade: " + grade);
        if (VitalsGrader.CRITICAL.equals(grade)) {
            vitalsGradeLabel.setForeground(new Color(0xC0392B));
        } else if (VitalsGrader.ELEVATED.equals(grade)) {
            vitalsGradeLabel.setForeground(new Color(0xD68910));
        } else {
            vitalsGradeLabel.setForeground(UITheme.TEXT_MUTED);
        }
    }

    /** Returns the matching ConsultationConfig for this doctor's own specialty, or null if Admin
     *  hasn't set one up yet - used to block billing instead of silently charging RM 0.00. */
    private ConsultationConfig configForOwnSpecialty() {
        for (ConsultationConfig c : configList) {
            if (c.getDepartment().equalsIgnoreCase(loggedInDoctor.getSpecialty())) return c;
        }
        return null;
    }

    private double rateForOwnSpecialty() {
        ConsultationConfig c = configForOwnSpecialty();
        return c != null ? c.getBaseRate() : 0.0;
    }

    private void clearConsultForm() {
        bpField.setText("");
        tempField.setText("");
        pulseField.setText("");
        notesArea.setText("");
        apptTable.clearSelection();
        selectedApptId = null;
        apptContextLabel.setText("Select an appointment from the table to log vitals and notes.");
        updateVitalsGradeLabel();
    }

    private void saveConsultation() {
        if (selectedApptId == null) {
            JOptionPane.showMessageDialog(this, "Select an appointment first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Appointment a = findAppt(selectedApptId);
        if (a == null) return;

        if (configForOwnSpecialty() == null) {
            JOptionPane.showMessageDialog(this,
                    "No consultation rate configured for the " + loggedInDoctor.getSpecialty()
                            + " department - contact Admin before completing this visit.",
                    "Cannot Bill Visit", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String bp = bpField.getText().trim();
        String temp = tempField.getText().trim();
        String pulse = pulseField.getText().trim();
        if (bp.isEmpty() || temp.isEmpty() || pulse.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Blood pressure, temperature, and pulse are all required before saving.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (VitalsGrader.gradeBloodPressure(bp).equals(VitalsGrader.UNKNOWN)
                || VitalsGrader.gradeTemperature(temp).equals(VitalsGrader.UNKNOWN)
                || VitalsGrader.gradePulse(pulse).equals(VitalsGrader.UNKNOWN)) {
            JOptionPane.showMessageDialog(this, "Enter valid numeric vitals (BP as systolic/diastolic, e.g. 120/80).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ConsultationNote existing = null;
        for (ConsultationNote n : noteList) {
            if (n.getAppointmentId().equals(selectedApptId)) { existing = n; break; }
        }
        if (existing != null) {
            noteList.remove(existing);
        }
        List<String> ids = new ArrayList<>();
        for (ConsultationNote n : noteList) ids.add(n.getNoteId());
        String noteId = existing != null ? existing.getNoteId() : FileManager.nextId(ids, "CN");
        ConsultationNote note = new ConsultationNote(noteId, selectedApptId, a.getPatientId(), loggedInDoctor.getUserId(),
                LocalDate.now().toString(), bp, temp, pulse, notesArea.getText().trim());
        noteList.add(note);
        FileManager.saveNotes(noteList);

        a.setStatus("Completed");
        FileManager.saveAppointments(apptList);

        String grade = note.getVitalsGrade();
        double amount = generateBillForVisit(a, grade);

        refreshApptTable();
        clearConsultForm();
        JOptionPane.showMessageDialog(this, String.format(
                "Vitals and notes saved. Visit marked Completed.\nVitals grade: %s\nBill generated: RM %.2f",
                grade, amount));
    }

    /**
     * Creates (or replaces, if this appointment already had one) the Bill for a completed visit.
     * Amount is the department's base rate, bumped up a bit if the vitals came back Elevated/Critical.
     * Returns the final amount so the caller can show it in the confirmation dialog.
     */
    private double generateBillForVisit(Appointment a, String grade) {
        billList.removeIf(b -> b.getAppointmentId().equals(a.getAppointmentId()));

        double baseRate = rateForOwnSpecialty();
        double amount = baseRate * VitalsGrader.surchargeMultiplier(grade);

        List<String> billIds = new ArrayList<>();
        for (Bill b : billList) billIds.add(b.getBillId());
        String billId = FileManager.nextId(billIds, "BL");

        Bill bill = new Bill(billId, a.getAppointmentId(), a.getPatientId(), a.getPatientName(),
                loggedInDoctor.getUserId(), loggedInDoctor.getName(), loggedInDoctor.getSpecialty(),
                LocalDate.now().toString(), baseRate, grade, amount);
        billList.add(bill);
        FileManager.saveBills(billList);
        return amount;
    }

    // ================= 3) PRESCRIPTIONS =================

    private JPanel buildRxPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        rxTableModel = new DefaultTableModel(new String[]{"Rx ID", "Patient", "Medication", "Dosage", "Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable rxTable = new JTable(rxTableModel);
        UITheme.styleTable(rxTable);

        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        tableCard.add(new JScrollPane(rxTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(UITheme.sectionTitle("New Prescription"));

        rxPatientCombo = new JComboBox<>();
        rxMedField = new JTextField();
        rxDosageField = new JTextField();
        rxInstructionsArea = new JTextArea(3, 20);
        rxInstructionsArea.setLineWrap(true);
        rxInstructionsArea.setWrapStyleWord(true);
        UITheme.styleComboBox(rxPatientCombo);
        UITheme.styleTextField(rxMedField);
        UITheme.styleTextField(rxDosageField);
        rxInstructionsArea.setFont(UITheme.FONT_BODY);
        rxInstructionsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        form.add(formLabel("Patient:"));
        form.add(rxPatientCombo);
        form.add(formLabel("Medication:"));
        form.add(rxMedField);
        form.add(formLabel("Dosage (e.g. 500mg, 2x daily):"));
        form.add(rxDosageField);

        JPanel instrPanel = new JPanel(new BorderLayout(0, 4));
        instrPanel.setOpaque(false);
        instrPanel.add(formLabel("Instructions:"), BorderLayout.NORTH);
        instrPanel.add(new JScrollPane(rxInstructionsArea), BorderLayout.CENTER);

        JPanel formWrap = new JPanel(new BorderLayout(0, 10));
        formWrap.setOpaque(false);
        formWrap.add(form, BorderLayout.CENTER);
        formWrap.add(instrPanel, BorderLayout.SOUTH);

        JButton issueBtn = UITheme.primaryButton("Issue Prescription");
        issueBtn.addActionListener(e -> issuePrescription());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.add(issueBtn);

        JPanel formCard = UITheme.card();
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));
        formCard.add(formWrap, BorderLayout.CENTER);
        formCard.add(btnPanel, BorderLayout.SOUTH);

        panel.add(tableCard, BorderLayout.CENTER);
        panel.add(formCard, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshRxPatientCombo() {
        rxPatientCombo.removeAllItems();
        for (String p : myPatientOptions()) rxPatientCombo.addItem(p);
    }

    private void refreshRxTable() {
        rxList = FileManager.loadPrescriptions();
        rxTableModel.setRowCount(0);
        for (Prescription p : rxList) {
            if (p.getDoctorId().equals(loggedInDoctor.getUserId())) {
                rxTableModel.addRow(new Object[]{p.getPrescriptionId(), patientNameFor(p.getPatientId()), p.getMedication(), p.getDosage(), p.getDate()});
            }
        }
    }

    private String patientNameFor(String patientId) {
        for (User u : userList) if (u.getUserId().equals(patientId)) return u.getName();
        return patientId;
    }

    private void issuePrescription() {
        String sel = (String) rxPatientCombo.getSelectedItem();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "No patients yet \u2014 a patient must book an appointment with you first.", "No Patients", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (rxMedField.getText().trim().isEmpty() || rxDosageField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Medication and dosage are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String patientId = sel.split(" - ", 2)[0];
        List<String> ids = new ArrayList<>();
        for (Prescription p : rxList) ids.add(p.getPrescriptionId());
        String id = FileManager.nextId(ids, "RX");
        Prescription rx = new Prescription(id, patientId, loggedInDoctor.getUserId(), loggedInDoctor.getName(),
                rxMedField.getText().trim(), rxDosageField.getText().trim(), rxInstructionsArea.getText().trim(), LocalDate.now().toString());
        rxList.add(rx);
        FileManager.savePrescriptions(rxList);
        refreshRxTable();
        rxMedField.setText("");
        rxDosageField.setText("");
        rxInstructionsArea.setText("");
        JOptionPane.showMessageDialog(this, "Prescription issued: " + id);
    }

    // ================= 4) LAB / IMAGING REQUESTS =================

    private JPanel buildLabPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        labTableModel = new DefaultTableModel(new String[]{"Req ID", "Patient", "Test Type", "Status", "Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable labTable = new JTable(labTableModel);
        UITheme.styleTable(labTable);

        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        tableCard.add(new JScrollPane(labTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(UITheme.sectionTitle("New Request to Admin"));

        labPatientCombo = new JComboBox<>();
        labTypeCombo = new JComboBox<>(new String[]{"Blood Test", "X-Ray", "MRI Scan", "CT Scan", "Ultrasound", "Other Specialized Imaging"});
        labNotesArea = new JTextArea(3, 20);
        labNotesArea.setLineWrap(true);
        labNotesArea.setWrapStyleWord(true);
        UITheme.styleComboBox(labPatientCombo);
        UITheme.styleComboBox(labTypeCombo);
        labNotesArea.setFont(UITheme.FONT_BODY);
        labNotesArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        form.add(formLabel("Patient:"));
        form.add(labPatientCombo);
        form.add(formLabel("Test / Imaging Type:"));
        form.add(labTypeCombo);

        JPanel notesPanel = new JPanel(new BorderLayout(0, 4));
        notesPanel.setOpaque(false);
        notesPanel.add(formLabel("Notes for Admin:"), BorderLayout.NORTH);
        notesPanel.add(new JScrollPane(labNotesArea), BorderLayout.CENTER);

        JPanel formWrap = new JPanel(new BorderLayout(0, 10));
        formWrap.setOpaque(false);
        formWrap.add(form, BorderLayout.CENTER);
        formWrap.add(notesPanel, BorderLayout.SOUTH);

        JButton sendBtn = UITheme.primaryButton("Send Request to Admin");
        sendBtn.addActionListener(e -> sendLabRequest());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.add(sendBtn);

        JPanel formCard = UITheme.card();
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));
        formCard.add(formWrap, BorderLayout.CENTER);
        formCard.add(btnPanel, BorderLayout.SOUTH);

        panel.add(tableCard, BorderLayout.CENTER);
        panel.add(formCard, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshLabPatientCombo() {
        labPatientCombo.removeAllItems();
        for (String p : myPatientOptions()) labPatientCombo.addItem(p);
    }

    private void refreshLabTable() {
        labList = FileManager.loadLabRequests();
        labTableModel.setRowCount(0);
        for (LabRequest r : labList) {
            if (r.getDoctorId().equals(loggedInDoctor.getUserId())) {
                labTableModel.addRow(new Object[]{r.getRequestId(), r.getPatientName(), r.getTestType(), r.getStatus(), r.getDate()});
            }
        }
    }

    private void sendLabRequest() {
        String sel = (String) labPatientCombo.getSelectedItem();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "No patients yet \u2014 a patient must book an appointment with you first.", "No Patients", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String[] parts = sel.split(" - ", 2);
        String patientId = parts[0];
        String patientName = parts.length > 1 ? parts[1] : patientId;
        List<String> ids = new ArrayList<>();
        for (LabRequest r : labList) ids.add(r.getRequestId());
        String id = FileManager.nextId(ids, "LR");
        LabRequest req = new LabRequest(id, patientId, patientName, loggedInDoctor.getUserId(), loggedInDoctor.getName(),
                (String) labTypeCombo.getSelectedItem(), labNotesArea.getText().trim(), "Pending", LocalDate.now().toString());
        labList.add(req);
        FileManager.saveLabRequests(labList);
        refreshLabTable();
        labNotesArea.setText("");
        JOptionPane.showMessageDialog(this, "Request sent to Admin: " + id);
    }
}
