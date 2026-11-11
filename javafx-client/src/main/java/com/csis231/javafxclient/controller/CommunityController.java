package com.csis231.javafxclient.controller;

import com.csis231.javafxclient.model.ClientDto;
import com.csis231.javafxclient.model.CommunityDto;
import com.csis231.javafxclient.service.ApiClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class CommunityController {

    @FXML private ListView<CommunityDto> communityList;
    @FXML private TextField nameField;
    @FXML private TextArea descField;
    @FXML private Button createCommunityBtn;
    @FXML private Label statusLabel;
    @FXML private VBox communityDetailPane;
    @FXML private Label communityDetailName;
    @FXML private TextArea communityDetailDesc;

    private ApiClient apiClient;
    private ClientDto currentClient;
    private ObservableList<CommunityDto> communityObservableList = FXCollections.observableArrayList();

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
        loadCommunities();
    }

    public void setCurrentClient(ClientDto currentClient) {
        this.currentClient = currentClient;
    }

    @FXML
    public void initialize() {
        communityList.setItems(communityObservableList);
        communityList.setCellFactory(lv -> new ListCell<CommunityDto>() {
            @Override
            protected void updateItem(CommunityDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getName() == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        // Show description in the detail pane when a community is selected
        communityList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) showCommunityDetail(selected);
        });

        if (communityDetailPane != null) communityDetailPane.setVisible(false);
    }

    // -------------------------------------------------------
    // Detail panel
    // -------------------------------------------------------

    private void showCommunityDetail(CommunityDto community) {
        if (communityDetailPane == null) return;
        communityDetailPane.setVisible(true);
        communityDetailPane.setManaged(true);
        communityDetailName.setText(community.getName());
        String desc = community.getDescription();
        communityDetailDesc.setText(desc == null || desc.isBlank() ? "(No description provided)" : desc);
    }

    // -------------------------------------------------------
    // Data loading
    // -------------------------------------------------------

    private void loadCommunities() {
        if (apiClient == null) return;
        Task<List<CommunityDto>> task = new Task<>() {
            @Override
            protected List<CommunityDto> call() throws Exception {
                return apiClient.getAllCommunities();
            }
        };
        task.setOnSucceeded(e -> {
            CommunityDto previousSelection = communityList.getSelectionModel().getSelectedItem();
            communityObservableList.setAll(task.getValue());
            statusLabel.setText("Communities loaded: " + communityObservableList.size());
            // Restore selection after refresh
            if (previousSelection != null) {
                communityObservableList.stream()
                        .filter(c -> c.getId().equals(previousSelection.getId()))
                        .findFirst()
                        .ifPresent(updated -> {
                            communityList.getSelectionModel().select(updated);
                            showCommunityDetail(updated);
                        });
            }
        });
        task.setOnFailed(e -> statusLabel.setText("Error loading communities: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    // -------------------------------------------------------
    // Create community
    // -------------------------------------------------------

    @FXML
    private void handleCreateCommunity() {
        String name = nameField.getText().trim();
        String description = descField.getText().trim();
        if (name.isEmpty()) {
            statusLabel.setText("Community name is required.");
            return;
        }
        if (currentClient == null) {
            statusLabel.setText("You must be logged in to create a community.");
            return;
        }

        CommunityDto newCommunity = new CommunityDto();
        newCommunity.setName(name);
        newCommunity.setDescription(description);
        newCommunity.setCreatorId(currentClient.getId());

        Task<CommunityDto> task = new Task<>() {
            @Override
            protected CommunityDto call() throws Exception {
                return apiClient.createCommunity(newCommunity);
            }
        };
        task.setOnSucceeded(e -> {
            CommunityDto created = task.getValue();
            statusLabel.setText("Community '" + created.getName() + "' created.");
            nameField.clear();
            descField.clear();
            loadCommunities();
        });
        task.setOnFailed(e -> statusLabel.setText("Error creating community: " + task.getException().getMessage()));
        new Thread(task).start();
    }
}