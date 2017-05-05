package com.kaanburaksener.ast.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import com.kaanburaksener.ast.controller.ASTNodeController;
import com.kaanburaksener.ast.model.*;
import com.kaanburaksener.ast.model.nodes.AbstractStructure;
import com.kaanburaksener.ast.model.nodes.ClassStructure;
import com.kaanburaksener.ast.model.nodes.EnumerationStructure;
import com.kaanburaksener.ast.model.nodes.InterfaceStructure;

import com.kaanburaksener.octoUML.src.controller.AbstractDiagramController;
import com.kaanburaksener.octoUML.src.model.edges.*;
import com.kaanburaksener.octoUML.src.model.nodes.AbstractNode;
import com.kaanburaksener.octoUML.src.model.nodes.ClassNode;
import com.kaanburaksener.octoUML.src.model.nodes.EnumerationNode;
import com.kaanburaksener.octoUML.src.util.commands.*;
import com.kaanburaksener.octoUML.src.view.nodes.AbstractNodeView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kaanburaksener on 03/05/17.
 *
 * This class projects any changes in the source code of bubble to the UML diagram.
 */
public class BubbleParser {
    private static final double CLASS_MIN_WIDTH = 120;
    private static final double CLASS_MIN_HEIGHT = 120.0;
    private static final double LINE_HEIGHT_CLASS = 20.0;
    private static final double LINE_HEIGHT_ENUM = 22.50;
    private AbstractStructure existingAbstractStructure;
    private AbstractStructure newAbstractStructure;
    private CompilationUnit compilationUnit;
    private AbstractNode refNode;
    private NodeHolder nodeHolder;
    private AbstractDiagramController diagramController;

    public BubbleParser(CompilationUnit compilationUnit, AbstractNode refNode, ASTNodeController astNodeController) {
        this.compilationUnit = compilationUnit;
        this.refNode = refNode;
        this.nodeHolder = astNodeController.getNodeHolder();
        this.diagramController = astNodeController.getDiagramController();
    }

    public void projectChangesInBubble() {
        this.existingAbstractStructure = refNode.getRefExistingNode();
        if(existingAbstractStructure instanceof ClassStructure || existingAbstractStructure instanceof InterfaceStructure) {
            if(existingAbstractStructure instanceof ClassStructure) {
                newAbstractStructure = new ClassStructure(existingAbstractStructure.getName(), existingAbstractStructure.getPath());
            } else if(existingAbstractStructure instanceof InterfaceStructure) {
                newAbstractStructure = new InterfaceStructure(existingAbstractStructure.getName(), existingAbstractStructure.getPath());
            }
            newAbstractStructure.setCompilationUnit(compilationUnit);
            initializeBubblesModifiers();
            loadBubbleMethods();
            loadBubbleAttributes();
            loadBubbleRelations();
            newAbstractStructure.printStructure();
            updateNodeView();
        } else if(refNode instanceof EnumerationNode) {
            newAbstractStructure = new EnumerationStructure(existingAbstractStructure.getName(), existingAbstractStructure.getPath());
            newAbstractStructure.setCompilationUnit(compilationUnit);
            initializeBubblesModifiers();
            loadBubbleValues();
            updateEnumerationNodeView();
        }
    }

    /**
     * This method lists all the methods in a given class or interface.
     */
    private void loadBubbleMethods() {
        try {
            compilationUnit.getChildNodesByType(ClassOrInterfaceDeclaration.class).stream().forEach(c -> {
                new NodeParser.MethodVisitor(newAbstractStructure).visit(c,null);
            });
        } catch (Exception e) {
            System.out.println("Error occured while opening the given file: " + e.getMessage());
        }
    }

    /**
     * This method lists all the attributes in a given class or interface.
     */
    private void loadBubbleAttributes() {
        try {
            compilationUnit.getChildNodesByType(FieldDeclaration.class).stream().forEach(field -> {
                List<VariableDeclarator> variableDeclarators = field.getVariables();
                variableDeclarators.stream().forEach(vd -> {
                    AttributeStructure attributeStructure;

                    List<Modifier> modifiers = NodeParser.castStringToModifiers(field.getModifiers().toString());
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

                    ((ClassStructure)newAbstractStructure).addAttribute(attributeStructure);
                });
            });
        } catch (Exception e) {
            System.out.println("Error occured while opening the given file: " + e.getMessage());
        }
    }

    /**
     * This method lists all the values in a given enumeration.
     */
    private void loadBubbleValues() {
        try {
            compilationUnit.getChildNodesByType(EnumDeclaration.class).stream().forEach(enumeration -> {
                List<EnumConstantDeclaration> enumValues = enumeration.getEntries();
                enumValues.stream().forEach(ev -> {
                    ((EnumerationStructure)newAbstractStructure).addValue(ev.getNameAsString());
                });
            });
        } catch (Exception e) {
            System.out.println("Error occured while opening the given file: " + e.getMessage());
        }
    }

