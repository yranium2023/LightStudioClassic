package org.example.Curve;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class DraggableCurve extends Application {

    private static final int SCENE_WIDTH = 600;
    private static final int SCENE_HEIGHT = 400;
    private static final int POINT_RADIUS = 5;

    private List<Circle> points;
    private Path path;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        // Create points on the curve
        points = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Circle point = createPoint(50 + i * 50, SCENE_HEIGHT / 2);
            points.add(point);
            root.getChildren().add(point);

            final int index = i;
            point.setOnMouseDragged(event -> {
                points.get(index).setCenterX(event.getX());
                points.get(index).setCenterY(event.getY());
                updatePath();
            });
        }

        // Create initial curve
        path = new Path();
        path.setStroke(Color.BLACK);
        updatePath();

        root.getChildren().add(path);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Draggable Curve");
        primaryStage.show();
    }

    private Circle createPoint(double x, double y) {
        Circle point = new Circle(x, y, POINT_RADIUS, Color.RED);
        point.setStroke(Color.BLACK);
        point.setStrokeWidth(1);
        return point;
    }

    private void updatePath() {
        path.getElements().clear();
        path.getElements().add(new MoveTo(points.get(0).getCenterX(), points.get(0).getCenterY()));
        for (int i = 1; i < points.size() - 1; i++) {
            Circle current = points.get(i);
            Circle next = points.get(i + 1);
            path.getElements().add(new QuadCurveTo(current.getCenterX(), current.getCenterY(),
                    (current.getCenterX() + next.getCenterX()) / 2, (current.getCenterY() + next.getCenterY()) / 2));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
