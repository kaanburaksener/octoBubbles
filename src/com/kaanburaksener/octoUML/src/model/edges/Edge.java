package com.kaanburaksener.octoUML.src.model.edges;

import com.kaanburaksener.octoUML.src.model.GraphElement;
import com.kaanburaksener.octoUML.src.model.nodes.AbstractNode;
import com.kaanburaksener.octoUML.src.model.nodes.Node;

/**
 * Interfaced used by all Edge-classes, represents a relationship between two nodes.
 */
public interface Edge extends GraphElement {
    Node getStartNode();
    Node getEndNode();
    Edge copy(AbstractNode startNodeCopy, AbstractNode endNodeCopy);
    String getType();
    String getId();
}