    /**
     * This methods lists the relations (extends, implements) between classes and interfaces.
     */
    private void loadBubbleRelations() {
        try {
            compilationUnit.getChildNodesByType(ClassOrInterfaceDeclaration.class).stream().forEach(c -> {
                List<ClassOrInterfaceType> implementsList = c.getImplementedTypes();
                List<ClassOrInterfaceType> extendsList = c.getExtendedTypes();

                implementsList.stream().forEach(impl -> {
                    ((ClassStructure)newAbstractStructure).addAssociation(AssociationType.IMPLEMENTATION, impl.getNameAsString());
                });
                extendsList.stream().forEach(extd -> {
                    ((ClassStructure)newAbstractStructure).addAssociation(AssociationType.INHERITENCE, extd.getNameAsString());
                });
            });

            List<String> bodyOfConstructors = new ArrayList<String>();
            compilationUnit.getChildNodesByType(ConstructorDeclaration.class).stream().forEach(cnstr -> {
                bodyOfConstructors.add(cnstr.getBody().toString());
            });

            if(bodyOfConstructors.size() > 0) {// A class might have multiple constructors
                checkCompositionAssociation(newAbstractStructure, bodyOfConstructors);
            } else {// If there is no constructor, there is no possibility to have a composition association
                checkAggregationAssociation();
            }
        } catch (Exception e) {
            System.out.println("Error occured while opening the given file: " + e.getMessage());
        }
    }

    /**
     * This methods lists the relations (aggregation) between classes.
     */
    private void checkAggregationAssociation() {
        ((ClassStructure)newAbstractStructure).getAllAttributes().stream().forEach(a -> {
            for(AbstractStructure node : nodeHolder.getAllNodes()) {
                if(!node.equals(existingAbstractStructure)) {
                    if (node.getName().equals(a.getDataType())) {
                        ((ClassStructure) newAbstractStructure).addAssociation(AssociationType.AGGREGATION, node.getName());
                    }
                }
            }
        });
    }

    /**
     * This methods lists the relations (aggregation, composition) between classes.
     */
    private void checkCompositionAssociation(AbstractStructure abstractStructure, List<String> bodyOfConstructors) {
        boolean isFound = false;

        for(AttributeStructure attribute : ((ClassStructure)abstractStructure).getAllAttributes()) {
            for(AbstractStructure node : nodeHolder.getAllNodes()) {
                if(!node.equals(existingAbstractStructure)) {
                    if(node.getName().equals(attribute.getDataType())) {
                        for(String body : bodyOfConstructors) {
                            if(body.contains(attribute.getName())) {
                                String regex = "( (?:this\\.)?" + attribute.getName() + "= | " + "(?:this\\.)?" + attribute.getName() + " = )";

                                Pattern p = Pattern.compile(regex);
                                Matcher m = p.matcher(body);

                                if(m.find()) {
                                    ((ClassStructure)abstractStructure).addAssociation(AssociationType.COMPOSITION, node.getName());
                                    isFound = true;
                                }
                            }
                        }

                        if(!isFound) {
                            ((ClassStructure)abstractStructure).addAssociation(AssociationType.AGGREGATION, node.getName());
                        }
                    }
                }
            }
        }
    }

    /**
     * This method sets the modifiers into the nodes
     */
    private void initializeBubblesModifiers() {
        try {
            compilationUnit.getChildNodesByType(ClassOrInterfaceDeclaration.class).stream().forEach(c -> {
                newAbstractStructure.setAccessModifiers(NodeParser.castStringToModifiers(c.getModifiers().toString()));
            });

            compilationUnit.getChildNodesByType(EnumDeclaration.class).stream().forEach(c -> {
                newAbstractStructure.setAccessModifiers(NodeParser.castStringToModifiers(c.getModifiers().toString()));
            });
        } catch (Exception e) {
            System.out.println("Error occured while opening the given file: " + e.getMessage());
        }
    }

