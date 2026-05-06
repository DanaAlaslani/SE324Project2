package nutrition.dao;

import db.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class NutritionDAO {

    private NutritionDAO() {}

    // ── Preset Meals ──────────────────────────────────────────

    public static List<String[]> getPresetMeals() throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = """
            SELECT m.meal_id, m.meal_name, m.calories, m.protein_g, m.carbs_g, m.fat_g,
                   pm.category, pm.prep_time_min
            FROM meals m
            JOIN preset_meals pm ON m.meal_id = pm.meal_id
            ORDER BY pm.category, m.meal_name""";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("meal_id"),
                        rs.getString("meal_name"),
                        rs.getString("calories"),
                        rs.getString("protein_g"),
                        rs.getString("carbs_g"),
                        rs.getString("fat_g"),
                        rs.getString("category"),
                        rs.getString("prep_time_min")
                });
            }
        }
        return list;
    }

    // ── Custom Meals ──────────────────────────────────────────

    public static int addCustomMeal(int traineeId, String mealName, double calories,
                                     double protein, double carbs, double fat, String notes)
            throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            int mealId;
            String sql1 = "INSERT INTO meals (meal_name,calories,protein_g,carbs_g,fat_g,meal_type) VALUES (?,?,?,?,?,'CUSTOM')";
            try (PreparedStatement ps = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, mealName);
                ps.setDouble(2, calories);
                ps.setDouble(3, protein);
                ps.setDouble(4, carbs);
                ps.setDouble(5, fat);
                ps.executeUpdate();
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    mealId = gk.next() ? gk.getInt(1) : -1;
                }
            }
            String sql2 = "INSERT INTO custom_meals (meal_id,trainee_id,notes) VALUES (?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setInt(1, mealId);
                ps.setInt(2, traineeId);
                ps.setString(3, notes);
                ps.executeUpdate();
            }
            conn.commit();
            return mealId;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public static List<String[]> getCustomMealsByTrainee(int traineeId) throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = """
            SELECT m.meal_id, m.meal_name, m.calories, m.protein_g, m.carbs_g, m.fat_g, cm.notes
            FROM meals m
            JOIN custom_meals cm ON m.meal_id = cm.meal_id
            WHERE cm.trainee_id = ?
            ORDER BY m.meal_name""";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, traineeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            rs.getString("meal_id"),
                            rs.getString("meal_name"),
                            rs.getString("calories"),
                            rs.getString("protein_g"),
                            rs.getString("carbs_g"),
                            rs.getString("fat_g"),
                            rs.getString("notes")
                    });
                }
            }
        }
        return list;
    }

    // ── Meal Plans ────────────────────────────────────────────

    public static int createMealPlan(int nutritionistId, int traineeId,
                                      String planName, float dailyTarget) throws SQLException {
        String sql = "INSERT INTO meal_plans (plan_name,nutritionist_id,trainee_id,daily_target_cal) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, planName);
            ps.setInt(2, nutritionistId);
            ps.setInt(3, traineeId);
            ps.setFloat(4, dailyTarget);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                return gk.next() ? gk.getInt(1) : -1;
            }
        }
    }

    public static boolean addMealToPlan(int planId, int mealId, int dayNumber) throws SQLException {
        String sql = "INSERT IGNORE INTO meal_plan_items (plan_id,meal_id,day_number) VALUES (?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, planId);
            ps.setInt(2, mealId);
            ps.setInt(3, dayNumber);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean activatePlan(int planId) throws SQLException {
        String sql = "UPDATE meal_plans SET status='ACTIVE' WHERE plan_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, planId);
            return ps.executeUpdate() > 0;
        }
    }

    public static List<String[]> getMealPlansByTrainee(int traineeId) throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = """
            SELECT mp.plan_id, mp.plan_name, mp.daily_target_cal, mp.status, mp.created_at,
                   u.full_name AS nutritionist_name
            FROM meal_plans mp
            JOIN users u ON mp.nutritionist_id = u.user_id
            WHERE mp.trainee_id = ?
            ORDER BY mp.created_at DESC""";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, traineeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            rs.getString("plan_id"),
                            rs.getString("plan_name"),
                            rs.getString("daily_target_cal"),
                            rs.getString("status"),
                            rs.getString("created_at"),
                            rs.getString("nutritionist_name")
                    });
                }
            }
        }
        return list;
    }

    public static List<String[]> getMealsInPlan(int planId) throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = """
            SELECT m.meal_name, m.calories, m.protein_g, m.carbs_g, m.fat_g, mpi.day_number
            FROM meal_plan_items mpi
            JOIN meals m ON mpi.meal_id = m.meal_id
            WHERE mpi.plan_id = ?
            ORDER BY mpi.day_number, m.meal_name""";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, planId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            rs.getString("meal_name"),
                            rs.getString("calories"),
                            rs.getString("protein_g"),
                            rs.getString("carbs_g"),
                            rs.getString("fat_g"),
                            rs.getString("day_number")
                    });
                }
            }
        }
        return list;
    }

    public static List<String[]> getMealPlansByNutritionist(int nutritionistId) throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = """
            SELECT mp.plan_id, mp.plan_name, mp.daily_target_cal, mp.status, u.full_name AS trainee_name
            FROM meal_plans mp
            JOIN users u ON mp.trainee_id = u.user_id
            WHERE mp.nutritionist_id = ?
            ORDER BY mp.created_at DESC""";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nutritionistId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            rs.getString("plan_id"),
                            rs.getString("plan_name"),
                            rs.getString("daily_target_cal"),
                            rs.getString("status"),
                            rs.getString("trainee_name")
                    });
                }
            }
        }
        return list;
    }
}
