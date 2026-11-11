package com.csis231.javafxclient.controller;

import com.csis231.javafxclient.model.*;
import com.csis231.javafxclient.service.ApiClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class PostController {

    @FXML private ComboBox<CommunityDto> communityCombo;
    @FXML private ListView<PostDto> postsList;
    @FXML private TextField titleField;
    @FXML private TextArea contentField;
    @FXML private Button createPostBtn;
    @FXML private Label statusLabel;
    @FXML private VBox postDetailPane;
    @FXML private Label postDetailTitle;
    @FXML private TextArea postDetailContent;
    @FXML private Label postDetailMeta;

    private ApiClient apiClient;
    private ClientDto currentClient;
    private ObservableList<CommunityDto> communityObservableList = FXCollections.observableArrayList();
    private ObservableList<PostDto> postObservableList = FXCollections.observableArrayList();

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
        loadCommunities();
    }

    public void setCurrentClient(ClientDto currentClient) {
        this.currentClient = currentClient;
    }

    @FXML
    public void initialize() {
        communityCombo.setItems(communityObservableList);
        postsList.setItems(postObservableList);

        communityCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(CommunityDto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        communityCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CommunityDto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        postsList.setCellFactory(lv -> new ListCell<PostDto>() {
            private final Label titleLabel = new Label();
            private final Button upvoteBtn = new Button("▲");
            private final Button downvoteBtn = new Button("▼");
            private final Label voteLabel = new Label();
            private final HBox hbox = new HBox(8);

            {
                HBox.setHgrow(titleLabel, Priority.ALWAYS);
                titleLabel.setMaxWidth(Double.MAX_VALUE);
                titleLabel.setPrefWidth(0);
                hbox.setPadding(new Insets(4));
                hbox.getChildren().addAll(titleLabel, upvoteBtn, voteLabel, downvoteBtn);

                upvoteBtn.setOnAction(e -> {
                    PostDto post = getItem();
                    if (post != null) handleVote(post, VoteType.UPVOTE);
                });
                downvoteBtn.setOnAction(e -> {
                    PostDto post = getItem();
                    if (post != null) handleVote(post, VoteType.DOWNVOTE);
                });
            }

            @Override
            protected void updateItem(PostDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    titleLabel.setText(item.getTitle() + "  💬 " + item.getCommentCount());
                    voteLabel.setText(String.valueOf(item.getVoteCount()));
                    setGraphic(hbox);
                    setText(null);
                }
            }
        });

        // Show post detail when a post is selected
        postsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) showPostDetail(selected);
        });

        // Hide the detail pane initially (if it exists in FXML)
        if (postDetailPane != null) postDetailPane.setVisible(false);
    }

    // -------------------------------------------------------
    // Detail panel
    // -------------------------------------------------------

    private void showPostDetail(PostDto post) {
        if (postDetailPane == null) return;
        postDetailPane.setVisible(true);
        postDetailPane.setManaged(true);
        postDetailTitle.setText(post.getTitle());
        postDetailContent.setText(post.getContent() == null || post.getContent().isBlank()
                ? "(No content)" : post.getContent());
        postDetailMeta.setText("Votes: " + post.getVoteCount()
                + "  |  Comments: " + post.getCommentCount());
    }

    // -------------------------------------------------------
    // Voting
    // -------------------------------------------------------

    /**
     * Toggle-vote logic:
     *  - No existing vote → cast the requested vote.
     *  - Existing vote of the same type → remove it (un-vote).
     *  - Existing vote of a different type → cast the new vote (server replaces it).
     */
    private void handleVote(PostDto post, VoteType voteType) {
        if (currentClient == null) {
            statusLabel.setText("You must be logged in to vote.");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                PostVoteDto existing = apiClient.getPostVote(post.getId(), currentClient.getId());
                if (existing == null) {
                    apiClient.castPostVote(post.getId(), currentClient.getId(), voteType);
                } else if (existing.getVoteType() == voteType) {
                    apiClient.deletePostVote(post.getId(), currentClient.getId());
                } else {
                    apiClient.castPostVote(post.getId(), currentClient.getId(), voteType);
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            statusLabel.setText("Vote recorded.");
            // Refresh the list so vote counts update
            CommunityDto selected = communityCombo.getSelectionModel().getSelectedItem();
            if (selected != null) loadPostsForCommunity(selected.getId());
        });
        task.setOnFailed(e -> statusLabel.setText("Vote failed: " + task.getException().getMessage()));
        new Thread(task).start();
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
            communityObservableList.setAll(task.getValue());
            statusLabel.setText("Communities loaded: " + communityObservableList.size());
            CommunityDto selected = communityCombo.getSelectionModel().getSelectedItem();
            if (selected != null) loadPostsForCommunity(selected.getId());
        });
        task.setOnFailed(e -> statusLabel.setText("Error loading communities: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML
    private void handleCommunitySelected() {
        CommunityDto selected = communityCombo.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        if (postDetailPane != null) postDetailPane.setVisible(false);
        loadPostsForCommunity(selected.getId());
    }

    private void loadPostsForCommunity(Long communityId) {
        if (apiClient == null) return;
        Task<List<PostDto>> task = new Task<>() {
            @Override
            protected List<PostDto> call() throws Exception {
                List<PostDto> all = apiClient.getAllPosts();
                return all.stream()
                        .filter(p -> p.getCommunityId().equals(communityId))
                        .collect(Collectors.toList());
            }
        };
        task.setOnSucceeded(e -> {
            PostDto currentlySelected = postsList.getSelectionModel().getSelectedItem();
            postObservableList.setAll(task.getValue());
            statusLabel.setText("Posts loaded: " + postObservableList.size());
            // Restore selection + refresh detail panel with updated vote count
            if (currentlySelected != null) {
                postObservableList.stream()
                        .filter(p -> p.getId().equals(currentlySelected.getId()))
                        .findFirst()
                        .ifPresent(updated -> {
                            postsList.getSelectionModel().select(updated);
                            showPostDetail(updated);
                        });
            }
        });
        task.setOnFailed(e -> statusLabel.setText("Error loading posts: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML
    private void handleRefreshPosts() {
        loadCommunities();
        CommunityDto selected = communityCombo.getSelectionModel().getSelectedItem();
        if (selected != null) loadPostsForCommunity(selected.getId());
    }

    @FXML
    private void handleCreatePost() {
        CommunityDto selectedCommunity = communityCombo.getSelectionModel().getSelectedItem();
        if (selectedCommunity == null) {
            statusLabel.setText("Select a community first.");
            return;
        }
        String title = titleField.getText().trim();
        String content = contentField.getText().trim();
        if (title.isEmpty()) {
            statusLabel.setText("Post title is required.");
            return;
        }
        if (currentClient == null) {
            statusLabel.setText("You must be logged in.");
            return;
        }

        PostDto newPost = new PostDto();
        newPost.setTitle(title);
        newPost.setContent(content);
        newPost.setAuthorId(currentClient.getId());
        newPost.setCommunityId(selectedCommunity.getId());

        Task<PostDto> task = new Task<>() {
            @Override
            protected PostDto call() throws Exception {
                return apiClient.createPost(newPost);
            }
        };
        task.setOnSucceeded(e -> {
            statusLabel.setText("Post created.");
            titleField.clear();
            contentField.clear();
            handleRefreshPosts();
        });
        task.setOnFailed(e -> statusLabel.setText("Error creating post: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML
    private void handleSummarize() {
        PostDto selected = postsList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a post first.");
            return;
        }
        if (selected.getContent() == null || selected.getContent().isBlank()) {
            statusLabel.setText("This post has no content to summarize.");
            return;
        }

        statusLabel.setText("Thinking...");

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return apiClient.summarizePost(selected.getId());
            }
        };
        task.setOnSucceeded(e -> {
            String summary = task.getValue();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("AI Summary");
            alert.setHeaderText("Summary for: " + selected.getTitle());
            alert.setContentText(summary);
            alert.setGraphic(null);
            alert.showAndWait();
        });
        task.setOnFailed(e -> statusLabel.setText("Error: " + task.getException().getMessage()));
        new Thread(task).start();
    }
}