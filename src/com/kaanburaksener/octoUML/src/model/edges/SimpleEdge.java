package com.kaanburaksener.octoUML.src.model.edges;

import com.kaanburaksener.octoUML.src.model.GraphElement;
import com.kaanburaksener.octoUML.src.model.nodes.Bubble;
import com.kaanburaksener.octoUML.src.model.nodes.Node;
import com.kaanburaksener.octoUML.src.util.Constants;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 * Created by kaanburaksener on 29/04/17.
 *
 * Represent a relation between UML Class diagram and its Bubble which shows its source code inside
 */
public class SimpleEdge implements GraphElement, Serializable {
    private static int objectCount = 0;  //Used to ID instance
    private int id = 0;
    private static final long serialVersionUID = 1L;

    //Listened to by the view, is always fired.
    protected transient PropertyChangeSupport changes = new PropertyChangeSupport(this);
    //Listened to by the server/client, only fired when the change comes from local interaction.
    protected transient PropertyChangeSupport remoteChanges = new PropertyChangeSupport(this);

    protected Node startNode;
    protected Bubble endNode;
    protected double zoom;

    public enum Direction {
        NO_DIRECTION
    }

    private Direction direction;

    public SimpleEdge(Node startNode, Bubble endNode) {
        this.startNode = startNode;
        this.endNode = endNode;
        direction = Direction.NO_DIRECTION;
        id = ++objectCount;
    }

    public void setStartNode(Node pNode) {
        startNode = pNode;
        changes.firePropertyChange(Constants.changeEdgeStartNode, null, startNode);
        remoteChanges.firePropertyChange(Constants.changeEdgeStartNode, null, startNode);
    }

    public void setEndNode(Bubble pNode) {
        endNode = pNode;
        changes.firePropertyChange(Constants.changeEdgeEndNode, null, endNode);
        remoteChanges.firePropertyChange(Constants.changeEdgeEndNode, null, endNode);
    }

    public void setZoom(double scale){
        zoom = scale;
        changes.firePropertyChange(Constants.changeEdgeZoom, null, zoom);
        remoteChanges.firePropertyChange(Constants.changeEdgeZoom, null, zoom);
    }

    public void remoteSetStartNode(Node pNode) {
        this.startNode = pNode;
        changes.firePropertyChange(Constants.changeEdgeStartNode, null, startNode);
    }

    public void remoteSetEndNode(Bubble pNode) {
        this.endNode = pNode;
        changes.firePropertyChange(Constants.changeEdgeEndNode, null, endNode);
    }

    public void remoteSetZoom(double scale){
        zoom = scale;
        changes.firePropertyChange(Constants.changeEdgeZoom, null, zoom);
    }

    public Node getStartNode() {
        return startNode;
    }

    public Bubble getEndNode() {
        return endNode;
    }

    public double getZoom(){
        return zoom;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public void setTranslateX(double x) {}

    @Override
    public void setTranslateY(double y) {}

    @Override
    public void setScaleX(double x) {}

    @Override
    public void setScaleY(double y) {}

    @Override
    public double getTranslateX() {
        return 0;
    }

    @Override
    public double getTranslateY() {
        return 0;
    }

    @Override
    public double getScaleY() {
        return 0;
    }

    @Override
    public double getScaleX() {
        return 0;
    }

    public String getType(){
        return "Simple Edge";
    }

    @Override
    public String getId() {
        return "SIMPLE_EDGE_" + id;
    }

    public static void incrementObjectCount(){
        objectCount++;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        changes.removePropertyChangeListener(l);
    }
}