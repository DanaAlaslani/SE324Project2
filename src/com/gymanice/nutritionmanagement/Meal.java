package com.gymanice.nutritionmanagement;

// Represents a single meal with its nutritional values
public class Meal {
    private final String mealName;
    private final double calories;
    private final double protein;
    private final double carbs;
    private final double fat;
    
    public Meal(String mealName, double calories, double protein, double carbs, double fat) {
        this.mealName = mealName;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
    }
    
    public String getMealName() { return mealName; }
    public double getCalories() { return calories; }
    public double getProtein() { return protein; }
    public double getCarbs() { return carbs; }
    public double getFat() { return fat; }
    
    @Override
    public String toString() {
        return mealName + " - " + calories + " calories";
    }
}