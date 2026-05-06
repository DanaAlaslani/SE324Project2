package user;

public class Trainee extends User {
    private float height;
    private float weight;
    private String fitnessGoal;
    private int age;
    private float bmi;

    public Trainee(String username, String password, String name,
                   String dob, String email, String phone,
                   float height, float weight, int age, String fitnessGoal) {
        super(username, "TRAINEE", password, name, dob, email, phone);
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.fitnessGoal = fitnessGoal;
        this.isVerified = true;
        calculateBMI();
    }

    private void calculateBMI() {
        float h = height / 100f;
        this.bmi = weight / (h * h);
    }

    public void setHealthMetrics(float height, float weight) {
        this.height = height;
        this.weight = weight;
        calculateBMI();
        System.out.println("Health metrics updated for: " + name);
    }

    @Override
    public void viewProfile() {
        super.viewProfile();
        System.out.println("Age      : " + age);
        System.out.printf("Height   : %.1f cm%n", height);
        System.out.printf("Weight   : %.1f kg%n", weight);
        System.out.printf("BMI      : %.2f%n", bmi);
        System.out.println("Goal     : " + fitnessGoal);
    }

    public float getHeight()       { return height; }
    public float getWeight()       { return weight; }
    public String getFitnessGoal() { return fitnessGoal; }
    public int getAge()            { return age; }
    public float getBmi()          { return bmi; }
    public void setFitnessGoal(String fitnessGoal) { this.fitnessGoal = fitnessGoal; }
    public void setWeight(float weight) {
        this.weight = weight;
        calculateBMI();
    }
}
