package com.kaanburaksener.octoUML.src.util.commands;

import com.kaanburaksener.octoUML.src.controller.AbstractDiagramController;
import com.kaanburaksener.octoUML.src.model.edges.SimpleEdge;
import com.kaanburaksener.octoUML.src.view.edges.SimpleEdgeView;

/**
 * Created by kaanburaksener on 05/05/17.
 */
public class AddDeleteSimpleEdgeCommand implements Command
{
    private AbstractDiagramController aController;
    private SimpleEdgeView simpleEdgeView;
    private SimpleEdge simpleEdge;
    private boolean aAdding; //true for adding, false for deleting

    /**
     * Creates the command.
     * @param pSimpleEdge The simple edge to be added/deleted
     * @param pAdding True when adding, false when deleting
     */
    public AddDeleteSimpleEdgeCommand(AbstractDiagramController pController, SimpleEdgeView pSimpleEdgeView, SimpleEdge pSimpleEdge, boolean pAdding)
    {
        aController = pController;
        simpleEdgeView = pSimpleEdgeView;
        simpleEdge = pSimpleEdge;
        aAdding = pAdding;
    }

    /**
     * Undoes the command and adds/deletes the edge.
     */
    public void undo()
    {
        if(aAdding)
        {
            delete();
        }
        else
        {
            add();
        }
    }

    /**
     * Performs the command and adds/deletes the edge.
     */
    public void execute()
    {
        if(aAdding)
        {
            add();
        }
        else
        {
            delete();
        }
    }

    /**
     * Removes the node from the graph.
     */
    private void delete()
    {
        aController.deleteSimpleEdgeView(simpleEdgeView, null, true, false);
    }

    /**
     * Adds the edge to the graph at the points in its start and end node properties.
     */
    private void add()
    {
        aController.addSimpleEdgeView(simpleEdgeView);
    }

    public SimpleEdge getEdge() {
        return simpleEdge;
    }

    public boolean isAdding() {
        return aAdding;
    }
}