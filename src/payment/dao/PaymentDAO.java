package payment.dao;

import db.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class PaymentDAO {

    private PaymentDAO() {}

    public static int savePayment(int traineeId, int trainerId, String paymentType,
                                   double amount) throws SQLException {
        String sql = """
            INSERT INTO payments (trainee_id,trainer_id,payment_type,amount_sar,status)
            VALUES (?,?,?,?,'PENDING')""";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, traineeId);
            ps.setInt(2, trainerId);
            ps.setString(3, paymentType);
            ps.setDouble(4, amount);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                return gk.next() ? gk.getInt(1) : -1;
            }
        }
    }

    public static boolean updateStatus(int paymentId, String status,
                                        String transactionRef) throws SQLException {
        String sql = "UPDATE payments SET status=?, transaction_ref=? WHERE payment_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, transactionRef);
            ps.setInt(3, paymentId);
            return ps.executeUpdate() > 0;
        }
    }

    public static List<String[]> getPaymentsByTrainee(int traineeId) throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = """
            SELECT p.payment_id, p.payment_date, p.payment_type, p.amount_sar,
                   ROUND(p.amount_sar * 0.05, 2) AS platform_fee,
                   ROUND(p.amount_sar * 0.95, 2) AS trainer_amount,
                   p.status, p.transaction_ref, u.full_name AS trainer_name
            FROM payments p
            JOIN users u ON p.trainer_id = u.user_id
            WHERE p.trainee_id = ?
            ORDER BY p.payment_date DESC""";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, traineeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            rs.getString("payment_id"),
                            rs.getString("payment_date"),
                            rs.getString("payment_type"),
                            rs.getString("amount_sar"),
                            rs.getString("platform_fee"),
                            rs.getString("trainer_amount"),
                            rs.getString("status"),
                            rs.getString("transaction_ref"),
                            rs.getString("trainer_name")
                    });
                }
            }
        }
        return list;
    }

    public static List<String[]> getPaymentsByTrainer(int trainerId) throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = """
            SELECT p.payment_id, p.payment_date, p.payment_type, p.amount_sar,
                   ROUND(p.amount_sar * 0.95, 2) AS trainer_amount,
                   p.status, u.full_name AS trainee_name
            FROM payments p
            JOIN users u ON p.trainee_id = u.user_id
            WHERE p.trainer_id = ?
            ORDER BY p.payment_date DESC""";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            rs.getString("payment_id"),
                            rs.getString("payment_date"),
                            rs.getString("payment_type"),
                            rs.getString("amount_sar"),
                            rs.getString("trainer_amount"),
                            rs.getString("status"),
                            rs.getString("trainee_name")
                    });
                }
            }
        }
        return list;
    }

    public static List<String[]> getAllTrainers() throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT t.trainer_id, u.full_name, t.monthly_fee FROM trainers t JOIN users u ON t.trainer_id = u.user_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{rs.getString("trainer_id"), rs.getString("full_name"), rs.getString("monthly_fee")});
            }
        }
        return list;
    }
}
