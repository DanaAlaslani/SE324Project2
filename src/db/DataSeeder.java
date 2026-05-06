package db;

import auth.AuthService;

import java.sql.*;

public class DataSeeder {

    private DataSeeder() {}

    public static void seedIfEmpty() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next() && rs.getInt(1) > 0) return;
            }
            System.out.println("Seeding sample data...");
            seed(conn);
            System.out.println("Sample data ready.");
        } catch (Exception e) {
            System.err.println("Seeding error: " + e.getMessage());
        }
    }

    private static void seed(Connection conn) throws SQLException {

        // ── Users ─────────────────────────────────────────────

        // Trainers
        int t1 = insertUser(conn, "ahmed_trainer", "ahmed@gymanice.com",  "Test1234",
                "Ahmed Ali",    "1988-03-10", "0512345001", "TRAINER");
        insertTrainer(conn, t1, "NASM Certified, CPT", "Strength & Conditioning", 79.00f);

        int t2 = insertUser(conn, "khalid_trainer", "khalid@gymanice.com", "Test1234",
                "Khalid Hassan", "1985-07-22", "0512345002", "TRAINER");
        insertTrainer(conn, t2, "CSCS, NSCA Certified", "Weight Loss & Cardio", 89.00f);

        // Nutritionists
        int n1 = insertUser(conn, "sara_nutri", "sara@gymanice.com", "Test1234",
                "Sara Hassan",   "1990-11-05", "0523456001", "NUTRITIONIST");
        insertNutritionist(conn, n1, "Registered Dietitian, MSc Nutrition", "Sports Nutrition", 69.00f);

        int n2 = insertUser(conn, "roaa_nutri", "roaa@gymanice.com", "Test1234",
                "Roaa Allaf",    "1992-04-18", "0523456002", "NUTRITIONIST");
        insertNutritionist(conn, n2, "Clinical Nutritionist, BSc Dietetics", "Weight Management", 79.00f);

        // Trainees
        int tr1 = insertUser(conn, "rima_trainee", "rima@gymanice.com", "Test1234",
                "Rima Al-Saedi", "2000-07-20", "0534567001", "TRAINEE");
        insertTrainee(conn, tr1, 165.0f, 72.0f, 24, "Lose Weight");

        int tr2 = insertUser(conn, "dana_trainee", "dana@gymanice.com", "Test1234",
                "Dana Alaslani", "1998-02-14", "0534567002", "TRAINEE");
        insertTrainee(conn, tr2, 170.0f, 65.0f, 26, "Build Muscle");

        // ── Workout schedules ──────────────────────────────────

        int s1 = insertSchedule(conn, t1, tr1, "2026-05-05", "Full Body Strength - Week 1");
        int s2 = insertSchedule(conn, t1, tr1, "2026-05-12", "Full Body Strength - Week 2");
        int s3 = insertSchedule(conn, t2, tr2, "2026-05-06", "Hypertrophy Program - Day 1");

        // Fetch exercise IDs by name
        int squat     = getExId(conn, "Barbell Squat");
        int deadlift  = getExId(conn, "Deadlift");
        int bench     = getExId(conn, "Bench Press");
        int pullUp    = getExId(conn, "Pull-Up");
        int ohp       = getExId(conn, "Overhead Press");
        int row       = getExId(conn, "Barbell Row");
        int rdl       = getExId(conn, "Romanian Deadlift");
        int lateral   = getExId(conn, "Lateral Raise");
        int plank     = getExId(conn, "Plank");

        addEx(conn, s1, squat,   4, 10, 60);
        addEx(conn, s1, bench,   3, 12, 50);
        addEx(conn, s1, deadlift,3,  6, 80);
        addEx(conn, s1, plank,   3, 60,  0);

        addEx(conn, s2, squat,   4, 10, 65);
        addEx(conn, s2, rdl,     3, 12, 55);
        addEx(conn, s2, ohp,     3, 10, 40);
        addEx(conn, s2, lateral, 3, 15, 10);

        addEx(conn, s3, bench,   4, 10, 70);
        addEx(conn, s3, pullUp,  4,  8,  0);
        addEx(conn, s3, row,     3, 10, 60);

        // Performance logs (trainee 1)
        addPerf(conn, s1, squat,   tr1, "2026-05-05", 4, 10, 60);
        addPerf(conn, s1, bench,   tr1, "2026-05-05", 3, 12, 48);
        addPerf(conn, s1, deadlift,tr1, "2026-05-05", 3,  6, 75);

        // ── Meal plans ─────────────────────────────────────────

        int mp1 = insertMealPlan(conn, n1, tr1, 1800.0f, "Rima Weight-Loss Plan");
        int mp2 = insertMealPlan(conn, n2, tr2, 2400.0f, "Dana Muscle-Gain Plan");

        // Fetch meal IDs
        int oatmeal  = getMealId(conn, "Oatmeal with Berries");
        int eggs     = getMealId(conn, "Scrambled Eggs & Toast");
        int chicken  = getMealId(conn, "Grilled Chicken & Rice");
        int tuna     = getMealId(conn, "Tuna Salad Wrap");
        int salmon   = getMealId(conn, "Salmon & Vegetables");
        int beef     = getMealId(conn, "Beef Stir Fry");
        int yogurt   = getMealId(conn, "Greek Yogurt & Nuts");
        int shake    = getMealId(conn, "Protein Shake & Banana");

        // Rima's plan (weight loss, 1800 cal)
        addMealToPlan(conn, mp1, oatmeal, 1); // Mon breakfast
        addMealToPlan(conn, mp1, tuna,    1); // Mon lunch
        addMealToPlan(conn, mp1, salmon,  1); // Mon dinner
        addMealToPlan(conn, mp1, yogurt,  2); // Tue snack
        addMealToPlan(conn, mp1, eggs,    2); // Tue breakfast
        addMealToPlan(conn, mp1, chicken, 2); // Tue lunch
        activatePlan(conn, mp1);

        // Dana's plan (muscle gain, 2400 cal)
        addMealToPlan(conn, mp2, eggs,   1);
        addMealToPlan(conn, mp2, chicken,1);
        addMealToPlan(conn, mp2, beef,   1);
        addMealToPlan(conn, mp2, shake,  1);
        addMealToPlan(conn, mp2, oatmeal,2);
        addMealToPlan(conn, mp2, tuna,   2);
        activatePlan(conn, mp2);

        // ── Progress records ───────────────────────────────────

        String[][] rima = {
            {"2026-03-01", "72.0"},{"2026-03-15", "71.2"},{"2026-04-01", "70.0"},
            {"2026-04-15", "69.1"},{"2026-05-01", "68.3"}
        };
        for (String[] r : rima) addProgress(conn, tr1, r[0], Float.parseFloat(r[1]), 165f, 72f, 62f);

        String[][] dana = {
            {"2026-03-10", "65.0"},{"2026-04-10", "66.4"},{"2026-05-01", "67.8"}
        };
        for (String[] r : dana) addProgress(conn, tr2, r[0], Float.parseFloat(r[1]), 170f, 65f, 78f);

        // ── Payments ───────────────────────────────────────────

        addPayment(conn, tr1, t1, "MADA",        299.00, "COMPLETED", "TXN-A1B2C3D4");
        addPayment(conn, tr1, n1, "APPLE_PAY",   199.00, "COMPLETED", "TXN-E5F6G7H8");
        addPayment(conn, tr2, t2, "CREDIT_CARD", 399.00, "COMPLETED", "TXN-I9J0K1L2");
        addPayment(conn, tr2, n2, "MADA",        199.00, "COMPLETED", "TXN-M3N4O5P6");
    }

    // ── Insert helpers ────────────────────────────────────────

    private static int insertUser(Connection c, String uname, String email, String pass,
                                   String name, String dob, String phone, String role)
            throws SQLException {
        String sql = "INSERT INTO users (username,email,password,full_name,dob,phone,role,is_verified) VALUES (?,?,?,?,?,?,?,1)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, uname); ps.setString(2, email);
            ps.setString(3, AuthService.hashPassword(pass));
            ps.setString(4, name);  ps.setString(5, dob);
            ps.setString(6, phone); ps.setString(7, role);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) { gk.next(); return gk.getInt(1); }
        }
    }

    private static void insertTrainee(Connection c, int id, float h, float w, int age, String goal)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO trainees (trainee_id,height_cm,weight_kg,age,fitness_goal) VALUES (?,?,?,?,?)")) {
            ps.setInt(1,id); ps.setFloat(2,h); ps.setFloat(3,w); ps.setInt(4,age); ps.setString(5,goal);
            ps.executeUpdate();
        }
    }

    private static void insertTrainer(Connection c, int id, String cred, String spec, float fee)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO trainers (trainer_id,credentials,specialization,monthly_fee) VALUES (?,?,?,?)")) {
            ps.setInt(1,id); ps.setString(2,cred); ps.setString(3,spec); ps.setFloat(4,fee);
            ps.executeUpdate();
        }
    }

    private static void insertNutritionist(Connection c, int id, String cred, String spec, float fee)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO nutritionists (nutritionist_id,credentials,specialization,monthly_fee) VALUES (?,?,?,?)")) {
            ps.setInt(1,id); ps.setString(2,cred); ps.setString(3,spec); ps.setFloat(4,fee);
            ps.executeUpdate();
        }
    }

    private static int insertSchedule(Connection c, int trainerId, int traineeId,
                                       String date, String notes) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO workout_schedules (trainer_id,trainee_id,schedule_date,notes) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,trainerId); ps.setInt(2,traineeId);
            ps.setString(3,date);   ps.setString(4,notes);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) { gk.next(); return gk.getInt(1); }
        }
    }

    private static void addEx(Connection c, int schedId, int exId, int sets, int reps, int weight)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT IGNORE INTO schedule_exercises (schedule_id,exercise_id,target_sets,target_reps,target_weight) VALUES (?,?,?,?,?)")) {
            ps.setInt(1,schedId); ps.setInt(2,exId);
            ps.setInt(3,sets); ps.setInt(4,reps); ps.setInt(5,weight);
            ps.executeUpdate();
        }
    }

    private static void addPerf(Connection c, int schedId, int exId, int traineeId,
                                  String date, int sets, int reps, int weight) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO exercise_performances (schedule_id,exercise_id,trainee_id,logged_date,actual_sets,actual_reps,actual_weight) VALUES (?,?,?,?,?,?,?)")) {
            ps.setInt(1,schedId); ps.setInt(2,exId); ps.setInt(3,traineeId);
            ps.setString(4,date); ps.setInt(5,sets); ps.setInt(6,reps); ps.setInt(7,weight);
            ps.executeUpdate();
        }
    }

    private static int insertMealPlan(Connection c, int nutId, int traineeId,
                                       float target, String name) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO meal_plans (plan_name,nutritionist_id,trainee_id,daily_target_cal) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,name); ps.setInt(2,nutId); ps.setInt(3,traineeId); ps.setFloat(4,target);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) { gk.next(); return gk.getInt(1); }
        }
    }

    private static void addMealToPlan(Connection c, int planId, int mealId, int day)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT IGNORE INTO meal_plan_items (plan_id,meal_id,day_number) VALUES (?,?,?)")) {
            ps.setInt(1,planId); ps.setInt(2,mealId); ps.setInt(3,day);
            ps.executeUpdate();
        }
    }

    private static void activatePlan(Connection c, int planId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE meal_plans SET status='ACTIVE' WHERE plan_id=?")) {
            ps.setInt(1, planId); ps.executeUpdate();
        }
    }

    private static void addProgress(Connection c, int traineeId, String date,
                                     float weight, float height, float initial, float target)
            throws SQLException {
        float progress = initial == target ? 100f
                : Math.min(Math.abs(initial - weight) / Math.abs(initial - target) * 100f, 100f);
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO progress_records (trainee_id,record_date,weight_kg,height_cm,initial_weight_kg,target_weight_kg,progress_percent) VALUES (?,?,?,?,?,?,?)")) {
            ps.setInt(1,traineeId); ps.setString(2,date); ps.setFloat(3,weight);
            ps.setFloat(4,height); ps.setFloat(5,initial); ps.setFloat(6,target); ps.setFloat(7,progress);
            ps.executeUpdate();
        }
    }

    private static void addPayment(Connection c, int traineeId, int trainerId,
                                    String type, double amount, String status, String ref)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO payments (trainee_id,trainer_id,payment_type,amount_sar,status,transaction_ref) VALUES (?,?,?,?,?,?)")) {
            ps.setInt(1,traineeId); ps.setInt(2,trainerId); ps.setString(3,type);
            ps.setDouble(4,amount); ps.setString(5,status); ps.setString(6,ref);
            ps.executeUpdate();
        }
    }

    private static int getExId(Connection c, String name) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT exercise_id FROM exercises WHERE name=?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : -1; }
        }
    }

    private static int getMealId(Connection c, String name) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT meal_id FROM meals WHERE meal_name=?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : -1; }
        }
    }
}
