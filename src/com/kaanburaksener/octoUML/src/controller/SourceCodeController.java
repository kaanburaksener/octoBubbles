package com.kaanburaksener.octoUML.src.controller;

import com.kaanburaksener.ast.controller.ASTNodeController;
import com.kaanburaksener.ast.model.nodes.AbstractStructure;
import com.kaanburaksener.ast.util.BubbleParser;

import com.kaanburaksener.octoUML.src.model.Graph;
import com.kaanburaksener.octoUML.src.model.edges.SimpleEdge;
import com.kaanburaksener.octoUML.src.model.nodes.AbstractNode;
import com.kaanburaksener.octoUML.src.model.nodes.Bubble;
import com.kaanburaksener.octoUML.src.model.nodes.ClassNode;
import com.kaanburaksener.octoUML.src.model.nodes.EnumerationNode;
import com.kaanburaksener.octoUML.src.model.Region;
import com.kaanburaksener.octoUML.src.view.BubbleView;
import com.kaanburaksener.octoUML.src.view.nodes.AbstractNodeView;
import com.kaanburaksener.octoUML.src.view.nodes.ClassNodeView;
import com.kaanburaksener.octoUML.src.view.nodes.EnumerationNodeView;
import com.kaanburaksener.octoUML.src.view.nodes.NodeView;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaanburaksener on 19/02/17.
 */
public class SourceCodeController {
    private AbstractDiagramController diagramController;
    private ASTNodeController astNodeController;
    private Graph graph;
    private List<AbstractStructure> existingNodes;
    private List<AbstractNode> recognizedNodes;
    private List<Region> regionsInBorder;
    private double borderX, borderY, borderWidth, borderHeight;

    public SourceCodeController(AbstractDiagramController pController, ASTNodeController astNodeController) {
        diagramController = pController;
        graph = diagramController.getGraphModel();
        this.astNodeController = astNodeController;
    }

    public synchronized void recognize(ArrayList<AbstractNodeView> selectedNodes) {
        recognizedNodes = new ArrayList<>();

        for(AbstractNodeView selectedNode : selectedNodes) {
            if(diagramController.findBubbleView(selectedNode.getRefNode()) == null) {
                if(selectedNode instanceof ClassNodeView) {
                    recognizedNodes.add((ClassNode) selectedNode.getRefNode());
                } else if(selectedNode instanceof EnumerationNodeView) {
                    recognizedNodes.add((EnumerationNode) selectedNode.getRefNode());
                }
            }
        }

        match();
        drawBorders();
        createRegionsInBorder();
        createBubbles();
        rescaleView();
    }

    /**
     * Matches the UML model with the existing source codes in the target folder
     */
    private void match() {
        astNodeController.initialize();
        existingNodes = astNodeController.getNodeHolder().getAllNodes();

        for(AbstractNode graphNode: recognizedNodes) {
            existingNodes.stream().forEach(existingNode -> {
                if(graphNode.getType().equals(existingNode.getType())) {
                    if(graphNode.getTitle().equals(existingNode.getName())) {
                        graphNode.setRefExistingNode(existingNode);
                        BubbleParser bubbleParser = new BubbleParser(existingNode.getCompilationUnit(), graphNode, astNodeController);
                        bubbleParser.projectChangesInBubble();
                    }
                }
            });
        }
    }

    /**
     * Matches the UML model with the existing source codes in the target folder
     */
    private void createBubbles() {
        for(AbstractNode graphNode: recognizedNodes) {
            AbstractNodeView nodeView = diagramController.findSelectedNodeView(graphNode);
            Region region = checkIntersectionAreaInBorder(nodeView);
            findAvailableSpace(region, nodeView, graphNode, graphNode.getRefExistingNode());
        }
    }

    /**
     * Creates a border around the UML model
     */
    private void drawBorders() {
        final double MARGIN = 50.0;

        AbstractNode firstNode = graph.getAllNodes().get(0);

        double xMin = firstNode.getX();
        double yMin = firstNode.getY();
        double xMax = firstNode.getWidth() + firstNode.getX();
        double yMax = firstNode.getHeight() + firstNode.getY();

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

        borderX = xMin - MARGIN;
        borderY = yMin - MARGIN;
        borderHeight = yMax - yMin + (MARGIN * 2);
        borderWidth = xMax - xMin + (MARGIN * 2);
    }

