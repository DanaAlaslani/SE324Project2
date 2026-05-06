package user;

public abstract class User {
    protected int userId;
    protected String username;
    protected String role;
    protected String password;
    protected String name;
    protected String dob;
    protected String email;
    protected String phone;
    protected boolean isVerified;

    public User(String username, String role, String password, String name,
                String dob, String email, String phone) {
        this.username = username;
        this.role = role;
        this.password = password;
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.phone = phone;
        this.isVerified = false;
    }

    public void resetPassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        this.password = newPassword;
        System.out.println("Password reset successfully for: " + name);
    }

    public void viewProfile() {
        System.out.println("\n=== Profile ===");
        System.out.println("Name     : " + name);
        System.out.println("Username : " + username);
        System.out.println("Email    : " + email);
        System.out.println("Phone    : " + phone);
        System.out.println("Role     : " + role);
        System.out.println("Verified : " + isVerified);
    }

    public void editProfile(String newName, String newEmail, String newPhone) {
        if (newName == null || newName.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty.");
        if (newEmail == null || newEmail.isEmpty())
            throw new IllegalArgumentException("Email cannot be empty.");
        this.name = newName;
        this.email = newEmail;
        this.phone = newPhone;
        System.out.println("Profile updated successfully.");
    }

    public int    getUserId()   { return userId; }
    public void   setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public String getRole()     { return role; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public String getDob()      { return dob; }
    public String getPhone()    { return phone; }
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { this.isVerified = verified; }
}
