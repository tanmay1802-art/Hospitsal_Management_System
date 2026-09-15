package hms;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Login screen (Feature: "Login access"). Routes each of the 4 roles to its own dashboard. */
public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Hospital_Management_System - Sign In");
        setSize(420, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(UITheme.PRIMARY_DARK);
        setContentPane(root);

        UITheme.RoundedPanel card = new UITheme.RoundedPanel(18);
        card.setBackground(UITheme.SURFACE);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(340, 440));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(6, 24, 6, 24);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel badgeWrap = new JPanel();
        badgeWrap.setOpaque(false);
        badgeWrap.add(roundBadge());

        gbc.gridy = 0;
        gbc.insets = new Insets(28, 24, 4, 24);
        card.add(badgeWrap, gbc);

        JLabel title = new JLabel("Hospital_Management_System", SwingConstants.CENTER);
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY_DARK);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 24, 0, 24);
        card.add(title, gbc);

        JLabel subtitle = new JLabel("Sign in to continue", SwingConstants.CENTER);
        subtitle.setFont(UITheme.FONT_SUBTITLE);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 24, 22, 24);
        card.add(subtitle, gbc);

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(UITheme.FONT_LABEL);
        userLabel.setForeground(UITheme.TEXT_DARK);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 24, 2, 24);
        card.add(userLabel, gbc);

        usernameField = new JTextField();
        UITheme.styleTextField(usernameField);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 24, 14, 24);
        card.add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(UITheme.FONT_LABEL);
        passLabel.setForeground(UITheme.TEXT_DARK);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 24, 2, 24);
        card.add(passLabel, gbc);

        passwordField = new JPasswordField();
        UITheme.styleTextField(passwordField);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 24, 20, 24);
        card.add(passwordField, gbc);

        JButton loginBtn = UITheme.primaryButton("Sign In");
        loginBtn.setPreferredSize(new Dimension(100, 40));
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 24, 14, 24);
        card.add(loginBtn, gbc);

        JLabel hint = new JLabel("<html><center>Default admin login &mdash; admin / admin123 , patient/patient ,doctor/doctor ,medicalmanager/manager</center></html>", SwingConstants.CENTER);

        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(UITheme.TEXT_MUTED);
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 24, 6, 24);
        card.add(hint, gbc);

        JButton registerLink = UITheme.ghostButton("New patient? Register here");
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 24, 22, 24);
        card.add(registerLink, gbc);

        loginBtn.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());
        registerLink.addActionListener(e -> {
            dispose();
            new RegisterFrame().setVisible(true);
        });

        GridBagConstraints rootGbc = new GridBagConstraints();
        root.add(card, rootGbc);
    }

    private JComponent roundBadge() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.ACCENT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2, cy = getHeight() / 2, arm = 11;
                g2.drawLine(cx - arm, cy, cx + arm, cy);
                g2.drawLine(cx, cy - arm, cx, cy + arm);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(60, 60));
        return p;
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        List<User> users = FileManager.loadUsers();
        User matched = null;
        for (User u : users) {
            if (u.getUsername().equals(username) && PasswordUtil.matches(password, u.getPassword())) {
                matched = u;
                break;
            }
        }

        if (matched == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed Contact Tanmay_Sarkar_Emon(Comibinator)", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Routes to each role's dashboard. This is POLYMORPHISM in action: LoginFrame
        // only knows it has a User, but the actual runtime type (checked once here)
        // decides which concrete dashboard opens.
        JOptionPane.showMessageDialog(this, "Welcome, " + matched.getName() + "!");
        if (matched instanceof AdminStaff) {
            new AdminDashboard((AdminStaff) matched).setVisible(true);
        } else if (matched instanceof Doctor) {
            new DoctorDashboard((Doctor) matched).setVisible(true);
        } else if (matched instanceof MedicalManager) {
            new MedicalManagerDashboard((MedicalManager) matched).setVisible(true);
        } else if (matched instanceof Patient) {
            new PatientDashboard((Patient) matched).setVisible(true);
        }
        dispose();
    }
}
