package com.model;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Holds every user in the system. Singleton, so the whole app shares one list.
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

    /**
     * Finds a user by id.
     * @param id the user's id
     * @return the user, or null if not found
     */
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

    /**
     * Finds a user by username, ignoring case. Used for login.
     * @param userName the username to look for
     * @return the user, or null if not found
     */
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

    /**
     * Adds a new user. Used for create account.
     * @param user the user to add
     * @return false if the user is null or the username is taken
     */
    public boolean addUser(User user) {
        if (user == null || getUserByUserName(user.getUserName()) != null) {
            return false;
        }
        users.add(user);
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
    public void notifyResponders(ReliefRquest request){
        for (User user : users){
            if(user.isResponder()){
                System.out.println("Notifying " + user.getUserName() + ": new request " + request.getID());
            }
        }
    }
    public void save(){
        DataWriter.saveUsers(users);
    }
}
