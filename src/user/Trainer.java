package user;

public class Trainer extends User {
    private float fee;
    private String credentials;
    private String specialization;

    public Trainer(String username, String password, String name, String dob,
                   String email, String phone,
                   float fee, String credentials, String specialization) {
        super(username, "TRAINER", password, name, dob, email, phone);
        this.fee = fee;
        this.credentials = credentials;
        this.specialization = specialization;
        this.isVerified = true;
    }

    public void createWorkoutSchedule(String traineeUsername, String goal) {
        System.out.println("Workout schedule created for " + traineeUsername + " | Goal: " + goal);
    }

    @Override
    public void viewProfile() {
        super.viewProfile();
        System.out.println("Credentials   : " + credentials);
        System.out.println("Specialization: " + specialization);
        System.out.printf("Monthly Fee   : %.0f SAR%n", fee);
    }

    public float getFee()            { return fee; }
    public String getCredentials()   { return credentials; }
    public String getSpecialization(){ return specialization; }
    public void setFee(float fee)    { this.fee = fee; }
}
