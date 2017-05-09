package com.kaanburaksener.ast.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;

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

import java.util.List;

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
    private AbstractNode refNode;
    private AbstractDiagramController diagramController;
    private NodeParser nodeParser;

    public BubbleParser(CompilationUnit compilationUnit, AbstractNode refNode, ASTNodeController astNodeController) {
        this.refNode = refNode;
        this.diagramController = astNodeController.getDiagramController();
        this.existingAbstractStructure = refNode.getRefExistingNode();

        if(!compilationUnit.toString().equals(existingAbstractStructure.getCompilationUnit().toString())) { //The bubble is already created and its content is already projected to the nodeView
            this.nodeParser = astNodeController.getNodeParser();
            if(existingAbstractStructure instanceof ClassStructure || existingAbstractStructure instanceof InterfaceStructure) {
                ((ClassStructure)this.existingAbstractStructure).updateCompilationUnit(compilationUnit);
                nodeParser.loadNodeRelations(existingAbstractStructure); //Associations are cleaned during the update of the compilation unit so we need to reload them.
            } else if(refNode instanceof EnumerationNode) {
                ((EnumerationStructure)this.existingAbstractStructure).updateCompilationUnit(compilationUnit);
            }
        }
    }

    public void projectChangesInBubble() {
        if(existingAbstractStructure instanceof ClassStructure || existingAbstractStructure instanceof InterfaceStructure) {
            updateNodeView();
        } else if(refNode instanceof EnumerationNode) {
            updateEnumerationNodeView();
        }
    }

    /**
     * It updates the NodeView with according to the edited source code.
     */
    private synchronized void updateNodeView() {
        double newWidth = 0.0;
        final double COEFFICIENT_FOR_LETTER = 7.50;

        StringBuilder attributes = new StringBuilder();
        StringBuilder operations = new StringBuilder();

        List<AttributeStructure> allAttributes = ((ClassStructure)existingAbstractStructure).getAllAttributes();
        if(allAttributes.size() > 1) {
            for(int i = 0; i < allAttributes.size() - 1; i++) {
                AttributeStructure attributeStructure = allAttributes.get(i);
                attributes.append(getSignOfAccessModifier(attributeStructure.getAccessModifiers()) + " " + attributeStructure.castAttributeToUMLNotation() + "\r\n");
            }
            attributes.append(getSignOfAccessModifier(allAttributes.get(allAttributes.size() - 1).getAccessModifiers()) + " " + allAttributes.get(allAttributes.size() - 1).castAttributeToUMLNotation());
        } else if(allAttributes.size() == 1) {
            attributes.append(getSignOfAccessModifier(allAttributes.get(0).getAccessModifiers()) + " " + allAttributes.get(0).castAttributeToUMLNotation());
        }

        List<MethodStructure> allMethods = ((ClassStructure)existingAbstractStructure).getAllMethods();
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

        updateClassNodeAssociations();
    }

    /**
     * It updates the EnumerationNodeView with according to the edited source code.
     */
    private synchronized void updateEnumerationNodeView() {
        StringBuilder values = new StringBuilder();

        List<String> allValues = ((EnumerationStructure)existingAbstractStructure).getAllValues();
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
    private synchronized void updateClassNodeAssociations() {
        AbstractNodeView startNodeView = diagramController.findNodeView(refNode);
        diagramController.cleanNodeEdges(startNodeView);

        List<AssociationStructure> allAssociations = ((ClassStructure)existingAbstractStructure).getAllAssociations();
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
}