    /**
     * It updates the NodeView with according to the edited source code.
     */
    private void updateNodeView() {
        double newWidth = 0.0;
        final double COEFFICIENT_FOR_LETTER = 7.50;

        StringBuilder attributes = new StringBuilder();
        StringBuilder operations = new StringBuilder();

        List<AttributeStructure> allAttributes = ((ClassStructure)newAbstractStructure).getAllAttributes();
        if(allAttributes.size() > 1) {
            for(int i = 0; i < allAttributes.size() - 1; i++) {
                AttributeStructure attributeStructure = allAttributes.get(i);
                attributes.append(getSignOfAccessModifier(attributeStructure.getAccessModifiers()) + " " + attributeStructure.castAttributeToUMLNotation() + "\r\n");
            }
            attributes.append(getSignOfAccessModifier(allAttributes.get(allAttributes.size() - 1).getAccessModifiers()) + " " + allAttributes.get(allAttributes.size() - 1).castAttributeToUMLNotation());
        } else if(allAttributes.size() == 1) {
            attributes.append(getSignOfAccessModifier(allAttributes.get(0).getAccessModifiers()) + " " + allAttributes.get(0).castAttributeToUMLNotation());
        }

        List<MethodStructure> allMethods = ((ClassStructure)newAbstractStructure).getAllMethods();
        if(allMethods.size() > 1) {
            newWidth = (allMethods.get(0).castMethodToUMLNotation().length()) * COEFFICIENT_FOR_LETTER;
            for(int i = 0; i < allMethods.size() - 1; i++) {
                double tempNewWidth = (allMethods.get(0).castMethodToUMLNotation().length()) * COEFFICIENT_FOR_LETTER;
                MethodStructure methodStructure = allMethods.get(i);
                operations.append(getSignOfAccessModifier(methodStructure.getAccessModifiers()) + " " + methodStructure.castMethodToUMLNotation() + "\r\n");

                if(tempNewWidth > newWidth) {
                    newWidth = tempNewWidth;
                }
            }
            operations.append(getSignOfAccessModifier(allMethods.get(allMethods.size() - 1).getAccessModifiers()) + " " + allMethods.get(allMethods.size() - 1).castMethodToUMLNotation());
        } else if(allMethods.size() == 1) {
            operations.append(getSignOfAccessModifier(allMethods.get(0).getAccessModifiers()) + " " + allMethods.get(0).castMethodToUMLNotation());
        }

        CompoundCommand command = new CompoundCommand();

        String attr = attributes.toString();
        command.add(new SetNodeAttributeCommand(((ClassNode)refNode), attr, attr));
        ((ClassNode)refNode).setAttributes(attr);

        String oprt = operations.toString();
        command.add(new SetNodeOperationsCommand(((ClassNode)refNode), oprt, oprt));
        ((ClassNode)refNode).setOperations(oprt);

        double newHeight = (allAttributes.size() * LINE_HEIGHT_CLASS) + (allMethods.size() * LINE_HEIGHT_CLASS);

        if(newHeight > CLASS_MIN_HEIGHT) {
            refNode.setHeight(newHeight);
        } else {
            refNode.setHeight(CLASS_MIN_HEIGHT);
        }

        if(newWidth > CLASS_MIN_WIDTH) {
            refNode.setWidth(newWidth);
        }

        System.out.println(refNode.getTitle() + " calculated height" + newHeight);

        updateClassNodeAssociations();
    }

    /**
     * It updates the EnumerationNodeView with according to the edited source code.
     */
    private void updateEnumerationNodeView() {
        StringBuilder values = new StringBuilder();

        List<String> allValues = ((EnumerationStructure)newAbstractStructure).getAllValues();
        if(allValues.size() > 1) {
            for (int i = 0; i < allValues.size() - 1; i++) {
                values.append(allValues.get(i) + "\r\n");
            }
            values.append(allValues.get(allValues.size() - 1));
        } else if (allValues.size() == 1) {
            values.append(allValues.get(0));
        }

        CompoundCommand command = new CompoundCommand();

        String vls = values.toString();
        command.add(new SetNodeValuesCommand(((EnumerationNode)refNode), vls, vls));
        ((EnumerationNode)refNode).setValues(vls);

        double newHeight = allValues.size() * LINE_HEIGHT_ENUM;
        if(newHeight > CLASS_MIN_HEIGHT) {
            refNode.setHeight(newHeight);
        }
    }

    /**
     * Updates the associations between Classes and Interfaces.
     */
    private void updateClassNodeAssociations() {
        AbstractNodeView startNodeView = diagramController.findNodeView(refNode);
        diagramController.cleanNodeEdges(startNodeView);

        List<AssociationStructure> allAssociations = ((ClassStructure)newAbstractStructure).getAllAssociations();
        if(allAssociations.size() > 0) {
            for (AssociationStructure associationStructure : allAssociations) {
                AbstractNode endNode = diagramController.findNode(associationStructure.getAssociatedClassName());
                if (endNode != null) {
                    AbstractNodeView endNodeView = diagramController.findNodeView(endNode);
                    AbstractEdge newEdge = null;
                    if (associationStructure.getType().equals(AssociationType.AGGREGATION)) {
                        newEdge = new AggregationEdge(refNode, endNode);
                    } else if (associationStructure.getType().equals(AssociationType.COMPOSITION)) {
                        newEdge = new CompositionEdge(refNode, endNode);
                    } else if (associationStructure.getType().equals(AssociationType.IMPLEMENTATION)) {
                        newEdge = new RealizationEdge(refNode, endNode);
                    } else if (associationStructure.getType().equals(AssociationType.INHERITENCE)) {
                        newEdge = new InheritanceEdge(refNode, endNode);
                    }
                    newEdge.setDirection(AbstractEdge.Direction.START_TO_END);
                    diagramController.createEdgeView(newEdge, startNodeView, endNodeView);
                }
            }
        }
    }

    /**
     * It returns the sign of a given access modifier
     */
    private String getSignOfAccessModifier(List<Modifier> modifiers) {
        String sign = "";
        Modifier modifier = modifiers.get(0);

        if(modifier.equals(Modifier.PRIVATE)) {
            sign = "-";
        } else if (modifier.equals(Modifier.PROTECTED)) {
            sign = "#";
        } else if (modifier.equals(Modifier.PUBLIC)) {
            sign = "+";
        }

        return sign;
    }

    public AbstractNode getUpdatedNode() {
        return refNode;
    }
}