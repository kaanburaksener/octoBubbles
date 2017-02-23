package com.kaanburaksener;

import com.kaanburaksener.ast.controller.NodeController;

public class Main {
    public static void main(String[] args) {
        String path = "test-source-code";
        NodeController nodeController = new NodeController(path);
        nodeController.initialize();
    }
}
