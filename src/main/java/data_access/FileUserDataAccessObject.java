package data_access;

import entity.User;
import entity.UserFactory;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * File-based DAO for user data.
 * Stores users in a text file: username,password on each line.
 */
public class FileUserDataAccessObject implements
        SignupUserDataAccessInterface,
        LoginUserDataAccessInterface,
        LogoutUserDataAccessInterface {

    private final Map<String, User> users = new HashMap<>();
    private final String filePath;
    private final UserFactory userFactory;
    private String currentUsername;

    public FileUserDataAccessObject(String filePath, UserFactory userFactory) {
        this.filePath = filePath;
        this.userFactory = userFactory;
        loadFromFile();
    }

    // ---------- File loading & saving ----------

    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // username,password
                String[] parts = line.split(",", 2);
                if (parts.length != 2) continue;

                String username = parts[0];
                String password = parts[1];

                User user = userFactory.create(username, password);
                users.put(username, user);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read users file: " + filePath, e);
        }
    }

    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (User user : users.values()) {
                writer.println(user.getName() + "," + user.getPassword());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write users file: " + filePath, e);
        }
    }


    @Override
    public boolean existsByName(String username) {
        return users.containsKey(username);
    }

    @Override
    public void save(User user) {
        users.put(user.getName(), user);
        // after every change
        saveToFile();
    }

    @Override
    public User get(String username) {
        return users.get(username);
    }

    @Override
    public void setCurrentUsername(String name) {
        currentUsername = name;
    }

    @Override
    public String getCurrentUsername() {
        return currentUsername;
    }
}
