package user.dao;

import db.DatabaseConnection;
import auth.AuthService;

import java.sql.*;

public class UserDAO {

    private UserDAO() {}

    public static boolean updateProfile(int userId, String fullName, String email, String phone)
            throws SQLException {
        String sql = "UPDATE users SET full_name=?, email=?, phone=? WHERE user_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean resetPassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password=? WHERE user_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, AuthService.hashPassword(newPassword));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean updateTraineeMetrics(int traineeId, float height, float weight,
                                                int age, String goal) throws SQLException {
        String sql = "UPDATE trainees SET height_cm=?, weight_kg=?, age=?, fitness_goal=? WHERE trainee_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setFloat(1, height);
            ps.setFloat(2, weight);
            ps.setInt(3, age);
            ps.setString(4, goal);
            ps.setInt(5, traineeId);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public static boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
