package db;

import java.sql.*;

public class DatabaseConnection {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/gymanice"
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Ss-412006";

    private static Connection connection;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        }
        return connection;
    }

    public static void initialize() {
        try {
            Connection conn = getConnection();
            System.out.println("Connected to MySQL gymanice database.");
            seedIfEmpty(conn);
            DataSeeder.seedIfEmpty();   // inserts sample users/schedules/plans if table is empty
        } catch (SQLException e) {
            System.err.println("DB connection error: " + e.getMessage());
        }
    }

    private static void seedIfEmpty(Connection conn) throws SQLException {
        try (ResultSet rs = conn.createStatement()
                .executeQuery("SELECT COUNT(*) FROM exercises")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }
        seedExercises(conn);
        seedPresetMeals(conn);
    }

    private static void seedExercises(Connection conn) throws SQLException {
        String[][] exercises = {
            {"Barbell Squat",     "Legs",       "Keep back straight, knees over toes"},
            {"Deadlift",          "Back",        "Hinge at hips, neutral spine"},
            {"Bench Press",       "Chest",       "Grip slightly wider than shoulder width"},
            {"Pull-Up",           "Back",        "Full range of motion, controlled descent"},
            {"Overhead Press",    "Shoulders",   "Press straight overhead, brace core"},
            {"Barbell Row",       "Back",        "Pull to lower chest, elbows close"},
            {"Lunges",            "Legs",        "Step forward, knee above ankle"},
            {"Dumbbell Curl",     "Biceps",      "Controlled curl, no swinging"},
            {"Tricep Pushdown",   "Triceps",     "Keep elbows fixed at sides"},
            {"Plank",             "Core",        "Neutral spine, engage core throughout"},
            {"Romanian Deadlift", "Hamstrings",  "Hinge at hips, slight knee bend"},
            {"Lateral Raise",     "Shoulders",   "Raise to shoulder height, slight bend in elbow"}
        };
        String sql = "INSERT IGNORE INTO exercises (name, muscle_group, description) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String[] ex : exercises) {
                ps.setString(1, ex[0]); ps.setString(2, ex[1]); ps.setString(3, ex[2]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void seedPresetMeals(Connection conn) throws SQLException {
        Object[][] meals = {
            {"Oatmeal with Berries",      350, 12, 55,  8,  "BREAKFAST", 10},
            {"Scrambled Eggs & Toast",    420, 24, 38,  14, "BREAKFAST", 15},
            {"Grilled Chicken & Rice",    520, 45, 50,  10, "LUNCH",     25},
            {"Tuna Salad Wrap",           380, 30, 35,  10, "LUNCH",     10},
            {"Salmon & Vegetables",       480, 42, 20,  20, "DINNER",    30},
            {"Beef Stir Fry",             560, 40, 45,  18, "DINNER",    25},
            {"Greek Yogurt & Nuts",       280, 18, 22,  12, "SNACK",      5},
            {"Protein Shake & Banana",    310, 28, 40,   5, "SNACK",      5},
            {"Quinoa & Black Beans",      440, 22, 68,  10, "LUNCH",     20},
            {"Turkey & Avocado Sandwich", 490, 35, 42,  16, "LUNCH",     10}
        };
        String mealSql   = "INSERT IGNORE INTO meals (meal_name,calories,protein_g,carbs_g,fat_g,meal_type) VALUES (?,?,?,?,?,'PRESET')";
        String presetSql = "INSERT IGNORE INTO preset_meals (meal_id,category,prep_time_min) VALUES (?,?,?)";
        try (PreparedStatement mp = conn.prepareStatement(mealSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pp = conn.prepareStatement(presetSql)) {
            for (Object[] m : meals) {
                mp.setString(1, (String) m[0]);
                mp.setInt(2, (int) m[1]); mp.setInt(3, (int) m[2]);
                mp.setInt(4, (int) m[3]); mp.setInt(5, (int) m[4]);
                mp.executeUpdate();
                try (ResultSet gk = mp.getGeneratedKeys()) {
                    if (gk.next()) {
                        pp.setInt(1, gk.getInt(1));
                        pp.setString(2, (String) m[5]);
                        pp.setInt(3, (int) m[6]);
                        pp.executeUpdate();
                    }
                }
            }
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException ignored) {}
    }
}
