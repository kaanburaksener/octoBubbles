package com.kaanburaksener.octoUML.src.util.commands;

import com.kaanburaksener.octoUML.src.model.nodes.EnumerationNode;
import com.kaanburaksener.octoUML.src.model.nodes.Node;

/**
 * Created by kaanburaksener on 13/02/17.
 */
public class SetNodeValuesCommand implements Command
{
    private EnumerationNode node;
    private String newValues;
    private String oldValues;

    public SetNodeValuesCommand(EnumerationNode pNode, String pNewValues, String pOldValues){
        node = pNode;
        newValues = pNewValues;
        oldValues = pOldValues;
    }

    @Override
    public void undo() {
        node.setValues(oldValues);
    }

    @Override
    public void execute() {
        node.setValues(newValues);
    }

    public Node getNode() {
        return node;
    }

    public String getNewValues() {
        return newValues;
    }

    public String getOldValues() {
        return oldValues;
    }
}
