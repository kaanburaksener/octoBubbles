package com.kaanburaksener.octoUML.src.controller;

import com.kaanburaksener.ast.controller.NodeController;
import com.kaanburaksener.ast.model.nodes.AbstractStructure;

import com.kaanburaksener.octoUML.src.model.Graph;
import com.kaanburaksener.octoUML.src.model.edges.Edge;
import com.kaanburaksener.octoUML.src.model.nodes.AbstractNode;
import com.kaanburaksener.octoUML.src.model.nodes.ClassNode;
import com.kaanburaksener.octoUML.src.model.nodes.EnumerationNode;
import com.kaanburaksener.octoUML.src.model.nodes.Node;
import com.kaanburaksener.octoUML.src.util.commands.CompoundCommand;

import com.kaanburaksener.octoUML.src.view.BubbleView;
import com.kaanburaksener.octoUML.src.view.nodes.AbstractNodeView;
import com.kaanburaksener.octoUML.src.view.nodes.ClassNodeView;
import com.kaanburaksener.octoUML.src.view.nodes.EnumerationNodeView;
import com.kaanburaksener.octoUML.src.view.nodes.NodeView;
import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.recognition.paleo.PaleoConfig;
import edu.tamu.recognition.paleo.PaleoSketchRecognizer;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaanburaksener on 19/02/17.
 */
public class SourceCodeController {
    private Pane aDrawPane;
    private AbstractDiagramController diagramController;
    private PaleoSketchRecognizer recognizer;
    private Graph graph;
    private List<AbstractStructure> existingNodes;
    private List<AbstractNode> recognizedNodes;

    public SourceCodeController(Pane pDrawPane, AbstractDiagramController pController) {
        aDrawPane = pDrawPane;
        diagramController = pController;
        graph = diagramController.getGraphModel();

        recognizedNodes = new ArrayList<>();
        existingNodes = new ArrayList<>();

        //TODO Find a nicer solution for this:
        //This is to load the recognizer when starting app, not when starting to draw.

        recognizer = new PaleoSketchRecognizer(PaleoConfig.allOn());
        Stroke init = new Stroke();
        init.addPoint(new Point(0,1));
        recognizer.setStroke(init);
        recognizer.recognize().getBestShape();
    }

    public synchronized void recognize(ArrayList<AbstractNodeView> selectedNodes) {
        CompoundCommand recognizeCompoundCommand = new CompoundCommand();
        recognizedNodes = new ArrayList<>();

        //Perform matching between selected nodes and their views
        for(AbstractNodeView selectedNode : selectedNodes) {
            if(selectedNode instanceof ClassNodeView) {
                recognizedNodes.add((ClassNode) selectedNode.getRefNode());
            } else if(selectedNode instanceof EnumerationNodeView) {
                recognizedNodes.add((EnumerationNode) selectedNode.getRefNode());
            }
        }

        this.drawBorders();
        this.match();
    }

    public void match() {
        final String path = "test-source-code";
        NodeController nodeController = new NodeController(path);
        nodeController.initialize();

        existingNodes = nodeController.getNodeHolder().getAllNodes();

        for (AbstractNode graphNode: recognizedNodes) {
            existingNodes.stream().forEach(existingNode -> {
                if(graphNode.getType().equals(existingNode.getType())) {
                    if(graphNode.getTitle().equals(existingNode.getName())) {
                        NodeView nodeView = diagramController.findSelectedNodeView(graphNode);
                        nodeView.setFill(Color.PALEGREEN);

                        existingNode.setId(graphNode.getId());// These two nodes get connected by id
                    }
                }
            });
        }
    }

    private void drawBorders() {
        final double MARGIN = 50.0;
        double xMin, yMin, xMax, yMax;

        AbstractNode firstNode = graph.getAllNodes().get(0);

        xMin = firstNode.getX();
        yMin = firstNode.getY();
        xMax = firstNode.getWidth() + firstNode.getX();
        yMax = firstNode.getHeight() + firstNode.getY();

        //Go through all sketches to find Nodes.
        for (AbstractNode graphNode: graph.getAllNodes()) {
            if((graphNode.getX() + graphNode.getWidth()) > xMax) {
                xMax = graphNode.getX() + graphNode.getWidth();
            }

            if(graphNode.getX() < xMin) {
                xMin = graphNode.getX();
            }

            if((graphNode.getY() + graphNode.getHeight()) > yMax) {
                yMax = graphNode.getY() + graphNode.getHeight();
            }

            if(graphNode.getY() < yMin) {
                yMin = graphNode.getY();
            }
        }

        Rectangle rectangle = new Rectangle();
        rectangle.setX(xMin - MARGIN);
        rectangle.setY(yMin - MARGIN);
        rectangle.setWidth(xMax - xMin + (MARGIN * 2));
        rectangle.setHeight(yMax - yMin + (MARGIN * 2));
        rectangle.setStroke(Color.GOLD);
        rectangle.setStrokeWidth(10);
        rectangle.setFill(Color.TRANSPARENT);

        aDrawPane.getChildren().add(rectangle);
    }
}