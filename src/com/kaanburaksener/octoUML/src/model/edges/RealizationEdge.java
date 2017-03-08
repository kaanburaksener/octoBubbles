package com.kaanburaksener.octoUML.src.model.edges;

import com.kaanburaksener.octoUML.src.model.nodes.AbstractNode;
import com.kaanburaksener.octoUML.src.model.nodes.Node;

/**
 * Created by kaanburaksener on 08/03/17.
 *
 * Represents a realization relationship between class and interface.
 */
public class RealizationEdge extends AbstractEdge {
    public RealizationEdge(Node startNode, Node endNode) {
        super(startNode, endNode);
    }

    /**
     * No-arg constructor for JavaBean convention
     */
    public RealizationEdge() {}

    public Edge copy(AbstractNode startNodeCopy, AbstractNode endNodeCopy) {
        return new RealizationEdge(startNodeCopy, endNodeCopy);
    }

    @Override
    public void setTranslateX(double x) {

    }

    @Override
    public void setTranslateY(double y) {

    }

    @Override
    public void setScaleX(double x) {

    }

    @Override
    public void setScaleY(double y) {

    }

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

    @Override
    public String getType(){
        return "Realization";
    }
}
