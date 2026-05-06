package workout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkoutSchedule {

    private int scheduleId;
    private int trainerId;
    private int traineeId;
    private Date date;
    private List<Exercise> exercises;

    public WorkoutSchedule(int scheduleId, int trainerId, int traineeId, Date date) {
        this.scheduleId = scheduleId;
        this.trainerId = trainerId;
        this.traineeId = traineeId;
        this.date = date;
        this.exercises = new ArrayList<>();
    }

    public int getScheduleId() { return scheduleId; }
    public int getTrainerId() { return trainerId; }
    public int getTraineeId() { return traineeId; }
    public Date getDate() { return date; }
    public List<Exercise> getExercises() { return exercises; }

    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }
    public void setTrainerId(int trainerId) { this.trainerId = trainerId; }
    public void setTraineeId(int traineeId) { this.traineeId = traineeId; }
    public void setDate(Date date) { this.date = date; }

    public List<Exercise> getExercisesList() {
        return exercises;
    }

    public void addExercise(Exercise exercise) {
        exercises.add(exercise);
    }

    public void removeExercise(Exercise exercise) {
        exercises.remove(exercise);
    }

    @Override
    public String toString() {
        return "Schedule #" + scheduleId + " | Trainee: " + traineeId + " | Date: " + date;
    }
}