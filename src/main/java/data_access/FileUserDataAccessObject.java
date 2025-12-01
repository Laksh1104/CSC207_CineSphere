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
 * File-based implementation of the user data access object.
 * Stores users in CSV format: username,password
 */
public class FileUserDataAccessObject
        implements LoginUserDataAccessInterface,
        SignupUserDataAccessInterface,
        LogoutUserDataAccessInterface {

    private final File file;
    private final UserFactory userFactory;
    private final Map<String, User> users = new HashMap<>();

    private String currentUsername;

    public FileUserDataAccessObject(String filePath, UserFactory userFactory) {
        this.file = new File(filePath);
        this.userFactory = userFactory;

        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create user file: " + filePath, e);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String row;
            while ((row = reader.readLine()) != null) {
                if (row.trim().isEmpty()) continue;

                String[] parts = row.split(",", 2);
                if (parts.length < 2) continue;

                String username = parts[0];
                String password = parts[1];

                User user = userFactory.create(username, password);
                users.put(username, user);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to read users from file: " + filePath, e);
        }
    }

    @Override
    public boolean existsByName(String username) {
        return users.containsKey(username);
    }

    @Override
    public void save(User user) {
        users.put(user.getName(), user);
        writeToFile();
    }

    @Override
    public User get(String username) {
        return users.get(username);
    }

    @Override
    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    @Override
    public String getCurrentUsername() {
        return currentUsername;
    }

    @Override
    public void logout() {
        this.currentUsername = null;
    }

    private void writeToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (User user : users.values()) {
                writer.write(user.getName() + "," + user.getPassword());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to write users to file: " + file.getPath(), e);
        }
    }
}
