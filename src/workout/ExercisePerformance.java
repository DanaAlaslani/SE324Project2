package workout;

import java.util.Date;

public class ExercisePerformance {

    private int scheduled;
    private int traineeId;
    private Date date;
    private int actualSets;
    private int actualReps;
    private int actualWeight;

    public ExercisePerformance(int scheduled, int traineeId, Date date,int actualSets, int actualReps, int actualWeight) {
        this.scheduled = scheduled;
        this.traineeId = traineeId;
        this.date = date;
        this.actualSets = actualSets;
        this.actualReps = actualReps;
        this.actualWeight = actualWeight;
    }

    public int getScheduled() { 
        return scheduled; }
    public int getTraineeId() {
         return traineeId; }
    public Date getDate() {
         return date; }
    public int getActualSets() { 
        return actualSets; }
    public int getActualReps() {
         return actualReps; }
    public int getActualWeight() { 
        return actualWeight; }

    public void setScheduled(int scheduled) { 
        this.scheduled = scheduled; }
    public void setTraineeId(int traineeId) {
         this.traineeId = traineeId; }
    public void setDate(Date date) {
         this.date = date; }
    public void setActualSets(int actualSets) {
         this.actualSets = actualSets; }
    public void setActualReps(int actualReps) { 
        this.actualReps = actualReps; }
    public void setActualWeight(int actualWeight) {
         this.actualWeight = actualWeight; }

    public void logPerformance() {
        System.out.println("Trainee: " + traineeId + " | Date: " + date);
        System.out.println("Sets: " + actualSets + " | Reps: " + actualReps + " | Weight: " + actualWeight + "kg");
    }

    @Override
    public String toString() {
        return "Trainee: " + traineeId + " | Sets: " + actualSets + " | Reps: " + actualReps + " | Weight: " + actualWeight;
    }
}
