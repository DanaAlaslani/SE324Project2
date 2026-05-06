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

public class TrainerSchedulesPanel extends JPanel {

    private JTable scheduleTable;
    private JTable exerciseTable;

    public TrainerSchedulesPanel() {
        setBackground(AppColors.BG_PRIMARY);
        setLayout(new BorderLayout());
        build();
    }

    private void build() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(AppColors.BG_PRIMARY);
        header.setBorder(new EmptyBorder(24, 28, 8, 28));
        header.add(UIHelper.heading("Workout Schedules"));

        JPanel body = new JPanel(new GridLayout(1, 2, 16, 0));
        body.setBackground(AppColors.BG_PRIMARY);
        body.setBorder(new EmptyBorder(8, 28, 28, 28));

        body.add(buildSchedulesPanel());
        body.add(buildExercisesPanel());

        add(header, BorderLayout.NORTH);
        add(body,   BorderLayout.CENTER);

        loadSchedules();
    }

    private JPanel buildSchedulesPanel() {
        JPanel card = UIHelper.card();
        card.setLayout(new BorderLayout(0, 10));
        card.add(UIHelper.label("My Schedules", AppFonts.SUBHEADING, AppColors.TEXT_PRIMARY), BorderLayout.NORTH);

        scheduleTable = UIHelper.styledTable(new String[]{"ID", "Date", "Notes", "Trainee"});
        scheduleTable.getColumnModel().getColumn(0).setMaxWidth(40);
        scheduleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadExercisesForSelected();
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnRow.setBackground(AppColors.BG_CARD);
        JButton refreshBtn = UIHelper.ghostButton("Refresh");
        JButton newBtn     = UIHelper.primaryButton("+ New Schedule");
        refreshBtn.addActionListener(e -> loadSchedules());
        newBtn.addActionListener(e -> openNewScheduleDialog());
        btnRow.add(newBtn);
        btnRow.add(refreshBtn);

        card.add(UIHelper.scrollPane(scheduleTable), BorderLayout.CENTER);
        card.add(btnRow,                             BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildExercisesPanel() {
        JPanel card = UIHelper.card();
        card.setLayout(new BorderLayout(0, 10));
        card.add(UIHelper.label("Exercises in Schedule", AppFonts.SUBHEADING, AppColors.TEXT_PRIMARY), BorderLayout.NORTH);

        exerciseTable = UIHelper.styledTable(
                new String[]{"Exercise", "Muscle", "Target Sets", "Target Reps", "Weight (kg)"});

        JButton addExBtn = UIHelper.primaryButton("+ Add Exercise");
        addExBtn.addActionListener(e -> openAddExerciseDialog());

        card.add(UIHelper.scrollPane(exerciseTable), BorderLayout.CENTER);
        card.add(addExBtn,                           BorderLayout.SOUTH);
        return card;
    }

    private void loadSchedules() {
        DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();
        model.setRowCount(0);
        try {
            List<String[]> rows = WorkoutDAO.getSchedulesByTrainer(SessionManager.getUserId());
            for (String[] r : rows) model.addRow(r);
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to load schedules: " + ex.getMessage());
        }
    }

    private void loadExercisesForSelected() {
        int row = scheduleTable.getSelectedRow();
        if (row < 0) return;
        int scheduleId = Integer.parseInt(
                scheduleTable.getModel().getValueAt(row, 0).toString());

        DefaultTableModel model = (DefaultTableModel) exerciseTable.getModel();
        model.setRowCount(0);
        try {
            List<String[]> rows = WorkoutDAO.getExercisesForSchedule(scheduleId);
            for (String[] r : rows) {
                model.addRow(new Object[]{r[1], r[2], r[4], r[5], r[6]});
            }
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to load exercises: " + ex.getMessage());
        }
    }

    private void openNewScheduleDialog() {
        List<String[]> trainees;
        try {
            trainees = WorkoutDAO.getAllTrainees();
        } catch (Exception ex) { UIHelper.showError(this, ex.getMessage()); return; }

        if (trainees.isEmpty()) {
            UIHelper.showError(this, "No trainees registered yet."); return;
        }

        String[] names = trainees.stream().map(t -> t[0] + " – " + t[1]).toArray(String[]::new);
        JComboBox<String> traineeBox = UIHelper.comboBox(names);
        JTextField dateF  = UIHelper.inputField("YYYY-MM-DD");
        JTextField notesF = UIHelper.inputField("optional notes");
        dateF.setText(java.time.LocalDate.now().toString());

        JPanel p = new JPanel(new GridLayout(6, 1, 4, 4));
        p.add(UIHelper.muted("Trainee")); p.add(traineeBox);
        p.add(UIHelper.muted("Date"));    p.add(dateF);
        p.add(UIHelper.muted("Notes"));   p.add(notesF);

        int res = JOptionPane.showConfirmDialog(this, p, "New Schedule", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        int traineeId = Integer.parseInt(trainees.get(traineeBox.getSelectedIndex())[0]);
        try {
            WorkoutDAO.createSchedule(SessionManager.getUserId(), traineeId,
                    dateF.getText().trim(), notesF.getText().trim());
            loadSchedules();
            UIHelper.showSuccess(this, "Schedule created!");
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed: " + ex.getMessage());
        }
    }

    private void openAddExerciseDialog() {
        int row = scheduleTable.getSelectedRow();
        if (row < 0) { UIHelper.showError(this, "Select a schedule first."); return; }
        int scheduleId = Integer.parseInt(
                scheduleTable.getModel().getValueAt(row, 0).toString());

        List<String[]> exercises;
        try {
            exercises = WorkoutDAO.getAllExercises();
        } catch (Exception ex) { UIHelper.showError(this, ex.getMessage()); return; }

        String[] exNames = exercises.stream()
                .map(e -> e[1] + " (" + e[2] + ")")
                .toArray(String[]::new);

        JComboBox<String> exBox  = UIHelper.comboBox(exNames);
        JTextField setsF         = UIHelper.inputField("e.g. 3");
        JTextField repsF         = UIHelper.inputField("e.g. 10");
        JTextField weightF       = UIHelper.inputField("e.g. 60");

        JPanel p = new JPanel(new GridLayout(8, 1, 4, 4));
        p.add(UIHelper.muted("Exercise"));    p.add(exBox);
        p.add(UIHelper.muted("Target Sets")); p.add(setsF);
        p.add(UIHelper.muted("Target Reps")); p.add(repsF);
        p.add(UIHelper.muted("Weight (kg)")); p.add(weightF);

        int res = JOptionPane.showConfirmDialog(this, p, "Add Exercise", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        int exId = Integer.parseInt(exercises.get(exBox.getSelectedIndex())[0]);
        try {
            WorkoutDAO.addExerciseToSchedule(scheduleId, exId,
                    Integer.parseInt(setsF.getText().trim()),
                    Integer.parseInt(repsF.getText().trim()),
                    Integer.parseInt(weightF.getText().trim()));
            loadExercisesForSelected();
            UIHelper.showSuccess(this, "Exercise added!");
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed: " + ex.getMessage());
        }
    }
}
