package gui;

import auth.AuthService;
import gui.util.AppColors;
import gui.util.AppFonts;
import gui.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class RegisterFrame extends JFrame {

    private JTextField     usernameField, nameField, emailField, phoneField, dobField;
    private JPasswordField passwordField;
    private JTextField     heightField, weightField, ageField, goalField;
    private JTextField     feeField, credField, specField;
    private JPanel         extraPanel;
    private CardLayout     extraCards;
    private ButtonGroup    roleGroup;
    private JRadioButton   rbTrainee, rbTrainer, rbNutritionist;

    public RegisterFrame() {
        setTitle("Gymanice – Create Account");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(580, 760);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppColors.BG_PRIMARY);

        // Header
        JPanel header = new JPanel();
        header.setBackground(AppColors.SIDEBAR);
        header.setBorder(new EmptyBorder(22, 36, 22, 36));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel title = UIHelper.label("GYMANICE", AppFonts.HEADING, AppColors.ACCENT);
        JLabel sub   = UIHelper.label("Create your account", AppFonts.BODY, AppColors.TEXT_MUTED);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);

        JScrollPane scroll = new JScrollPane(buildForm());
        scroll.setBorder(null);
        scroll.getViewport().setBackground(AppColors.BG_PRIMARY);

        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildForm() {
        JPanel wrap = new JPanel();
        wrap.setBackground(AppColors.BG_PRIMARY);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBorder(new EmptyBorder(24, 36, 36, 36));

        // ── Role selector ──
        wrap.add(sectionLabel("Select Role"));
        wrap.add(Box.createVerticalStrut(10));
        wrap.add(buildRoleSelector());
        wrap.add(Box.createVerticalStrut(20));

        // ── Common fields ──
        wrap.add(sectionLabel("Account Information"));
        wrap.add(Box.createVerticalStrut(10));

        usernameField = UIHelper.inputField("");
        nameField     = UIHelper.inputField("");
        emailField    = UIHelper.inputField("");
        passwordField = UIHelper.passwordField();
        phoneField    = UIHelper.inputField("");
        dobField      = UIHelper.inputField("YYYY-MM-DD");

        wrap.add(row2("Username", usernameField, "Full Name", nameField));
        wrap.add(Box.createVerticalStrut(12));
        wrap.add(row2("Email", emailField, "Password", passwordField));
        wrap.add(Box.createVerticalStrut(12));
        wrap.add(row2("Phone", phoneField, "Date of Birth (YYYY-MM-DD)", dobField));
        wrap.add(Box.createVerticalStrut(20));

        // ── Role-specific fields ──
        extraCards = new CardLayout();
        extraPanel = new JPanel(extraCards);
        extraPanel.setBackground(AppColors.BG_PRIMARY);
        extraPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        extraPanel.add(buildTraineeExtras(),    "TRAINEE");
        extraPanel.add(buildProfExtras("Trainer credentials"),     "TRAINER");
        extraPanel.add(buildProfExtras("Nutritionist credentials"), "NUTRITIONIST");

        wrap.add(extraPanel);
        wrap.add(Box.createVerticalStrut(24));

        JButton registerBtn = UIHelper.primaryButton("Create Account");
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JPanel linkRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linkRow.setBackground(AppColors.BG_PRIMARY);
        linkRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton backBtn = UIHelper.linkButton("Already have an account? Sign in");
        linkRow.add(backBtn);

        wrap.add(registerBtn);
        wrap.add(Box.createVerticalStrut(12));
        wrap.add(linkRow);

        registerBtn.addActionListener(e -> attemptRegister());
        backBtn.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        return wrap;
    }

    private JPanel buildRoleSelector() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBackground(AppColors.BG_PRIMARY);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        roleGroup       = new ButtonGroup();
        rbTrainee       = styledRadio("Trainee",       true);
        rbTrainer       = styledRadio("Trainer",       false);
        rbNutritionist  = styledRadio("Nutritionist",  false);

        roleGroup.add(rbTrainee);
        roleGroup.add(rbTrainer);
        roleGroup.add(rbNutritionist);

        p.add(rbTrainee);
        p.add(Box.createHorizontalStrut(12));
        p.add(rbTrainer);
        p.add(Box.createHorizontalStrut(12));
        p.add(rbNutritionist);

        ActionListener switchRole = e -> {
            if (rbTrainee.isSelected())      extraCards.show(extraPanel, "TRAINEE");
            else if (rbTrainer.isSelected()) extraCards.show(extraPanel, "TRAINER");
            else                             extraCards.show(extraPanel, "NUTRITIONIST");
        };
        rbTrainee.addActionListener(switchRole);
        rbTrainer.addActionListener(switchRole);
        rbNutritionist.addActionListener(switchRole);

        return p;
    }

    private JRadioButton styledRadio(String text, boolean selected) {
        JRadioButton rb = new JRadioButton(text, selected);
        rb.setFont(AppFonts.BUTTON);
        rb.setForeground(AppColors.TEXT_PRIMARY);
        rb.setBackground(AppColors.BG_PRIMARY);
        rb.setFocusPainted(false);
        rb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rb.setBorder(new EmptyBorder(8, 16, 8, 16));
        rb.setOpaque(true);
        rb.setBackground(selected ? AppColors.ACCENT_DARK : AppColors.INPUT_BG);

        rb.addChangeListener(e ->
                rb.setBackground(rb.isSelected() ? AppColors.ACCENT_DARK : AppColors.INPUT_BG));
        return rb;
    }

    private JPanel buildTraineeExtras() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppColors.BG_PRIMARY);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        heightField = UIHelper.inputField("");
        weightField = UIHelper.inputField("");
        ageField    = UIHelper.inputField("");
        goalField   = UIHelper.inputField("e.g. Lose Weight / Build Muscle");

        p.add(sectionLabel("Physical Information"));
        p.add(Box.createVerticalStrut(10));
        p.add(row2("Height (cm)", heightField, "Weight (kg)", weightField));
        p.add(Box.createVerticalStrut(12));
        p.add(row2("Age", ageField, "Fitness Goal", goalField));
        return p;
    }

    private JPanel buildProfExtras(String sectionTitle) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppColors.BG_PRIMARY);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        feeField  = UIHelper.inputField("59 – 89");
        credField = UIHelper.inputField("e.g. NASM Certified");
        specField = UIHelper.inputField("e.g. Strength & Conditioning");

        p.add(sectionLabel(sectionTitle));
        p.add(Box.createVerticalStrut(10));
        p.add(row2("Monthly Fee (SAR)", feeField, "Specialization", specField));
        p.add(Box.createVerticalStrut(12));
        p.add(fieldBlock("Credentials / Certifications", credField));
        return p;
    }

    // ── Layout helpers ────────────────────────────────────────

    private JPanel row2(String l1, JComponent f1, String l2, JComponent f2) {
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setBackground(AppColors.BG_PRIMARY);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(fieldBlock(l1, f1));
        row.add(fieldBlock(l2, f2));
        return row;
    }

    private JPanel fieldBlock(String labelText, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppColors.BG_PRIMARY);
        JLabel lbl = UIHelper.fieldLabel(labelText);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        p.add(lbl);
        p.add(Box.createVerticalStrut(5));
        p.add(field);
        return p;
    }

    private JPanel sectionLabel(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBackground(AppColors.BG_PRIMARY);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = UIHelper.label(text, AppFonts.SUBHEADING, AppColors.ACCENT);
        p.add(lbl);
        return p;
    }

    // ── Registration logic ────────────────────────────────────

    private void attemptRegister() {
        String role     = rbTrainer.isSelected() ? "TRAINER"
                        : rbNutritionist.isSelected() ? "NUTRITIONIST" : "TRAINEE";
        String username = usernameField.getText().trim();
        String name     = nameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String phone    = phoneField.getText().trim();
        String dob      = dobField.getText().trim();

        if (username.isEmpty() || name.isEmpty() || email.isEmpty()
                || password.isEmpty() || dob.isEmpty()) {
            UIHelper.showError(this, "Please fill in all required fields.");
            return;
        }
        if (password.length() < 6) {
            UIHelper.showError(this, "Password must be at least 6 characters.");
            return;
        }
        try {
            boolean ok;
            switch (role) {
                case "TRAINEE" -> {
                    float h   = parseFloat(heightField.getText(), 170);
                    float w   = parseFloat(weightField.getText(), 70);
                    int   age = parseInt(ageField.getText(), 20);
                    ok = AuthService.registerTrainee(username, email, password, name,
                            dob, phone, h, w, age, goalField.getText().trim());
                }
                case "TRAINER" -> {
                    float fee = parseFloat(feeField.getText(), 69);
                    ok = AuthService.registerTrainer(username, email, password, name,
                            dob, phone, fee, credField.getText().trim(), specField.getText().trim());
                }
                default -> {
                    float fee = parseFloat(feeField.getText(), 69);
                    ok = AuthService.registerNutritionist(username, email, password, name,
                            dob, phone, fee, credField.getText().trim(), specField.getText().trim());
                }
            }
            if (ok) {
                UIHelper.showSuccess(this, "Account created! You can now sign in.");
                dispose();
                new LoginFrame().setVisible(true);
            }
        } catch (SQLException ex) {
            String msg = ex.getMessage();
            if (msg != null && (msg.contains("Duplicate") || msg.contains("UNIQUE")))
                UIHelper.showError(this, "Email or username already in use.");
            else
                UIHelper.showError(this, "Registration failed: " + msg);
        }
    }

    private float parseFloat(String s, float def) {
        try { return Float.parseFloat(s.trim()); } catch (Exception e) { return def; }
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }
}
