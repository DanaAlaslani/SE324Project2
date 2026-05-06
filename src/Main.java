import db.DatabaseConnection;
import gui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection.initialize();
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new LoginFrame().setVisible(true);
        });
    }
}
