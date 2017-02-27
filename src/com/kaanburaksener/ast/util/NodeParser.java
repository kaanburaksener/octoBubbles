package com.kaanburaksener.ast.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
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

/***
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
            } catch (Exception e) {
                System.out.println("Error occured while opening the given file: " + e.getMessage());
            }
        }
    }

    /***
     * This methods lists all the .java files and their names in a given directory
     *
     * @param projectDir
     */
    public void loadNodes(File projectDir) {
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

    /***
     * This methods lists all the methods in a given class or interface
     */
    public void loadNodesMethods() {
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

    /***
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

                methodStructure = new MethodStructure(castStringToModifiers(n.getModifiers().toString()), n.getType().toString(), n.getNameAsString(), parameters);
            } else {
                methodStructure = new MethodStructure(castStringToModifiers(n.getModifiers().toString()), n.getType().toString(), n.getNameAsString());
            }

            ((ClassStructure)this.abstractStructure).addMethod(methodStructure);

            super.visit(n, arg);
        }
    }

    /***
     * This methods lists all the attributes in a given class or interface and all the values in a given enumeration
     */
    public void loadNodesAttributesOrValues() {
        for(AbstractStructure abstractStructure : nodeHolder.getAllNodes()) {
            if(abstractStructure instanceof ClassStructure || abstractStructure instanceof InterfaceStructure) {
                try {
                    CompilationUnit compilationUnit =  getCompilationUnit(abstractStructure);
                    compilationUnit.getNodesByType(FieldDeclaration.class).stream().forEach(field -> {
                        List<VariableDeclarator> variableDeclarators = field.getVariables();
                        variableDeclarators.stream().forEach(vd -> {
                            AttributeStructure attributeStructure;

                            List<Modifier> modifiers = castStringToModifiers(field.getModifiers().toString());
                            String type = vd.getType().toString();
                            String name = vd.getNameAsString();

                            if(vd.getInitializer().toString().equals("Optional.empty")) {
                                    attributeStructure = new AttributeStructure(modifiers, type, name);
                            } else {
                                String unstructuredInitializer = vd.getInitializer().toString();
                                String initializer = unstructuredInitializer.substring(unstructuredInitializer.indexOf("[") + 1, unstructuredInitializer.indexOf("]"));
                                initializer = initializer.substring(1,initializer.length()-1);
                                attributeStructure = new AttributeStructure(modifiers, type, name, initializer);
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

    /***
     * This methods lists the relations (extends, implements) between Classes and Interfaces
     */
    public void loadRelations() {
        for(AbstractStructure abstractStructure : nodeHolder.getAllNodes()) {
            if(abstractStructure instanceof ClassStructure || abstractStructure instanceof InterfaceStructure) {
                try {
                    CompilationUnit compilationUnit =  getCompilationUnit(abstractStructure);
                    compilationUnit.getNodesByType(ClassOrInterfaceDeclaration.class).stream().forEach(c -> {
                        List<ClassOrInterfaceType> implementsList = c.getImplementedTypes();
                        List<ClassOrInterfaceType> extendsList = c.getExtendedTypes();

                        implementsList.stream().forEach(impl -> {
                            ((ClassStructure)abstractStructure).addImplementsClass(impl.getNameAsString());
                        });
                        extendsList.stream().forEach(extd -> {
                            ((ClassStructure)abstractStructure).addExtendsClass(extd.getNameAsString());
                        });
                    });
                } catch (Exception e) {
                    System.out.println("Error occured while opening the given file: " + e.getMessage());
                }
            }
        }
    }

    /***
     * This method sets the modifiers into the nodes
     */
    public void initializeNodesModifiers() {
        for(AbstractStructure abstractStructure : nodeHolder.getAllNodes()) {
            try {
                CompilationUnit compilationUnit =  getCompilationUnit(abstractStructure);

                compilationUnit.getNodesByType(ClassOrInterfaceDeclaration.class).stream().forEach(c -> {
                    abstractStructure.setAccessModifiers(castStringToModifiers(c.getModifiers().toString()));
                });

                compilationUnit.getNodesByType(EnumDeclaration.class).stream().forEach(c -> {
                    abstractStructure.setAccessModifiers(castStringToModifiers(c.getModifiers().toString()));
                });
            } catch (Exception e) {
                System.out.println("Error occured while opening the given file: " + e.getMessage());
            }
        }
    }

    /***
     * @param modifiers
     * @return
     */
    public static List<Modifier> castStringToModifiers(String modifiers) {
        List<Modifier> castModifiers = new ArrayList<Modifier>();

        if(modifiers.toLowerCase().contains(",")) {
            String content = modifiers.substring(modifiers.indexOf("[") + 1, modifiers.indexOf("]"));
            String[] modifierList = content.split(",");

            for(String modifier : modifierList) {
                modifier = modifier.trim();
                modifier = "[" + modifier + "]";
                castModifiers.add(getModifier(modifier));
            }
        } else {
            castModifiers.add(getModifier(modifiers));
        }

        return castModifiers;
    }

    /***
     * This methods return the equivalent access modifier in Modifier Class Type
     * @param modifier
     * @return
     */
    public static Modifier getModifier(String modifier) {
        Modifier originalModifier = null;

        switch (modifier) {
            case "[PUBLIC]":
                originalModifier = Modifier.PUBLIC;
                break;
            case "[PRIVATE]":
                originalModifier = Modifier.PRIVATE;
                break;
            case "[PROTECTED]":
                originalModifier = Modifier.PROTECTED;
                break;
            case "[ABSTRACT]":
                originalModifier = Modifier.ABSTRACT;
                break;
            case "[STATIC]":
                originalModifier = Modifier.STATIC;
                break;
            case "[FINAL]":
                originalModifier = Modifier.FINAL;
                break;
            case "[SYNCHRONIZED]":
                originalModifier = Modifier.SYNCHRONIZED;
                break;
            case "[]":
                originalModifier = Modifier.PUBLIC;
                break;
            default:
                System.out.println("***** SOMETHING GOES WRONG!" + modifier);
                break;
        }

        return originalModifier;
    }

    /***
     * This method is used to control how accurately the source codes turned into structured node objects
     */
    public void testNodes() {
        nodeHolder.printAllNodes();
    }
}