package com.gymanice.nutritionmanagement;

// Meals that trainees create themselves
public class CustomMeal extends Meal {
    private int traineeId;     // Who created this meal
    private String notes;      // Personal notes about the meal
    
    public CustomMeal(String mealName, double calories, double protein, double carbs, double fat, int traineeId, String notes) {
        super(mealName, calories, protein, carbs, fat);
        this.traineeId = traineeId;
        this.notes = notes;
    }
    
    public int getTraineeId() { return traineeId; }
    public String getNotes() { return notes; }
}
