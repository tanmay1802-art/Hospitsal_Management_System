package hms;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * Central place for the app's look and feel: colors, fonts, and small helper
 * methods used to style Swing components consistently across screens.
 * 
 * Palette: "Health Green" - deep forest green for structure/nav, a brighter
 * leaf green for actions/accents, and warm off-white surfaces for content.
 * 
 * [Integrated and structured by Tanmay - System UI & Core Theme Configuration]
 */
public final class UITheme {

    // Private constructor to prevent instantiation (Utility class pattern)
    private UITheme() { }

    // ---- Color Palette (Health Green Theme) ----
    public static final Color PRIMARY_DARK   = new Color(0x0B, 0x3D, 0x2E); // Deep forest green (Sidebar / Header backgrounds)
    public static final Color PRIMARY        = new Color(0x1B, 0x5E, 0x45); // Standard forest green
    public static final Color PRIMARY_LIGHT  = new Color(0x2E, 0x7D, 0x5B); // Hover effects / Secondary elements
    public static final Color ACCENT         = new Color(0x4C, 0xAF, 0x50); // Leaf green for primary action buttons (Save, Create, Login)
    public static final Color ACCENT_DARK    = new Color(0x3D, 0x8B, 0x40); // Pressed/Hover state for accent buttons
    public static final Color DANGER         = new Color(0xC6, 0x3B, 0x3B); // Red color for destructive actions (Delete / Remove)
    public static final Color WARNING        = new Color(0xE0, 0xA5, 0x2E); // Orange/Yellow for update/caution warnings

    public static final Color BACKGROUND     = new Color(0xF3, 0xF7, 0xF3); // Main window background color
    public static final Color SURFACE        = Color.WHITE;                 // Cards / Panels background surface color
    public static final Color BORDER         = new Color(0xDD, 0xE7, 0xDE); // Light border color for separation
    public static final Color TEXT_DARK      = new Color(0x1E, 0x2A, 0x22); // Primary dark text color for readability
    public static final Color TEXT_MUTED     = new Color(0x64, 0x77, 0x6A); // Subdued text for secondary labels
    public static final Color TEXT_ON_DARK   = new Color(0xEA, 0xF3, 0xEC); // Light text used on dark green backgrounds
    public static final Color TABLE_STRIPE   = new Color(0xEE, 0xF6, 0xEF); // Alternative row color for tables (striped look)
    public static final Color SELECTION      = new Color(0xC8, 0xE6, 0xC9); // Background color when a table row is selected

    // ---- Typography / Fonts ----
    public static final Font FONT_LOGO       = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONT_TITLE      = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE   = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_HEADING    = new Font("SansSerif", Font.BOLD, 15);
    public static final Font FONT_LABEL      = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BODY       = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BUTTON     = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_NAV        = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_NAV_BOLD   = new Font("SansSerif", Font.BOLD, 14);

    // ---- Button Styles (Factory Methods) ----

    /** Solid accent-green button used for primary/confirm actions (Create, Assign, Login...). */
    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        baseButton(b);
        b.setBackground(ACCENT);
        b.setForeground(Color.WHITE);
        return b;
    }

    /** Outlined/Primary button used for secondary actions (Update, Clear). */
    public static JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        baseButton(b);
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        return b;
    }

    /** Muted/Red button used exclusively for destructive actions (Delete). */
    public static JButton dangerButton(String text) {
        JButton b = new JButton(text);
        baseButton(b);
        b.setBackground(DANGER);
        b.setForeground(Color.WHITE);
        return b;
    }

    /** Plain-text/ghost button with border (Clear form, cancel-style actions). */
    public static JButton ghostButton(String text) {
        JButton b = new JButton(text);
        baseButton(b);
        b.setBackground(SURFACE);
        b.setForeground(PRIMARY_DARK);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        return b;
    }

    // Common baseline configurations for all buttons to ensure consistent UX
    private static void baseButton(JButton b) {
        b.setFont(FONT_BUTTON);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setBorderPainted(false);
    }

    // ---- Input Fields Styling ----

    // Applies standard font and padding borders to text fields and password fields
    public static void styleTextField(JTextComponent field) {
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }

    // Styles dropdown menus (JComboBox) consistently with the theme
    public static void styleComboBox(JComboBox<?> combo) {
        combo.setFont(FONT_BODY);
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)));
    }

    // ---- Tables Styling ----

    // Customizes JTable rows, height, headers, selection colors, and striped alternating row backgrounds
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(SELECTION);
        table.setSelectionForeground(TEXT_DARK);
        table.setFillsViewportHeight(true);
        table.setGridColor(BORDER);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_HEADING.deriveFont(13f));
        header.setBackground(PRIMARY);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 34));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);

        // Custom cell renderer to handle row striping (even/odd row coloring) and inner padding
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? SURFACE : TABLE_STRIPE);
                }
                return c;
            }
        });
    }

    // ---- Containers & Panels ----

    /** A white "card" panel with rounded corners and a subtle border, used across dashboards for clean layout grouping. */
    public static JPanel card() {
        RoundedPanel p = new RoundedPanel(14);
        p.setBackground(SURFACE);
        return p;
    }

    // Creates styled section titles with custom heading font and primary dark color
    public static TitledBorder sectionTitle(String text) {
        TitledBorder tb = BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), text);
        tb.setTitleFont(FONT_HEADING);
        tb.setTitleColor(PRIMARY_DARK);
        return tb;
    }

    /** Simple rounded-rectangle custom JPanel used for card-style UI components. */
    public static class RoundedPanel extends JPanel {
        private final int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.setColor(BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
