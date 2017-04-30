package com.kaanburaksener.octoUML.src.view.edges;

import com.kaanburaksener.octoUML.src.model.edges.SimpleEdge;
import com.kaanburaksener.octoUML.src.util.Constants;
import com.kaanburaksener.octoUML.src.view.BubbleView;
import com.kaanburaksener.octoUML.src.view.nodes.AbstractNodeView;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by kaanburaksener on 29/04/17.
 *
 * Visual representation of SimpleEdge class.
 */
public class SimpleEdgeView extends Group implements EdgeView, PropertyChangeListener {
    private static int objectCounter = 0;

    protected SimpleEdge refEdge;
    protected AbstractNodeView startNode;
    protected BubbleView endNode;
    protected boolean selected = false;
    public final double STROKE_WIDTH = 3;

    public enum Position{
        ABOVE, BELOW, RIGHT, LEFT, NONE
    }

    protected Position position = Position.NONE;

    protected Line startLine = new Line();
    protected Line middleLine = new Line();
    protected Line endLine = new Line();

    public SimpleEdgeView(SimpleEdge edge, AbstractNodeView startNode, BubbleView endNode) {
        super();

        setId("VIEWASSOCIATION_" + ++objectCounter);

        this.refEdge = edge;
        this.startNode = startNode;
        this.endNode = endNode;
        this.setVisible(true);
        this.getChildren().add(startLine);
        this.getChildren().add(middleLine);
        this.getChildren().add(endLine);

        startLine.setStrokeWidth(STROKE_WIDTH);
        middleLine.setStrokeWidth(STROKE_WIDTH);
        endLine.setStrokeWidth(STROKE_WIDTH);

        refEdge.addPropertyChangeListener(this);
        if(startNode != null){
            startNode.getRefNode().addPropertyChangeListener(this);
        }
        endNode.getRefNode().addPropertyChangeListener(this);

        this.setStroke(Color.ROYALBLUE);
        setPosition();
        setSelected(selected);
    }

    public SimpleEdge getRefEdge() {
        return refEdge;
    }

    public void setStrokeWidth(double width) {
        startLine.setStrokeWidth(width);
    }

    public void setStroke(Paint value){
        startLine.setStroke(value);
    }

    public double getStartX() {
        return startLine.getStartX();
    }

    public double getStartY(){
        return startLine.getStartY();
    }

    public double getEndX(){
        return endLine.getEndX();
    }

    public double getEndY(){
        return endLine.getEndY();
    }

    public Line getStartLine() {
        return startLine;
    }

    public Line getMiddleLine() {
        return middleLine;
    }

    public Line getEndLine() {
        return endLine;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected){
        this.selected = selected;
        if (selected){
            startLine.setStroke(Constants.selected_color);
            middleLine.setStroke(Constants.selected_color);
            endLine.setStroke(Constants.selected_color);
        } else {
            startLine.setStroke(Color.ROYALBLUE);
            middleLine.setStroke(Color.ROYALBLUE);
            endLine.setStroke(Color.ROYALBLUE);
        }
    }

    public Position getPosition() {
        return position;
    }

