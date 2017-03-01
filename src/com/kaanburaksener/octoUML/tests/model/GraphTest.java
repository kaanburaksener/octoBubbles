package com.kaanburaksener.octoUML.tests.model;

import com.kaanburaksener.octoUML.src.model.Graph;
import com.kaanburaksener.octoUML.src.model.nodes.ClassNode;
import com.kaanburaksener.octoUML.src.model.nodes.EnumerationNode;
import com.kaanburaksener.octoUML.src.model.nodes.PackageNode;
import javafx.geometry.Point2D;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by chris on 2016-02-29.
 */
public class GraphTest {

    @Test
    public void testGraph() {
        Graph graph = new Graph();
        PackageNode packageNode = new PackageNode(0, 0 ,150, 150);
        ClassNode c1 = new ClassNode(0, 0, 25, 25);
        ClassNode c2 = new ClassNode(50, 50, 25, 25);
        EnumerationNode e1 = new EnumerationNode(100, 100, 25, 25);

        packageNode.addChild(c1);
        packageNode.addChild(c2);
        packageNode.addChild(e1);

        Point2D p1 = new Point2D(12, 12);
        Point2D p2 = new Point2D(70, 70);
        Point2D p3 = new Point2D(112, 112);
        Point2D p4 = new Point2D(140, 140);
        graph.addNode(packageNode, false);
        graph.addNode(c1, false);
        graph.addNode(c2, false);
        graph.addNode(e1, false);
        assertEquals(graph.findNode(p1), c1);
        assertEquals(graph.findNode(p2), c2);
        assertEquals(graph.findNode(p3), e1);
        assertEquals(graph.findNode(p4), packageNode);
    }
}
