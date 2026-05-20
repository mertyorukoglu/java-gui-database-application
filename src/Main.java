import db.DatabaseManager;
import ui.LoginFrame;
import util.UITheme;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        
        try {
            DatabaseManager.getInstance();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Failed to initialize database:\n" + e.getMessage(),
                "Startup Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            UITheme.applyGlobalLook();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
