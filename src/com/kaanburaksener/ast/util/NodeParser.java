package com.kaanburaksener.ast.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.Modifier;

import com.google.common.base.Strings;

import com.kaanburaksener.ast.helper.NodeIterator;
import com.kaanburaksener.ast.model.NodeHolder;
import com.kaanburaksener.ast.model.nodes.AbstractStructure;
import com.kaanburaksener.ast.helper.DirExplorer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Created by kaanburaksener on 11/02/17.
 */
public class NodeParser {
    private NodeHolder nodeHolder;
    private static String targetFolderName;

    public NodeParser(NodeHolder nodeHolder, String targetFolderName) {
        this.nodeHolder = nodeHolder;
        this.targetFolderName = targetFolderName;
    }

    public static CompilationUnit getCompilationUnit(AbstractStructure abstractStructure) {
        CompilationUnit compilationUnit = new CompilationUnit();

        try {
            compilationUnit = JavaParser.parse(new File(targetFolderName + abstractStructure.getPath()));
        } catch (Exception e) {
            System.out.println("Error occured while opening the given file: " + e.getMessage());
        }

        return compilationUnit;
    }

    public void getCompilationUnits() {
        for(AbstractStructure abstractStructure : nodeHolder.getAllNodes()) {
            try {
                CompilationUnit compilationUnit = JavaParser.parse(new File(targetFolderName + abstractStructure.getPath()));
                compilationUnit.getNodesByType(ClassOrInterfaceDeclaration.class).stream().
                        filter(f -> f.getModifiers().contains(Modifier.PUBLIC)).
                        forEach(f -> System.out.println("Class Name: " + f.getNameAsString()));
            } catch (Exception e) {
                System.out.println("Error occured while opening the given file: " + e.getMessage());
            }
        }
    }

    /**
     * This methods lists all the .java files and their names in a given directory
     *
     * @param projectDir
     */
    public void listNodes(File projectDir) {
        List<AbstractStructure> nodes = new ArrayList<AbstractStructure>();

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            try {
                /*** This visitor finds and lists all the classes and interfaces in a given directory ***/
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                        super.visit(n, arg);
                        AbstractStructure abstractStructure = new AbstractStructure(n.getNameAsString(), path);
                        nodes.add(abstractStructure);
                    }
                }.visit(JavaParser.parse(file), null);

                /*** This visitor finds and lists all the enumerations in a given directory ***/
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(EnumDeclaration m, Object arg) {
                        super.visit(m, arg);
                        AbstractStructure abstractStructure = new AbstractStructure(m.getNameAsString(), path);
                        nodes.add(abstractStructure);
                    }
                }.visit(JavaParser.parse(file), null);
            } catch (IOException e) {
                new IOException(e);
            }
        }).explore(projectDir);

        nodeHolder.setNodes(nodes);
    }

    /**
     * This method distinguishes classes, interfaces, and enumeration in a given node list
     *
     * @param
     */
    public void nodeRecognizer() {
        for(AbstractStructure abstractStructure : nodeHolder.getAllNodes()) {
            System.out.println(abstractStructure.getName() + " --->" + abstractStructure.getPath());

            try {
                CompilationUnit compilationUnit =  getCompilationUnit(abstractStructure);
                compilationUnit.getNodesByType(ClassOrInterfaceDeclaration.class).stream().filter(c -> c.isInterface()).forEach(c -> {
                    abstractStructure.setAccessModifier(castStringToModifier(c.getModifiers().toString()));
                    System.out.println("Interface: " + c.getNameAsString());
                    System.out.println("Modifier" + c.getModifiers().toString());
                    System.out.println("--------");
                });

                compilationUnit.getNodesByType(ClassOrInterfaceDeclaration.class).stream().filter(c -> !c.isInterface()).forEach(c -> {
                    abstractStructure.setAccessModifier(castStringToModifier(c.getModifiers().toString()));
                    System.out.println("Class: " + c.getNameAsString());
                    System.out.println("Modifier" + c.getModifiers().toString());
                    System.out.println("--------");
                });

                compilationUnit.getNodesByType(EnumDeclaration.class).stream().forEach(c -> {
                    abstractStructure.setAccessModifier(castStringToModifier(c.getModifiers().toString()));
                    System.out.println("Enumeration: " + c.getNameAsString());
                    System.out.println("Modifier" + c.getModifiers().toString());
                });
            } catch (Exception e) {
                System.out.println("Error occured while opening the given file: " + e.getMessage());
            }
        }
    }

    public static void statementsByLine(File projectDir) {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            System.out.println(path);
            System.out.println(Strings.repeat("=", path.length()));
            try {
                new NodeIterator(new NodeIterator.NodeHandler() {
                    @Override
                    public boolean handle(Node node) {
                        System.out.println("Parent Node" + node.getParentNode());
                        System.out.println("Parent Node for Children" + node.getParentNodeForChildren());
                        return true;
                    }
                }).explore(JavaParser.parse(file));
                System.out.println(); // empty line
            } catch (IOException e) {
                new RuntimeException(e);
            }
        }).explore(projectDir);
    }

    public static Modifier castStringToModifier(String modifier) {
        Modifier castModifier;

        switch (modifier) {
            case "[PUBLIC]":
                castModifier = Modifier.PUBLIC;
                break;
            case "[PRIVATE]":
                castModifier = Modifier.PRIVATE;
                break;
            case "[PROTECTED]":
                castModifier = Modifier.PROTECTED;
                break;
            case "[ABSTRACT]":
                castModifier = Modifier.ABSTRACT;
                break;
            case "[STATIC]":
                castModifier = Modifier.STATIC;
                break;
            case "[FINAL]":
                castModifier = Modifier.FINAL;
                break;
            default:
                castModifier = Modifier.SYNCHRONIZED;
                break;
        }

        return castModifier;
    }
}
