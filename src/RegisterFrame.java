package hms;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Self-service account registration (Feature: "User registration").
 *
 * Design note: per Table 1.0, Admin Staff already owns Create/Read/Update/
 * Delete for end users (Doctors, Medical Managers, other Admins are
 * provisioned through AdminDashboard). The one role that logically signs
 * itself up in a real hospital is the Patient, so this public form is
 * scoped to Patient accounts. Worth a line in your report's design
 * justification section.
 */
public class RegisterFrame extends JFrame {

    private JTextField nameField, usernameField, contactField;
    private JPasswordField passwordField, confirmField;

    public RegisterFrame() {
        setTitle(" Hospital_Management_System - Patient Registration");
        setSize(440, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(UITheme.PRIMARY_DARK);
        setContentPane(root);

        UITheme.RoundedPanel card = new UITheme.RoundedPanel(18);
        card.setBackground(UITheme.SURFACE);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(360, 540));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(6, 24, 6, 24);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Create Patient Account", SwingConstants.CENTER);
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY_DARK);
        gbc.gridy = 0;
        gbc.insets = new Insets(26, 24, 16, 24);
        card.add(title, gbc);

        nameField = addField(card, gbc, 1, "Full Name");
        usernameField = addField(card, gbc, 3, "Username");
        passwordField = addPasswordField(card, gbc, 5, "Password");
        confirmField = addPasswordField(card, gbc, 7, "Confirm Password");
        contactField = addField(card, gbc, 9, "Contact Number");

        JButton registerBtn = UITheme.primaryButton("Register");
        registerBtn.setPreferredSize(new Dimension(100, 40));
        gbc.gridy = 11;
        gbc.insets = new Insets(16, 24, 8, 24);
        card.add(registerBtn, gbc);

        JButton backBtn = UITheme.ghostButton("Back to Sign In");
        gbc.gridy = 12;
        gbc.insets = new Insets(0, 24, 24, 24);
        card.add(backBtn, gbc);

        registerBtn.addActionListener(e -> attemptRegister());
        backBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        GridBagConstraints rootGbc = new GridBagConstraints();
        root.add(card, rootGbc);
    }

    private JTextField addField(JPanel card, GridBagConstraints gbc, int row, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_LABEL);
        label.setForeground(UITheme.TEXT_DARK);
        gbc.gridy = row;
        gbc.insets = new Insets(0, 24, 2, 24);
        card.add(label, gbc);

        JTextField field = new JTextField();
        UITheme.styleTextField(field);
        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 24, 10, 24);
        card.add(field, gbc);
        return field;
    }

    private JPasswordField addPasswordField(JPanel card, GridBagConstraints gbc, int row, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_LABEL);
        label.setForeground(UITheme.TEXT_DARK);
        gbc.gridy = row;
        gbc.insets = new Insets(0, 24, 2, 24);
        card.add(label, gbc);

        JPasswordField field = new JPasswordField();
        UITheme.styleTextField(field);
        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 24, 10, 24);
        card.add(field, gbc);
        return field;
    }

    /** Feature: "User registration", with input validation to avoid logical errors. */
    private void attemptRegister() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());
        String contact = contactField.getText().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in every field.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.",
                    "Weak Password", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.",
                    "Password Mismatch", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!contact.matches("[0-9+\\-\\s]{7,15}")) {
            JOptionPane.showMessageDialog(this, "Enter a valid contact number.",
                    "Invalid Contact Number", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<User> users = FileManager.loadUsers();
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                JOptionPane.showMessageDialog(this, "That username is already taken.",
                        "Username Unavailable", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String newId = FileManager.nextUserId(users, "P");
        Patient patient = new Patient(newId, name, username, PasswordUtil.hash(password), contact);
        users.add(patient);
        FileManager.saveUsers(users);

        JOptionPane.showMessageDialog(this,
                "Account created! You can now sign in as " + username + ".",
                "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        new LoginFrame().setVisible(true);
    }
}
