package view;

import interface_adapter.login.LoginController;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;

import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupState;
import interface_adapter.signup.SignupViewModel;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel {

    private final LoginController loginController;
    private final LoginViewModel loginViewModel;

    private final SignupController signupController;
    private final SignupViewModel signupViewModel;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JButton cancelButton;

    private JLabel errorLabel;
    private JLabel statusLabel;

    public LoginView(LoginController loginController,
                     LoginViewModel loginViewModel,
                     SignupController signupController,
                     SignupViewModel signupViewModel) {

        this.loginController = loginController;
        this.loginViewModel = loginViewModel;
        this.signupController = signupController;
        this.signupViewModel = signupViewModel;

        buildUI();
        bindListeners();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        formPanel.add(usernameField, gbc);

        // Row 1: Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        loginButton = new JButton("log in");
        signupButton = new JButton("sign up");
        cancelButton = new JButton("cancel");

        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        buttonPanel.add(cancelButton);

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.CENTER);

        JPanel messagePanel = new JPanel(new GridLayout(2, 1));
        messagePanel.add(errorLabel);
        messagePanel.add(statusLabel);

        southPanel.add(messagePanel, BorderLayout.SOUTH);

        root.add(formPanel, BorderLayout.CENTER);
        root.add(southPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(root, BorderLayout.CENTER);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        JRootPane rootPane = SwingUtilities.getRootPane(this);
        if (rootPane != null) {
            rootPane.setDefaultButton(loginButton);
        }
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        errorLabel.setText(" ");
        statusLabel.setText(" ");
    }


    private void bindListeners() {
        loginButton.addActionListener(e -> handleLogin());
        signupButton.addActionListener(e -> handleSignup());

        cancelButton.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) w.dispose();
            else System.exit(0);
        });

        // Listen for login state changes
        loginViewModel.addPropertyChangeListener(evt -> {
            if (!"state".equals(evt.getPropertyName())) return;

            LoginState state = (LoginState) evt.getNewValue();

            String message = state.getErrorMessage();
            if (message != null && !message.isEmpty()) {
                errorLabel.setText(message);
            } else {
                errorLabel.setText(" ");
            }

            if (state.isLoggedIn()) {
                statusLabel.setText("Logged in as: " + state.getUsername());
            } else {
                statusLabel.setText(" ");
            }
        });

        // Listen for signup state changes (dialogs OK here)
        signupViewModel.addPropertyChangeListener(evt -> {
            if (!"state".equals(evt.getPropertyName())) return;

            SignupState state = (SignupState) evt.getNewValue();

            if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        state.getErrorMessage(),
                        "Sign up failed",
                        JOptionPane.ERROR_MESSAGE
                );
            } else if (state.isSignupSuccess()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Account created for " + state.getUsername(),
                        "Sign up successful",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        loginController.execute(username, password);
    }

    private void handleSignup() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        signupController.execute(username, password);
    }
}
