package com.kaanburaksener.ast.model;

import com.kaanburaksener.ast.model.nodes.AbstractStructure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaanburaksener on 08/02/17.
 */
public class NodeHolder {
    private List<AbstractStructure> nodes;

    public NodeHolder() {
        this.nodes = new ArrayList<AbstractStructure>();
    }

    public void addNode(AbstractStructure node) {
        nodes.add(node);
    }

    public void removeNode(AbstractStructure node) {
        nodes.remove(node);
    }

    public List<AbstractStructure> getAllNodes() {
        return nodes;
    }

    public void printAllNodes() {
        for(AbstractStructure node : nodes) {
            node.printStructure();
        }
    }

    public void setNodes(List<AbstractStructure> nodes) {
        this.nodes = nodes;
    }
}