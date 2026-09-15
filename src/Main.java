package hms;
/**
 * Application entry point. Initializes the flat-file data store, then
 * launches the login screen on the Swing event dispatch thread.
 */

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Any DataAccessException (or other runtime exception) thrown from inside a button
        // click, table refresh, etc. runs on the Event Dispatch Thread and would otherwise
        // just print a stack trace to stderr and leave the user staring at a frozen dialog.
        // Registering a default handler here catches it centrally and shows a real message,
        // without having to wrap every single FileManager call site in its own try/catch.
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            ex.printStackTrace();
            String message = (ex instanceof DataAccessException)
                    ? ex.getMessage()
                    : "An unexpected error occurred: " + ex.getMessage();
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        });

        hms.FileManager.initialize();
        SwingUtilities.invokeLater(() -> new hms.LoginFrame().setVisible(true));
    }
}

