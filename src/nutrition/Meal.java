package nutrition;

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
    public double getProtein()  { return protein; }
    public double getCarbs()    { return carbs; }
    public double getFat()      { return fat; }

    @Override
    public String toString() {
        return String.format("%s | Cal: %.0f | P: %.1fg | C: %.1fg | F: %.1fg",
                mealName, calories, protein, carbs, fat);
    }
}
