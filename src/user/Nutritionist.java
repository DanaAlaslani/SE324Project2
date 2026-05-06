package user;

public class Nutritionist extends User {
    private float fee;
    private String credentials;
    private String specialization;

    public Nutritionist(String username, String password, String name, String dob,
                        String email, String phone,
                        float fee, String credentials, String specialization) {
        super(username, "NUTRITIONIST", password, name, dob, email, phone);
        this.fee = fee;
        this.credentials = credentials;
        this.specialization = specialization;
        this.isVerified = true;
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
