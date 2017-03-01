package com.kaanburaksener.octoUML.src.util.commands;

import com.kaanburaksener.octoUML.src.model.nodes.ClassNode;
import com.kaanburaksener.octoUML.src.model.nodes.Node;

/**
 * Created by kaanburaksener on 15/02/17.
 */
public class SetNodeTypeCommand implements Command {

    private ClassNode node;
    private String newType;
    private String oldType;

    public SetNodeTypeCommand(ClassNode pNode, String pNewType, String pOldType){
        node = pNode;
        newType = pNewType;
        oldType = pOldType;
    }

    @Override
    public void undo() {
        node.setType(oldType);
    }

    @Override
    public void execute() {
        node.setType(newType);
    }

    public Node getNode() {
        return node;
    }

    public String getNewType() {
        return newType;
    }

    public String getOldType() {
        return oldType;
    }
}

