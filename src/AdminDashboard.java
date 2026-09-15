package hms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
/**
 * @author TP092959 (Tanmay)
 * @module AdminStaff-MasterController
 * @description Central orchestration layer consolidating multi-module workflows (Parts 1-4).
 * Handles cross-cutting concerns including cross-role profile synchronization, end-to-end
 * diagnostics loop closure, dynamic resource allocation, and core financial configurations.
 *
 * Detailed Implementation Scope:
 * - Admin Staff dashboard (TP092959): Implements all 4 Admin functionalities + Integration of all 4 parts.
 * - Plus the cross-role "Edit Personal Profile" requirement and a view onto Doctors' lab/imaging requests.
 *
 * Core Functionalities:
 * 1) Create/Read/Update/Delete end users
 * 2) Assign doctors to their respective Medical Managers
 * 3) Manage and allocate physical hospital assets
 * 4) Configure base consultation rates and accepted insurance networks
 * 
 * [Managed, Developed, and System-Integrated by Tanmay Sarkar Emon]
 */ 

public class AdminDashboard extends JFrame {

    private AdminStaff loggedInAdmin;
    private List<User> userList;
    private List<HospitalAsset> assetList;
    private List<ConsultationConfig> configList;
    private List<LabRequest> labRequestList;

    // ---- Navigation ----
    private CardLayout cardLayout;
    private JPanel contentCards;
    private JLabel sectionTitleLabel;
    private NavButton navUsers, navAssign, navAssets, navBilling, navLabRequests, navProfile;

    // ---- Users tab components ----
    private DefaultTableModel userTableModel;
    private JTable userTable;
    private JTextField userIdField, userNameField, userUsernameField, userExtraField;
    private JPasswordField userPasswordField;
    private JComboBox<String> userRoleCombo;

    // ---- Assign tab components ----
    private JComboBox<String> assignDoctorCombo;
    private JComboBox<String> assignManagerCombo;
    private DefaultTableModel assignTableModel;

    // ---- Assets tab components ----
    private DefaultTableModel assetTableModel;
    private JTable assetTable;
    private JTextField assetIdField, assetNameField, assetLocationField;
    private JComboBox<String> assetTypeCombo, assetStatusCombo;

    // ---- Billing config tab components ----
    private DefaultTableModel configTableModel;
    private JTable configTable;
    private JTextField configDeptField, configRateField, configInsuranceField;

    // ---- Lab requests tab components ----
    private DefaultTableModel labRequestTableModel;
    private JTable labRequestTable;

    // ---- Profile tab components ----
    private JTextField profileNameField;
    private JPasswordField profilePasswordField;

