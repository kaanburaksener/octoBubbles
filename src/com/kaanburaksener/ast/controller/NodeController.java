package com.kaanburaksener.ast.controller;

import com.kaanburaksener.ast.util.NodeParser;
import com.kaanburaksener.ast.model.NodeHolder;

import java.io.File;

/**
 * Created by kaanburaksener on 09/02/17.
 */
public class NodeController {
    private String path;
    private NodeHolder nodeHolder;
    private NodeParser nodeParser;
    private File projectDir;

    public NodeController(String path) {
        this.path = path;
        this.projectDir = new File(this.path);
    }

    public void initialize() {
        this.nodeHolder = new NodeHolder();
        this.nodeParser = new NodeParser(nodeHolder, path);
        nodeParser.loadNodes(projectDir);
        nodeParser.initializeNodesModifiers();
        nodeParser.loadNodesMethods();
        nodeParser.loadNodesAttributesOrValues();
        nodeParser.loadRelations();
        //nodeParser.testNodes();
    }

    public NodeHolder getNodeHolder() {
        return nodeHolder;
    }
}
