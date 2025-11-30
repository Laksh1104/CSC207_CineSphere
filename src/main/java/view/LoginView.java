package view;

import interface_adapter.login.LoginController;
import interface_adapter.login.LoginViewModel;
import interface_adapter.login.LoginState;

import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupViewModel;
import interface_adapter.signup.SignupState;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

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
    private JLabel statusLabel; // shows logged-in info

    // NEW overload for cases where you only want login (e.g., from Logout)
    public LoginView(LoginController loginController, LoginViewModel loginViewModel) {
        this(loginController, loginViewModel, null, null);
    }

    public LoginView(LoginController loginController,
                     LoginViewModel loginViewModel,
                     SignupController signupController,
                     SignupViewModel signupViewModel) {

        this.loginController = loginController;
        this.loginViewModel = loginViewModel;
        this.signupController = signupController;
        this.signupViewModel = signupViewModel;

        setTitle("log in");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(340, 200);
        setLocationRelativeTo(null);
        setResizable(false);

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

        getRootPane().setDefaultButton(loginButton);
        setContentPane(root);
    }

    private void bindListeners() {
        // login button
        loginButton.addActionListener(e -> handleLogin());

        // sign up button (reuse fields)
        signupButton.addActionListener(e -> {
            if (signupController == null) {
                JOptionPane.showMessageDialog(this, "Sign up is not available from this screen.");
                return;
            }
            handleSignup();
        });

        // cancel button: close app
        cancelButton.addActionListener(e -> dispose());

        // disable signup if not provided
        if (signupController == null || signupViewModel == null) {
            signupButton.setEnabled(false);
        }

        // Listen for login state changes
        loginViewModel.addPropertyChangeListener(evt -> {
            if ("state".equals(evt.getPropertyName())) {
                LoginState state = (LoginState) evt.getNewValue();

                String message = state.getErrorMessage();
                if (message != null && !message.isEmpty()) {
                    errorLabel.setText(message);
                } else {
                    errorLabel.setText(" ");
                }

                if (state.isLoggedIn()) {
                    statusLabel.setText("Logged in as: " + state.getUsername());
                }
            }
        });

        // Listen for signup state changes (show dialogs here, not in presenter)
        if (signupViewModel != null) {
            signupViewModel.addPropertyChangeListener(evt -> {
                if ("state".equals(evt.getPropertyName())) {
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
                }
            });
        }
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
