package gui;

import auth.AuthService;
import auth.SessionManager;
import gui.util.AppColors;
import gui.util.AppFonts;
import gui.util.UIHelper;
import user.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private JTextField     emailField;
    private JPasswordField passwordField;
    private JLabel         errorLabel;

    public LoginFrame() {
        setTitle("Gymanice");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 580);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setBackground(AppColors.BG_PRIMARY);
        root.add(buildBrandPanel());
        root.add(buildFormPanel());
        setContentPane(root);
    }

    // ── Left brand panel ──────────────────────────────────────

    private JPanel buildBrandPanel() {
        JPanel p = new JPanel();
        p.setBackground(AppColors.SIDEBAR);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(60, 50, 60, 50));

        JLabel logo = UIHelper.label("GYMANICE", AppFonts.TITLE, AppColors.ACCENT);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = UIHelper.label("Your Personal Fitness Platform",
                AppFonts.BODY, AppColors.TEXT_MUTED);
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(AppColors.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JLabel q1 = UIHelper.label("Track workouts.", AppFonts.SUBHEADING, AppColors.TEXT_PRIMARY);
        JLabel q2 = UIHelper.label("Plan nutrition.", AppFonts.SUBHEADING, AppColors.TEXT_PRIMARY);
        JLabel q3 = UIHelper.label("Reach your goals.", AppFonts.SUBHEADING, AppColors.ACCENT);
        for (JLabel l : new JLabel[]{q1, q2, q3}) l.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel testBox = new JPanel();
        testBox.setLayout(new BoxLayout(testBox, BoxLayout.Y_AXIS));
        testBox.setBackground(AppColors.BG_CARD);
        testBox.setBorder(new EmptyBorder(14, 16, 14, 16));
        testBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        testBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel hint = UIHelper.label("Test Accounts", AppFonts.LABEL, AppColors.ACCENT);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        testBox.add(hint);
        testBox.add(Box.createVerticalStrut(8));

        String[][] accounts = {
            {"Trainee",      "rima@gymanice.com"},
            {"Trainer",      "ahmed@gymanice.com"},
            {"Nutritionist", "sara@gymanice.com"}
        };
        for (String[] a : accounts) {
            JLabel row = UIHelper.label(a[0] + ":  " + a[1] + "  /  Test1234",
                    AppFonts.SMALL, AppColors.TEXT_MUTED);
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            testBox.add(row);
            testBox.add(Box.createVerticalStrut(4));
        }

        p.add(logo);
        p.add(Box.createVerticalStrut(6));
        p.add(tagline);
        p.add(Box.createVerticalStrut(40));
        p.add(sep);
        p.add(Box.createVerticalStrut(30));
        p.add(q1);
        p.add(Box.createVerticalStrut(8));
        p.add(q2);
        p.add(Box.createVerticalStrut(8));
        p.add(q3);
        p.add(Box.createVerticalGlue());
        p.add(testBox);
        return p;
    }

    // ── Right form panel ──────────────────────────────────────

    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(AppColors.BG_PRIMARY);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(AppColors.BG_PRIMARY);
        form.setPreferredSize(new Dimension(340, 400));

        JLabel title = UIHelper.label("Sign In", AppFonts.HEADING, AppColors.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sub = UIHelper.label("Welcome back", AppFonts.SMALL, AppColors.TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField    = UIHelper.inputField("");
        passwordField = UIHelper.passwordField();

        errorLabel = new JLabel(" ");
        errorLabel.setFont(AppFonts.SMALL);
        errorLabel.setForeground(AppColors.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginBtn = UIHelper.primaryButton("Sign In");
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JPanel registerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        registerRow.setBackground(AppColors.BG_PRIMARY);
        registerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel noAcc = UIHelper.muted("Don't have an account?  ");
        JButton regLink = UIHelper.linkButton("Create one");
        registerRow.add(noAcc);
        registerRow.add(regLink);

        form.add(title);
        form.add(Box.createVerticalStrut(4));
        form.add(sub);
        form.add(Box.createVerticalStrut(32));
        form.add(fieldBlock("Email Address", emailField));
        form.add(Box.createVerticalStrut(16));
        form.add(fieldBlock("Password", passwordField));
        form.add(Box.createVerticalStrut(6));
        form.add(errorLabel);
        form.add(Box.createVerticalStrut(20));
        form.add(loginBtn);
        form.add(Box.createVerticalStrut(16));
        form.add(registerRow);

        outer.add(form);

        loginBtn.addActionListener(e -> attemptLogin());
        regLink.addActionListener(e -> { dispose(); new RegisterFrame().setVisible(true); });
        getRootPane().setDefaultButton(loginBtn);
        return outer;
    }

    private JPanel fieldBlock(String labelText, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppColors.BG_PRIMARY);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = UIHelper.fieldLabel(labelText);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        p.add(lbl);
        p.add(Box.createVerticalStrut(5));
        p.add(field);
        return p;
    }

    private void attemptLogin() {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }
        try {
            User user = AuthService.login(email, password);
            if (user == null) {
                errorLabel.setText("Invalid email or password.");
                return;
            }
            SessionManager.login(user);
            dispose();
            new MainFrame().setVisible(true);
        } catch (SQLException ex) {
            UIHelper.showError(this, "Database error: " + ex.getMessage());
        }
    }
}
