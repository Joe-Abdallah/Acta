package com.csis231.javafxclient.controller;

import com.csis231.javafxclient.model.AuthResponseDto;
import com.csis231.javafxclient.model.ClientDto;
import com.csis231.javafxclient.model.LoginDto;
import com.csis231.javafxclient.service.ApiClient;
import com.csis231.javafxclient.service.ApiSession;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Main controller for the application.
 * Handles authentication and switches between the authentication pane and the main tab pane.
 */
public class MainController {

    @FXML
    private VBox authPane;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab communitiesTab;
    @FXML
    private Tab postsTab;
    @FXML
    private Tab commentsTab;
    @FXML
    private Tab profileTab;
    @FXML
    private Tab analyticsTab;

    @FXML
    private TextField loginUsernameField;
    @FXML
    private PasswordField loginPasswordField;

    @FXML
    private TextField registerUsernameField;
    @FXML
    private TextField registerEmailField;
    @FXML
    private PasswordField registerPasswordField;
    @FXML
    private PasswordField registerConfirmPasswordField;

    @FXML
    private Label authStatusLabel;
    @FXML
    private Label userInfoLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;

    private final ApiClient apiClient = new ApiClient();
    private boolean tabsLoaded = false;

    private CommunityController communityController;
    private PostController postController;
    private CommentController commentController;
    private ProfileController profileController;
    private Visual3DController visual3DController;

    private ClientDto currentClient;

    @FXML
    public void initialize() {
        showAuthenticationState();
    }

    /**
     * Loads the content of each tab from its FXML file.
     * Called only once after successful login.
     */
    private void loadMainTabs() {
        if (tabsLoaded) {
            return;
        }

        try {
            FXMLLoader communityLoader = new FXMLLoader(getClass().getResource("/fxml/Community.fxml"));
            communitiesTab.setContent(communityLoader.load());
            communityController = communityLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            authStatusLabel.setText("Failed to load Communities tab.");
        }

        try {
            FXMLLoader postLoader = new FXMLLoader(getClass().getResource("/fxml/Post.fxml"));
            postsTab.setContent(postLoader.load());
            postController = postLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            authStatusLabel.setText("Failed to load Posts tab.");
        }

        try {
            FXMLLoader commentLoader = new FXMLLoader(getClass().getResource("/fxml/Comment.fxml"));
            commentsTab.setContent(commentLoader.load());
            commentController = commentLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            authStatusLabel.setText("Failed to load Comments tab.");
        }

        try {
            FXMLLoader profileLoader = new FXMLLoader(getClass().getResource("/fxml/Profile.fxml"));
            profileTab.setContent(profileLoader.load());
            profileController = profileLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            authStatusLabel.setText("Failed to load Profile tab.");
        }

        try {
            FXMLLoader visualLoader = new FXMLLoader(getClass().getResource("/fxml/Visual3D.fxml"));
            analyticsTab.setContent(visualLoader.load());
            visual3DController = visualLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            authStatusLabel.setText("Failed to load 3D Analytics tab.");
        }

        tabsLoaded = true;
    }

    @FXML
    private void handleLogin() {
        String username = valueOf(loginUsernameField);
        String password = loginPasswordField.getText();

        if (username.isEmpty() || password == null || password.isBlank()) {
            authStatusLabel.setText("Enter your username/email and password.");
            return;
        }

        runAuthTask(new Task<>() {
            @Override
            protected AuthResponseDto call() throws Exception {
                return apiClient.authenticate(new LoginDto(username, password));
            }
        }, auth -> {
            currentClient = auth.getClient();
            authStatusLabel.setText("Welcome back, " + currentClient.getUsername() + ".");
            loginPasswordField.clear();
            showMainApp(currentClient);
        });
    }

    @FXML
    private void handleRegister() {
        String username = valueOf(registerUsernameField);
        String email = valueOf(registerEmailField);
        String password = registerPasswordField.getText();
        String confirmPassword = registerConfirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password == null || password.isBlank()) {
            authStatusLabel.setText("Complete username, email, and password to create an account.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            authStatusLabel.setText("Passwords do not match.");
            return;
        }

        ClientDto newClient = new ClientDto();
        newClient.setUsername(username);
        newClient.setEmail(email);
        newClient.setPassword(password);

        runAuthTask(new Task<>() {
            @Override
            protected AuthResponseDto  call() throws Exception {
                return apiClient.registerClient(newClient);
            }
        }, auth -> {
            ClientDto client = auth.getClient();
            authStatusLabel.setText("Registration successful. You can now sign in as " + client.getUsername() + ".");
            loginUsernameField.setText(client.getUsername());
            loginPasswordField.clear();
            registerUsernameField.clear();
            registerEmailField.clear();
            registerPasswordField.clear();
            registerConfirmPasswordField.clear();
        });
    }

    @FXML
    private void handleLogout() {
        ApiSession.clear();
        currentClient = null;
        showAuthenticationState();
        authStatusLabel.setText("You have been logged out.");
        loginPasswordField.clear();
    }

    /**
     * Switches the UI to the main tab pane and passes the logged‑in client to all child controllers.
     *
     * @param client the authenticated client
     */
    private void showMainApp(ClientDto client) {
        loadMainTabs();
        authPane.setManaged(false);
        authPane.setVisible(false);
        mainTabPane.setManaged(true);
        mainTabPane.setVisible(true);
        userInfoLabel.setText("Signed in as " + client.getUsername() + " (" + client.getEmail() + ")");
        logoutButton.setVisible(true);
        logoutButton.setManaged(true);

        if (communityController != null) {
            communityController.setApiClient(apiClient);
            communityController.setCurrentClient(client);
        }
        if (postController != null) {
            postController.setApiClient(apiClient);
            postController.setCurrentClient(client);
        }
        if (commentController != null) {
            commentController.setApiClient(apiClient);
            commentController.setCurrentClient(client);
        }
        if (profileController != null) {
            profileController.setApiClient(apiClient);
            profileController.setCurrentClient(client);
        }
        if (visual3DController != null) {
            visual3DController.setApiClient(apiClient);
            visual3DController.setCurrentClient(client);
        }
    }

    /**
     * Shows the authentication pane (login/register) and hides the main tabs.
     */
    private void showAuthenticationState() {
        authPane.setManaged(true);
        authPane.setVisible(true);
        mainTabPane.setManaged(false);
        mainTabPane.setVisible(false);
        userInfoLabel.setText("Not signed in");
        logoutButton.setVisible(false);
        logoutButton.setManaged(false);
    }

    /**
     * Runs a background task for authentication or registration.
     *
     * @param task      the Task to run
     * @param onSuccess consumer that receives the successful result
     */
    private void runAuthTask(Task<AuthResponseDto> task, java.util.function.Consumer<AuthResponseDto> onSuccess) {
        setAuthLoading(true);
        authStatusLabel.setText("");
        task.setOnSucceeded(event -> {
            setAuthLoading(false);
            onSuccess.accept(task.getValue());
        });
        task.setOnFailed(event -> {
            setAuthLoading(false);
            Throwable error = task.getException();
            authStatusLabel.setText(error == null ? "Authentication request failed." : error.getMessage());
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void setAuthLoading(boolean loading) {
        loginButton.setDisable(loading);
        registerButton.setDisable(loading);
        logoutButton.setDisable(loading);
    }

    private String valueOf(TextField textField) {
        return textField.getText() == null ? "" : textField.getText().trim();
    }
}