    /**
     * Divides the whole model into following four parts: Top Left, Top Right, Bottom Left, Bottom Right
     */
    private void createRegionsInBorder() {
        Region topLeft = new Region(borderX, borderY, borderWidth/2, borderHeight/2, "top-left");
        Region topRight = new Region(borderX + (borderWidth/2), borderY, borderWidth/2, borderHeight/2, "top-right");
        Region bottomLeft = new Region(borderX, borderY + (borderHeight/2), borderWidth/2, borderHeight/2, "bottom-left");
        Region bottomRight = new Region(borderX + (borderWidth/2), borderY + (borderHeight/2), borderWidth/2, borderHeight/2, "bottom-right");

        regionsInBorder = new ArrayList<>();
        regionsInBorder.add(topLeft);
        regionsInBorder.add(topRight);
        regionsInBorder.add(bottomLeft);
        regionsInBorder.add(bottomRight);
    }

    /**
     * Finds in which area the node intersects
     *
     * @param node - given node view
     * @return region which contains the node
     */
    private Region checkIntersectionAreaInBorder(NodeView node) {
        double endX = node.getX() + node.getWidth();
        double endY = node.getY() + node.getHeight();
        double nodeSize = node.getWidth() * node.getHeight();
        double intersectionSize = 0.0;
        Region intersected = null;
        boolean fullIntersection = false;

        for(int i = 0; i < regionsInBorder.size() && !fullIntersection; i++) {
            Region region = regionsInBorder.get(i);

            double xL = Math.max(node.getX(), region.getXStart());
            double xR = Math.min(endX, region.getXEnd());

            if(xR > xL) {
                double yT = Math.max(node.getY(), region.getYStart());
                double yB = Math.min(endY, region.getYEnd());

                if(yB > yT) {
                    double tempIntersectionSize = (xR - xL) * (yB - yT);

                    if(Double.compare(tempIntersectionSize, nodeSize) == 0) {//Stop searching other regions, fully surrounded by a region
                        intersected = region;
                        fullIntersection = true;
                    } else {
                        if(intersected != null) {
                            if(tempIntersectionSize > intersectionSize) {//Compare the size with the other intersection
                                intersectionSize = tempIntersectionSize;
                                intersected = region;
                            }
                        } else {//First partly intersection
                            intersectionSize = tempIntersectionSize;
                            intersected = region;
                        }
                    }
                }
            }
        }

        return intersected;
    }

