package com.csis231.javafxclient.controller;

import com.csis231.javafxclient.model.*;
import com.csis231.javafxclient.service.ApiClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.List;

public class CommentController {

    @FXML private ComboBox<PostDto> postCombo;
    @FXML private ListView<CommentDisplayItem> commentsList;
    @FXML private Label replyingToLabel;
    @FXML private Button cancelReplyBtn;
    @FXML private TextArea commentContentField;
    @FXML private Label statusLabel;

    private ApiClient apiClient;
    private ClientDto currentClient;
    private final ObservableList<PostDto> postObservableList = FXCollections.observableArrayList();
    private final ObservableList<CommentDisplayItem> commentObservableList = FXCollections.observableArrayList();
    private Long replyingToCommentId = null;

    // -------------------------------------------------------
    // Inner class: comment + nesting depth
    // -------------------------------------------------------

    public static class CommentDisplayItem {
        public final CommentDto comment;
        public final int depth;

        public CommentDisplayItem(CommentDto comment, int depth) {
            this.comment = comment;
            this.depth = depth;
        }
    }

    // -------------------------------------------------------
    // Setup
    // -------------------------------------------------------

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
        loadPosts();
    }

    public void setCurrentClient(ClientDto currentClient) {
        this.currentClient = currentClient;
    }

    @FXML
    public void initialize() {
        postCombo.setItems(postObservableList);
        commentsList.setItems(commentObservableList);

        postCombo.setCellFactory(lv -> new ListCell<PostDto>() {
            @Override
            protected void updateItem(PostDto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle());
            }
        });
        postCombo.setButtonCell(new ListCell<PostDto>() {
            @Override
            protected void updateItem(PostDto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle());
            }
        });

        commentsList.setCellFactory(lv -> new ListCell<CommentDisplayItem>() {
            private final Label textLabel = new Label();
            private final Label voteLabel = new Label();
            private final Button upvoteBtn = new Button("\u25b2");
            private final Button downvoteBtn = new Button("\u25bc");
            private final Button replyBtn = new Button("Reply");
            private final HBox hbox = new HBox(6);

            {
                HBox.setHgrow(textLabel, Priority.ALWAYS);
                textLabel.setMaxWidth(Double.MAX_VALUE);
                textLabel.setPrefWidth(0);
                textLabel.setWrapText(true);
                hbox.setPadding(new Insets(4, 4, 4, 4));
                hbox.getChildren().addAll(textLabel, upvoteBtn, voteLabel, downvoteBtn, replyBtn);

                upvoteBtn.setOnAction(e -> {
                    CommentDisplayItem item = getItem();
                    if (item != null) handleVote(item.comment, VoteType.UPVOTE);
                });
                downvoteBtn.setOnAction(e -> {
                    CommentDisplayItem item = getItem();
                    if (item != null) handleVote(item.comment, VoteType.DOWNVOTE);
                });
                replyBtn.setOnAction(e -> {
                    CommentDisplayItem item = getItem();
                    if (item != null) setReplyingTo(item.comment);
                });
            }

            @Override
            protected void updateItem(CommentDisplayItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    String prefix = item.depth > 0 ? "  ".repeat(item.depth * 2) + "L " : "";
                    textLabel.setText(prefix + item.comment.getContent());
                    voteLabel.setText(String.valueOf(
                            item.comment.getVoteCount() == null ? 0 : item.comment.getVoteCount()));
                    hbox.setPadding(new Insets(4, 4, 4, 4 + item.depth * 16));
                    setGraphic(hbox);
                    setText(null);
                }
            }
        });
    }

    // -------------------------------------------------------
    // Data loading
    // -------------------------------------------------------

    private void loadPosts() {
        if (apiClient == null) return;
        Task<List<PostDto>> task = new Task<>() {
            @Override
            protected List<PostDto> call() throws Exception {
                return apiClient.getAllPosts();
            }
        };
        task.setOnSucceeded(e -> {
            postObservableList.setAll(task.getValue());
            statusLabel.setText("Posts loaded: " + postObservableList.size());
        });
        task.setOnFailed(e -> statusLabel.setText("Error loading posts: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML
    private void handlePostSelected() {
        PostDto selected = postCombo.getSelectionModel().getSelectedItem();
        if (selected != null) loadCommentsForPost(selected.getId());
    }

    @FXML
    private void handleRefreshComments() {
        loadPosts();
        PostDto selected = postCombo.getSelectionModel().getSelectedItem();
        if (selected != null) loadCommentsForPost(selected.getId());
    }

    private void loadCommentsForPost(Long postId) {
        if (apiClient == null) return;
        Task<List<CommentDisplayItem>> task = new Task<>() {
            @Override
            protected List<CommentDisplayItem> call() throws Exception {
                List<CommentDto> roots = apiClient.getCommentsByPost(postId);
                return buildCommentTree(roots);
            }
        };
        task.setOnSucceeded(e -> {
            commentObservableList.setAll(task.getValue());
            statusLabel.setText("Comments loaded: " + commentObservableList.size());
        });
        task.setOnFailed(e -> statusLabel.setText("Error loading comments: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    // -------------------------------------------------------
    // Tree builder - backend nests replies inside getReplies()
    // -------------------------------------------------------

    private List<CommentDisplayItem> buildCommentTree(List<CommentDto> comments) {
        List<CommentDisplayItem> result = new ArrayList<>();
        for (CommentDto root : comments) {
            addCommentRecursive(result, root, 0);
        }
        return result;
    }

    private void addCommentRecursive(List<CommentDisplayItem> result, CommentDto comment, int depth) {
        result.add(new CommentDisplayItem(comment, depth));
        if (comment.getReplies() != null) {
            for (CommentDto reply : comment.getReplies()) {
                addCommentRecursive(result, reply, depth + 1);
            }
        }
    }

    // -------------------------------------------------------
    // Voting
    // -------------------------------------------------------

    private void handleVote(CommentDto comment, VoteType voteType) {
        if (currentClient == null) {
            statusLabel.setText("You must be logged in to vote.");
            return;
        }
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                CommentVoteDto existing = apiClient.getCommentVote(comment.getId(), currentClient.getId());
                if (existing == null) {
                    apiClient.castCommentVote(comment.getId(), currentClient.getId(), voteType);
                } else if (existing.getVoteType() == voteType) {
                    apiClient.deleteCommentVote(comment.getId(), currentClient.getId());
                } else {
                    apiClient.castCommentVote(comment.getId(), currentClient.getId(), voteType);
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            statusLabel.setText("Vote recorded.");
            PostDto selected = postCombo.getSelectionModel().getSelectedItem();
            if (selected != null) loadCommentsForPost(selected.getId());
        });
        task.setOnFailed(e -> statusLabel.setText("Vote failed: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    // -------------------------------------------------------
    // Reply management
    // -------------------------------------------------------

    private void setReplyingTo(CommentDto parent) {
        replyingToCommentId = parent.getId();
        String preview = parent.getContent().length() > 60
                ? parent.getContent().substring(0, 60) + "..."
                : parent.getContent();
        replyingToLabel.setText("Replying to: \"" + preview + "\"");
        cancelReplyBtn.setVisible(true);
        cancelReplyBtn.setManaged(true);
    }

    @FXML
    private void handleCancelReply() {
        replyingToCommentId = null;
        replyingToLabel.setText("");
        cancelReplyBtn.setVisible(false);
        cancelReplyBtn.setManaged(false);
    }

    // -------------------------------------------------------
    // Create comment
    // -------------------------------------------------------

    @FXML
    private void handleCreateComment() {
        PostDto selectedPost = postCombo.getSelectionModel().getSelectedItem();
        if (selectedPost == null) {
            statusLabel.setText("Select a post first.");
            return;
        }
        String content = commentContentField.getText().trim();
        if (content.isEmpty()) {
            statusLabel.setText("Comment content cannot be empty.");
            return;
        }
        if (currentClient == null) {
            statusLabel.setText("You must be logged in.");
            return;
        }

        CommentDto newComment = new CommentDto();
        newComment.setContent(content);
        newComment.setAuthorId(currentClient.getId());
        newComment.setPostId(selectedPost.getId());
        if (replyingToCommentId != null) {
            newComment.setParentCommentId(replyingToCommentId);
        }

        Task<CommentDto> task = new Task<>() {
            @Override
            protected CommentDto call() throws Exception {
                return apiClient.createComment(newComment);
            }
        };
        task.setOnSucceeded(e -> {
            statusLabel.setText("Comment posted.");
            commentContentField.clear();
            handleCancelReply();
            loadCommentsForPost(selectedPost.getId());
        });
        task.setOnFailed(e -> statusLabel.setText("Error posting comment: " + task.getException().getMessage()));
        new Thread(task).start();
    }
}