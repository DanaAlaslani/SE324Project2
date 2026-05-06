package workout.dao;

import db.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class WorkoutDAO {

    private WorkoutDAO() {}

    // ── Exercises ────────────────────────────────────────────

    public static List<String[]> getAllExercises() throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT exercise_id, name, muscle_group, description FROM exercises ORDER BY muscle_group, name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("exercise_id"),
                        rs.getString("name"),
                        rs.getString("muscle_group"),
                        rs.getString("description")
                });
            }
        }
        return list;
    }

    // ── Workout Schedules ─────────────────────────────────────

    public static int createSchedule(int trainerId, int traineeId, String date, String notes)
            throws SQLException {
        String sql = "INSERT INTO workout_schedules (trainer_id,trainee_id,schedule_date,notes) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, trainerId);
            ps.setInt(2, traineeId);
            ps.setString(3, date);
            ps.setString(4, notes);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                return gk.next() ? gk.getInt(1) : -1;
            }
        }
    }

    public static boolean addExerciseToSchedule(int scheduleId, int exerciseId,
                                                  int sets, int reps, int weight) throws SQLException {
        String sql = """
            INSERT INTO schedule_exercises
            (schedule_id,exercise_id,target_sets,target_reps,target_weight)
            VALUES (?,?,?,?,?)
            ON DUPLICATE KEY UPDATE
              target_sets=VALUES(target_sets),
              target_reps=VALUES(target_reps),
              target_weight=VALUES(target_weight)""";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            ps.setInt(2, exerciseId);
            ps.setInt(3, sets);
            ps.setInt(4, reps);
            ps.setInt(5, weight);
            return ps.executeUpdate() > 0;
        }
    }

    // Returns [{schedule_id, schedule_date, notes, trainer_name}]
    public static List<String[]> getSchedulesByTrainee(int traineeId) throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = """
            SELECT ws.schedule_id, ws.schedule_date, ws.notes, u.full_name AS trainer_name
            FROM workout_schedules ws
            JOIN users u ON ws.trainer_id = u.user_id
            WHERE ws.trainee_id = ?
            ORDER BY ws.schedule_date DESC""";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, traineeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            rs.getString("schedule_id"),
                            rs.getString("schedule_date"),
                            rs.getString("notes"),
                            rs.getString("trainer_name")
                    });
                }
            }
        }
        return list;
    }

    // Returns [{exercise_id, name, muscle_group, target_sets, target_reps, target_weight}]
    public static List<String[]> getExercisesForSchedule(int scheduleId) throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = """
            SELECT e.exercise_id, e.name, e.muscle_group, e.description,
                   se.target_sets, se.target_reps, se.target_weight
            FROM schedule_exercises se
            JOIN exercises e ON se.exercise_id = e.exercise_id
            WHERE se.schedule_id = ?
            ORDER BY se.sort_order""";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            rs.getString("exercise_id"),
                            rs.getString("name"),
                            rs.getString("muscle_group"),
                            rs.getString("description"),
                            rs.getString("target_sets"),
                            rs.getString("target_reps"),
                            rs.getString("target_weight")
                    });
                }
            }
        }
        return list;
    }

    // ── Performance Logging ───────────────────────────────────

    public static boolean logPerformance(int scheduleId, int exerciseId, int traineeId,
                                          String date, int sets, int reps, int weight,
                                          String notes) throws SQLException {
        String sql = """
            INSERT INTO exercise_performances
            (schedule_id,exercise_id,trainee_id,logged_date,actual_sets,actual_reps,actual_weight,notes)
            VALUES (?,?,?,?,?,?,?,?)""";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            ps.setInt(2, exerciseId);
            ps.setInt(3, traineeId);
            ps.setString(4, date);
            ps.setInt(5, sets);
            ps.setInt(6, reps);
            ps.setInt(7, weight);
            ps.setString(8, notes);
            return ps.executeUpdate() > 0;
        }
    }

    public static List<String[]> getPerformanceHistory(int traineeId) throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = """
            SELECT ep.logged_date, e.name, e.muscle_group,
                   ep.actual_sets, ep.actual_reps, ep.actual_weight
            FROM exercise_performances ep
            JOIN exercises e ON ep.exercise_id = e.exercise_id
            WHERE ep.trainee_id = ?
            ORDER BY ep.logged_date DESC LIMIT 50""";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, traineeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            rs.getString("logged_date"),
                            rs.getString("name"),
                            rs.getString("muscle_group"),
                            rs.getString("actual_sets"),
                            rs.getString("actual_reps"),
                            rs.getString("actual_weight")
                    });
                }
            }
        }
        return list;
    }

    // Returns schedules created by a trainer
    public static List<String[]> getSchedulesByTrainer(int trainerId) throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = """
            SELECT ws.schedule_id, ws.schedule_date, ws.notes, u.full_name AS trainee_name
            FROM workout_schedules ws
            JOIN users u ON ws.trainee_id = u.user_id
            WHERE ws.trainer_id = ?
            ORDER BY ws.schedule_date DESC""";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            rs.getString("schedule_id"),
                            rs.getString("schedule_date"),
                            rs.getString("notes"),
                            rs.getString("trainee_name")
                    });
                }
            }
        }
        return list;
    }

    // Returns all trainees (for trainer to assign schedules)
    public static List<String[]> getAllTrainees() throws SQLException {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT t.trainee_id, u.full_name, u.email FROM trainees t JOIN users u ON t.trainee_id = u.user_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{rs.getString("trainee_id"), rs.getString("full_name"), rs.getString("email")});
            }
        }
        return list;
    }
}
