package view;

import interface_adapter.login.LoginController;
import interface_adapter.login.LoginViewModel;
import interface_adapter.login.LoginState;

import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupViewModel;

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
        setSize(340, 180);
        setLocationRelativeTo(null);
        setResizable(false);

        buildUI();
        bindListeners();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- form panel (Username / Password) ---
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

        // --- error label under buttons ---
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        southPanel.add(errorLabel, BorderLayout.SOUTH);

        root.add(formPanel, BorderLayout.CENTER);
        root.add(southPanel, BorderLayout.SOUTH);

        // enter triggers "log in"
        getRootPane().setDefaultButton(loginButton);

        setContentPane(root);
    }

    private void bindListeners() {
        // login button
        loginButton.addActionListener(e -> handleLogin());

        // sign up button (reuses same username/password fields)
        signupButton.addActionListener(e -> handleSignup());

        // cancel button: close app
        cancelButton.addActionListener(e -> dispose());

        // listen for login errors and show them in the red label
        loginViewModel.addPropertyChangeListener(evt -> {
            if ("state".equals(evt.getPropertyName())) {
                LoginState state = (LoginState) evt.getNewValue();
                String message = state.getErrorMessage();
                if (message != null && !message.isEmpty()) {
                    errorLabel.setText(message);
                } else {
                    errorLabel.setText(" ");
                }
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