    protected void setPosition() {
        //If end node is to the right of startNode:
        if (startNode.getTranslateX() + startNode.getWidth() <= endNode.getX()) { //Straight line if height difference is small
            if(Math.abs(startNode.getTranslateY() + (startNode.getHeight()/2) - (endNode.getY() + (endNode.getHeight()/2))) < 20){
                startLine.setStartX(startNode.getTranslateX() + startNode.getWidth());
                startLine.setStartY(startNode.getTranslateY() + (startNode.getHeight() / 2));
                startLine.setEndX(endNode.getX());
                startLine.setEndY(endNode.getY() + (endNode.getHeight() / 2));

                middleLine.setStartX(0);
                middleLine.setStartY(0);
                middleLine.setEndX(0);
                middleLine.setEndY(0);

                endLine.setStartX(0);
                endLine.setStartY(0);
                endLine.setEndX(0);
                endLine.setEndY(0);
            } else {
                startLine.setStartX(startNode.getTranslateX() + startNode.getWidth());
                startLine.setStartY(startNode.getTranslateY() + (startNode.getHeight() / 2));
                startLine.setEndX(startNode.getTranslateX() + startNode.getWidth() + ((endNode.getX() - (startNode.getTranslateX() + startNode.getWidth()))/2));
                startLine.setEndY(startNode.getTranslateY() + (startNode.getHeight() / 2));

                middleLine.setStartX(startNode.getTranslateX() + startNode.getWidth() + ((endNode.getX() - (startNode.getTranslateX() + startNode.getWidth()))/2));
                middleLine.setStartY(startNode.getTranslateY() + (startNode.getHeight() / 2));
                middleLine.setEndX(startNode.getTranslateX() + startNode.getWidth() + ((endNode.getX() - (startNode.getTranslateX() + startNode.getWidth()))/2));
                middleLine.setEndY(endNode.getY() + (endNode.getHeight() / 2));

                endLine.setStartX(startNode.getTranslateX() + startNode.getWidth() + ((endNode.getX() - (startNode.getTranslateX() + startNode.getWidth()))/2));
                endLine.setStartY(endNode.getY() + (endNode.getHeight() / 2));
                endLine.setEndX(endNode.getX());
                endLine.setEndY(endNode.getY() + (endNode.getHeight() / 2));
            }

            position = Position.RIGHT;
        }
        //If end node is to the left of startNode:
        else if (startNode.getTranslateX() > endNode.getX() + endNode.getWidth()) {
            if(Math.abs(startNode.getTranslateY() + (startNode.getHeight()/2) - (endNode.getY() + (endNode.getHeight()/2))) < 20){
                startLine.setStartX(startNode.getTranslateX());
                startLine.setStartY(startNode.getTranslateY() + (startNode.getHeight() / 2));
                startLine.setEndX(endNode.getX() + endNode.getWidth());
                startLine.setEndY(endNode.getY() + (endNode.getHeight() / 2));

                middleLine.setStartX(0);
                middleLine.setStartY(0);
                middleLine.setEndX(0);
                middleLine.setEndY(0);

                endLine.setStartX(0);
                endLine.setStartY(0);
                endLine.setEndX(0);
                endLine.setEndY(0);
            } else {
                startLine.setStartX(startNode.getTranslateX());
                startLine.setStartY(startNode.getTranslateY() + (startNode.getHeight() / 2));
                startLine.setEndX(endNode.getX() + endNode.getWidth()  + ((startNode.getTranslateX() - (endNode.getX() + endNode.getWidth()))/2));
                startLine.setEndY(startNode.getTranslateY() + (startNode.getHeight() / 2));

                middleLine.setStartX(endNode.getX() + endNode.getWidth()  + ((startNode.getTranslateX() - (endNode.getX() + endNode.getWidth()))/2));
                middleLine.setStartY(startNode.getTranslateY() + (startNode.getHeight() / 2));
                middleLine.setEndX(endNode.getX() + endNode.getWidth()  + ((startNode.getTranslateX() - (endNode.getX() + endNode.getWidth()))/2));
                middleLine.setEndY(endNode.getY() + (endNode.getHeight() / 2));

                endLine.setStartX(endNode.getX() + endNode.getWidth()  + ((startNode.getTranslateX() - (endNode.getX() + endNode.getWidth()))/2));
                endLine.setStartY(endNode.getY() + (endNode.getHeight() / 2));
                endLine.setEndX(endNode.getX() + endNode.getWidth());
                endLine.setEndY(endNode.getY() + (endNode.getHeight() / 2));
            }

            position = Position.LEFT;
        }
        // If end node is below startNode:
        else if (startNode.getTranslateY() + startNode.getHeight() < endNode.getY()){
            if(Math.abs(startNode.getTranslateX() + (startNode.getWidth()/2) - (endNode.getX() + (endNode.getWidth()/2))) < 20){
                startLine.setStartX(startNode.getTranslateX() + (startNode.getWidth() /2));
                startLine.setStartY(startNode.getTranslateY() + startNode.getHeight());
                startLine.setEndX(endNode.getX() + (endNode.getWidth()/2));
                startLine.setEndY(endNode.getY());

                middleLine.setStartX(0);
                middleLine.setStartY(0);
                middleLine.setEndX(0);
                middleLine.setEndY(0);

                endLine.setStartX(0);
                endLine.setStartY(0);
                endLine.setEndX(0);
                endLine.setEndY(0);
            } else {
                startLine.setStartX(startNode.getTranslateX() + (startNode.getWidth() /2));
                startLine.setStartY(startNode.getTranslateY() + startNode.getHeight());
                startLine.setEndX(startNode.getTranslateX() + (startNode.getWidth() /2));
                startLine.setEndY(startNode.getTranslateY() + startNode.getHeight() + ((endNode.getY() - (startNode.getTranslateY() + startNode.getHeight()))/2));

                middleLine.setStartX(startNode.getTranslateX() + (startNode.getWidth() /2));
                middleLine.setStartY(startNode.getTranslateY() + startNode.getHeight() + ((endNode.getY() - (startNode.getTranslateY() + startNode.getHeight()))/2));
                middleLine.setEndX(endNode.getX() + (endNode.getWidth()/2));
                middleLine.setEndY(startNode.getTranslateY() + startNode.getHeight() + ((endNode.getY() - (startNode.getTranslateY() + startNode.getHeight()))/2));

                endLine.setStartX(endNode.getX() + (endNode.getWidth()/2));
                endLine.setStartY(startNode.getTranslateY() + startNode.getHeight() + ((endNode.getY() - (startNode.getTranslateY() + startNode.getHeight()))/2));
                endLine.setEndX(endNode.getX() + (endNode.getWidth()/2));
                endLine.setEndY(endNode.getY());
            }

            position = Position.BELOW;
        }
        //If end node is above startNode:
        else if (startNode.getTranslateY() >= endNode.getY() + endNode.getHeight()) {
            if(Math.abs(startNode.getTranslateX() + (startNode.getWidth()/2) - (endNode.getX() + (endNode.getWidth()/2))) < 20){
                startLine.setStartX(startNode.getTranslateX() + (startNode.getWidth() / 2));
                startLine.setStartY(startNode.getTranslateY());
                startLine.setEndX(endNode.getX() + (endNode.getWidth()/2));
                startLine.setEndY(endNode.getY() + endNode.getHeight());

                middleLine.setStartX(0);
                middleLine.setStartY(0);
                middleLine.setEndX(0);
                middleLine.setEndY(0);

                endLine.setStartX(0);
                endLine.setStartY(0);
                endLine.setEndX(0);
                endLine.setEndY(0);
            } else {
                startLine.setStartX(startNode.getTranslateX() + (startNode.getWidth() /2));
                startLine.setStartY(startNode.getTranslateY());
                startLine.setEndX(startNode.getTranslateX() + (startNode.getWidth() /2));
                startLine.setEndY(endNode.getY() + endNode.getHeight() + ((startNode.getTranslateY() - (endNode.getY() + endNode.getHeight()))/2));

                middleLine.setStartX(startNode.getTranslateX() + (startNode.getWidth() /2));
                middleLine.setStartY(endNode.getY() + endNode.getHeight() + ((startNode.getTranslateY() - (endNode.getY() + endNode.getHeight()))/2));
                middleLine.setEndX(endNode.getX() + (endNode.getWidth()/2));
                middleLine.setEndY(endNode.getY() + endNode.getHeight() + ((startNode.getTranslateY() - (endNode.getY() + endNode.getHeight()))/2));

                endLine.setStartX(endNode.getX() + (endNode.getWidth()/2));
                endLine.setStartY(endNode.getY() + endNode.getHeight() + ((startNode.getTranslateY() - (endNode.getY() + endNode.getHeight()))/2));
                endLine.setEndX(endNode.getX() + (endNode.getWidth()/2));
                endLine.setEndY(endNode.getY() + endNode.getHeight());
            }

            position = Position.ABOVE;
        }
    }

    public AbstractNodeView getStartNode() {
        return startNode;
    }

    public void setStartNode(AbstractNodeView startNode) {
        this.startNode = startNode;
    }

    public BubbleView getEndNode() {
        return endNode;
    }

    public void setEndNode(BubbleView endNode) {
        this.endNode = endNode;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(Constants.changeNodeTranslateX) || evt.getPropertyName().equals(Constants.changeNodeTranslateY) || evt.getPropertyName().equals(Constants.changeBubbleX) || evt.getPropertyName().equals(Constants.changeBubbleY)){
            setPosition();
        } else if(evt.getPropertyName().equals(Constants.changeEdgeZoom)) {
            setStrokeWidth((double)evt.getNewValue());
            setPosition();
        } else if (evt.getPropertyName().equals(Constants.changeNodeWidth) || evt.getPropertyName().equals(Constants.changeNodeHeight)){
            setPosition();
        }
    }
}
