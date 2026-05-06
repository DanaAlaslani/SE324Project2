package gui.panels;

import auth.SessionManager;
import gui.util.AppColors;
import gui.util.AppFonts;
import gui.util.UIHelper;
import user.*;
import user.dao.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProfilePanel extends JPanel {

    private JTextField nameField, emailField, phoneField;
    private JLabel bmiLabel, roleLabel;

    public ProfilePanel() {
        setBackground(AppColors.BG_PRIMARY);
        setLayout(new BorderLayout());
        build();
    }

    private void build() {
        User user = SessionManager.getCurrentUser();

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(AppColors.BG_PRIMARY);
        header.setBorder(new EmptyBorder(24, 28, 8, 28));
        header.add(UIHelper.heading("My Profile"));

        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(AppColors.BG_PRIMARY);
        body.setBorder(new EmptyBorder(8, 28, 28, 28));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill   = GridBagConstraints.HORIZONTAL;

        // ── Info card ──
        JPanel infoCard = UIHelper.card();
        infoCard.setLayout(new GridBagLayout());

        GridBagConstraints ig = new GridBagConstraints();
        ig.insets = new Insets(8, 8, 8, 8);
        ig.fill   = GridBagConstraints.HORIZONTAL;
        ig.anchor = GridBagConstraints.WEST;

        roleLabel = UIHelper.label(user.getRole(), AppFonts.SUBHEADING, AppColors.ACCENT);
        ig.gridx = 0; ig.gridy = 0; ig.gridwidth = 2;
        infoCard.add(roleLabel, ig);

        ig.gridwidth = 1;
        nameField  = addFormRow(infoCard, ig, "Full Name",    user.getName(),  1);
        emailField = addFormRow(infoCard, ig, "Email",        user.getEmail(), 2);
        phoneField = addFormRow(infoCard, ig, "Phone",        user.getPhone() != null ? user.getPhone() : "", 3);

        ig.gridy = 4; ig.gridx = 0;
        infoCard.add(UIHelper.muted("Date of Birth"), ig);
        ig.gridx = 1;
        infoCard.add(UIHelper.label(user.getDob(), AppFonts.BODY, AppColors.TEXT_PRIMARY), ig);

        // BMI row (trainee only)
        if (user instanceof Trainee t) {
            ig.gridy = 5; ig.gridx = 0;
            infoCard.add(UIHelper.muted("BMI"), ig);
            ig.gridx = 1;
            float bmi = t.getBmi();
            String cat = bmi < 18.5f ? "Underweight" : bmi < 25f ? "Normal" : bmi < 30f ? "Overweight" : "Obese";
            Color bmiColor = bmi < 18.5f || bmi >= 30f ? AppColors.DANGER
                           : bmi < 25f               ? AppColors.SUCCESS : AppColors.WARNING;
            bmiLabel = UIHelper.label(String.format("%.2f  (%s)", bmi, cat), AppFonts.BODY, bmiColor);
            infoCard.add(bmiLabel, ig);

            ig.gridy = 6; ig.gridx = 0;
            infoCard.add(UIHelper.muted("Fitness Goal"), ig);
            ig.gridx = 1;
            infoCard.add(UIHelper.label(t.getFitnessGoal(), AppFonts.BODY, AppColors.TEXT_PRIMARY), ig);
        }

        if (user instanceof Trainer tr) {
            ig.gridy = 5; ig.gridx = 0;
            infoCard.add(UIHelper.muted("Specialization"), ig);
            ig.gridx = 1;
            infoCard.add(UIHelper.label(tr.getSpecialization(), AppFonts.BODY, AppColors.TEXT_PRIMARY), ig);
            ig.gridy = 6; ig.gridx = 0;
            infoCard.add(UIHelper.muted("Monthly Fee"), ig);
            ig.gridx = 1;
            infoCard.add(UIHelper.label(tr.getFee() + " SAR", AppFonts.BODY, AppColors.ACCENT), ig);
        }

        if (user instanceof Nutritionist n) {
            ig.gridy = 5; ig.gridx = 0;
            infoCard.add(UIHelper.muted("Specialization"), ig);
            ig.gridx = 1;
            infoCard.add(UIHelper.label(n.getSpecialization(), AppFonts.BODY, AppColors.TEXT_PRIMARY), ig);
            ig.gridy = 6; ig.gridx = 0;
            infoCard.add(UIHelper.muted("Monthly Fee"), ig);
            ig.gridx = 1;
            infoCard.add(UIHelper.label(n.getFee() + " SAR", AppFonts.BODY, AppColors.ACCENT), ig);
        }

        // ── Buttons ──
        JButton saveBtn = UIHelper.primaryButton("Save Changes");
        JButton pwdBtn  = UIHelper.ghostButton("Change Password");

        ig.gridy = 10; ig.gridx = 0; ig.gridwidth = 2;
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(AppColors.BG_CARD);
        btnRow.add(saveBtn);
        btnRow.add(pwdBtn);
        infoCard.add(btnRow, ig);

        saveBtn.addActionListener(e -> saveProfile());
        pwdBtn.addActionListener(e  -> changePassword());

        g.gridx = 0; g.gridy = 0; g.weightx = 1; g.weighty = 1;
        g.anchor = GridBagConstraints.NORTHWEST;
        body.add(infoCard, g);

        add(header, BorderLayout.NORTH);
        add(body,   BorderLayout.CENTER);
    }

    private JTextField addFormRow(JPanel panel, GridBagConstraints g,
                                   String label, String value, int row) {
        g.gridy = row; g.gridx = 0;
        panel.add(UIHelper.muted(label), g);
        g.gridx = 1;
        JTextField field = UIHelper.inputField(value);
        field.setText(value);
        field.setPreferredSize(new Dimension(260, 36));
        panel.add(field, g);
        return field;
    }

    private void saveProfile() {
        String name  = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            UIHelper.showError(this, "Name and email cannot be empty.");
            return;
        }
        try {
            UserDAO.updateProfile(SessionManager.getUserId(), name, email, phone);
            SessionManager.getCurrentUser().editProfile(name, email, phone);
            UIHelper.showSuccess(this, "Profile updated successfully.");
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to save: " + ex.getMessage());
        }
    }

    private void changePassword() {
        JPasswordField pw1 = new JPasswordField(16);
        JPasswordField pw2 = new JPasswordField(16);
        JPanel panel = new JPanel(new GridLayout(4, 1, 4, 4));
        panel.add(new JLabel("New Password:"));
        panel.add(pw1);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(pw2);

        int res = JOptionPane.showConfirmDialog(this, panel,
                "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        String p1 = new String(pw1.getPassword());
        String p2 = new String(pw2.getPassword());

        if (!p1.equals(p2)) {
            UIHelper.showError(this, "Passwords do not match."); return;
        }
        if (p1.length() < 6) {
            UIHelper.showError(this, "Password must be at least 6 characters."); return;
        }
        try {
            UserDAO.resetPassword(SessionManager.getUserId(), p1);
            UIHelper.showSuccess(this, "Password changed successfully.");
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed: " + ex.getMessage());
        }
    }
}
