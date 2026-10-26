package com.model;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Holds every user in the system. Singleton, so the whole app shares one list.
 * 
 * @author Abaan Jafri
 */
public class UserManagement {
    private static UserManagement userManagement;
    private ArrayList<User> users;

    // Private so only getInstance() can create it. Loads the saved users.
    private UserManagement() {
        users = DataLoader.getUsers();
    }

    // Returns the one shared instance, creating it the first time.
    public static UserManagement getInstance() {
        if (userManagement == null) {
            userManagement = new UserManagement();
        }
        return userManagement;
    }

    // Returns all users.
    public ArrayList<User> getUsers() {
        return users;
    }

    // Finds a user by id, or null if not found.
    public User getUser(UUID id) {
        if (id == null) {
            return null;
        }
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    // Finds a user by username, ignoring case.
    public User getUserByUserName(String userName) {
        if (userName == null) {
            return null;
        }
        for (User user : users) {
            if (user.getUserName().equalsIgnoreCase(userName.trim())) {
                return user;
            }
        }
        return null;
    }

    // Finds a user whose username AND password match. Used for login.
    public User getUser(String userName, String password) {
        User user = getUserByUserName(userName);
        if (user == null || password == null || !user.getPassword().equals(password)) {
            return null;
        }
        return user;
    }

    // Makes a new user and adds them. Same parameters as the User constructor.
    // Returns false if username/password is blank or the username is taken.
    public boolean addUser(String userName, String firstName, String lastName,
            String password, Location location) {
        if (userName == null || userName.isBlank() || password == null || password.isBlank()) {
            return false;
        }
        if (getUserByUserName(userName) != null) {
            return false;
        }
        users.add(new User(userName.trim(), firstName, lastName, password, location));
        return true;
    }

    // Removes the user with the given id.
    public void removeUser(UUID id) {
        users.removeIf(user -> user.getId().equals(id));
    }

    // Updates the name and location of the user with the given id.
    public void editUser(UUID id, String firstName, String lastName, Location location) {
        User user = getUser(id);
        if (user != null) {
            user.editProfile(firstName, lastName, location);
        }
    }

    // Tells every volunteer and professional about a new request.
    public void notifyResponders(ReliefRequest request) {
        for (User user : users) {
            if (user.isResponder()) {
                System.out.println("Notifying " + user.getUserName() + ": new request " + request.getId());
            }
        }
    }

    // Writes the current user list back to the JSON file.
    public void saveUsers() {
        DataWriter.saveUsers(users);
    }
}