    /**
     * Finds available space and creates a bubble
     *
     * @param region - Region which the node intersects
     * @param nodeView - Node View
     * @param graphNode - Node
     * @param existingNode - Source Code
     */
    private void findAvailableSpace(Region region, AbstractNodeView nodeView, AbstractNode graphNode, AbstractStructure existingNode) {
        double bubbleWidth = 400.0, bubbleHeight = existingNode.calculateHeight(), bubbleX = 0.0, bubbleY = 0.0;
        double closestDirectionX = 0.0, closestDirectionY = 0.0, tempFirst, tempSecond, margin = 50.0, shiftingPortion = 75.0;
        int shiftingTry = 0;

        switch (region.getName()) {
            case "top-left":
                tempFirst = nodeView.getX() - borderX; //x-axis through left
                tempSecond = nodeView.getY() - borderY; //y-axis through top

                if(tempFirst <= tempSecond) {//Closest Direction is LEFT
                    closestDirectionX = borderX - margin;
                    closestDirectionY = nodeView.getY() + (nodeView.getHeight() / 2);

                    bubbleX = closestDirectionX - bubbleWidth;
                    bubbleY = closestDirectionY - (bubbleHeight / 2);

                    shiftingTry = 0;

                    while(!isAreaFree(bubbleX, bubbleY, bubbleWidth, bubbleHeight) && (bubbleX >= 0 && bubbleX <= 8000) && (bubbleY >= 0 && bubbleY <= 8000)) {
                        bubbleY -= shiftingPortion;

                        shiftingTry++;
                        if(shiftingTry > 4) {
                            bubbleX -= shiftingPortion;
                        }
                    }
                } else {//Closest Direction is TOP
                    closestDirectionX = nodeView.getX() + (nodeView.getWidth() / 2);
                    closestDirectionY = borderY - margin;

                    bubbleX = closestDirectionX - (bubbleWidth / 2);
                    bubbleY = closestDirectionY - bubbleHeight;

                    shiftingTry = 0;

                    while(!isAreaFree(bubbleX, bubbleY, bubbleWidth, bubbleHeight) && (bubbleX >= 0 && bubbleX <= 8000) && (bubbleY >= 0 && bubbleY <= 8000)) {
                        bubbleX -= shiftingPortion;

                        shiftingTry++;
                        if(shiftingTry > 4) {
                            bubbleY -= shiftingPortion;
                        }
                    }
                }
                break;
            case "top-right":
                tempFirst = (borderX + borderWidth) - (nodeView.getX() + nodeView.getWidth()); //x-axis through right
                tempSecond = nodeView.getY() - borderY; //y-axis through top

                if(tempFirst <= tempSecond) {//Closest Direction is RIGHT
                    closestDirectionX = borderX + borderWidth + margin;
                    closestDirectionY = nodeView.getY() + (nodeView.getHeight() / 2);

                    bubbleX = closestDirectionX;
                    bubbleY = closestDirectionY - (bubbleHeight / 2);

                    shiftingTry = 0;

                    while(!isAreaFree(bubbleX, bubbleY, bubbleWidth, bubbleHeight) && (bubbleX >= 0 && bubbleX <= 8000) && (bubbleY >= 0 && bubbleY <= 8000)) {
                        bubbleY -= shiftingPortion;

                        shiftingTry++;
                        if(shiftingTry > 4) {
                            bubbleX += shiftingPortion;
                        }
                    }
                } else {//Closest Direction is TOP
                    closestDirectionX = nodeView.getX() + (nodeView.getWidth() / 2);
                    closestDirectionY = borderY - margin;

                    bubbleX = closestDirectionX - (bubbleWidth / 2);
                    bubbleY = closestDirectionY - bubbleHeight;

                    shiftingTry = 0;

                    while(!isAreaFree(bubbleX, bubbleY, bubbleWidth, bubbleHeight) && (bubbleX >= 0 && bubbleX <= 8000) && (bubbleY >= 0 && bubbleY <= 8000)) {
                        bubbleX += shiftingPortion;

                        shiftingTry++;
                        if(shiftingTry > 4) {
                            bubbleY -= shiftingPortion;
                        }
                    }
                }
                break;
            case "bottom-right":
                tempFirst = (borderX + borderWidth) - (nodeView.getX() + nodeView.getWidth()); //x-axis through right
                tempSecond = (borderY + borderHeight) - (nodeView.getY() + nodeView.getHeight()); //y-axis through bottom

                if(tempFirst <= tempSecond) {//Closest Direction is RIGHT
                    closestDirectionX = borderX + borderWidth + margin;
                    closestDirectionY = nodeView.getY() + (nodeView.getHeight() / 2);

                    bubbleX = closestDirectionX;
                    bubbleY = closestDirectionY - (bubbleHeight / 2);

                    shiftingTry = 0;

                    while(!isAreaFree(bubbleX, bubbleY, bubbleWidth, bubbleHeight) && (bubbleX >= 0 && bubbleX <= 8000) && (bubbleY >= 0 && bubbleY <= 8000)) {
                        bubbleY += shiftingPortion;

                        shiftingTry++;
                        if(shiftingTry > 4) {
                            bubbleX += shiftingPortion;
                        }
                    }
                } else {//Closest Direction is BOTTOM
                    closestDirectionX = nodeView.getX() + (nodeView.getWidth() / 2);
                    closestDirectionY = borderY + borderHeight + margin;

                    bubbleX = closestDirectionX - (bubbleWidth / 2);
                    bubbleY = closestDirectionY;

                    shiftingTry = 0;

                    while(!isAreaFree(bubbleX, bubbleY, bubbleWidth, bubbleHeight) && (bubbleX >= 0 && bubbleX <= 8000) && (bubbleY >= 0 && bubbleY <= 8000)) {
                        bubbleX += shiftingPortion;

                        shiftingTry++;
                        if(shiftingTry > 4) {
                            bubbleY += shiftingPortion;
                        }
                    }
                }
                break;
            case "bottom-left":
                tempFirst = nodeView.getX() - borderX; //x-axis through left
                tempSecond = (borderY + borderHeight) - (nodeView.getY() + nodeView.getHeight()); //y-axis through bottom

                if(tempFirst <= tempSecond) {//Closest Direction is LEFT
                    closestDirectionX = borderX - margin;
                    closestDirectionY = nodeView.getY() + (nodeView.getHeight() / 2);

                    bubbleX = closestDirectionX - bubbleWidth;
                    bubbleY = closestDirectionY - (bubbleHeight / 2);

                    shiftingTry = 0;

                    while(!isAreaFree(bubbleX, bubbleY, bubbleWidth, bubbleHeight) && (bubbleX >= 0 && bubbleX <= 8000) && (bubbleY >= 0 && bubbleY <= 8000)) {
                        bubbleY += shiftingPortion;

                        shiftingTry++;

                        if(shiftingTry > 4) {
                            bubbleX -= shiftingPortion;
                        }
                    }
                } else {//Closest Direction is BOTTOM
                    closestDirectionX = nodeView.getX() + (nodeView.getWidth() / 2);
                    closestDirectionY = borderY + borderHeight + margin;

                    bubbleX = closestDirectionX - (bubbleWidth / 2);
                    bubbleY = closestDirectionY;

                    shiftingTry = 0;

                    while(!isAreaFree(bubbleX, bubbleY, bubbleWidth, bubbleHeight) && (bubbleX >= 0 && bubbleX <= 8000) && (bubbleY >= 0 && bubbleY <= 8000)) {
                        bubbleX -= shiftingPortion;

                        shiftingTry++;
                        if(shiftingTry > 4) {
                            bubbleY += shiftingPortion;
                        }
                    }
                }
                break;
            default:
                break;
        }

        Bubble bubble = new Bubble(bubbleX, bubbleY, bubbleWidth, bubbleHeight, existingNode.getName(), existingNode.getCompilationUnit(), graphNode);
        BubbleView bubbleView = diagramController.createBubbleView(bubble, false);
        SimpleEdge edge = new SimpleEdge(graphNode, bubble);
        diagramController.createSimpleEdgeView(edge, nodeView, bubbleView);
    }

