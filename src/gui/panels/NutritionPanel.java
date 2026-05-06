package gui.panels;

import auth.SessionManager;
import gui.util.AppColors;
import gui.util.AppFonts;
import gui.util.UIHelper;
import nutrition.dao.NutritionDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class NutritionPanel extends JPanel {

    private final String role;
    private JTable planTable;
    private JTable mealTable;
    private JLabel totalCalLabel;

    public NutritionPanel() {
        this.role = SessionManager.getRole();
        setBackground(AppColors.BG_PRIMARY);
        setLayout(new BorderLayout());
        build();
    }

    private void build() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(AppColors.BG_PRIMARY);
        header.setBorder(new EmptyBorder(24, 28, 8, 28));
        header.add(UIHelper.heading(role.equals("NUTRITIONIST") ? "Meal Plans Management" : "Meal Plans"));

        JPanel body = new JPanel(new GridLayout(1, 2, 16, 0));
        body.setBackground(AppColors.BG_PRIMARY);
        body.setBorder(new EmptyBorder(8, 28, 28, 28));

        body.add(buildPlansPanel());
        body.add(buildMealsPanel());

        add(header, BorderLayout.NORTH);
        add(body,   BorderLayout.CENTER);

        loadPlans();
    }

    private JPanel buildPlansPanel() {
        JPanel card = UIHelper.card();
        card.setLayout(new BorderLayout(0, 10));
        card.add(UIHelper.label("Plans", AppFonts.SUBHEADING, AppColors.TEXT_PRIMARY), BorderLayout.NORTH);

        planTable = UIHelper.styledTable(
                new String[]{"ID", "Plan Name", "Target Cal", "Status",
                        role.equals("NUTRITIONIST") ? "Trainee" : "Nutritionist"});
        planTable.getColumnModel().getColumn(0).setMaxWidth(40);
        planTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadMealsForSelected();
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnRow.setBackground(AppColors.BG_CARD);
        JButton refreshBtn = UIHelper.ghostButton("Refresh");
        refreshBtn.addActionListener(e -> loadPlans());
        btnRow.add(refreshBtn);

        if (role.equals("NUTRITIONIST")) {
            JButton newPlanBtn = UIHelper.primaryButton("+ New Plan");
            newPlanBtn.addActionListener(e -> openNewPlanDialog());
            btnRow.add(newPlanBtn);
        } else {
            JButton customMealBtn = UIHelper.ghostButton("+ Add Custom Meal");
            customMealBtn.addActionListener(e -> openCustomMealDialog());
            btnRow.add(customMealBtn);
        }

        card.add(UIHelper.scrollPane(planTable), BorderLayout.CENTER);
        card.add(btnRow,                         BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildMealsPanel() {
        JPanel card = UIHelper.card();
        card.setLayout(new BorderLayout(0, 10));
        card.add(UIHelper.label("Meals in Plan", AppFonts.SUBHEADING, AppColors.TEXT_PRIMARY), BorderLayout.NORTH);

        mealTable = UIHelper.styledTable(
                new String[]{"Meal", "Cal", "Protein(g)", "Carbs(g)", "Fat(g)", "Day"});

        totalCalLabel = UIHelper.label("Total: — cal", AppFonts.BODY, AppColors.ACCENT);
        totalCalLabel.setBorder(new EmptyBorder(6, 4, 0, 0));

        card.add(UIHelper.scrollPane(mealTable), BorderLayout.CENTER);
        card.add(totalCalLabel,                  BorderLayout.SOUTH);
        return card;
    }

    private void loadPlans() {
        DefaultTableModel model = (DefaultTableModel) planTable.getModel();
        model.setRowCount(0);
        try {
            List<String[]> rows = role.equals("NUTRITIONIST")
                    ? NutritionDAO.getMealPlansByNutritionist(SessionManager.getUserId())
                    : NutritionDAO.getMealPlansByTrainee(SessionManager.getUserId());
            for (String[] r : rows) model.addRow(r);
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to load plans: " + ex.getMessage());
        }
    }

    private void loadMealsForSelected() {
        int row = planTable.getSelectedRow();
        if (row < 0) return;
        int planId = Integer.parseInt(planTable.getModel().getValueAt(row, 0).toString());

        DefaultTableModel model = (DefaultTableModel) mealTable.getModel();
        model.setRowCount(0);
        double total = 0;
        try {
            List<String[]> meals = NutritionDAO.getMealsInPlan(planId);
            for (String[] m : meals) {
                model.addRow(m);
                total += Double.parseDouble(m[1]);
            }
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to load meals: " + ex.getMessage());
        }
        totalCalLabel.setText(String.format("Total: %.0f cal", total));
    }

    private void openNewPlanDialog() {
        List<String[]> trainees;
        try {
            trainees = workout.dao.WorkoutDAO.getAllTrainees();
        } catch (Exception ex) {
            UIHelper.showError(this, ex.getMessage()); return;
        }
        if (trainees.isEmpty()) {
            UIHelper.showError(this, "No trainees registered yet."); return;
        }

        String[] traineeNames = trainees.stream().map(t -> t[0] + " – " + t[1]).toArray(String[]::new);
        JComboBox<String> traineeBox = UIHelper.comboBox(traineeNames);
        JTextField planNameF  = UIHelper.inputField("e.g. Weight Loss Plan");
        JTextField targetCalF = UIHelper.inputField("e.g. 1800");

        JPanel panel = new JPanel(new GridLayout(6, 1, 4, 4));
        panel.add(UIHelper.muted("Trainee"));         panel.add(traineeBox);
        panel.add(UIHelper.muted("Plan Name"));       panel.add(planNameF);
        panel.add(UIHelper.muted("Daily Target Cal")); panel.add(targetCalF);

        int res = JOptionPane.showConfirmDialog(this, panel, "New Meal Plan", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        int traineeId = Integer.parseInt(trainees.get(traineeBox.getSelectedIndex())[0]);
        try {
            int planId = NutritionDAO.createMealPlan(
                    SessionManager.getUserId(), traineeId,
                    planNameF.getText().trim(),
                    Float.parseFloat(targetCalF.getText().trim()));
            openAddMealsToPlanDialog(planId);
            loadPlans();
            UIHelper.showSuccess(this, "Meal plan created!");
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed: " + ex.getMessage());
        }
    }

    private void openAddMealsToPlanDialog(int planId) {
        List<String[]> presets;
        try {
            presets = NutritionDAO.getPresetMeals();
        } catch (Exception ex) { return; }

        String[] mealNames = presets.stream().map(m -> m[1] + " (" + m[2] + " cal)").toArray(String[]::new);
        JList<String> mealList = new JList<>(mealNames);
        mealList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        mealList.setBackground(AppColors.INPUT_BG);
        mealList.setForeground(AppColors.TEXT_PRIMARY);

        JComboBox<String> dayBox = UIHelper.comboBox(
                new String[]{"1-Mon","2-Tue","3-Wed","4-Thu","5-Fri","6-Sat","7-Sun"});

        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.add(UIHelper.muted("Select meals (Ctrl+click for multiple)"), BorderLayout.NORTH);
        p.add(new JScrollPane(mealList), BorderLayout.CENTER);
        p.add(dayBox, BorderLayout.SOUTH);
        p.setPreferredSize(new Dimension(360, 300));

        int res = JOptionPane.showConfirmDialog(this, p, "Add Meals to Plan", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        int day = dayBox.getSelectedIndex() + 1;
        try {
            for (int idx : mealList.getSelectedIndices()) {
                int mealId = Integer.parseInt(presets.get(idx)[0]);
                NutritionDAO.addMealToPlan(planId, mealId, day);
            }
            NutritionDAO.activatePlan(planId);
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to add meals: " + ex.getMessage());
        }
    }

    private void openCustomMealDialog() {
        JTextField nameF    = UIHelper.inputField("Meal name");
        JTextField calF     = UIHelper.inputField("e.g. 350");
        JTextField proteinF = UIHelper.inputField("e.g. 25");
        JTextField carbsF   = UIHelper.inputField("e.g. 40");
        JTextField fatF     = UIHelper.inputField("e.g. 8");
        JTextField noteF    = UIHelper.inputField("optional note");

        JPanel p = new JPanel(new GridLayout(12, 1, 4, 4));
        p.add(UIHelper.muted("Meal Name"));   p.add(nameF);
        p.add(UIHelper.muted("Calories"));    p.add(calF);
        p.add(UIHelper.muted("Protein (g)")); p.add(proteinF);
        p.add(UIHelper.muted("Carbs (g)"));   p.add(carbsF);
        p.add(UIHelper.muted("Fat (g)"));     p.add(fatF);
        p.add(UIHelper.muted("Note"));        p.add(noteF);

        int res = JOptionPane.showConfirmDialog(this, p, "Add Custom Meal", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;
        try {
            NutritionDAO.addCustomMeal(
                    SessionManager.getUserId(),
                    nameF.getText().trim(),
                    Double.parseDouble(calF.getText().trim()),
                    Double.parseDouble(proteinF.getText().trim()),
                    Double.parseDouble(carbsF.getText().trim()),
                    Double.parseDouble(fatF.getText().trim()),
                    noteF.getText().trim());
            UIHelper.showSuccess(this, "Custom meal saved!");
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed: " + ex.getMessage());
        }
    }
}