    public AdminDashboard(AdminStaff admin) {
        this.loggedInAdmin = admin;
        this.userList = FileManager.loadUsers();
        this.assetList = FileManager.loadAssets();
        this.configList = FileManager.loadConfigs();
        this.labRequestList = FileManager.loadLabRequests();

        setTitle("Hospital_Management_System- Admin Console (" + admin.getName() + ")");
        setSize(1040, 660);
        setMinimumSize(new Dimension(880, 560));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BACKGROUND);
        setContentPane(root);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContentArea(), BorderLayout.CENTER);

        showSection("users");
    }

    // ================= SHELL: SIDEBAR + CONTENT SWITCHER =================

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

        JLabel role = new JLabel("Admin Console");
        role.setFont(UITheme.FONT_SUBTITLE);
        role.setForeground(new Color(0xB9, 0xD6, 0xC6));
        role.setAlignmentX(Component.LEFT_ALIGNMENT);
        role.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));

        JPanel adminCard = new JPanel();
        adminCard.setLayout(new BoxLayout(adminCard, BoxLayout.Y_AXIS));
        adminCard.setOpaque(true);
        adminCard.setBackground(UITheme.PRIMARY);
        adminCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        adminCard.setMaximumSize(new Dimension(206, 54));
        adminCard.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JLabel nameLbl = new JLabel(loggedInAdmin.getName());
        nameLbl.setFont(UITheme.FONT_NAV_BOLD);
        nameLbl.setForeground(Color.WHITE);
        JLabel idLbl = new JLabel(loggedInAdmin.getUserId() + " - AdminStaff");
        idLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        idLbl.setForeground(new Color(0xC9, 0xE0, 0xD3));
        adminCard.add(nameLbl);
        adminCard.add(idLbl);

        navUsers = new NavButton(NavButton.Icon.USERS, "Manage Users");
        navAssign = new NavButton(NavButton.Icon.ASSIGN, "Assign Doctors");
        navAssets = new NavButton(NavButton.Icon.ASSETS, "Hospital Assets");
        navBilling = new NavButton(NavButton.Icon.BILLING, "Rates & Insurance");
        navLabRequests = new NavButton(NavButton.Icon.LAB, "Lab Requests");
        navProfile = new NavButton(NavButton.Icon.PROFILE, "My Profile");
        NavButton navLogout = new NavButton(NavButton.Icon.LOGOUT, "Log Out");

        for (NavButton nb : new NavButton[]{navUsers, navAssign, navAssets, navBilling, navLabRequests, navProfile, navLogout}) {
            nb.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        navUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("users"); }
        });
        navAssign.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("assign"); }
        });
        navAssets.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("assets"); }
        });
        navBilling.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("billing"); }
        });
        navLabRequests.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("labrequests"); }
        });
        navProfile.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showSection("profile"); }
        });
        navLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(AdminDashboard.this,
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
        sidebar.add(adminCard);
        sidebar.add(Box.createRigidArea(new Dimension(0, 22)));
        sidebar.add(navUsers);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(navAssign);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(navAssets);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(navBilling);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(navLabRequests);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(navProfile);
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
        sectionTitleLabel = new JLabel("Manage Users");
        sectionTitleLabel.setFont(UITheme.FONT_TITLE);
        sectionTitleLabel.setForeground(UITheme.PRIMARY_DARK);
        header.add(sectionTitleLabel, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setOpaque(false);
        contentCards.setBorder(BorderFactory.createEmptyBorder(10, 26, 20, 26));

        contentCards.add(buildUserPanel(), "users");
        contentCards.add(buildAssignPanel(), "assign");
        contentCards.add(buildAssetPanel(), "assets");
        contentCards.add(buildConfigPanel(), "billing");
        contentCards.add(buildLabRequestPanel(), "labrequests");
        contentCards.add(buildProfilePanel(), "profile");

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(contentCards, BorderLayout.CENTER);
        return wrapper;
    }

    private void showSection(String key) {
        cardLayout.show(contentCards, key);
        navUsers.setSelected("users".equals(key));
        navAssign.setSelected("assign".equals(key));
        navAssets.setSelected("assets".equals(key));
        navBilling.setSelected("billing".equals(key));
        navLabRequests.setSelected("labrequests".equals(key));
        navProfile.setSelected("profile".equals(key));
        if ("labrequests".equals(key)) refreshLabRequestTable();
        switch (key) {
            case "users": sectionTitleLabel.setText("Manage Users"); break;
            case "assign": sectionTitleLabel.setText("Assign Doctors to Managers"); break;
            case "assets": sectionTitleLabel.setText("Hospital Assets"); break;
            case "labrequests": sectionTitleLabel.setText("Lab / Imaging Requests from Doctors"); break;
            case "profile": sectionTitleLabel.setText("My Profile"); break;
            case "billing": sectionTitleLabel.setText("Consultation Rates & Insurance"); break;
        }
    }

    // ================= 1) MANAGE USERS (CRUD) =================

    private JPanel buildUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        userTableModel = new DefaultTableModel(new String[]{"User ID", "Name", "Username", "Role"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        userTable = new JTable(userTableModel);
        UITheme.styleTable(userTable);
        refreshUserTable();
        userTable.getSelectionModel().addListSelectionListener(e -> loadSelectedUserIntoForm());

        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        tableCard.add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setOpaque(false);
        formPanel.setBorder(UITheme.sectionTitle("User Details"));

        userIdField = new JTextField();
        userNameField = new JTextField();
        userUsernameField = new JTextField();
        userPasswordField = new JPasswordField();
        userRoleCombo = new JComboBox<>(new String[]{"AdminStaff", "Doctor", "MedicalManager", "Patient"});
        userExtraField = new JTextField();
        for (JTextField f : new JTextField[]{userIdField, userNameField, userUsernameField, userExtraField}) {
            UITheme.styleTextField(f);
        }
        UITheme.styleTextField(userPasswordField);
        UITheme.styleComboBox(userRoleCombo);

        formPanel.add(formLabel("User ID (blank = auto):"));
        formPanel.add(userIdField);
        formPanel.add(formLabel("Name:"));
        formPanel.add(userNameField);
        formPanel.add(formLabel("Username:"));
        formPanel.add(userUsernameField);
        formPanel.add(formLabel("Password:"));
        formPanel.add(userPasswordField);
        formPanel.add(formLabel("Role:"));
        formPanel.add(userRoleCombo);
        formPanel.add(formLabel("Extra (Specialty/Dept/Phone):"));
        formPanel.add(userExtraField);

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

        createBtn.addActionListener(e -> createUser());
        updateBtn.addActionListener(e -> updateUser());
        deleteBtn.addActionListener(e -> deleteUser());
        clearBtn.addActionListener(e -> clearUserForm());

        JPanel formCard = UITheme.card();
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));
        formCard.add(formPanel, BorderLayout.CENTER);
        formCard.add(btnPanel, BorderLayout.SOUTH);

        panel.add(tableCard, BorderLayout.CENTER);
        panel.add(formCard, BorderLayout.SOUTH);
        return panel;
    }

    private JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_LABEL);
        l.setForeground(UITheme.TEXT_DARK);
        return l;
    }

    private void refreshUserTable() {
        userTableModel.setRowCount(0);
        for (User u : userList) {
            userTableModel.addRow(new Object[]{u.getUserId(), u.getName(), u.getUsername(), u.getRole()});
        }
    }

    private void loadSelectedUserIntoForm() {
        int row = userTable.getSelectedRow();
        if (row < 0) return;
        User u = userList.get(row);
        userIdField.setText(u.getUserId());
        userNameField.setText(u.getName());
        userUsernameField.setText(u.getUsername());
        userPasswordField.setText(u.getPassword());
        userRoleCombo.setSelectedItem(u.getRole());
        if (u instanceof Doctor) userExtraField.setText(((Doctor) u).getSpecialty());
        else if (u instanceof MedicalManager) userExtraField.setText(((MedicalManager) u).getDepartment());
        else if (u instanceof Patient) userExtraField.setText(((Patient) u).getContactNumber());
        else userExtraField.setText("");
    }

    private void clearUserForm() {
        userIdField.setText("");
        userNameField.setText("");
        userUsernameField.setText("");
        userPasswordField.setText("");
        userExtraField.setText("");
        userTable.clearSelection();
    }

    private String nextUserId() {
        String role = (String) userRoleCombo.getSelectedItem();
        String prefix;
        switch (role) {
            case "Doctor": prefix = "D"; break;
            case "MedicalManager": prefix = "M"; break;
            case "Patient": prefix = "P"; break;
            default: prefix = "A"; break; // AdminStaff
        }
        return FileManager.nextUserId(userList, prefix);
    }

    private User buildUserFromForm(String id) {
        String name = userNameField.getText().trim();
        String username = userUsernameField.getText().trim();
        // The password field gets pre-filled with the existing hash when editing a user
        // (see loadSelectedUserIntoForm), so only re-hash it if it actually changed.
        String existingHash = null;
        for (User u : userList) {
            if (u.getUserId().equals(id)) { existingHash = u.getPassword(); break; }
        }
        String password = PasswordUtil.resolvePassword(existingHash, new String(userPasswordField.getPassword()));
        String role = (String) userRoleCombo.getSelectedItem();
        String extra = userExtraField.getText().trim();

        switch (role) {
            case "Doctor": return new Doctor(id, name, username, password, extra);
            case "MedicalManager": return new MedicalManager(id, name, username, password, extra);
            case "Patient": return new Patient(id, name, username, password, extra);
            default: return new AdminStaff(id, name, username, password);
        }
    }

    private void createUser() {
        if (userNameField.getText().trim().isEmpty() || userUsernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Username are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (User u : userList) {
            if (u.getUsername().equalsIgnoreCase(userUsernameField.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Username already exists. Choose another.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        String id = userIdField.getText().trim().isEmpty() ? nextUserId() : userIdField.getText().trim();
        User newUser = buildUserFromForm(id);
        userList.add(newUser);
        FileManager.saveUsers(userList);
        refreshUserTable();
        refreshAssignCombos();
        clearUserForm();
        JOptionPane.showMessageDialog(this, "User created: " + id);
    }

    private void updateUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userNameField.getText().trim().isEmpty() || userUsernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Username are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = userList.get(row).getUserId();
        String newUsername = userUsernameField.getText().trim();
        for (int i = 0; i < userList.size(); i++) {
            if (i == row) continue;
            if (userList.get(i).getUsername().equalsIgnoreCase(newUsername)) {
                JOptionPane.showMessageDialog(this, "Username already exists. Choose another.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        User updated = buildUserFromForm(id);
        userList.set(row, updated);
        FileManager.saveUsers(userList);
        refreshUserTable();
        refreshAssignCombos();
        JOptionPane.showMessageDialog(this, "User updated: " + id);
    }

    private void deleteUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this user permanently?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            userList.remove(row);
            FileManager.saveUsers(userList);
            refreshUserTable();
            refreshAssignCombos();
            clearUserForm();
        }
    }

    // ================= 2) ASSIGN DOCTORS TO MEDICAL MANAGERS =================

    private JPanel buildAssignPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        JPanel top = new JPanel(new GridLayout(0, 2, 10, 10));
        top.setOpaque(false);
        top.setBorder(UITheme.sectionTitle("Assign Doctor to Medical Manager"));
        assignDoctorCombo = new JComboBox<>();
        assignManagerCombo = new JComboBox<>();
        UITheme.styleComboBox(assignDoctorCombo);
        UITheme.styleComboBox(assignManagerCombo);
        top.add(formLabel("Doctor:"));
        top.add(assignDoctorCombo);
        top.add(formLabel("Medical Manager:"));
        top.add(assignManagerCombo);

        JButton assignBtn = UITheme.primaryButton("Assign");

        assignTableModel = new DefaultTableModel(new String[]{"Doctor", "Specialty", "Assigned Manager"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable assignTable = new JTable(assignTableModel);
        UITheme.styleTable(assignTable);

        refreshAssignCombos();
        refreshAssignTable();

        JPanel topCard = UITheme.card();
        topCard.setLayout(new BorderLayout(0, 10));
        topCard.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        topCard.add(top, BorderLayout.CENTER);
        JPanel assignBtnWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        assignBtnWrap.setOpaque(false);
        assignBtnWrap.add(assignBtn);
        topCard.add(assignBtnWrap, BorderLayout.SOUTH);

        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        tableCard.add(new JScrollPane(assignTable), BorderLayout.CENTER);

        panel.add(topCard, BorderLayout.NORTH);
        panel.add(tableCard, BorderLayout.CENTER);

        assignBtn.addActionListener(e -> assignDoctorToManager());

        return panel;
    }

    private void refreshAssignCombos() {
        if (assignDoctorCombo == null || assignManagerCombo == null) return;
        assignDoctorCombo.removeAllItems();
        assignManagerCombo.removeAllItems();
        for (User u : userList) {
            if (u instanceof Doctor) assignDoctorCombo.addItem(u.getUserId() + " - " + u.getName());
            if (u instanceof MedicalManager) assignManagerCombo.addItem(u.getUserId() + " - " + u.getName());
        }
    }

    private void refreshAssignTable() {
        assignTableModel.setRowCount(0);
        for (User u : userList) {
            if (u instanceof Doctor) {
                Doctor d = (Doctor) u;
                String managerName = "Unassigned";
                if (d.getAssignedManagerId() != null && !d.getAssignedManagerId().isEmpty()) {
                    for (User m : userList) {
                        if (m.getUserId().equals(d.getAssignedManagerId())) {
                            managerName = m.getName();
                            break;
                        }
                    }
                }
                assignTableModel.addRow(new Object[]{d.getName(), d.getSpecialty(), managerName});
            }
        }
    }

    private void assignDoctorToManager() {
        if (assignDoctorCombo.getSelectedItem() == null || assignManagerCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Add at least one Doctor and one Medical Manager first (Manage Users tab).", "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String doctorId = ((String) assignDoctorCombo.getSelectedItem()).split(" - ")[0];
        String managerId = ((String) assignManagerCombo.getSelectedItem()).split(" - ")[0];

        for (User u : userList) {
            if (u instanceof Doctor && u.getUserId().equals(doctorId)) {
                ((Doctor) u).setAssignedManagerId(managerId);
            }
        }
        FileManager.saveUsers(userList);
        FileManager.appendLine(FileManager.ASSIGN_FILE, doctorId + "|" + managerId);
        refreshAssignTable();
        JOptionPane.showMessageDialog(this, "Assignment saved.");
    }

    // ================= 3) MANAGE HOSPITAL ASSETS =================

    private JPanel buildAssetPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        assetTableModel = new DefaultTableModel(new String[]{"Asset ID", "Type", "Name", "Location", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        assetTable = new JTable(assetTableModel);
        UITheme.styleTable(assetTable);
        refreshAssetTable();
        assetTable.getSelectionModel().addListSelectionListener(e -> loadSelectedAssetIntoForm());

        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        tableCard.add(new JScrollPane(assetTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(UITheme.sectionTitle("Asset Details"));

        assetIdField = new JTextField();
        assetTypeCombo = new JComboBox<>(new String[]{"Consultation Room", "Ward", "Lab", "Imaging Room"});
        assetNameField = new JTextField();
        assetLocationField = new JTextField();
        assetStatusCombo = new JComboBox<>(new String[]{"Available", "Occupied", "Maintenance"});
        UITheme.styleTextField(assetIdField);
        UITheme.styleTextField(assetNameField);
        UITheme.styleTextField(assetLocationField);
        UITheme.styleComboBox(assetTypeCombo);
        UITheme.styleComboBox(assetStatusCombo);

        form.add(formLabel("Asset ID (blank = auto):"));
        form.add(assetIdField);
        form.add(formLabel("Type:"));
        form.add(assetTypeCombo);
        form.add(formLabel("Name:"));
        form.add(assetNameField);
        form.add(formLabel("Location:"));
        form.add(assetLocationField);
        form.add(formLabel("Status:"));
        form.add(assetStatusCombo);

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

        createBtn.addActionListener(e -> createAsset());
        updateBtn.addActionListener(e -> updateAsset());
        deleteBtn.addActionListener(e -> deleteAsset());
        clearBtn.addActionListener(e -> clearAssetForm());

        JPanel formCard = UITheme.card();
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));
        formCard.add(form, BorderLayout.CENTER);
        formCard.add(btnPanel, BorderLayout.SOUTH);

        panel.add(tableCard, BorderLayout.CENTER);
        panel.add(formCard, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshAssetTable() {
        assetTableModel.setRowCount(0);
        for (HospitalAsset a : assetList) {
            assetTableModel.addRow(new Object[]{a.getAssetId(), a.getAssetType(), a.getAssetName(), a.getLocation(), a.getStatus()});
        }
    }

    private void loadSelectedAssetIntoForm() {
        int row = assetTable.getSelectedRow();
        if (row < 0) return;
        HospitalAsset a = assetList.get(row);
        assetIdField.setText(a.getAssetId());
        assetTypeCombo.setSelectedItem(a.getAssetType());
        assetNameField.setText(a.getAssetName());
        assetLocationField.setText(a.getLocation());
        assetStatusCombo.setSelectedItem(a.getStatus());
    }

    private void clearAssetForm() {
        assetIdField.setText("");
        assetNameField.setText("");
        assetLocationField.setText("");
        assetTable.clearSelection();
    }

    private String nextAssetId() {
        int max = 0;
        for (HospitalAsset a : assetList) {
            String digits = a.getAssetId().replaceAll("[^0-9]", "");
            if (!digits.isEmpty()) max = Math.max(max, Integer.parseInt(digits));
        }
        return "AS" + String.format("%03d", max + 1);
    }

    private void createAsset() {
        if (assetNameField.getText().trim().isEmpty() || assetLocationField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Location are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = assetIdField.getText().trim().isEmpty() ? nextAssetId() : assetIdField.getText().trim();
        HospitalAsset newAsset = new HospitalAsset(id, (String) assetTypeCombo.getSelectedItem(),
                assetNameField.getText().trim(), assetLocationField.getText().trim(), (String) assetStatusCombo.getSelectedItem());
        assetList.add(newAsset);
        FileManager.saveAssets(assetList);
        refreshAssetTable();
        clearAssetForm();
        JOptionPane.showMessageDialog(this, "Asset created: " + id);
    }

    private void updateAsset() {
        int row = assetTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an asset from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = assetList.get(row).getAssetId();
        HospitalAsset updated = new HospitalAsset(id, (String) assetTypeCombo.getSelectedItem(),
                assetNameField.getText().trim(), assetLocationField.getText().trim(), (String) assetStatusCombo.getSelectedItem());
        assetList.set(row, updated);
        FileManager.saveAssets(assetList);
        refreshAssetTable();
        JOptionPane.showMessageDialog(this, "Asset updated: " + id);
    }

    private void deleteAsset() {
        int row = assetTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an asset from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this asset permanently?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            assetList.remove(row);
            FileManager.saveAssets(assetList);
            refreshAssetTable();
            clearAssetForm();
        }
    }

    // ================= 4) CONSULTATION RATES & INSURANCE =================

    private JPanel buildConfigPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        configTableModel = new DefaultTableModel(new String[]{"Department", "Base Rate (RM)", "Accepted Insurance Networks"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        configTable = new JTable(configTableModel);
        UITheme.styleTable(configTable);
        refreshConfigTable();
        configTable.getSelectionModel().addListSelectionListener(e -> loadSelectedConfigIntoForm());

        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        tableCard.add(new JScrollPane(configTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(UITheme.sectionTitle("Billing Configuration"));

        configDeptField = new JTextField();
        configRateField = new JTextField();
        configInsuranceField = new JTextField();
        UITheme.styleTextField(configDeptField);
        UITheme.styleTextField(configRateField);
        UITheme.styleTextField(configInsuranceField);

        form.add(formLabel("Department:"));
        form.add(configDeptField);
        form.add(formLabel("Base Consultation Rate (RM):"));
        form.add(configRateField);
        form.add(formLabel("Accepted Insurance (comma-separated):"));
        form.add(configInsuranceField);

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

        createBtn.addActionListener(e -> createConfig());
        updateBtn.addActionListener(e -> updateConfig());
        deleteBtn.addActionListener(e -> deleteConfig());
        clearBtn.addActionListener(e -> clearConfigForm());

        JPanel formCard = UITheme.card();
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));
        formCard.add(form, BorderLayout.CENTER);
        formCard.add(btnPanel, BorderLayout.SOUTH);

        panel.add(tableCard, BorderLayout.CENTER);
        panel.add(formCard, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshConfigTable() {
        configTableModel.setRowCount(0);
        for (ConsultationConfig c : configList) {
            configTableModel.addRow(new Object[]{c.getDepartment(), c.getBaseRate(), c.getAcceptedInsuranceNetworks()});
        }
    }

    private void loadSelectedConfigIntoForm() {
        int row = configTable.getSelectedRow();
        if (row < 0) return;
        ConsultationConfig c = configList.get(row);
        configDeptField.setText(c.getDepartment());
        configRateField.setText(String.valueOf(c.getBaseRate()));
        configInsuranceField.setText(c.getAcceptedInsuranceNetworks());
    }

    private void clearConfigForm() {
        configDeptField.setText("");
        configRateField.setText("");
        configInsuranceField.setText("");
        configTable.clearSelection();
    }

    private void createConfig() {
        if (configDeptField.getText().trim().isEmpty() || configRateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Department and Base Rate are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double rate;
        try {
            rate = Double.parseDouble(configRateField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Base Rate must be a number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (ConsultationConfig c : configList) {
            if (c.getDepartment().equalsIgnoreCase(configDeptField.getText().trim())) {
                JOptionPane.showMessageDialog(this, "This department already has a configuration. Select it to update instead.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        ConsultationConfig newConfig = new ConsultationConfig(configDeptField.getText().trim(), rate, configInsuranceField.getText().trim());
        configList.add(newConfig);
        FileManager.saveConfigs(configList);
        refreshConfigTable();
        clearConfigForm();
        JOptionPane.showMessageDialog(this, "Configuration created.");
    }

    private void updateConfig() {
        int row = configTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a configuration from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double rate;
        try {
            rate = Double.parseDouble(configRateField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Base Rate must be a number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ConsultationConfig updated = new ConsultationConfig(configDeptField.getText().trim(), rate, configInsuranceField.getText().trim());
        configList.set(row, updated);
        FileManager.saveConfigs(configList);
        refreshConfigTable();
        JOptionPane.showMessageDialog(this, "Configuration updated.");
    }

    private void deleteConfig() {
        int row = configTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a configuration from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this configuration permanently?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            configList.remove(row);
            FileManager.saveConfigs(configList);
            refreshConfigTable();
            clearConfigForm();
        }
    }

    // ================= LAB / IMAGING REQUESTS (from Doctors) =================

    private JPanel buildLabRequestPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        labRequestTableModel = new DefaultTableModel(
                new String[]{"Req ID", "Patient", "Doctor", "Test Type", "Notes", "Status", "Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        labRequestTable = new JTable(labRequestTableModel);
        UITheme.styleTable(labRequestTable);
        refreshLabRequestTable();

        JPanel tableCard = UITheme.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        tableCard.add(new JScrollPane(labRequestTable), BorderLayout.CENTER);

        JButton completeBtn = UITheme.primaryButton("Mark Selected as Completed");
        completeBtn.addActionListener(e -> completeLabRequest());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.add(completeBtn);

        panel.add(tableCard, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshLabRequestTable() {
        labRequestList = FileManager.loadLabRequests();
        labRequestTableModel.setRowCount(0);
        for (LabRequest r : labRequestList) {
            labRequestTableModel.addRow(new Object[]{r.getRequestId(), r.getPatientName(), r.getDoctorName(),
                    r.getTestType(), r.getNotes(), r.getStatus(), r.getDate()});
        }
    }

    private void completeLabRequest() {
        int row = labRequestTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a request from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String reqId = (String) labRequestTableModel.getValueAt(row, 0);
        for (LabRequest r : labRequestList) {
            if (r.getRequestId().equals(reqId)) {
                r.setStatus("Completed");
                break;
            }
        }
        FileManager.saveLabRequests(labRequestList);
        refreshLabRequestTable();
    }

    // ================= EDIT PERSONAL PROFILE =================

    private JPanel buildProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        JPanel formCard = UITheme.card();
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
        formCard.setPreferredSize(new Dimension(480, 260));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 14));
        form.setOpaque(false);
        form.setBorder(UITheme.sectionTitle("Account Details"));

        profileNameField = new JTextField(loggedInAdmin.getName());
        profilePasswordField = new JPasswordField(loggedInAdmin.getPassword());
        UITheme.styleTextField(profileNameField);
        UITheme.styleTextField(profilePasswordField);

        form.add(formLabel("User ID:"));
        form.add(formLabel(loggedInAdmin.getUserId()));
        form.add(formLabel("Username:"));
        form.add(formLabel(loggedInAdmin.getUsername()));
        form.add(formLabel("Full Name:"));
        form.add(profileNameField);
        form.add(formLabel("Password:"));
        form.add(profilePasswordField);

        JButton saveBtn = UITheme.primaryButton("Save Changes");
        saveBtn.addActionListener(e -> saveAdminProfile());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.add(saveBtn);

        formCard.add(form, BorderLayout.CENTER);
        formCard.add(btnPanel, BorderLayout.SOUTH);
        panel.add(formCard);
        return panel;
    }

    private void saveAdminProfile() {
        if (profileNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        loggedInAdmin.setName(profileNameField.getText().trim());
        loggedInAdmin.setPassword(PasswordUtil.resolvePassword(loggedInAdmin.getPassword(), new String(profilePasswordField.getPassword())));
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserId().equals(loggedInAdmin.getUserId())) {
                userList.set(i, loggedInAdmin);
                break;
            }
        }
        FileManager.saveUsers(userList);
        setTitle("MediCore HMS - Admin Console (" + loggedInAdmin.getName() + ")");
        JOptionPane.showMessageDialog(this, "Profile updated.");
    }
}
