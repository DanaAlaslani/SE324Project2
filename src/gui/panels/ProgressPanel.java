package gui.panels;

import auth.SessionManager;
import gui.util.AppColors;
import gui.util.AppFonts;
import gui.util.UIHelper;
import progress.dao.ProgressDAO;
import user.Trainee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProgressPanel extends JPanel {

    private JTable    historyTable;
    private JLabel    currentWeightLbl, progressLbl, predictedLbl;
    private JProgressBar progressBar;

    public ProgressPanel() {
        setBackground(AppColors.BG_PRIMARY);
        setLayout(new BorderLayout());
        build();
    }

    private void build() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(AppColors.BG_PRIMARY);
        header.setBorder(new EmptyBorder(24, 28, 8, 28));
        header.add(UIHelper.heading("Progress Tracker"));

        JPanel body = new JPanel(new GridLayout(1, 2, 16, 0));
        body.setBackground(AppColors.BG_PRIMARY);
        body.setBorder(new EmptyBorder(8, 28, 28, 28));

        body.add(buildLogPanel());
        body.add(buildStatsPanel());

        add(header, BorderLayout.NORTH);
        add(body,   BorderLayout.CENTER);

        loadLatest();
        loadHistory();
    }

    private JPanel buildLogPanel() {
        JPanel card = UIHelper.card();
        card.setLayout(new BorderLayout(0, 12));
        card.add(UIHelper.label("Log Weight", AppFonts.SUBHEADING, AppColors.TEXT_PRIMARY), BorderLayout.NORTH);

        Trainee t = (Trainee) SessionManager.getCurrentUser();

        JTextField weightF  = UIHelper.inputField("Current weight (kg)");
        JTextField targetF  = UIHelper.inputField("Target weight (kg)");
        JTextField dateF    = UIHelper.inputField("YYYY-MM-DD");
        dateF.setText(java.time.LocalDate.now().toString());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(AppColors.BG_CARD);

        form.add(row("Date",             dateF));   form.add(Box.createVerticalStrut(10));
        form.add(row("Weight (kg)",      weightF)); form.add(Box.createVerticalStrut(10));
        form.add(row("Target (kg)",      targetF)); form.add(Box.createVerticalStrut(20));

        JButton logBtn = UIHelper.primaryButton("Save Record");
        logBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        form.add(logBtn);

        card.add(form, BorderLayout.CENTER);

        JLabel histTitle = UIHelper.label("History", AppFonts.SUBHEADING, AppColors.TEXT_PRIMARY);
        historyTable = UIHelper.styledTable(new String[]{"Date", "Weight (kg)", "Progress %", "Est. Completion"});

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setBackground(AppColors.BG_CARD);
        bottom.add(histTitle,                           BorderLayout.NORTH);
        bottom.add(UIHelper.scrollPane(historyTable),   BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        logBtn.addActionListener(e -> {
            String date   = dateF.getText().trim();
            String wStr   = weightF.getText().trim();
            String tStr   = targetF.getText().trim();

            if (wStr.isEmpty() || tStr.isEmpty()) {
                UIHelper.showError(this, "Please enter weight and target."); return;
            }
            try {
                float w  = Float.parseFloat(wStr);
                float tg = Float.parseFloat(tStr);
                float h  = t.getHeight() > 0 ? t.getHeight() : 170f;
                float init = t.getWeight() > 0 ? t.getWeight() : w;

                ProgressDAO.saveRecord(SessionManager.getUserId(), date, w, h, init, tg);
                UIHelper.showSuccess(this, "Progress saved!");
                loadLatest();
                loadHistory();
            } catch (Exception ex) {
                UIHelper.showError(this, "Failed: " + ex.getMessage());
            }
        });

        return card;
    }

    private JPanel buildStatsPanel() {
        JPanel card = UIHelper.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel statsTitle = UIHelper.label("Current Stats", AppFonts.SUBHEADING, AppColors.TEXT_PRIMARY);
        statsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsTitle.setBorder(new EmptyBorder(0, 0, 16, 0));

        currentWeightLbl = UIHelper.label("Weight: —", AppFonts.BODY, AppColors.TEXT_PRIMARY);
        progressLbl      = UIHelper.label("Progress: —", AppFonts.BODY, AppColors.TEXT_PRIMARY);
        predictedLbl     = UIHelper.label("Est. Goal Date: —", AppFonts.BODY, AppColors.TEXT_MUTED);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(AppColors.SUCCESS);
        progressBar.setBackground(AppColors.INPUT_BG);
        progressBar.setFont(AppFonts.SMALL);
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (JLabel lbl : new JLabel[]{currentWeightLbl, progressLbl, predictedLbl}) {
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        // BMI card
        Trainee t = (Trainee) SessionManager.getCurrentUser();
        float   bmi = t.getBmi();
        String  cat = bmi < 18.5f ? "Underweight" : bmi < 25f ? "Normal" : bmi < 30f ? "Overweight" : "Obese";
        Color   col = bmi < 18.5f || bmi >= 30f ? AppColors.DANGER
                    : bmi < 25f                ? AppColors.SUCCESS : AppColors.WARNING;

        JPanel bmiCard = UIHelper.card();
        bmiCard.setLayout(new BoxLayout(bmiCard, BoxLayout.Y_AXIS));
        bmiCard.setBackground(AppColors.BG_PRIMARY);
        JLabel bmiTitle = UIHelper.muted("Body Mass Index");
        JLabel bmiVal   = UIHelper.label(String.format("%.2f", bmi), AppFonts.TITLE, col);
        JLabel bmiCat   = UIHelper.label(cat, AppFonts.BODY, col);
        bmiTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        bmiVal.setAlignmentX(Component.LEFT_ALIGNMENT);
        bmiCat.setAlignmentX(Component.LEFT_ALIGNMENT);
        bmiCard.add(bmiTitle);
        bmiCard.add(Box.createVerticalStrut(4));
        bmiCard.add(bmiVal);
        bmiCard.add(bmiCat);

        card.add(statsTitle);
        card.add(currentWeightLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(progressLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(progressBar);
        card.add(Box.createVerticalStrut(8));
        card.add(predictedLbl);
        card.add(Box.createVerticalStrut(24));
        card.add(bmiCard);

        return card;
    }

    private JPanel row(String label, JTextField field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(AppColors.BG_CARD);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = UIHelper.muted(label);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        p.add(field);
        return p;
    }

    private void loadLatest() {
        try {
            String[] r = ProgressDAO.getLatestRecord(SessionManager.getUserId());
            if (r == null) return;
            currentWeightLbl.setText("Weight: " + r[1] + " kg   (Start: " + r[2] + " kg,  Target: " + r[3] + " kg)");
            float pct = Float.parseFloat(r[4]);
            progressLbl.setText(String.format("Progress: %.1f%%", pct));
            progressBar.setValue((int) pct);
            progressBar.setString(String.format("%.1f%%", pct));
            predictedLbl.setText("Est. Goal Date: " + r[5]);
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to load stats: " + ex.getMessage());
        }
    }

    private void loadHistory() {
        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0);
        try {
            List<String[]> rows = ProgressDAO.getRecordsByTrainee(SessionManager.getUserId());
            for (String[] r : rows) model.addRow(r);
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to load history: " + ex.getMessage());
        }
    }
}
