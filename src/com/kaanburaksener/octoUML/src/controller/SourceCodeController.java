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

import com.kaanburaksener.octoUML.src.view.nodes.AbstractNodeView;
import com.kaanburaksener.octoUML.src.view.nodes.ClassNodeView;
import com.kaanburaksener.octoUML.src.view.nodes.NodeView;
import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.recognition.paleo.PaleoConfig;
import edu.tamu.recognition.paleo.PaleoSketchRecognizer;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

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
    private List<ClassNode> recognizedClasses;
    private List<ClassNode> recognizedInterfaces;
    private List<EnumerationNode> recognizedEnumerations;
    private List<AbstractStructure> recognizedNodes;
    private List<AbstractNodeView> allNodeViews;

    public SourceCodeController(Pane pDrawPane, AbstractDiagramController pController) {
        aDrawPane = pDrawPane;
        diagramController = pController;
        graph = diagramController.getGraphModel();

        recognizedClasses = new ArrayList<ClassNode>();
        recognizedInterfaces = new ArrayList<ClassNode>();
        recognizedEnumerations = new ArrayList<EnumerationNode>();

        //TODO Find a nicer solution for this:
        //This is to load the recognizer when starting app, not when starting to draw.

        recognizer = new PaleoSketchRecognizer(PaleoConfig.allOn());
        Stroke init = new Stroke();
        init.addPoint(new Point(0,1));
        recognizer.setStroke(init);
        recognizer.recognize().getBestShape();
    }

    public synchronized void recognize() {
        List<Edge> recognizedEdges = new ArrayList<>();
        CompoundCommand recognizeCompoundCommand = new CompoundCommand();

        recognizedEdges = graph.getAllEdges();

        //Go through all sketches to find Nodes.
        for (AbstractNode rn : graph.getAllNodes()) {
            switch (rn.getType()) {
                case "CLASS":
                    recognizedClasses.add((ClassNode)rn);
                    break;
                case "ENUM":
                    recognizedEnumerations.add((EnumerationNode)rn);
                    break;
                case "INTERFACE":
                    recognizedInterfaces.add((ClassNode)rn);
                    break;
                default:
                    System.out.println("Something goes wrong!");
                    break;
            }
        }

        this.match();
    }

    public void match() {
        String path = "test-source-code";
        NodeController nodeController = new NodeController(path);
        nodeController.initialize();

        recognizedNodes = new ArrayList<AbstractStructure>();
        recognizedNodes = nodeController.getNodeHolder().getAllNodes();

        graph.getAllNodes().stream().forEach(graphNode -> {
            recognizedNodes.stream().forEach(codeNode -> {
                if(graphNode.getType().equals(codeNode.getType())) {
                    if(graphNode.getTitle().equals(codeNode.getName())) {
                        System.out.println("This " + graphNode.getType() + " has been found in Source Code -> " + codeNode.getPath());

                        Point2D point = new Point2D(graphNode.getX(), graphNode.getY());
                        NodeView nodeView = diagramController.findNodeView(point);
                        nodeView.setFill(Color.PALEGREEN);

                        codeNode.setId(graphNode.getId());// These two nodes get connected by id
                    }
                }
            });
        });
    }
}