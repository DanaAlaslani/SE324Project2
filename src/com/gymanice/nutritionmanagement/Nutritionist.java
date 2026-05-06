package com.gymanice.nutritionmanagement;

import java.util.ArrayList;
import java.util.List;

// Represents a nutritionist who can create and manage meal plans
public class Nutritionist {
    private final int id;
    private final String name;
    private final float fee;
    private final String credentials;
    private final List<MealPlan> createdPlans;
    
    public Nutritionist(int id, String name, float fee, String credentials) {
        this.id = id;
        this.name = name;
        this.fee = fee;
        this.credentials = credentials;
        this.createdPlans = new ArrayList<>();
    }
    
    // Creates a new meal plan for a specific trainee
    public MealPlan createMealPlan(int traineeId, float dailyTarget, String planName) {
        MealPlan plan = new MealPlan(this.id, traineeId, dailyTarget, planName);
        createdPlans.add(plan);
        System.out.println("Nutritionist " + name + " created meal plan: " + planName);
        return plan;
    }
    
    // Adds a meal to an existing meal plan
    public void addMealPlan(MealPlan plan, Meal meal) {
        plan.addMeal(meal);
        System.out.println("Added meal '" + meal.getMealName() + "' to plan: " + plan.getPlanName());
    }
    
    // Getters for all fields
    public int getId() { return id; }
    public String getName() { return name; }
    public float getFee() { return fee; }
    public String getCredentials() { return credentials; }
    public List<MealPlan> getCreatedPlans() { return createdPlans; }
}