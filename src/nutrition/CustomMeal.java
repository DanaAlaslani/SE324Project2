package nutrition;

public class CustomMeal extends Meal {
    private final int traineeId;
    private final String notes;

    public CustomMeal(String mealName, double calories, double protein,
                      double carbs, double fat, int traineeId, String notes) {
        super(mealName, calories, protein, carbs, fat);
        this.traineeId = traineeId;
        this.notes = notes;
    }

    public int getTraineeId() { return traineeId; }
    public String getNotes()  { return notes; }

    @Override
    public String toString() {
        return super.toString() + " | [Custom - " + notes + "]";
    }
}
