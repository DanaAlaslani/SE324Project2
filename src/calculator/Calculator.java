package calculator;

import nutrition.Meal;
import java.util.HashMap;
import java.util.Map;

public class Calculator {

    public float calculateBMI(float heightInCm, float weightInKg) {
        if (heightInCm <= 0 || weightInKg <= 0)
            throw new IllegalArgumentException("Height and weight must be positive.");
        float h = heightInCm / 100f;
        return weightInKg / (h * h);
    }

    public String getBMICategory(float bmi) {
        if (bmi < 18.5f) return "Underweight";
        if (bmi < 25.0f) return "Normal";
        if (bmi < 30.0f) return "Overweight";
        return "Obese";
    }

    // Returns the macros of any Meal object
    public Map<String, Double> getMacros(Meal meal) {
        Map<String, Double> macros = new HashMap<>();
        macros.put("Calories", meal.getCalories());
        macros.put("Protein",  meal.getProtein());
        macros.put("Carbs",    meal.getCarbs());
        macros.put("Fat",      meal.getFat());
        return macros;
    }

    // Calculates macros from raw ingredients and quantities (in grams)
    public Map<String, Float> calculateMacrosFromIngredients(Map<Ingredient, Float> ingredients) {
        float carbs = 0, protein = 0, fat = 0, fiber = 0;
        for (Map.Entry<Ingredient, Float> entry : ingredients.entrySet()) {
            Ingredient ing = entry.getKey();
            float grams = entry.getValue();
            carbs   += ing.getCarbsPerGram()   * grams;
            protein += ing.getProteinPerGram() * grams;
            fat     += ing.getFatPerGram()     * grams;
            fiber   += ing.getFiberPerGram()   * grams;
        }
        float calories = (carbs * 4) + (protein * 4) + (fat * 9);

        Map<String, Float> result = new HashMap<>();
        result.put("Calories", calories);
        result.put("Protein",  protein);
        result.put("Carbs",    carbs);
        result.put("Fat",      fat);
        result.put("Fiber",    fiber);
        return result;
    }
}
