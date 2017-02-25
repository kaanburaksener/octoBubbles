package com.kaanburaksener.ast.model;

import com.kaanburaksener.ast.model.nodes.AbstractStructure;
import com.kaanburaksener.ast.model.nodes.ClassStructure;
import com.kaanburaksener.ast.model.nodes.EnumerationStructure;
import com.kaanburaksener.ast.model.nodes.InterfaceStructure;

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

    public void setNodes(List<AbstractStructure> nodes) {
        this.nodes = nodes;
    }

    public void printAllNodes() {
        nodes.stream().forEach(node -> {
            if(node instanceof ClassStructure || node instanceof InterfaceStructure) {
                ((ClassStructure)node).printStructure();
            } else if(node instanceof EnumerationStructure) {
                ((EnumerationStructure)node).printStructure();
            }
            System.out.println("------------ END OF NODE ----------");
        });
    }
}