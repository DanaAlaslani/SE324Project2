package nutrition;

public class PresetMeal extends Meal {
    private final String category;
    private final int prepTimeMinutes;

    public PresetMeal(String mealName, double calories, double protein,
                      double carbs, double fat, String category, int prepTimeMinutes) {
        super(mealName, calories, protein, carbs, fat);
        this.category = category;
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public String getCategory()     { return category; }
    public int getPrepTimeMinutes() { return prepTimeMinutes; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | [%s, %d min]", category, prepTimeMinutes);
    }
}
