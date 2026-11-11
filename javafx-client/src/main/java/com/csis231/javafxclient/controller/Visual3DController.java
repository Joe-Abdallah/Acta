package com.csis231.javafxclient.controller;

import com.csis231.javafxclient.model.ClientDto;
import com.csis231.javafxclient.model.PostDto;
import com.csis231.javafxclient.service.ApiClient;
import com.csis231.javafxclient.animation.Xform;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.shape.Cylinder;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Visual3DController {

    @FXML private StackPane container3D;
    @FXML private Label statusLabel;

    private ApiClient apiClient;
    private ClientDto currentClient;
    private Group contentGroup;

    private SubScene subScene;
    private Xform world;        // root of all 3D objects
    private Xform cameraXform;
    private Xform cameraXform2;
    private Xform cameraXform3;
    private PerspectiveCamera camera;

    // Mouse control variables
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;

    private static final double CAMERA_INITIAL_DISTANCE = -450;
    private static final double CAMERA_INITIAL_X_ANGLE = 30.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 0.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 2000.0;
    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.3;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 0.5;

    // Bar chart parameters
    private static final double BAR_WIDTH = 40;
    private static final double BAR_DEPTH = 40;
    private static final double BASE_Y = 0;
    private static final double VOTE_SCALE = 10.0;
    private static final double BAR_SPACING = 60;

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
        loadTopPosts();
    }

    public void setCurrentClient(ClientDto currentClient) {
        this.currentClient = currentClient;
    }

    @FXML
    public void initialize() {
        // Build the 3D scene
        buildCamera();
        buildLight();
    }

    private void buildCamera() {
        world = new Xform();
        contentGroup = new Group();
        world.getChildren().add(contentGroup);
        cameraXform = new Xform();
        cameraXform2 = new Xform();
        cameraXform3 = new Xform();
        camera = new PerspectiveCamera(true);
        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);


        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(camera);
        cameraXform3.getChildren().add(camera);

        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);

        world.getChildren().add(cameraXform);

        subScene = new SubScene(world, 800, 600, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BLACK);
        subScene.setPickOnBounds(true);
        subScene.setCamera(camera);
        container3D.getChildren().add(subScene);

        // Add mouse controls
        addMouseControls();
    }

    private void addMouseControls() {
        subScene.setOnMousePressed(event -> {
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });

        subScene.setOnMouseDragged(event -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();
            mouseDeltaX = mousePosX - mouseOldX;
            mouseDeltaY = mousePosY - mouseOldY;

            double modifier = 1.0;
            if (event.isControlDown()) {
                modifier = CONTROL_MULTIPLIER;
            }
            if (event.isShiftDown()) {
                modifier = SHIFT_MULTIPLIER;
            }

            if (event.isPrimaryButtonDown()) {
                // Rotate camera around Y and X
                cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX * modifier * ROTATION_SPEED);
                cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY * modifier * ROTATION_SPEED);
            } else if (event.isSecondaryButtonDown()) {
                // Zoom (translate Z)
                double z = camera.getTranslateZ();
                double newZ = z + mouseDeltaX * MOUSE_SPEED * modifier;
                camera.setTranslateZ(newZ);
            } else if (event.isMiddleButtonDown()) {
                // Pan (translate X and Y)
                cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
                cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
            }
        });
    }

    private void buildLight() {
        // Add ambient light and point lights to illuminate the scene
        AmbientLight ambientLight = new AmbientLight(Color.color(0.4, 0.4, 0.4));
        PointLight light1 = new PointLight(Color.WHITE);
        light1.setTranslateX(200);
        light1.setTranslateY(-200);
        light1.setTranslateZ(300);
        PointLight light2 = new PointLight(Color.color(0.6, 0.6, 0.6));
        light2.setTranslateX(-200);
        light2.setTranslateY(200);
        light2.setTranslateZ(300);
        world.getChildren().addAll(ambientLight, light1, light2);
    }

    private void loadTopPosts() {
        if (apiClient == null) return;
        Task<List<PostDto>> task = new Task<>() {
            @Override
            protected List<PostDto> call() throws Exception {
                return apiClient.getAllPosts();
            }
        };
        task.setOnSucceeded(e -> {
            List<PostDto> all = task.getValue();
            List<PostDto> top5 = all.stream()
                    .sorted(Comparator.comparingInt(
                            (PostDto p) -> p.getVoteCount() == null ? 0 : p.getVoteCount()
                    ).reversed())
                    .limit(5)
                    .collect(Collectors.toList());
            Platform.runLater(() -> createBarChart(top5));
            statusLabel.setText("Top 5 posts loaded by vote count.");
        });
        task.setOnFailed(e -> statusLabel.setText("Error loading posts: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    private void createBarChart(List<PostDto> topPosts) {
        // Remove any previous bars
        contentGroup.getChildren().clear();

        if (topPosts.isEmpty()) {
            statusLabel.setText("No posts found.");
            return;
        }

        double startX = - (topPosts.size() - 1) * BAR_SPACING / 2.0;

        for (int i = 0; i < topPosts.size(); i++) {
            PostDto post = topPosts.get(i);
            int votes = post.getVoteCount() != null ? post.getVoteCount() : 0;
            double barHeight = Math.max(10, votes * VOTE_SCALE); // minimum height 10

            Box bar = new Box(BAR_WIDTH, barHeight, BAR_DEPTH);
            double hue = 200 - (i * 30.0); // blue → teal → green gradient by rank
            Color barColor = Color.hsb(hue, 0.85, 0.9);
            bar.setMaterial(new PhongMaterial(barColor));
            bar.setTranslateX(startX + i * BAR_SPACING);
            bar.setTranslateY(-barHeight / 2.0); // base is Y=0
            bar.setTranslateZ(0);

            // label above the bar
            Text label = new Text(votes + "");
            label.setFont(Font.font("Arial", 14));
            label.setFill(Color.WHITE);
            label.setTranslateX(bar.getTranslateX());
            label.setTranslateY(-barHeight - 18);
            label.setTranslateZ(-1);


            // sphere on top of bar to represent the vote count
            Sphere topSphere = new Sphere(6);
            topSphere.setMaterial(new PhongMaterial(Color.YELLOW));
            topSphere.setTranslateX(bar.getTranslateX());
            topSphere.setTranslateY(-barHeight);
            topSphere.setTranslateZ(-5);

            // text label
            Text3D text3d = new Text3D(String.valueOf(votes), 12, bar.getTranslateX(), -barHeight - 25, -5);
            world.getChildren().addAll(bar, topSphere, text3d);
        }

        // ground
        Box floor = new Box(500, 1, 500);
        floor.setMaterial(new PhongMaterial(Color.DARKGRAY));
        floor.setTranslateY(1);
        floor.setOpacity(0.3);
        world.getChildren().add(floor);
    }

    // Helper class to create 3D text
    private static class Text3D extends Text {
        public Text3D(String text, int fontSize, double x, double y, double z) {
            super(text);
            setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
            setFill(Color.YELLOW);
            setTranslateX(x);
            setTranslateY(y);
            setTranslateZ(z);
        }
    }

    @FXML
    private void handleRefresh() {
        loadTopPosts();
    }
}