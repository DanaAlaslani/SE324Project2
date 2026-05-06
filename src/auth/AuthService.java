package auth;

import db.DatabaseConnection;
import user.*;

import java.security.MessageDigest;
import java.sql.*;

public class AuthService {

    private AuthService() {}

    public static String hashPassword(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(plain.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    // Returns User object on success, null on failure
    public static User login(String email, String password) throws SQLException {
        String hashed = hashPassword(password);
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ps.setString(2, hashed);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return buildUser(rs, conn);
            }
        }
    }

    public static boolean registerTrainee(String username, String email, String password,
                                           String fullName, String dob, String phone,
                                           float height, float weight, int age, String goal)
            throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            int userId = insertUser(conn, username, email, password, fullName, dob, phone, "TRAINEE");
            String sql = "INSERT INTO trainees (trainee_id,height_cm,weight_kg,age,fitness_goal) VALUES (?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.setFloat(2, height);
                ps.setFloat(3, weight);
                ps.setInt(4, age);
                ps.setString(5, goal);
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public static boolean registerTrainer(String username, String email, String password,
                                           String fullName, String dob, String phone,
                                           float fee, String credentials, String specialization)
            throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            int userId = insertUser(conn, username, email, password, fullName, dob, phone, "TRAINER");
            String sql = "INSERT INTO trainers (trainer_id,credentials,specialization,monthly_fee) VALUES (?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.setString(2, credentials);
                ps.setString(3, specialization);
                ps.setFloat(4, fee);
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public static boolean registerNutritionist(String username, String email, String password,
                                                String fullName, String dob, String phone,
                                                float fee, String credentials, String specialization)
            throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            int userId = insertUser(conn, username, email, password, fullName, dob, phone, "NUTRITIONIST");
            String sql = "INSERT INTO nutritionists (nutritionist_id,credentials,specialization,monthly_fee) VALUES (?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.setString(2, credentials);
                ps.setString(3, specialization);
                ps.setFloat(4, fee);
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private static int insertUser(Connection conn, String username, String email, String password,
                                   String fullName, String dob, String phone, String role)
            throws SQLException {
        String sql = "INSERT INTO users (username,email,password,full_name,dob,phone,role) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, hashPassword(password));
            ps.setString(4, fullName);
            ps.setString(5, dob);
            ps.setString(6, phone);
            ps.setString(7, role);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) return gk.getInt(1);
                throw new SQLException("Failed to get generated user ID");
            }
        }
    }

    private static User buildUser(ResultSet rs, Connection conn) throws SQLException {
        int    userId = rs.getInt("user_id");
        String role   = rs.getString("role");
        String uname  = rs.getString("username");
        String pass   = rs.getString("password");
        String name   = rs.getString("full_name");
        String dob    = rs.getString("dob");
        String email  = rs.getString("email");
        String phone  = rs.getString("phone");

        User user;
        switch (role) {
            case "TRAINEE" -> {
                String sql2 = "SELECT * FROM trainees WHERE trainee_id = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                    ps2.setInt(1, userId);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        if (rs2.next()) {
                            user = new Trainee(uname, pass, name, dob, email, phone,
                                    rs2.getFloat("height_cm"), rs2.getFloat("weight_kg"),
                                    rs2.getInt("age"), rs2.getString("fitness_goal"));
                        } else {
                            user = new Trainee(uname, pass, name, dob, email, phone, 0, 0, 0, "");
                        }
                    }
                }
            }
            case "TRAINER" -> {
                String sql2 = "SELECT * FROM trainers WHERE trainer_id = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                    ps2.setInt(1, userId);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        if (rs2.next()) {
                            user = new Trainer(uname, pass, name, dob, email, phone,
                                    rs2.getFloat("monthly_fee"), rs2.getString("credentials"),
                                    rs2.getString("specialization"));
                        } else {
                            user = new Trainer(uname, pass, name, dob, email, phone, 0, "", "");
                        }
                    }
                }
            }
            default -> {
                String sql2 = "SELECT * FROM nutritionists WHERE nutritionist_id = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                    ps2.setInt(1, userId);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        if (rs2.next()) {
                            user = new Nutritionist(uname, pass, name, dob, email, phone,
                                    rs2.getFloat("monthly_fee"), rs2.getString("credentials"),
                                    rs2.getString("specialization"));
                        } else {
                            user = new Nutritionist(uname, pass, name, dob, email, phone, 0, "", "");
                        }
                    }
                }
            }
        }
        user.setUserId(userId);
        return user;
    }
}
