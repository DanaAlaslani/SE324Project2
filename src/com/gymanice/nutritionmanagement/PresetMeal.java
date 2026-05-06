package com.gymanice.nutritionmanagement;

// Pre-defined meals that come with the app
public class PresetMeal extends Meal {
    private String category;  // Breakfast, Lunch, Dinner, Snack
    private int prepTime;     // Minutes to prepare
    
    public PresetMeal(String mealName, double calories, double protein, double carbs, double fat, String category, int prepTime) {
        super(mealName, calories, protein, carbs, fat);
        this.category = category;
        this.prepTime = prepTime;
    }
    
    public String getCategory() { return category; }
    public int getPrepTime() { return prepTime; }
}