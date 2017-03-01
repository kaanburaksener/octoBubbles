package com.kaanburaksener.octoUML.src.util.commands;

import com.kaanburaksener.octoUML.src.model.nodes.ClassNode;
import com.kaanburaksener.octoUML.src.model.nodes.Node;

/**
 * Created by chalmers on 2016-08-29.
 */
public class SetNodeAttributeCommand implements Command {
    private ClassNode node;
    private String newAttribute;
    private String oldAttribute;

    public SetNodeAttributeCommand(ClassNode pNode, String pNewAttribute, String pOldAttribute){
        node = pNode;
        newAttribute = pNewAttribute;
        oldAttribute = pOldAttribute;
    }

    @Override
    public void undo() {
        node.setAttributes(oldAttribute);
    }

    @Override
    public void execute() {
        node.setAttributes(newAttribute);
    }

    public Node getNode() {
        return node;
    }

    public String getNewAttribute() {
        return newAttribute;
    }

    public String getOldAttribute() {
        return oldAttribute;
    }
}
