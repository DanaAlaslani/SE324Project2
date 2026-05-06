package calculator;

public class Ingredient {
    private final String name;
    private final float carbsPerGram;
    private final float proteinPerGram;
    private final float fatPerGram;
    private final float fiberPerGram;

    // Inputs are per 100g values; stored as per-gram ratios
    public Ingredient(String name, float carbsPer100g, float proteinPer100g,
                      float fatPer100g, float fiberPer100g) {
        this.name = name;
        this.carbsPerGram   = carbsPer100g   / 100f;
        this.proteinPerGram = proteinPer100g / 100f;
        this.fatPerGram     = fatPer100g     / 100f;
        this.fiberPerGram   = fiberPer100g   / 100f;
    }

    public String getName()          { return name; }
    public float getCarbsPerGram()   { return carbsPerGram; }
    public float getProteinPerGram() { return proteinPerGram; }
    public float getFatPerGram()     { return fatPerGram; }
    public float getFiberPerGram()   { return fiberPerGram; }
}
