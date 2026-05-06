import java.util.Date;
import java.util.Calendar;

public class ProgressTracker {
    private int traineeId;
    private Date startDate;
    private Date endDate;
    private float progress;
    private Date predictedCompletion;

    public ProgressTracker(int traineeId, Date startDate) {
        this.traineeId = traineeId;
        this.startDate = startDate;
    }

    // Calculates progress as a percentage between initial and target weight
    public void calculateProgress(float initialWeight, float currentWeight, float targetWeight) {
        if (initialWeight == targetWeight) {
            this.progress = 100.0f;
            return;
        }
        
        float totalToLoseOrGain = Math.abs(initialWeight - targetWeight);
        float currentDifference = Math.abs(initialWeight - currentWeight);
        
        this.progress = (currentDifference / totalToLoseOrGain) * 100;
        
        // Cap progress at 100%
        if (this.progress > 100.0f) this.progress = 100.0f; 
    }

    // Predicts completion based on a healthy rate of 0.5kg per week
    public void predictCompletion(float currentWeight, float targetWeight) {
        float weightRemaining = Math.abs(currentWeight - targetWeight);
        int weeksRequired = (int) (weightRemaining / 0.5f); 
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date()); // Current date
        cal.add(Calendar.WEEK_OF_YEAR, weeksRequired);
        
        this.predictedCompletion = cal.getTime();
    }

    public void displayProgress() {
        System.out.println("--- Progress Report for Trainee ID: " + traineeId + " ---");
        System.out.printf("Current Progress: %.2f%%\n", progress);
        System.out.println("Predicted Goal Completion: " + predictedCompletion);
    }
}