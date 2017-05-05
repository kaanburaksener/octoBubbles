package com.kaanburaksener.ast.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;

import com.kaanburaksener.ast.controller.ASTNodeController;
import com.kaanburaksener.ast.model.AttributeStructure;
import com.kaanburaksener.ast.model.MethodStructure;
import com.kaanburaksener.ast.model.NodeHolder;
import com.kaanburaksener.ast.model.ParameterStructure;
import com.kaanburaksener.ast.model.nodes.AbstractStructure;
import com.kaanburaksener.ast.model.nodes.ClassStructure;
import com.kaanburaksener.ast.model.nodes.InterfaceStructure;

import com.kaanburaksener.octoUML.src.controller.AbstractDiagramController;
import com.kaanburaksener.octoUML.src.model.nodes.AbstractNode;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by kaanburaksener on 05/05/17.
 */
public class NodeViewParser {
    private AbstractStructure existingAbstractStructure;
    private AbstractStructure newAbstractStructure;
    private CompilationUnit compilationUnit;
    private AbstractNode refNode;
    private NodeHolder nodeHolder;
    private AbstractDiagramController diagramController;
    private List<MethodStructure> methods;
    private List<AttributeStructure> attributes;
    private String nodeViewMethods;
    private String nodeViewAttributes;
    private List<MethodStructure> methodsToBeDeleted;
    private List<MethodStructure> methodsToBeAdded;
    private List<AttributeStructure> attributesToBeDeleted;
    private List<AttributeStructure> attributesToBeAdded;

    public NodeViewParser(String nodeViewMethods, String nodeViewAttributes, AbstractNode refNode, ASTNodeController astNodeController) {
        this.nodeViewMethods = nodeViewMethods;
        this.nodeViewAttributes = nodeViewAttributes;
        this.refNode = refNode;
        this.nodeHolder = astNodeController.getNodeHolder();
        this.diagramController = astNodeController.getDiagramController();
        this.existingAbstractStructure = refNode.getRefExistingNode();
        this.compilationUnit = existingAbstractStructure.getCompilationUnit();
        this.methods = new ArrayList<>();
        this.attributes = new ArrayList<>();
        this.methodsToBeDeleted = new ArrayList<>();
        this.methodsToBeAdded = new ArrayList<>();
        this.attributesToBeDeleted = new ArrayList<>();
        this.attributesToBeAdded = new ArrayList<>();
    }

    public void projectChangesInNodeView() {
        this.existingAbstractStructure = refNode.getRefExistingNode();
        if(existingAbstractStructure instanceof ClassStructure || existingAbstractStructure instanceof InterfaceStructure) {
            if(existingAbstractStructure instanceof ClassStructure) {
                newAbstractStructure = new ClassStructure(existingAbstractStructure.getName(), existingAbstractStructure.getPath());
            } else if(existingAbstractStructure instanceof InterfaceStructure) {
                newAbstractStructure = new InterfaceStructure(existingAbstractStructure.getName(), existingAbstractStructure.getPath());
            }
            newAbstractStructure.setCompilationUnit(compilationUnit);
            loadBubbleViewMethods();
            loadBubbleViewAttributes();
            loadNodeViewMethods();
            loadNodeViewAttributes();
            checkAnyNewMethod();
            checkAnyNewAttribute();
        }
    }

