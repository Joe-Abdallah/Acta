package com.csis231.javafxclient.controller;

import com.csis231.javafxclient.model.ClientDto;
import com.csis231.javafxclient.service.ApiClient;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;

public class ProfileController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField createdAtField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button updateButton;
    @FXML
    private Button clearButton;
    @FXML
    private Label statusLabel;

    private ApiClient apiClient;
    private ClientDto currentClient;

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void setCurrentClient(ClientDto currentClient) {
        this.currentClient = currentClient;
        loadProfileData();
    }

    @FXML
    public void initialize() {
        //data will be loaded when currentClient is set.
    }

    private void loadProfileData() {
        if (currentClient == null) return;
        usernameField.setText(currentClient.getUsername());
        emailField.setText(currentClient.getEmail());
        if (currentClient.getCreatedAt() != null) {
            createdAtField.setText(currentClient.getCreatedAt().toString());
        }
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    @FXML
    private void handleUpdate() {
        if (currentClient == null) {
            statusLabel.setText("No user logged in.");
            return;
        }
        String newUsername = usernameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newUsername.isEmpty() || newEmail.isEmpty()) {
            statusLabel.setText("Username and email are required.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        ClientDto updatedClient = new ClientDto();
        updatedClient.setUsername(newUsername);
        updatedClient.setEmail(newEmail);
        if (!newPassword.isEmpty()) {
            updatedClient.setPassword(newPassword);
        }

        Task<ClientDto> task = new Task<>() {
            @Override
            protected ClientDto call() throws Exception {
                return apiClient.updateClient(currentClient.getId(), updatedClient);
            }
        };
        task.setOnSucceeded(e -> {
            ClientDto result = task.getValue();
            // Update stored currentClient
            currentClient.setUsername(result.getUsername());
            currentClient.setEmail(result.getEmail());
            loadProfileData(); // refresh fields (clear password fields)
            statusLabel.setText("Profile updated successfully.");
        });
        task.setOnFailed(e -> statusLabel.setText("Update failed: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML
    private void handleClear() {
        loadProfileData(); // revert to original values
    }
}