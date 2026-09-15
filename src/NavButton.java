package hms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A sidebar navigation entry: a small hand-drawn flat icon plus a label,
 * with a highlighted state for whichever section is currently active.
 * Drawn with Graphics2D so it never depends on an icon font/image asset.
 */
public class NavButton extends JPanel {

    public enum Icon { USERS, ASSIGN, ASSETS, BILLING, LOGOUT, PROFILE, CALENDAR, RECORDS, STAR, LAB, RX, REPORT }

    private final Icon icon;
    private final String label;
    private boolean selected;
    private boolean hover;

    public NavButton(Icon icon, String label) {
        this.icon = icon;
        this.label = label;
        setOpaque(false);
        setPreferredSize(new Dimension(210, 44));
        setMaximumSize(new Dimension(210, 44));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
        });
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        if (selected) {
            g2.setColor(UITheme.ACCENT);
            g2.fillRoundRect(0, 0, w, h, 10, 10);
        } else if (hover) {
            g2.setColor(UITheme.PRIMARY_LIGHT);
            g2.fillRoundRect(0, 0, w, h, 10, 10);
        }

        Color fg = UITheme.TEXT_ON_DARK;
        int cx = 26, cy = h / 2;
        g2.setColor(fg);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawIcon(g2, icon, cx, cy);

        g2.setFont(selected ? UITheme.FONT_NAV_BOLD : UITheme.FONT_NAV);
        g2.setColor(fg);
        FontMetrics fm = g2.getFontMetrics();
        int textY = cy + fm.getAscent() / 2 - 2;
        g2.drawString(label, 48, textY);

        g2.dispose();
    }

    private void drawIcon(Graphics2D g2, Icon icon, int cx, int cy) {
        switch (icon) {
            case USERS:
                g2.drawOval(cx - 6, cy - 9, 8, 8);
                g2.drawArc(cx - 10, cy - 1, 14, 12, 0, 180);
                g2.drawOval(cx + 1, cy - 6, 6, 6);
                g2.drawArc(cx - 1, cy - 1, 12, 10, 0, 160);
                break;
            case ASSIGN:
                g2.drawOval(cx - 9, cy - 2, 7, 7);
                g2.drawOval(cx + 3, cy - 8, 7, 7);
                g2.drawLine(cx - 3, cy + 1, cx + 3, cy - 5);
                break;
            case ASSETS:
                g2.drawRoundRect(cx - 9, cy - 7, 18, 14, 3, 3);
                g2.drawLine(cx - 9, cy - 1, cx + 9, cy - 1);
                break;
            case BILLING:
                g2.drawRoundRect(cx - 9, cy - 6, 18, 12, 3, 3);
                g2.drawLine(cx - 9, cy - 1, cx + 9, cy - 1);
                g2.drawLine(cx - 5, cy + 2, cx - 2, cy + 2);
                break;
            case LOGOUT:
                g2.drawRoundRect(cx - 8, cy - 8, 10, 16, 3, 3);
                g2.drawLine(cx - 1, cy, cx + 9, cy);
                g2.drawLine(cx + 5, cy - 4, cx + 9, cy);
                g2.drawLine(cx + 5, cy + 4, cx + 9, cy);
                break;
            case PROFILE:
                g2.drawOval(cx - 4, cy - 9, 8, 8);
                g2.drawArc(cx - 9, cy - 1, 18, 14, 0, 180);
                break;
            case CALENDAR:
                g2.drawRoundRect(cx - 9, cy - 7, 18, 15, 3, 3);
                g2.drawLine(cx - 9, cy - 2, cx + 9, cy - 2);
                g2.drawLine(cx - 5, cy - 9, cx - 5, cy - 5);
                g2.drawLine(cx + 5, cy - 9, cx + 5, cy - 5);
                break;
            case RECORDS:
                g2.drawRoundRect(cx - 8, cy - 9, 16, 18, 2, 2);
                g2.drawLine(cx - 4, cy - 4, cx + 4, cy - 4);
                g2.drawLine(cx - 4, cy, cx + 4, cy);
                g2.drawLine(cx - 4, cy + 4, cx + 4, cy + 4);
                break;
            case STAR: {
                int[] xs = new int[10], ys = new int[10];
                double outerR = 9, innerR = 4;
                for (int i = 0; i < 10; i++) {
                    double ang = Math.PI / 2 + i * Math.PI / 5;
                    double r = (i % 2 == 0) ? outerR : innerR;
                    xs[i] = (int) Math.round(cx + r * Math.cos(ang));
                    ys[i] = (int) Math.round(cy - r * Math.sin(ang));
                }
                g2.drawPolygon(xs, ys, 10);
                break;
            }
            case LAB:
                g2.drawLine(cx - 3, cy - 9, cx - 3, cy - 1);
                g2.drawLine(cx + 3, cy - 9, cx + 3, cy - 1);
                g2.drawLine(cx - 5, cy - 9, cx + 5, cy - 9);
                g2.drawLine(cx - 3, cy - 1, cx - 7, cy + 7);
                g2.drawLine(cx + 3, cy - 1, cx + 7, cy + 7);
                g2.drawLine(cx - 7, cy + 7, cx + 7, cy + 7);
                break;
            case RX:
                g2.drawRoundRect(cx - 9, cy - 4, 12, 8, 8, 8);
                g2.drawLine(cx + 1, cy - 3, cx + 8, cy - 8);
                g2.drawLine(cx + 3, cy + 1, cx + 8, cy + 6);
                break;
            case REPORT:
                g2.drawRoundRect(cx - 8, cy - 9, 16, 18, 2, 2);
                g2.drawLine(cx - 4, cy + 4, cx - 4, cy);
                g2.drawLine(cx, cy + 4, cx, cy - 3);
                g2.drawLine(cx + 4, cy + 4, cx + 4, cy - 6);
                break;
        }
    }
}
