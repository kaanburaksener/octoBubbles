package com.kaanburaksener.octoUML.src.util.commands;

import com.kaanburaksener.octoUML.src.controller.AbstractDiagramController;
import com.kaanburaksener.octoUML.src.model.Graph;
import com.kaanburaksener.octoUML.src.model.nodes.Bubble;
import com.kaanburaksener.octoUML.src.view.BubbleView;

/**
 * Created by kaanburaksener on 25/04/17.
 */
public class AddDeleteBubbleCommand implements Command {
    private AbstractDiagramController aController;
    private Graph aGraph;
    private BubbleView aBubbleView;
    private Bubble aBubble;
    private boolean aAdding; //true for adding, false for deleting

    /**
     * Creates the command.
     * @param pBubble The bubble to be added/deleted
     * @param pAdding True when adding, false when deleting
     */
    public AddDeleteBubbleCommand(AbstractDiagramController pController, Graph pGraph, BubbleView pBubbleView, Bubble pBubble, boolean pAdding)
    {
        aController = pController;
        aGraph = pGraph;
        aBubbleView = pBubbleView;
        aBubble = pBubble;
        aAdding = pAdding;
    }

    @Override
    public void undo() {
        if(aAdding)
        {
            delete();
        }
        else
        {
            add();
        }
    }

    @Override
    public void execute() {
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
     * Removes the bubble from the graph.
     */
    private void delete()
    {
        aGraph.removeBubble(aBubble, false);
        aController.deleteBubbleView(aBubbleView, null, true, false);
    }

    /**
     * Adds the bubble to the graph at the point in its properties.
     */
    private void add()
    {
        aGraph.addBubble(aBubble, false);
        aController.addBubbleView(aBubbleView, aBubble);
        aController.getGraphController().sketchesToFront();
    }

    public Bubble getBubble() {
        return aBubble;
    }

    public boolean isAdding() {
        return aAdding;
    }
}
