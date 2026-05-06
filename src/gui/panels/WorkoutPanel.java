package gui.panels;

import auth.SessionManager;
import gui.util.AppColors;
import gui.util.AppFonts;
import gui.util.UIHelper;
import workout.dao.WorkoutDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class WorkoutPanel extends JPanel {

    private JTable  scheduleTable;
    private JTable  exerciseTable;
    private JLabel  scheduleTitle;
    private int     selectedScheduleId = -1;

    public WorkoutPanel() {
        setBackground(AppColors.BG_PRIMARY);
        setLayout(new BorderLayout());
        build();
    }

    private void build() {
        // ── Header ──
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(AppColors.BG_PRIMARY);
        header.setBorder(new EmptyBorder(24, 28, 8, 28));
        header.add(UIHelper.heading("Workout Schedule"));

        // ── Body split: left = schedules, right = exercises ──
        JPanel body = new JPanel(new GridLayout(1, 2, 16, 0));
        body.setBackground(AppColors.BG_PRIMARY);
        body.setBorder(new EmptyBorder(8, 28, 28, 28));

        body.add(buildSchedulePanel());
        body.add(buildExercisePanel());

        add(header, BorderLayout.NORTH);
        add(body,   BorderLayout.CENTER);

        loadSchedules();
    }

    private JPanel buildSchedulePanel() {
        JPanel card = UIHelper.card();
        card.setLayout(new BorderLayout(0, 12));

        JLabel title = UIHelper.label("My Schedules", AppFonts.SUBHEADING, AppColors.TEXT_PRIMARY);

        scheduleTable = UIHelper.styledTable(
                new String[]{"ID", "Date", "Trainer", "Notes"});
        scheduleTable.getColumnModel().getColumn(0).setMaxWidth(40);
        scheduleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadExercisesForSelected();
        });

        JButton refreshBtn = UIHelper.ghostButton("Refresh");
        refreshBtn.addActionListener(e -> loadSchedules());

        card.add(title,                           BorderLayout.NORTH);
        card.add(UIHelper.scrollPane(scheduleTable), BorderLayout.CENTER);
        card.add(refreshBtn,                      BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildExercisePanel() {
        JPanel card = UIHelper.card();
        card.setLayout(new BorderLayout(0, 12));

        scheduleTitle = UIHelper.label("Exercises  —  select a schedule",
                AppFonts.SUBHEADING, AppColors.TEXT_MUTED);

        exerciseTable = UIHelper.styledTable(
                new String[]{"Exercise", "Muscle", "Sets", "Reps", "Weight (kg)"});

        JButton logBtn = UIHelper.primaryButton("Log Performance");
        logBtn.addActionListener(e -> openLogDialog());

        card.add(scheduleTitle,                    BorderLayout.NORTH);
        card.add(UIHelper.scrollPane(exerciseTable), BorderLayout.CENTER);
        card.add(logBtn,                           BorderLayout.SOUTH);
        return card;
    }

    private void loadSchedules() {
        DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();
        model.setRowCount(0);
        try {
            List<String[]> rows = WorkoutDAO.getSchedulesByTrainee(SessionManager.getUserId());
            for (String[] r : rows) model.addRow(r);
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to load schedules: " + ex.getMessage());
        }
    }

    private void loadExercisesForSelected() {
        int row = scheduleTable.getSelectedRow();
        if (row < 0) return;

        selectedScheduleId = Integer.parseInt(
                scheduleTable.getModel().getValueAt(row, 0).toString());
        String date = scheduleTable.getModel().getValueAt(row, 1).toString();

        scheduleTitle.setText("Exercises  —  Schedule " + date);
        scheduleTitle.setForeground(AppColors.TEXT_PRIMARY);

        DefaultTableModel model = (DefaultTableModel) exerciseTable.getModel();
        model.setRowCount(0);
        try {
            List<String[]> rows = WorkoutDAO.getExercisesForSchedule(selectedScheduleId);
            for (String[] r : rows) {
                model.addRow(new Object[]{r[1], r[2], r[4], r[5], r[6]});
            }
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to load exercises: " + ex.getMessage());
        }
    }

    private void openLogDialog() {
        if (selectedScheduleId < 0) {
            UIHelper.showError(this, "Please select a schedule first.");
            return;
        }
        int exRow = exerciseTable.getSelectedRow();
        if (exRow < 0) {
            UIHelper.showError(this, "Please select an exercise to log.");
            return;
        }

        String exName = exerciseTable.getModel().getValueAt(exRow, 0).toString();

        // Get exercise id from raw schedule exercises data
        List<String[]> exList;
        try {
            exList = WorkoutDAO.getExercisesForSchedule(selectedScheduleId);
        } catch (Exception ex) {
            UIHelper.showError(this, ex.getMessage()); return;
        }
        int exerciseId = Integer.parseInt(exList.get(exRow)[0]);

        JTextField setsF   = UIHelper.inputField("e.g. 3");
        JTextField repsF   = UIHelper.inputField("e.g. 10");
        JTextField weightF = UIHelper.inputField("e.g. 60");
        JTextField noteF   = UIHelper.inputField("optional note");
        JTextField dateF   = UIHelper.inputField("YYYY-MM-DD");
        dateF.setText(java.time.LocalDate.now().toString());

        JPanel panel = new JPanel(new GridLayout(10, 1, 4, 4));
        panel.add(UIHelper.label("Logging: " + exName, AppFonts.SUBHEADING, AppColors.ACCENT));
        panel.add(UIHelper.muted("Date"));         panel.add(dateF);
        panel.add(UIHelper.muted("Actual Sets"));  panel.add(setsF);
        panel.add(UIHelper.muted("Actual Reps"));  panel.add(repsF);
        panel.add(UIHelper.muted("Weight (kg)"));  panel.add(weightF);
        panel.add(UIHelper.muted("Note"));         panel.add(noteF);

        int res = JOptionPane.showConfirmDialog(this, panel,
                "Log Performance", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            WorkoutDAO.logPerformance(
                    selectedScheduleId, exerciseId, SessionManager.getUserId(),
                    dateF.getText().trim(),
                    Integer.parseInt(setsF.getText().trim()),
                    Integer.parseInt(repsF.getText().trim()),
                    Integer.parseInt(weightF.getText().trim()),
                    noteF.getText().trim());
            UIHelper.showSuccess(this, "Performance logged!");
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to log: " + ex.getMessage());
        }
    }
}
