package com.kaanburaksener.octoUML.src.controller;

import com.kaanburaksener.octoUML.src.model.Graph;
import com.kaanburaksener.octoUML.src.model.edges.Edge;
import com.kaanburaksener.octoUML.src.model.nodes.AbstractNode;
import com.kaanburaksener.octoUML.src.model.nodes.ClassNode;
import com.kaanburaksener.octoUML.src.model.nodes.EnumerationNode;
import com.kaanburaksener.octoUML.src.util.commands.CompoundCommand;
import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.recognition.paleo.PaleoConfig;
import edu.tamu.recognition.paleo.PaleoSketchRecognizer;
import javafx.scene.layout.Pane;

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
    private ArrayList<ClassNode> recognizedClasses;
    private ArrayList<ClassNode> recognizedInterfaces;
    private ArrayList<EnumerationNode> recognizedEnumerations;

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

        System.out.println("All Nodes:" + graph.getAllNodes());
        System.out.println("--------------------------------");
        System.out.println("All Edges:" + recognizedEdges);

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

        System.out.println("--------------------------------");
        System.out.println("Classes:" + recognizedClasses.size());
        System.out.println("--------------------------------");
        System.out.println("Enumeration:" + recognizedEnumerations.size());
        System.out.println("--------------------------------");
        System.out.println("Interface:" + recognizedInterfaces.size());
    }
}