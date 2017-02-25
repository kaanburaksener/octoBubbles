package com.kaanburaksener.ast.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.Modifier;

import com.kaanburaksener.ast.model.AttributeStructure;
import com.kaanburaksener.ast.model.MethodStructure;
import com.kaanburaksener.ast.model.NodeHolder;
import com.kaanburaksener.ast.model.ParameterStructure;
import com.kaanburaksener.ast.model.nodes.AbstractStructure;
import com.kaanburaksener.ast.helper.DirExplorer;
import com.kaanburaksener.ast.model.nodes.ClassStructure;
import com.kaanburaksener.ast.model.nodes.EnumerationStructure;
import com.kaanburaksener.ast.model.nodes.InterfaceStructure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                        AbstractStructure abstractStructure;

                        if(!n.isInterface()) {
                            abstractStructure = new ClassStructure(n.getNameAsString(), path);
                        } else {
                            abstractStructure = new InterfaceStructure(n.getNameAsString(), path);
                        }

                        nodes.add(abstractStructure);
                    }
                }.visit(JavaParser.parse(file), null);

                /*** This visitor finds and lists all the enumerations in a given directory ***/
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(EnumDeclaration m, Object arg) {
                        super.visit(m, arg);
                        AbstractStructure abstractStructure = new EnumerationStructure(m.getNameAsString(), path);
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
     * This methods lists all the methods in a given class or interface
     */
    public void listMethods() {
        for(AbstractStructure abstractStructure : nodeHolder.getAllNodes()) {
            if(abstractStructure instanceof ClassStructure || abstractStructure instanceof InterfaceStructure) {
                try {
                    CompilationUnit compilationUnit =  getCompilationUnit(abstractStructure);
                    compilationUnit.getNodesByType(ClassOrInterfaceDeclaration.class).stream().forEach(c -> {
                        new MethodVisitor(abstractStructure).visit(c,null);
                    });
                } catch (Exception e) {
                    System.out.println("Error occured while opening the given file: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Simple visitor implementation for visiting MethodDeclaration nodes
     */
    private static class MethodVisitor extends VoidVisitorAdapter<Void> {
        private AbstractStructure abstractStructure;

        public MethodVisitor(AbstractStructure abstractStructure) {
            this.abstractStructure = abstractStructure;
        }

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            MethodStructure methodStructure;

            if(n.getParameters().size() > 0) {
                List<ParameterStructure> parameters = new ArrayList<ParameterStructure>();

                NodeList<Parameter> unstructuredParameters = n.getParameters();
                unstructuredParameters.forEach(up -> {
                    ParameterStructure parameter = new ParameterStructure(up.getType().toString(), up.getNameAsString());
                    parameters.add(parameter);
                });

                methodStructure = new MethodStructure(castStringToModifier(n.getModifiers().toString()), n.getType().toString(), n.getNameAsString(), parameters);
            } else {
                methodStructure = new MethodStructure(castStringToModifier(n.getModifiers().toString()), n.getType().toString(), n.getNameAsString());
            }

            ((ClassStructure)this.abstractStructure).addMethod(methodStructure);

            super.visit(n, arg);
        }
    }

    /**
     * This methods lists all the attributes in a given class or interface and all the values in a given enumeration
     */
    public void listAttributes() {
        for(AbstractStructure abstractStructure : nodeHolder.getAllNodes()) {
            if(abstractStructure instanceof ClassStructure || abstractStructure instanceof InterfaceStructure) {
                try {
                    CompilationUnit compilationUnit =  getCompilationUnit(abstractStructure);
                    compilationUnit.getNodesByType(FieldDeclaration.class).stream().forEach(field -> {
                        List<VariableDeclarator> variableDeclarators = field.getVariables();
                        variableDeclarators.stream().forEach(vd -> {
                            AttributeStructure attributeStructure;

                            Modifier modifier = castStringToModifier(field.getModifiers().toString());
                            String type = vd.getType().toString();
                            String name = vd.getNameAsString();

                            if(vd.getInitializer().toString().equals("Optional.empty")) {
                                    attributeStructure = new AttributeStructure(modifier, type, name);
                            } else {
                                String unstructuredInitializer = vd.getInitializer().toString();
                                String initializer = unstructuredInitializer.substring(unstructuredInitializer.indexOf("[") + 1, unstructuredInitializer.indexOf("]"));
                                initializer = initializer.substring(1,initializer.length()-1);
                                attributeStructure = new AttributeStructure(modifier, type, name, initializer);
                            }

                            ((ClassStructure)abstractStructure).addAttribute(attributeStructure);
                        });
                    });
                } catch (Exception e) {
                    System.out.println("Error occured while opening the given file: " + e.getMessage());
                }
            } else if(abstractStructure instanceof EnumerationStructure) {
                try {
                    CompilationUnit compilationUnit =  getCompilationUnit(abstractStructure);
                    compilationUnit.getNodesByType(EnumDeclaration.class).stream().forEach(enumeration -> {
                        List<EnumConstantDeclaration> enumValues = enumeration.getEntries();
                        enumValues.stream().forEach(ev -> {
                            ((EnumerationStructure)abstractStructure).addValue(ev.getNameAsString());
                        });
                    });
                } catch (Exception e) {
                    System.out.println("Error occured while opening the given file: " + e.getMessage());
                }
            }
        }
    }

    /**
     * This method sets the modifiers to the nodes
     */
    public void nodeModifierInitializer() {
        for(AbstractStructure abstractStructure : nodeHolder.getAllNodes()) {
            try {
                CompilationUnit compilationUnit =  getCompilationUnit(abstractStructure);

                compilationUnit.getNodesByType(ClassOrInterfaceDeclaration.class).stream().forEach(c -> {
                    abstractStructure.setAccessModifier(castStringToModifier(c.getModifiers().toString()));
                });

                compilationUnit.getNodesByType(EnumDeclaration.class).stream().forEach(c -> {
                    abstractStructure.setAccessModifier(castStringToModifier(c.getModifiers().toString()));
                });
            } catch (Exception e) {
                System.out.println("Error occured while opening the given file: " + e.getMessage());
            }
        }
    }

    /***
     * A Class, a method, or an attribute might have more than one modifiers so this method should be reviewed!!!
     * @param modifier
     * @return
     */
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
            case "[SYNCHRONIZED]":
                castModifier = Modifier.SYNCHRONIZED;
                break;
            case "[]":
                castModifier = Modifier.PUBLIC;
                break;
            default:
                castModifier = null;
                break;
        }

        return castModifier;
    }

    /***
     * This method is used to control how accurately the source codes turned into structured objects
     */
    public void testNodes() {
        nodeHolder.printAllNodes();
    }
}