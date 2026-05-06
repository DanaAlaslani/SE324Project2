package progress;

import java.util.Calendar;
import java.util.Date;

public class ProgressTracker {
    private final int traineeId;
    private final float initialWeight;
    private final float targetWeight;
    private final Date startDate;
    private float currentWeight;
    private float progress;
    private Date predictedCompletion;

    public ProgressTracker(int traineeId, float initialWeight, float targetWeight) {
        this.traineeId     = traineeId;
        this.initialWeight = initialWeight;
        this.targetWeight  = targetWeight;
        this.currentWeight = initialWeight;
        this.startDate     = new Date();
        calculateProgress();
        predictCompletion();
    }

    public void updateWeight(float newWeight) {
        this.currentWeight = newWeight;
        calculateProgress();
        predictCompletion();
        System.out.printf("Weight updated to %.1f kg for Trainee #%d%n", newWeight, traineeId);
    }

    private void calculateProgress() {
        if (initialWeight == targetWeight) { this.progress = 100.0f; return; }
        float total = Math.abs(initialWeight - targetWeight);
        float done  = Math.abs(initialWeight - currentWeight);
        this.progress = Math.min((done / total) * 100f, 100.0f);
    }

    private void predictCompletion() {
        float remaining = Math.abs(currentWeight - targetWeight);
        int weeks = (int) Math.ceil(remaining / 0.5f);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR, weeks);
        this.predictedCompletion = cal.getTime();
    }

    public void displayProgress() {
        System.out.println("\n--- Progress Report: Trainee #" + traineeId + " ---");
        System.out.println("Start Date     : " + startDate);
        System.out.printf("Start Weight   : %.1f kg%n", initialWeight);
        System.out.printf("Current Weight : %.1f kg%n", currentWeight);
        System.out.printf("Target Weight  : %.1f kg%n", targetWeight);
        System.out.printf("Progress       : %.1f%%%n",  progress);
        System.out.printf("Remaining      : %.1f kg%n", Math.abs(currentWeight - targetWeight));
        System.out.println("Est. Completion: " + predictedCompletion);
    }

    public int getTraineeId()             { return traineeId; }
    public float getProgress()            { return progress; }
    public float getCurrentWeight()       { return currentWeight; }
    public float getTargetWeight()        { return targetWeight; }
    public float getInitialWeight()       { return initialWeight; }
    public Date getPredictedCompletion()  { return predictedCompletion; }
}
