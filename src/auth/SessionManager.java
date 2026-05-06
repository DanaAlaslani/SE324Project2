package auth;

import user.User;

public class SessionManager {

    private static User currentUser;

    private SessionManager() {}

    public static void login(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static int getUserId() {
        return currentUser != null ? currentUser.getUserId() : -1;
    }

    public static String getRole() {
        return currentUser != null ? currentUser.getRole() : "";
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
