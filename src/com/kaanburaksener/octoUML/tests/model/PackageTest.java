package com.kaanburaksener.octoUML.tests.model;

import com.kaanburaksener.octoUML.src.model.nodes.ClassNode;
import com.kaanburaksener.octoUML.src.model.nodes.EnumerationNode;
import com.kaanburaksener.octoUML.src.model.nodes.PackageNode;
import javafx.geometry.Point2D;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Created by chris on 2016-02-29.
 */
public class PackageTest {

    @Test
    public void testModelPackage() {
        PackageNode pNode = new PackageNode(0, 0, 150, 150);
        ClassNode c1 = new ClassNode(0, 0, 25, 25);
        ClassNode c2 = new ClassNode(50, 50, 25, 25);
        EnumerationNode e1 = new EnumerationNode(100, 100, 25, 25);

        pNode.addChild(c1);
        pNode.addChild(c2);
        pNode.addChild(e1);

        Point2D p1 = new Point2D(12, 12);
        Point2D p2 = new Point2D(70, 70);
        Point2D p3 = new Point2D(112, 112);
        assertEquals(pNode.findNode(p1), c1);
        assertEquals(pNode.findNode(p2), c2);
        assertEquals(pNode.findNode(p3), e1);
    }
}
