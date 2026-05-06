    import java.util.HashMap;
import java.util.Map;
public class CalCulator {


// 1. Abstract Meal Class
public abstract class Meal {
    protected int mealId;
    protected String name;
    protected String mealType;
    protected float carbs;
    protected float protein;
    protected float fat;
    protected float fiber; // Added based on your custom requirements
    protected float calories;

    // Abstract method to be implemented by child classes
    public abstract void calculateMacros();

    // Getters
    public float getCarbs() { return carbs; }
    public float getProtein() { return protein; }
    public float getFat() { return fat; }
    public float getFiber() { return fiber; }
    public float getCalories() { return calories; }
    public String getName() { return name; }
}

// 2. Preset Meal (Fetches from Database)
class PresetMeal extends Meal {
    private String recipe;

    public PresetMeal(int mealId, String name) {
        this.mealId = mealId;
        this.name = name;
        calculateMacros(); // Fetch macros upon instantiation
    }

    @Override
    public void calculateMacros() {
        // TODO: Replace this simulation with actual MySQL JDBC connection
        // Example: "SELECT carbs, protein, fat, fiber, calories FROM preset_meals WHERE meal_id = ?"
        System.out.println("Fetching macros from Database for preset meal: " + this.name);
        
        // Simulated DB Data
        this.carbs = 45.0f;
        this.protein = 30.0f;
        this.fat = 15.0f;
        this.fiber = 8.0f;
        // Standard macro calorie calculation: (Carbs * 4) + (Protein * 4) + (Fat * 9)
        this.calories = (this.carbs * 4) + (this.protein * 4) + (this.fat * 9); 
    }

    public String getRecipe() {
        return recipe;
    }
}

// 3. Ingredient Helper Class for Custom Meals
class Ingredient {
    private String name;
    private float carbsPerGram;
    private float proteinPerGram;
    private float fatPerGram;
    private float fiberPerGram;

    public Ingredient(String name, float carbs, float protein, float fat, float fiber) {
        this.name = name;
        this.carbsPerGram = carbs / 100f; // Assuming inputs are per 100g
        this.proteinPerGram = protein / 100f;
        this.fatPerGram = fat / 100f;
        this.fiberPerGram = fiber / 100f;
    }

    public float getCarbsPerGram() { return carbsPerGram; }
    public float getProteinPerGram() { return proteinPerGram; }
    public float getFatPerGram() { return fatPerGram; }
    public float getFiberPerGram() { return fiberPerGram; }
}

// 4. Custom Meal (Calculates from Ingredients)
class CustomMeal extends Meal {
    // Stores the ingredient and the amount (in grams) chosen by the user
    private Map<Ingredient, Float> ingredients = new HashMap<>();

    public CustomMeal(String name) {
        this.name = name;
    }

    public void addIngredient(Ingredient ingredient, float amountInGrams) {
        ingredients.put(ingredient, amountInGrams);
    }

    @Override
    public void calculateMacros() {
        this.carbs = 0; this.protein = 0; this.fat = 0; this.fiber = 0; this.calories = 0;

        for (Map.Entry<Ingredient, Float> entry : ingredients.entrySet()) {
            Ingredient ing = entry.getKey();
            float amount = entry.getValue();

            this.carbs += ing.getCarbsPerGram() * amount;
            this.protein += ing.getProteinPerGram() * amount;
            this.fat += ing.getFatPerGram() * amount;
            this.fiber += ing.getFiberPerGram() * amount;
        }
        
        this.calories = (this.carbs * 4) + (this.protein * 4) + (this.fat * 9);
        System.out.println("Custom Meal Macros Calculated Successfully.");
    }
}

// 5. The Shared Calculator
public class Calculator {
    
    public Map<String, Float> calculateMacros(Meal meal) {
        meal.calculateMacros(); // Polymorphic call
        
        Map<String, Float> macros = new HashMap<>();
        macros.put("Carbs", meal.getCarbs());
        macros.put("Protein", meal.getProtein());
        macros.put("Fat", meal.getFat());
        macros.put("Fiber", meal.getFiber());
        macros.put("Calories", meal.getCalories());
        
        return macros;
    }

    public float calculateBMI(float heightInCm, float weightInKg) {
        float heightInMeters = heightInCm / 100f;
        return weightInKg / (heightInMeters * heightInMeters);
    }
}
}

