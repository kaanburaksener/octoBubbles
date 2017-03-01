package com.kaanburaksener.octoUML.tests.view;

import com.kaanburaksener.octoUML.src.model.nodes.EnumerationNode;
import com.kaanburaksener.octoUML.src.view.nodes.EnumerationNodeView;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by kaanburaksener on 13/02/17.
 */
public class EnumerationNodeViewTest {
    @Test
    public void testNodes() {
        double delta = 0.001d;
        EnumerationNode mNode = new EnumerationNode(0, 0, 0, 0);
        EnumerationNodeView vNode = new EnumerationNodeView(mNode);
        assert vNode.getX() == mNode.getX();
        mNode.setX(5);
        mNode.setY(10.555);
        mNode.setWidth(66);
        mNode.setHeight(87);
        assertEquals(vNode.getX(), 5, delta);
        assertEquals(vNode.getY(), 10.555d, delta);
        assertEquals(vNode.getWidth(), 66, delta);
        assertEquals(vNode.getHeight(), 87, delta);
        assertEquals(vNode.getX(), mNode.getX(), delta);
    }
}