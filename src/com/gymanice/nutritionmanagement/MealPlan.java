package com.gymanice.nutritionmanagement;

import java.util.ArrayList;
import java.util.List;

// Main meal plan class that contains multiple meals for a trainee
public class MealPlan {
    private int planned;                 // Number of meals currently in the plan
    private final int nutritionist;      // ID of nutritionist who created this plan
    private final int trained;           // ID of trainee this plan belongs to
    private final float dailyTarget;     // Daily calorie goal for the trainee
    private final List<Meal> meals;      // List of all meals in this plan
    private final String planName;       // Name of the meal plan
    private String status;               // Status: Draft, Active, or Completed
    
    public MealPlan(int nutritionistId, int traineeId, float dailyTarget, String planName) {
        this.nutritionist = nutritionistId;
        this.trained = traineeId;
        this.dailyTarget = dailyTarget;
        this.planName = planName;
        this.planned = 0;
        this.meals = new ArrayList<>();
        this.status = "Draft";
    }
    
    // Returns the list of all meals in this plan
    public List<Meal> getMeals() {
        return meals;
    }
    
    // Adds a new meal to the plan and updates the count
    public void addMeal(Meal meal) {
        meals.add(meal);
        planned = meals.size();
    }
    
    // Removes a meal from the plan by its name
    public void removeMeal(String mealName) {
        meals.removeIf(meal -> meal.getMealName().equals(mealName));
        planned = meals.size();
    }
    
    // Calculates total calories from all meals in the plan
    public double calculateTotalCalories() {
        double total = 0;
        for (Meal meal : meals) {
            total += meal.getCalories();
        }
        return total;
    }
    
    // Checks if total calories are within the daily target
    public boolean meetsDailyTarget() {
        return calculateTotalCalories() <= dailyTarget;
    }
    
    // Returns how many calories are left for the day
    public double getRemainingCalories() {
        return dailyTarget - calculateTotalCalories();
    }
    
    // Changes plan status from Draft to Active
    public void activate() {
        this.status = "Active";
    }
    
    // Prints all details of the meal plan to console
    public void displayMealPlan() {
        System.out.println("\n=== Meal Plan: " + planName + " ===");
        System.out.println("For Trainee ID: " + trained);
        System.out.println("Created by Nutritionist ID: " + nutritionist);
        System.out.println("Daily Target: " + dailyTarget + " calories");
        System.out.println("Status: " + status);
        System.out.println("Planned Meals: " + planned);
        System.out.println("\nMeals:");
        
        for (int i = 0; i < meals.size(); i++) {
            Meal m = meals.get(i);
            System.out.println((i+1) + ". " + m.getMealName() + 
                             " | Calories: " + m.getCalories() +
                             " | Protein: " + m.getProtein() + "g" +
                             " | Carbs: " + m.getCarbs() + "g" +
                             " | Fat: " + m.getFat() + "g");
        }
        
        System.out.println("\nTotal Calories: " + calculateTotalCalories() + " / " + dailyTarget);
        System.out.println("Remaining: " + getRemainingCalories() + " calories");
        System.out.println("Meets Target: " + (meetsDailyTarget() ? "Yes" : "No"));
    }
    
    // Getters for all fields
    public int getPlanned() { return planned; }
    public int getNutritionist() { return nutritionist; }
    public int getTrained() { return trained; }
    public float getDailyTarget() { return dailyTarget; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPlanName() { return planName; }
}
