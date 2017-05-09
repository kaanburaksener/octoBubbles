package com.kaanburaksener.octoUML.src.controller;

import com.kaanburaksener.octoUML.src.model.Sketch;
import com.kaanburaksener.octoUML.src.util.Constants;
import com.kaanburaksener.octoUML.src.view.edges.SimpleEdgeView;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.ArrayList;

/**
 * Used by MainController for zooming and panning the view.
 */
public class GraphController {

    private double initMoveX, initMoveY;

    private Pane aDrawPane;
    private AbstractDiagramController diagramController;
    private ScrollPane aScrollPane;

    private ArrayList<Line> grid = new ArrayList<>();
    private boolean isGridVisible = true;

    GraphController(Pane pDrawPane, AbstractDiagramController pDiagramController, ScrollPane pScrollPane) {
        aDrawPane = pDrawPane;
        diagramController = pDiagramController;
        aScrollPane = pScrollPane;

        aScrollPane.setHvalue(aScrollPane.getHmin() + (aScrollPane.getHmax() - aScrollPane.getHmin()) / 2);
        aScrollPane.setVvalue(aScrollPane.getVmin() + (aScrollPane.getVmax() - aScrollPane.getVmin()) / 2);
        aScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        aScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    void movePaneStart(MouseEvent event) {
        initMoveX = event.getSceneX();
        initMoveY = event.getSceneY();
    }

    void movePane(MouseEvent event) {
        ScrollPane scrollPane = diagramController.getScrollPane();
        double xScroll =  (initMoveX - event.getSceneX()) / 8000; //8000 is the size of aDrawPane set in view.classDiagramView.fxml
        double yScroll = (initMoveY - event.getSceneY()) / 8000;

        scrollPane.setHvalue(scrollPane.getHvalue() + xScroll);
        scrollPane.setVvalue(scrollPane.getVvalue() + yScroll);

        initMoveX = event.getSceneX();
        initMoveY = event.getSceneY();
    }

    void movePaneFinished() {
        initMoveX = 0;
        initMoveY = 0;
    }

    //------------------------------------ MAKE VIEWPORT CENTER AFTER BUBBLES APPEARED -------------------------------

    void zoomPane(double newZoom) {
        double scale = newZoom / 100;

        final Timeline timeline = new Timeline();
        final KeyValue kv1 = new KeyValue(aDrawPane.scaleXProperty(), scale);
        final KeyValue kv2 = new KeyValue(aDrawPane.scaleYProperty(), scale);
        final KeyFrame kf = new KeyFrame(Duration.millis(100), kv1, kv2);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    void ensureVisible(double x, double y) {
        ScrollPane scrollPane = diagramController.getScrollPane();

        double xScroll = (x - scrollPane.getWidth() / 2) / (8000 - scrollPane.getWidth());
        double yScroll = (y - scrollPane.getHeight() / 2) / (8000 - scrollPane.getHeight());

        final Timeline timeline = new Timeline();
        final KeyValue kv1 = new KeyValue(scrollPane.hvalueProperty(), xScroll);
        final KeyValue kv2 = new KeyValue(scrollPane.vvalueProperty(), yScroll);
        final KeyFrame kf = new KeyFrame(Duration.millis(100), kv1, kv2);
        timeline.getKeyFrames().add(kf);
        timeline.play();

        aScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        aScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    //------------------------------------ GRID -------------------------------

    void drawGrid() {
        grid.clear();
        for (int i = 0; i < 8000; i += Constants.GRID_DISTANCE) {
            Line line1 = new Line(i, 0, i, 8000);
            line1.setStroke(Color.LIGHTGRAY);
            Line line2 = new Line(0, i, 8000, i);
            line2.setStroke(Color.LIGHTGRAY);
            grid.add(line1);
            grid.add(line2);
            aDrawPane.getChildren().addAll(line1, line2);
        }
    }

    void gridToBack() {
        for (Line line : grid) {
            line.toBack();
        }
    }


    void setGridVisible(boolean visible) {
        for (Line line : grid) {
            line.setVisible(visible);
        }
        isGridVisible = visible;
    }

    boolean isGridVisible() {
        return isGridVisible;
    }

    public void sketchesToFront() {
        for (Sketch sketch : diagramController.getGraphModel().getAllSketches()) {
            sketch.getPath().toFront();
        }
    }

    /**
     * It send all the simple edges to back of the UML model and bubbles
     */
    public void simplesEdgesToBack() {
        for (SimpleEdgeView simpleEdgeView : diagramController.getAllSimpleEdgeViews()) {
            simpleEdgeView.toBack();
        }
    }
}
