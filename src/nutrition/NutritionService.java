package nutrition;

import java.util.ArrayList;
import java.util.List;

public class NutritionService {
    private final int nutritionistId;
    private final List<MealPlan> createdPlans;

    public NutritionService(int nutritionistId) {
        this.nutritionistId = nutritionistId;
        this.createdPlans = new ArrayList<>();
    }

    public MealPlan createMealPlan(int traineeId, float dailyTarget, String planName) {
        MealPlan plan = new MealPlan(this.nutritionistId, traineeId, dailyTarget, planName);
        createdPlans.add(plan);
        System.out.println("Meal plan '" + planName + "' created for Trainee #" + traineeId);
        return plan;
    }

    public void addMealToPlan(MealPlan plan, Meal meal) {
        plan.addMeal(meal);
        System.out.println("Added '" + meal.getMealName() + "' to plan: " + plan.getPlanName());
    }

    public void removeMealFromPlan(MealPlan plan, String mealName) {
        plan.removeMeal(mealName);
        System.out.println("Removed '" + mealName + "' from plan: " + plan.getPlanName());
    }

    public int getNutritionistId()        { return nutritionistId; }
    public List<MealPlan> getCreatedPlans() { return createdPlans; }
}
