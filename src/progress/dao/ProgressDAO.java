package progress.dao;

import db.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class ProgressDAO {

    private ProgressDAO() {}

    public static boolean saveRecord(int traineeId, String date, float weightKg, float heightCm,
                                      float initialWeight, float targetWeight) throws SQLException {
        float progress = 0;
        if (initialWeight != targetWeight) {
            float done  = Math.abs(initialWeight - weightKg);
            float total = Math.abs(initialWeight - targetWeight);
            progress = Math.min((done / total) * 100f, 100f);
        }

        float remaining = Math.abs(weightKg - targetWeight);
        int   weeks     = (int) Math.ceil(remaining / 0.5f);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.WEEK_OF_YEAR, weeks);
        String predicted = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

        String sql = """
            INSERT INTO progress_records
            (trainee_id,record_date,weight_kg,height_cm,initial_weight_kg,target_weight_kg,
             progress_percent,predicted_completion)
            VALUES (?,?,?,?,?,?,?,?)
            ON DUPLICATE KEY UPDATE
              weight_kg=VALUES(weight_kg),
              progress_percent=VALUES(progress_percent),
              predicted_completion=VALUES(predicted_completion)""";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, traineeId);
            ps.setString(2, date);
            ps.setFloat(3, weightKg);
            ps.setFloat(4, heightCm);
            ps.setFloat(5, initialWeight);
            ps.setFloat(6, targetWeight);
            ps.setFloat(7, progress);
            ps.setString(8, predicted);
            return ps.executeUpdate() > 0;
        }
    }

    public static List<String[]> getRecordsByTrainee(int traineeId) throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = """
            SELECT record_date, weight_kg, progress_percent, predicted_completion
            FROM progress_records
            WHERE trainee_id = ?
            ORDER BY record_date DESC LIMIT 30""";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, traineeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            rs.getString("record_date"),
                            String.format("%.1f", rs.getFloat("weight_kg")),
                            String.format("%.1f", rs.getFloat("progress_percent")),
                            rs.getString("predicted_completion")
                    });
                }
            }
        }
        return list;
    }

    public static String[] getLatestRecord(int traineeId) throws SQLException {
        String sql = """
            SELECT record_date, weight_kg, initial_weight_kg, target_weight_kg,
                   progress_percent, predicted_completion
            FROM progress_records
            WHERE trainee_id = ?
            ORDER BY record_date DESC LIMIT 1""";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, traineeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                            rs.getString("record_date"),
                            String.format("%.1f", rs.getFloat("weight_kg")),
                            String.format("%.1f", rs.getFloat("initial_weight_kg")),
                            String.format("%.1f", rs.getFloat("target_weight_kg")),
                            String.format("%.1f", rs.getFloat("progress_percent")),
                            rs.getString("predicted_completion")
                    };
                }
            }
        }
        return null;
    }
}