    /**
     * This method lists all the methods in a given class or interface.
     */
    private void loadBubbleViewMethods() {
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
    private void loadBubbleViewAttributes() {
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
     * This method lists all the methods in a given class or interface node view.
     */
    private void loadNodeViewMethods() {
        if(nodeViewMethods != null) {
            String methods[] = nodeViewMethods.split("[\\r\\n]+");

            for(String method : methods) {
                String sign = "" + method.charAt(0);
                String name = method.substring(1, method.indexOf('(')).trim();
                String parameters = method.substring(method.indexOf('(') + 1, method.indexOf(')')).trim();

                System.out.println("Method name:" + name);
                System.out.println("Method parameters:" + parameters);

                List<ParameterStructure> prmtrs = new ArrayList<>();
                if(!parameters.equals("")) {//Function has parameter(s)
                    if(parameters.indexOf(",") != -1) {//More than one parameter
                        String params[] = parameters.split(Pattern.quote(","));
                        for (String param : params) {
                            String paramParts[] = param.split(Pattern.quote(":"));
                            String paramName = paramParts[0].trim();
                            String paramDataType = paramParts[1].trim();

                            ParameterStructure parameterStructure = new ParameterStructure(paramDataType, paramName);
                            prmtrs.add(parameterStructure);
                        }
                    } else {//Only one parameter
                        String paramParts[] = parameters.split(Pattern.quote(":"));
                        String paramName = paramParts[0].trim();
                        String paramDataType = paramParts[1].trim();
                        ParameterStructure parameterStructure = new ParameterStructure(paramDataType, paramName);
                        prmtrs.add(parameterStructure);
                    }
                }

                String returnType = method.substring(method.indexOf(')')).trim();
                if(returnType.indexOf(":") != -1) {//Function's return type is not VOID
                    returnType = returnType.substring(returnType.indexOf(':') + 1).trim();
                } else {
                    returnType = "void";
                }

                MethodStructure oprt;

                if(prmtrs.size() > 0) {//Function has parameter(s)
                    oprt = new MethodStructure(getAccessModifier(sign), returnType, name, prmtrs);
                } else {//Function does not have any parameter
                    oprt = new MethodStructure(getAccessModifier(sign), returnType, name);
                }

                this.methods.add(oprt);
            }

            System.out.println("Methods of NodeView");
            for(MethodStructure m : this.methods) {
                System.out.println();
                m.printMethodDeclaration();
            }
        }
    }

    /**
     * This attributes lists all the methods in a given class or interface node view.
     */
    private void loadNodeViewAttributes() {
        if(nodeViewAttributes != null) {
            String attributes[] = nodeViewAttributes.split("[\\r\\n]+");

            for(String attribute : attributes) {
                String sign = "" + attribute.charAt(0);
                String parts[] = attribute.substring(1).split(Pattern.quote(":"));
                String name = parts[0].trim();
                String dataType = parts[1].trim();

                AttributeStructure attr = new AttributeStructure(getAccessModifier(sign), dataType, name);
                this.attributes.add(attr);
            }

            System.out.println("Attributes of NodeView");
            for(AttributeStructure a : this.attributes) {
                System.out.println();
                a.printAttributeDeclaration();
            }
        }
    }

    private void checkAnyNewMethod() {
        List<MethodStructure> existingMethods = new ArrayList<>(((ClassStructure)newAbstractStructure).getAllMethods());

        for(int i = 0; i < existingMethods.size(); i++) {
            boolean methodIsFound = false;
            MethodStructure existingMethod = existingMethods.get(i);

            for(int j = 0; j < methods.size(); j++) {
                MethodStructure method = methods.get(j);
                if(existingMethod.getName().equals(method.getName()) && existingMethod.getReturnType().equals(method.getReturnType())) {
                    if(equalParameters(existingMethod.getParameters(), method.getParameters())) {
                        methodIsFound = true;
                    }
                }
            }

            if(!methodIsFound) {
                methodsToBeDeleted.add(existingMethod);
            }
        }

        for(int i = 0; i < methods.size(); i++) {
            boolean methodIsFound = false;
            MethodStructure method = methods.get(i);

            for(int j = 0; j < existingMethods.size(); j++) {
                MethodStructure existingMethod = existingMethods.get(j);
                if(method.getName().equals(existingMethod.getName()) && method.getReturnType().equals(existingMethod.getReturnType())) {
                    if(equalParameters(method.getParameters(), existingMethod.getParameters())) {
                        methodIsFound = true;
                    }
                }
            }

            if(!methodIsFound) {
                methodsToBeAdded.add(method);
            }
        }
    }

    private void checkAnyNewAttribute() {
        List<AttributeStructure> existingAttributes = new ArrayList<>(((ClassStructure)newAbstractStructure).getAllAttributes());

        for(int i = 0; i < existingAttributes.size(); i++) {
            boolean attributeIsFound = false;
            AttributeStructure existingAttribute = existingAttributes.get(i);

            for(int j = 0; j < attributes.size(); j++) {
                AttributeStructure attribute = attributes.get(j);
                if(existingAttribute.getName().equals(attribute.getName()) && existingAttribute.getDataType().equals(attribute.getDataType())) {
                    attributeIsFound = true;
                }
            }

            if(!attributeIsFound) {
                attributesToBeDeleted.add(existingAttribute);
            }
        }

        for(int i = 0; i < attributes.size(); i++) {
            boolean attributeIsFound = false;
            AttributeStructure attribute = attributes.get(i);

            for(int j = 0; j < existingAttributes.size(); j++) {
                AttributeStructure existingAttribute = existingAttributes.get(j);
                if(attribute.getName().equals(existingAttribute.getName()) && attribute.getDataType().equals(existingAttribute.getDataType())) {
                    attributeIsFound = true;
                }
            }

            if(!attributeIsFound) {
                attributesToBeAdded.add(attribute);
            }
        }
    }

    /**
     * It returns the access modifer of a given sign
     */
    private List<Modifier> getAccessModifier(String sign) {
        List<Modifier> modifiers = new ArrayList<>();

        Modifier modifier = null;

        if(sign.equals("-")) {
            modifier = Modifier.PRIVATE;
        } else if (sign.equals("#")) {
            modifier = Modifier.PROTECTED;
        } else if (sign.equals("+")) {
            modifier = Modifier.PUBLIC;
        }

        modifiers.add(modifier);

        return modifiers;
    }

    private boolean equalParameters(List<ParameterStructure> first, List<ParameterStructure> second){
        boolean result;

        if((first == null && second != null) || first != null && second == null || first.size() != second.size()) {
            result = false;
        } else {
            if (first == null && first == null){
                result = true;
            } else {
                boolean equal = true;

                for (int i = 0; i< first.size() && equal; i++) {
                    ParameterStructure ps1 = first.get(i);
                    ParameterStructure ps2 = second.get(i);

                    if(ps1.getName().equals(ps2.getName()) && ps1.getDataType().equals(ps2.getDataType())) {
                        continue;
                    } else {
                        equal = false;
                    }
                }

                result = equal;
            }
        }

        return result;
    }
}
