package nutrition;

import java.util.ArrayList;
import java.util.List;

public class MealPlan {
    private final String planName;
    private final int nutritionistId;
    private final int traineeId;
    private final float dailyTarget;
    private final List<Meal> meals;
    private String status;

    public MealPlan(int nutritionistId, int traineeId, float dailyTarget, String planName) {
        this.nutritionistId = nutritionistId;
        this.traineeId = traineeId;
        this.dailyTarget = dailyTarget;
        this.planName = planName;
        this.meals = new ArrayList<>();
        this.status = "Draft";
    }

    public void addMeal(Meal meal) {
        meals.add(meal);
    }

    public void removeMeal(String mealName) {
        meals.removeIf(m -> m.getMealName().equals(mealName));
    }

    public double calculateTotalCalories() {
        double total = 0;
        for (Meal m : meals) total += m.getCalories();
        return total;
    }

    public boolean meetsDailyTarget() {
        return calculateTotalCalories() <= dailyTarget;
    }

    public double getRemainingCalories() {
        return dailyTarget - calculateTotalCalories();
    }

    public void activate() {
        this.status = "Active";
        System.out.println("Plan '" + planName + "' is now Active.");
    }

    public void displayMealPlan() {
        System.out.println("\n=== Meal Plan: " + planName + " ===");
        System.out.println("Trainee ID        : " + traineeId);
        System.out.println("Nutritionist ID   : " + nutritionistId);
        System.out.printf("Daily Target      : %.0f cal%n", (double) dailyTarget);
        System.out.println("Status            : " + status);
        System.out.println("Meals (" + meals.size() + "):");
        for (int i = 0; i < meals.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + meals.get(i));
        }
        System.out.printf("Total             : %.0f / %.0f cal%n",
                calculateTotalCalories(), (double) dailyTarget);
        System.out.printf("Remaining         : %.0f cal%n", getRemainingCalories());
        System.out.println("Meets Target      : " + (meetsDailyTarget() ? "Yes" : "No"));
    }

    public String getPlanName()      { return planName; }
    public int getNutritionistId()   { return nutritionistId; }
    public int getTraineeId()        { return traineeId; }
    public float getDailyTarget()    { return dailyTarget; }
    public String getStatus()        { return status; }
    public void setStatus(String s)  { this.status = s; }
    public List<Meal> getMeals()     { return meals; }
    public int getMealCount()        { return meals.size(); }
}