    /**
     * Search any overlap between the given bubble and other existing bubbles
     *
     * @param x1 - X of Rectangle
     * @param y1 - Y of Rectangle
     * @param w1 - Width of Rectangle
     * @param h1 - Height of Rectangle
     * @return false is there is an overlap, otherwise true.
     */
    private boolean isAreaFree(double x1, double y1, double w1, double h1) {
        boolean result = true;

        List<BubbleView> bubbleViews = new ArrayList<>(diagramController.getAllBubbleViews());
        if(bubbleViews.size() > 0) {
            for(int i = 0; i < bubbleViews.size() && result; i++) {
                if(!isOverlapping(x1, y1, w1, h1, bubbleViews.get(i).getX(), bubbleViews.get(i).getY(), bubbleViews.get(i).getWidth(), bubbleViews.get(i).getHeight())) {
                    result = false;
                }
            }
        }

        return result;
    }

    /**
     * Finds any overlap between given two bubbles
     *
     * @param x1 - X of First Rectangle
     * @param y1 - Y of First Rectangle
     * @param w1 - Width of First Rectangle
     * @param h1 - Height of First Rectangle
     * @param x2 - X of Second Rectangle
     * @param y2 - Y of Second Rectangle
     * @param w2 - Width of Second Rectangle
     * @param h2 - Height of Second Rectangle
     * @return false is there is an overlap, otherwise true.
     */
    private boolean isOverlapping(double x1, double y1, double w1, double h1, double x2, double y2, double w2, double h2) {
        java.awt.Rectangle rect1 = new java.awt.Rectangle((int)x1, (int)y1, (int)w1, (int)h1);
        java.awt.Rectangle rect2 = new java.awt.Rectangle((int)x2, (int)y2, (int)w2, (int)h2);
        java.awt.Rectangle intersection = rect1.intersection(rect2);

        if(intersection.getWidth() >= 0 && intersection.getHeight() >= 0) {
            return false;
        }

        return true;
    }


    /**
     * Rescale viewport to show all the UML model and bubbles together
     */
    private void rescaleView() {
        double MARGIN = 5.0;

        List<BubbleView> bubbleViews = new ArrayList<>(diagramController.getAllBubbleViews());

        if(bubbleViews.size() > 0) {
            double xMin = bubbleViews.get(0).getX();
            double yMin = bubbleViews.get(0).getY();
            double xMax = bubbleViews.get(0).getX() + bubbleViews.get(0).getWidth();
            double yMax = bubbleViews.get(0).getY() + bubbleViews.get(0).getHeight();

            for(int i = 1; i < bubbleViews.size(); i++) {
                if((bubbleViews.get(i).getX() + bubbleViews.get(i).getWidth()) > xMax) {
                    xMax = bubbleViews.get(i).getX() + bubbleViews.get(i).getWidth();
                }

                if(bubbleViews.get(i).getX() < xMin) {
                    xMin = bubbleViews.get(i).getX();
                }

                if((bubbleViews.get(i).getY() + bubbleViews.get(i).getHeight()) > yMax) {
                    yMax = bubbleViews.get(i).getY() + bubbleViews.get(i).getHeight();
                }

                if(bubbleViews.get(i).getY() < yMin) {
                    yMin = bubbleViews.get(i).getY();
                }
            }

            final double scrollPaneWidth = diagramController.getScrollPane().getWidth();
            final double scrollPaneHeight = diagramController.getScrollPane().getHeight();
            final double contentsWidth = xMax - xMin;
            final double contentsHeight = yMax - yMin;
            double toBeScaled = Math.min(scrollPaneHeight / contentsHeight, scrollPaneWidth / contentsWidth) * 100;

            diagramController.getGraphController().zoomPane(toBeScaled - MARGIN);
            diagramController.zoomSlider.setValue(toBeScaled - MARGIN);
            diagramController.getGraphController().ensureVisible((xMax + xMin) / 2, (yMax + yMin) / 2);
            diagramController.getGraphController().simplesEdgesToBack();
        }
    }
}