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

    /** @return the single instance */
    public static UserManagement getInstance() {
        if (userManagement == null) {
            userManagement = new UserManagement();
        }
        return userManagement;
    }

    /** @return all users */
    public ArrayList<User> getUsers() {
        return users;
    }

    /** @param id user id @return the matching user, or null */
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

    /** @param userName username, case doesn't matter @return the matching user, or null */
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

    /** @param userName username @param password password @return the user if both match, or null */
    public User getUser(String userName, String password) {
        User user = getUserByUserName(userName);
        if (user == null || password == null || !user.getPassword().equals(password)) {
            return null;
        }
        return user;
    }

    /** @param userName username @param firstName first name @param lastName last name @param password password @param location home location @return false if blank or the username is taken */
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

    /** @param id id of user to remove */
    public void removeUser(UUID id) {
        users.removeIf(user -> user.getId().equals(id));
    }

    /** @param id user id @param firstName new first name @param lastName new last name @param location new location */
    public void editUser(UUID id, String firstName, String lastName, Location location) {
        User user = getUser(id);
        if (user != null) {
            user.editProfile(firstName, lastName, location);
        }
    }

    /** @param request the new request to tell responders about */
    public void notifyResponders(ReliefRequest request) {
        for (User user : users) {
            if (user.isResponder()) {
                System.out.println("Notifying " + user.getUserName() + ": new request " + request.getId());
            }
        }
    }

    /** Saves all users to the JSON file. */
    public void saveUsers() {
        DataWriter.saveUsers(users);
    }
}