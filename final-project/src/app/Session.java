package app;

import model.User;

/** Holds the logged-in user for authorization checks across the UI. */
public class Session {
    public static User current;
    public static boolean isAdmin() { return current != null && current.isAdmin(); }
}
