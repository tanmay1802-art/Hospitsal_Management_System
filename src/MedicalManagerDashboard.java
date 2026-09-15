package hms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * Medical Manager dashboard. Implements all 4 Medical Manager functionalities
 * from Table 1.0:
 *   1) Edit personal / individual profile
 *   2) Create or update specialized clinical departments
 *   3) Design and modify operational shift rosters for doctors
 *   4) View reports on hospital metrics and revenue summaries
 *
 * A manager only rosters/reports on doctors Admin has assigned to them
 * (Doctor.assignedManagerId), reusing the Admin "Assign Doctors" feature.
 */
public class MedicalManagerDashboard extends JFrame {

    private final hms.MedicalManager loggedInManager;
    private List<User> userList;
    private List<Department> deptList;
    private List<ShiftEntry> rosterList;
    private List<Appointment> apptList;
    private List<ConsultationConfig> configList;
    private List<Bill> billList;

    // ---- Navigation ----
    private CardLayout cardLayout;
    private JPanel contentCards;
    private JLabel sectionTitleLabel;
    private NavButton navProfile, navDept, navRoster, navReports, navAssessments;

    // ---- Profile tab ----
    private JTextField profileNameField;
    private JPasswordField profilePasswordField;
    private JTextField profileDeptField;

    // ---- Departments tab ----
    private DefaultTableModel deptTableModel;
    private JTable deptTable;
    private JTextField deptIdField, deptNameField, deptDescField;

    // ---- Assessment / check-up types tab ----
    private List<AssessmentType> assessmentList;
    private DefaultTableModel assessmentTableModel;
    private JTable assessmentTable;
    private JTextField assessmentIdField, assessmentNameField, assessmentDurationField, assessmentFeeField;
    private JComboBox<String> assessmentDeptCombo;

    // ---- Roster tab ----
    private DefaultTableModel rosterTableModel;
    private JTable rosterTable;
    private JComboBox<String> rosterDoctorCombo;
    private JComboBox<String> rosterDayCombo;
    private JTextField rosterShiftField;

    // ---- Reports tab ----
    private DefaultTableModel reportTableModel;
    private JLabel reportSummaryLabel;

