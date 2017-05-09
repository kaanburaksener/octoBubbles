package com.kaanburaksener.ast.controller;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;

import com.kaanburaksener.ast.model.nodes.AbstractStructure;
import com.kaanburaksener.ast.model.nodes.ClassStructure;
import com.kaanburaksener.ast.model.nodes.EnumerationStructure;
import com.kaanburaksener.ast.model.nodes.InterfaceStructure;
import com.kaanburaksener.ast.model.NodeHolder;
import com.kaanburaksener.ast.util.NodeParser;
import com.kaanburaksener.ast.util.NodeViewScratchParser;

import com.kaanburaksener.octoUML.src.controller.AbstractDiagramController;
import com.kaanburaksener.octoUML.src.model.nodes.AbstractNode;
import com.kaanburaksener.octoUML.src.model.nodes.ClassNode;
import com.kaanburaksener.octoUML.src.model.nodes.EnumerationNode;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by kaanburaksener on 09/02/17.
 */
public class ASTNodeController {
    private final String PACKAGE_NAME = "com.kaanburaksener";
    private String path;
    private NodeHolder nodeHolder;
    private NodeParser nodeParser;
    private File projectDir;
    private AbstractDiagramController diagramController;

    public ASTNodeController(String path, AbstractDiagramController diagramController) {
        this.path = path;
        this.projectDir = new File(this.path);
        this.diagramController = diagramController;
    }

    public void initialize() {
        this.nodeHolder = new NodeHolder();
        this.nodeParser = new NodeParser(nodeHolder, path);
        nodeParser.loadNodes(projectDir);
        nodeParser.initializeNodesModifiers();
        nodeParser.loadNodesMethods();
        nodeParser.loadNodesAttributesOrValues();
        nodeParser.loadRelations();
    }

    /**
     * It creates .java file for the nodeView, which doesn't exist inside the target folder.
     * @param node
     */
    public void createNewJavaFile(AbstractNode node) {
        String name = node.getTitle();

        Path javaFilePath = Paths.get(path, name + ".java");

        if (!Files.exists(javaFilePath)) {
            try {
                Files.createFile(javaFilePath);
                generateCompilationUnit(node, "/" + name + ".java");
            } catch (IOException x) {
                System.err.format("createFile error: %s%n", x);
            }
        }
    }

    /**
     * If the nodeView doesn't exist inside the target folder, it generates a new file from scratch.
     * @param node
     * @param filePath
     */
    private void generateCompilationUnit(AbstractNode node, String filePath) {
        try {
            String fileType = node.getType();
            String name = node.getTitle();

            FileInputStream in = new FileInputStream(new File(this.path + filePath));
            CompilationUnit compilationUnit = JavaParser.parse(in);

            AbstractStructure abstractStructure = null;

            if(fileType.equals("CLASS")) {
                ClassOrInterfaceDeclaration type = compilationUnit.addClass(name);
                abstractStructure = new ClassStructure(name, filePath);
            } else if(fileType.equals("INTERFACE")) {
                ClassOrInterfaceDeclaration type = compilationUnit.addInterface(name);
                abstractStructure = new InterfaceStructure(name, filePath);
            } else if(fileType.equals("ENUM")) {
                EnumDeclaration type = compilationUnit.addEnum(name);
                abstractStructure = new EnumerationStructure(name, filePath);
            }

            compilationUnit.setPackageDeclaration(PACKAGE_NAME);
            abstractStructure.setCompilationUnit(compilationUnit);

            if(abstractStructure instanceof ClassStructure || abstractStructure instanceof InterfaceStructure) {
                NodeViewScratchParser nodeViewScratchParser = new NodeViewScratchParser(abstractStructure, compilationUnit, ((ClassNode)node).getOperations(), ((ClassNode)node).getAttributes());
                nodeViewScratchParser.projectChangesInNodeView();
            } else {
                NodeViewScratchParser nodeViewScratchParser = new NodeViewScratchParser(abstractStructure, compilationUnit, ((EnumerationNode)node).getValues());
                nodeViewScratchParser.projectChangesInNodeView();
            }

            node.setRefExistingNode(abstractStructure);
            nodeHolder.addNode(abstractStructure);
        } catch (Exception e) {
            System.out.println("Error occured while opening the given file: " + e.getMessage());
        }
    }

    public NodeParser getNodeParser() {
        return nodeParser;
    }

    public NodeHolder getNodeHolder() {
        return nodeHolder;
    }

    public AbstractDiagramController getDiagramController() {
        return diagramController;
    }
}