    public MedicalManagerDashboard(MedicalManager manager) {
        this.loggedInManager = manager;
        this.userList = FileManager.loadUsers();
        this.deptList = FileManager.loadDepartments();
        this.rosterList = FileManager.loadRoster();
        this.apptList = FileManager.loadAppointments();
        this.configList = FileManager.loadConfigs();
        this.assessmentList = FileManager.loadAssessmentTypes();

        setTitle("Hospital_Management_System - Medical Manager Portal (" + manager.getName() + ")");
        setSize(1040, 660);
        setMinimumSize(new Dimension(880, 560));
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

        JLabel role = new JLabel("Medical Manager");
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
        JLabel nameLbl = new JLabel(loggedInManager.getName());
        nameLbl.setFont(UITheme.FONT_NAV_BOLD);
        nameLbl.setForeground(Color.WHITE);
        JLabel idLbl = new JLabel(loggedInManager.getUserId() + " - " + loggedInManager.getDepartment());
        idLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        idLbl.setForeground(new Color(0xC9, 0xE0, 0xD3));
        card.add(nameLbl);
        card.add(idLbl);

        navProfile = new NavButton(NavButton.Icon.PROFILE, "My Profile");
        navDept = new NavButton(NavButton.Icon.ASSETS, "Departments");
        navAssessments = new NavButton(NavButton.Icon.RECORDS, "Assessment Types");
        navRoster = new NavButton(NavButton.Icon.CALENDAR, "Shift Roster");
        navReports = new NavButton(NavButton.Icon.REPORT, "Reports");
        NavButton navLogout = new NavButton(NavButton.Icon.LOGOUT, "Log Out");

        for (NavButton nb : new NavButton[]{navProfile, navDept, navAssessments, navRoster, navReports, navLogout}) {
            nb.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        navProfile.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("profile"); }
        });
        navDept.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("dept"); }
        });
        navAssessments.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("assessments"); }
        });
        navRoster.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("roster"); }
        });
        navReports.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("reports"); }
        });
        navLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(MedicalManagerDashboard.this,
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
        sidebar.add(navDept);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(navAssessments);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(navRoster);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(navReports);
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
        contentCards.add(buildDeptPanel(), "dept");
        contentCards.add(buildAssessmentPanel(), "assessments");
        contentCards.add(buildRosterPanel(), "roster");
        contentCards.add(buildReportsPanel(), "reports");

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(contentCards, BorderLayout.CENTER);
        return wrapper;
    }

    private void showSection(String key) {
        cardLayout.show(contentCards, key);
        navProfile.setSelected("profile".equals(key));
        navDept.setSelected("dept".equals(key));
        navAssessments.setSelected("assessments".equals(key));
        navRoster.setSelected("roster".equals(key));
        navReports.setSelected("reports".equals(key));
        switch (key) {
            case "profile": sectionTitleLabel.setText("My Profile"); break;
            case "dept": sectionTitleLabel.setText("Clinical Departments"); break;
            case "assessments": sectionTitleLabel.setText("Assessment / Check-up Types"); break;
            case "roster": sectionTitleLabel.setText("Doctor Shift Roster"); break;
            case "reports": sectionTitleLabel.setText("Hospital Metrics & Revenue"); break;
        }
        if ("assessments".equals(key)) refreshAssessmentDeptCombo();
        if ("roster".equals(key)) refreshRosterDoctorCombo();
        if ("reports".equals(key)) refreshReports();
    }

    private JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_LABEL);
        l.setForeground(UITheme.TEXT_DARK);
        return l;
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

        profileNameField = new JTextField(loggedInManager.getName());
        profilePasswordField = new JPasswordField(loggedInManager.getPassword());
        profileDeptField = new JTextField(loggedInManager.getDepartment());
        UITheme.styleTextField(profileNameField);
        UITheme.styleTextField(profilePasswordField);
        UITheme.styleTextField(profileDeptField);

        form.add(formLabel("User ID:"));
        form.add(formLabel(loggedInManager.getUserId()));
        form.add(formLabel("Username:"));
        form.add(formLabel(loggedInManager.getUsername()));
        form.add(formLabel("Full Name:"));
        form.add(profileNameField);
        form.add(formLabel("Password:"));
        form.add(profilePasswordField);
        form.add(formLabel("Home Department:"));
        form.add(profileDeptField);

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
        loggedInManager.setName(profileNameField.getText().trim());
        loggedInManager.setPassword(PasswordUtil.resolvePassword(loggedInManager.getPassword(), new String(profilePasswordField.getPassword())));
        loggedInManager.setDepartment(profileDeptField.getText().trim());
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserId().equals(loggedInManager.getUserId())) {
                userList.set(i, loggedInManager);
                break;
            }
        }
        FileManager.saveUsers(userList);
        JOptionPane.showMessageDialog(this, "Profile updated.");
    }

    // ================= 2) DEPARTMENTS =================

    private JPanel buildDeptPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        deptTableModel = new DefaultTableModel(new String[]{"Dept ID", "Name", "Manager", "Description"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        deptTable = new JTable(deptTableModel);
        UITheme.styleTable(deptTable);
        refreshDeptTable();
        deptTable.getSelectionModel().addListSelectionListener(e -> loadSelectedDeptIntoForm());

        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        tableCard.add(new JScrollPane(deptTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(UITheme.sectionTitle("Department Details"));

        deptIdField = new JTextField();
        deptNameField = new JTextField();
        deptDescField = new JTextField();
        UITheme.styleTextField(deptIdField);
        UITheme.styleTextField(deptNameField);
        UITheme.styleTextField(deptDescField);

        form.add(formLabel("Dept ID (blank = auto):"));
        form.add(deptIdField);
        form.add(formLabel("Name:"));
        form.add(deptNameField);
        form.add(formLabel("Description:"));
        form.add(deptDescField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnPanel.setOpaque(false);
        JButton createBtn = UITheme.primaryButton("Create");
        JButton updateBtn = UITheme.secondaryButton("Update Selected");
        JButton deleteBtn = UITheme.dangerButton("Delete Selected");
        JButton clearBtn = UITheme.ghostButton("Clear Form");
        btnPanel.add(createBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn);

        createBtn.addActionListener(e -> createDept());
        updateBtn.addActionListener(e -> updateDept());
        deleteBtn.addActionListener(e -> deleteDept());
        clearBtn.addActionListener(e -> clearDeptForm());

        JPanel formCard = UITheme.card();
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));
        formCard.add(form, BorderLayout.CENTER);
        formCard.add(btnPanel, BorderLayout.SOUTH);

        panel.add(tableCard, BorderLayout.CENTER);
        panel.add(formCard, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshDeptTable() {
        deptTableModel.setRowCount(0);
        for (Department d : deptList) {
            deptTableModel.addRow(new Object[]{d.getDeptId(), d.getName(), d.getManagerId(), d.getDescription()});
        }
    }

    private void loadSelectedDeptIntoForm() {
        int row = deptTable.getSelectedRow();
        if (row < 0) return;
        Department d = deptList.get(row);
        deptIdField.setText(d.getDeptId());
        deptNameField.setText(d.getName());
        deptDescField.setText(d.getDescription());
    }

    private void clearDeptForm() {
        deptIdField.setText("");
        deptNameField.setText("");
        deptDescField.setText("");
        deptTable.clearSelection();
    }

    private void createDept() {
        if (deptNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<String> ids = new ArrayList<>();
        for (Department d : deptList) ids.add(d.getDeptId());
        String id = deptIdField.getText().trim().isEmpty() ? FileManager.nextId(ids, "DPT") : deptIdField.getText().trim();
        Department newDept = new Department(id, deptNameField.getText().trim(), loggedInManager.getUserId(), deptDescField.getText().trim());
        deptList.add(newDept);
        FileManager.saveDepartments(deptList);
        refreshDeptTable();
        clearDeptForm();
        JOptionPane.showMessageDialog(this, "Department created: " + id);
    }

    private void updateDept() {
        int row = deptTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a department from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Department existing = deptList.get(row);
        Department updated = new Department(existing.getDeptId(), deptNameField.getText().trim(), existing.getManagerId(), deptDescField.getText().trim());
        deptList.set(row, updated);
        FileManager.saveDepartments(deptList);
        refreshDeptTable();
        JOptionPane.showMessageDialog(this, "Department updated: " + existing.getDeptId());
    }

    private void deleteDept() {
        int row = deptTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a department from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this department permanently?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            deptList.remove(row);
            FileManager.saveDepartments(deptList);
            refreshDeptTable();
            clearDeptForm();
        }
    }

    // ================= ASSESSMENT / CHECK-UP TYPES =================
    // Section 2.0 feature: lets a Medical Manager design/maintain the assessment
    // and check-up types their departments offer (name, department, expected
    // duration, base fee). Same CRUD pattern as the Departments panel above.

    private JPanel buildAssessmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        assessmentTableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Department", "Duration (min)", "Base Fee (RM)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        assessmentTable = new JTable(assessmentTableModel);
        UITheme.styleTable(assessmentTable);
        refreshAssessmentTable();
        assessmentTable.getSelectionModel().addListSelectionListener(e -> loadSelectedAssessmentIntoForm());

        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        tableCard.add(new JScrollPane(assessmentTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(UITheme.sectionTitle("Assessment / Check-up Type Details"));

        assessmentIdField = new JTextField();
        assessmentNameField = new JTextField();
        assessmentDeptCombo = new JComboBox<>();
        assessmentDurationField = new JTextField();
        assessmentFeeField = new JTextField();
        UITheme.styleTextField(assessmentIdField);
        UITheme.styleTextField(assessmentNameField);
        UITheme.styleComboBox(assessmentDeptCombo);
        UITheme.styleTextField(assessmentDurationField);
        UITheme.styleTextField(assessmentFeeField);

        form.add(formLabel("ID (blank = auto):"));
        form.add(assessmentIdField);
        form.add(formLabel("Name:"));
        form.add(assessmentNameField);
        form.add(formLabel("Department:"));
        form.add(assessmentDeptCombo);
        form.add(formLabel("Duration (minutes):"));
        form.add(assessmentDurationField);
        form.add(formLabel("Base Fee (RM):"));
        form.add(assessmentFeeField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnPanel.setOpaque(false);
        JButton createBtn = UITheme.primaryButton("Create");
        JButton updateBtn = UITheme.secondaryButton("Update Selected");
        JButton deleteBtn = UITheme.dangerButton("Delete Selected");
        JButton clearBtn = UITheme.ghostButton("Clear Form");
        btnPanel.add(createBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn);

        createBtn.addActionListener(e -> createAssessment());
        updateBtn.addActionListener(e -> updateAssessment());
        deleteBtn.addActionListener(e -> deleteAssessment());
        clearBtn.addActionListener(e -> clearAssessmentForm());

        JPanel formCard = UITheme.card();
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));
        formCard.add(form, BorderLayout.CENTER);
        formCard.add(btnPanel, BorderLayout.SOUTH);

        panel.add(tableCard, BorderLayout.CENTER);
        panel.add(formCard, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshAssessmentDeptCombo() {
        String current = (String) assessmentDeptCombo.getSelectedItem();
        assessmentDeptCombo.removeAllItems();
        for (Department d : deptList) {
            assessmentDeptCombo.addItem(d.getName());
        }
        if (current != null) assessmentDeptCombo.setSelectedItem(current);
        refreshAssessmentTable();
    }

    private void refreshAssessmentTable() {
        assessmentTableModel.setRowCount(0);
        for (AssessmentType a : assessmentList) {
            assessmentTableModel.addRow(new Object[]{
                    a.getAssessmentId(), a.getName(), a.getDepartment(), a.getDurationMinutes(), a.getBaseFee()});
        }
    }

    private void loadSelectedAssessmentIntoForm() {
        int row = assessmentTable.getSelectedRow();
        if (row < 0) return;
        AssessmentType a = assessmentList.get(row);
        assessmentIdField.setText(a.getAssessmentId());
        assessmentNameField.setText(a.getName());
        assessmentDeptCombo.setSelectedItem(a.getDepartment());
        assessmentDurationField.setText(String.valueOf(a.getDurationMinutes()));
        assessmentFeeField.setText(String.valueOf(a.getBaseFee()));
    }

    private void clearAssessmentForm() {
        assessmentIdField.setText("");
        assessmentNameField.setText("");
        assessmentDurationField.setText("");
        assessmentFeeField.setText("");
        assessmentTable.clearSelection();
    }

    /** Parses and validates the duration/fee fields. Returns null (after showing a
     *  dialog) if either one is missing or not a valid number. */
    private double[] readAssessmentDurationAndFee() {
        if (assessmentNameField.getText().trim().isEmpty() || assessmentDeptCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Name and Department are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        try {
            double duration = Double.parseDouble(assessmentDurationField.getText().trim());
            double fee = Double.parseDouble(assessmentFeeField.getText().trim());
            if (duration <= 0 || fee < 0) throw new NumberFormatException();
            return new double[]{duration, fee};
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid duration (minutes) and base fee.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void createAssessment() {
        double[] vals = readAssessmentDurationAndFee();
        if (vals == null) return;
        List<String> ids = new ArrayList<>();
        for (AssessmentType a : assessmentList) ids.add(a.getAssessmentId());
        String id = assessmentIdField.getText().trim().isEmpty() ? FileManager.nextId(ids, "AS") : assessmentIdField.getText().trim();
        AssessmentType newAssessment = new AssessmentType(id, assessmentNameField.getText().trim(),
                (String) assessmentDeptCombo.getSelectedItem(), (int) vals[0], vals[1]);
        assessmentList.add(newAssessment);
        FileManager.saveAssessmentTypes(assessmentList);
        refreshAssessmentTable();
        clearAssessmentForm();
        JOptionPane.showMessageDialog(this, "Assessment type created: " + id);
    }

    private void updateAssessment() {
        int row = assessmentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an assessment type from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double[] vals = readAssessmentDurationAndFee();
        if (vals == null) return;
        AssessmentType existing = assessmentList.get(row);
        AssessmentType updated = new AssessmentType(existing.getAssessmentId(), assessmentNameField.getText().trim(),
                (String) assessmentDeptCombo.getSelectedItem(), (int) vals[0], vals[1]);
        assessmentList.set(row, updated);
        FileManager.saveAssessmentTypes(assessmentList);
        refreshAssessmentTable();
        JOptionPane.showMessageDialog(this, "Assessment type updated: " + existing.getAssessmentId());
    }

    private void deleteAssessment() {
        int row = assessmentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an assessment type from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this assessment type permanently?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            assessmentList.remove(row);
            FileManager.saveAssessmentTypes(assessmentList);
            refreshAssessmentTable();
            clearAssessmentForm();
        }
    }

    // ================= 3) SHIFT ROSTER =================

    private List<Doctor> myDoctors() {
        List<Doctor> mine = new ArrayList<>();
        for (User u : userList) {
            if (u instanceof Doctor && loggedInManager.getUserId().equals(((Doctor) u).getAssignedManagerId())) {
                mine.add((Doctor) u);
            }
        }
        return mine;
    }

    private JPanel buildRosterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        rosterTableModel = new DefaultTableModel(new String[]{"Shift ID", "Doctor", "Day", "Shift"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        rosterTable = new JTable(rosterTableModel);
        UITheme.styleTable(rosterTable);
        refreshRosterTable();
        rosterTable.getSelectionModel().addListSelectionListener(e -> loadSelectedRosterIntoForm());

        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        tableCard.add(new JScrollPane(rosterTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(UITheme.sectionTitle("Shift Details (only doctors assigned to you by Admin appear here)"));

        rosterDoctorCombo = new JComboBox<>();
        rosterDayCombo = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"});
        rosterShiftField = new JTextField();
        UITheme.styleComboBox(rosterDoctorCombo);
        UITheme.styleComboBox(rosterDayCombo);
        UITheme.styleTextField(rosterShiftField);
        rosterShiftField.setText("Morning (8:00 - 14:00)");

        form.add(formLabel("Doctor:"));
        form.add(rosterDoctorCombo);
        form.add(formLabel("Day of Week:"));
        form.add(rosterDayCombo);
        form.add(formLabel("Shift (e.g. Morning 8:00-14:00):"));
        form.add(rosterShiftField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnPanel.setOpaque(false);
        JButton createBtn = UITheme.primaryButton("Create");
        JButton updateBtn = UITheme.secondaryButton("Update Selected");
        JButton deleteBtn = UITheme.dangerButton("Delete Selected");
        JButton clearBtn = UITheme.ghostButton("Clear Form");
        btnPanel.add(createBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn);

        createBtn.addActionListener(e -> createRoster());
        updateBtn.addActionListener(e -> updateRoster());
        deleteBtn.addActionListener(e -> deleteRoster());
        clearBtn.addActionListener(e -> clearRosterForm());

        JPanel formCard = UITheme.card();
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));
        formCard.add(form, BorderLayout.CENTER);
        formCard.add(btnPanel, BorderLayout.SOUTH);

        panel.add(tableCard, BorderLayout.CENTER);
        panel.add(formCard, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshRosterDoctorCombo() {
        rosterDoctorCombo.removeAllItems();
        for (Doctor d : myDoctors()) {
            rosterDoctorCombo.addItem(d.getUserId() + " - " + d.getName());
        }
        refreshRosterTable();
    }

    private String doctorNameFor(String doctorId) {
        for (User u : userList) {
            if (u.getUserId().equals(doctorId)) return u.getName();
        }
        return doctorId;
    }

    private void refreshRosterTable() {
        rosterTableModel.setRowCount(0);
        List<String> myIds = new ArrayList<>();
        for (Doctor d : myDoctors()) myIds.add(d.getUserId());
        for (ShiftEntry s : rosterList) {
            if (myIds.contains(s.getDoctorId())) {
                rosterTableModel.addRow(new Object[]{s.getShiftId(), doctorNameFor(s.getDoctorId()), s.getDayOfWeek(), s.getShiftLabel()});
            }
        }
    }

    private void loadSelectedRosterIntoForm() {
        int row = rosterTable.getSelectedRow();
        if (row < 0) return;
        String shiftId = (String) rosterTableModel.getValueAt(row, 0);
        for (ShiftEntry s : rosterList) {
            if (s.getShiftId().equals(shiftId)) {
                for (int i = 0; i < rosterDoctorCombo.getItemCount(); i++) {
                    if (((String) rosterDoctorCombo.getItemAt(i)).startsWith(s.getDoctorId() + " - ")) {
                        rosterDoctorCombo.setSelectedIndex(i);
                        break;
                    }
                }
                rosterDayCombo.setSelectedItem(s.getDayOfWeek());
                rosterShiftField.setText(s.getShiftLabel());
                break;
            }
        }
    }

    private void clearRosterForm() {
        rosterShiftField.setText("Morning (8:00 - 14:00)");
        rosterTable.clearSelection();
    }

    private String selectedDoctorId() {
        String sel = (String) rosterDoctorCombo.getSelectedItem();
        if (sel == null) return null;
        return sel.split(" - ", 2)[0];
    }

    private void createRoster() {
        String doctorId = selectedDoctorId();
        if (doctorId == null) {
            JOptionPane.showMessageDialog(this, "No doctors are assigned to you yet. Ask Admin to assign one first.", "No Doctors", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (rosterShiftField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Shift description is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<String> ids = new ArrayList<>();
        for (ShiftEntry s : rosterList) ids.add(s.getShiftId());
        String id = FileManager.nextId(ids, "SH");
        ShiftEntry entry = new ShiftEntry(id, doctorId, (String) rosterDayCombo.getSelectedItem(), rosterShiftField.getText().trim());
        rosterList.add(entry);
        FileManager.saveRoster(rosterList);
        refreshRosterTable();
        clearRosterForm();
        JOptionPane.showMessageDialog(this, "Shift added.");
    }

    private void updateRoster() {
        int row = rosterTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a shift from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String shiftId = (String) rosterTableModel.getValueAt(row, 0);
        for (int i = 0; i < rosterList.size(); i++) {
            if (rosterList.get(i).getShiftId().equals(shiftId)) {
                ShiftEntry updated = new ShiftEntry(shiftId, selectedDoctorId(), (String) rosterDayCombo.getSelectedItem(), rosterShiftField.getText().trim());
                rosterList.set(i, updated);
                break;
            }
        }
        FileManager.saveRoster(rosterList);
        refreshRosterTable();
        JOptionPane.showMessageDialog(this, "Shift updated.");
    }

    private void deleteRoster() {
        int row = rosterTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a shift from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String shiftId = (String) rosterTableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this shift permanently?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            rosterList.removeIf(s -> s.getShiftId().equals(shiftId));
            FileManager.saveRoster(rosterList);
            refreshRosterTable();
            clearRosterForm();
        }
    }

    // ================= 4) REPORTS =================

    private JPanel buildReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        reportTableModel = new DefaultTableModel(new String[]{"Doctor", "Specialty", "Appointments Completed", "Critical Visits", "Revenue (RM)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable reportTable = new JTable(reportTableModel);
        UITheme.styleTable(reportTable);

        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        tableCard.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        reportSummaryLabel = new JLabel(" ");
        reportSummaryLabel.setFont(UITheme.FONT_HEADING);
        reportSummaryLabel.setForeground(UITheme.PRIMARY_DARK);

        JPanel summaryCard = UITheme.card();
        summaryCard.setLayout(new BorderLayout());
        summaryCard.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        summaryCard.add(reportSummaryLabel, BorderLayout.CENTER);

        JButton refreshBtn = UITheme.secondaryButton("Refresh Report");
        refreshBtn.addActionListener(e -> refreshReports());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btnPanel.setOpaque(false);
        btnPanel.add(refreshBtn);

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(summaryCard, BorderLayout.CENTER);
        south.add(btnPanel, BorderLayout.SOUTH);

        panel.add(tableCard, BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshReports() {
        // reload latest appointment/billing data so completed visits logged by Doctors show up
        apptList = FileManager.loadAppointments();
        userList = FileManager.loadUsers();
        configList = FileManager.loadConfigs();
        billList = FileManager.loadBills();

        reportTableModel.setRowCount(0);
        int totalCompleted = 0;
        int totalCritical = 0;
        double totalRevenue = 0.0;
        List<Doctor> mine = myDoctors();
        for (Doctor d : mine) {
            int completed = 0;
            for (Appointment a : apptList) {
                if (a.getDoctorId().equals(d.getUserId()) && "Completed".equals(a.getStatus())) {
                    completed++;
                }
            }
            // revenue and critical-visit count come straight from the itemised bills the
            // doctor generated per visit (base rate + surcharge for Elevated/Critical grades),
            // not just an estimate of completed-visits x rate
            int critical = 0;
            double revenue = 0.0;
            for (Bill b : billList) {
                if (!b.getDoctorId().equals(d.getUserId())) continue;
                revenue += b.getTotalAmount();
                if (VitalsGrader.CRITICAL.equals(b.getGrade())) critical++;
            }
            totalCompleted += completed;
            totalCritical += critical;
            totalRevenue += revenue;
            reportTableModel.addRow(new Object[]{d.getName(), d.getSpecialty(), completed, critical, String.format("%.2f", revenue)});
        }
        reportSummaryLabel.setText(String.format(
                "<html>Doctors under your management: <b>%d</b> &nbsp;|&nbsp; "
                        + "Total completed consultations: <b>%d</b> &nbsp;|&nbsp; "
                        + "Critical-grade visits: <b>%d</b> &nbsp;|&nbsp; "
                        + "Total revenue: <b>RM %.2f</b> &nbsp;|&nbsp; Report generated: %s</html>",
                mine.size(), totalCompleted, totalCritical, totalRevenue, LocalDate.now()));
    }
}
