package com.kaanburaksener.ast.util;

import com.github.javaparser.JavaParser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.visitor.ModifierVisitor;

import com.kaanburaksener.ast.controller.ASTNodeController;
import com.kaanburaksener.ast.model.AttributeStructure;
import com.kaanburaksener.ast.model.MethodStructure;
import com.kaanburaksener.ast.model.ParameterStructure;
import com.kaanburaksener.ast.model.nodes.AbstractStructure;
import com.kaanburaksener.ast.model.nodes.ClassStructure;
import com.kaanburaksener.ast.model.nodes.EnumerationStructure;
import com.kaanburaksener.ast.model.nodes.InterfaceStructure;

import com.kaanburaksener.octoUML.src.controller.AbstractDiagramController;
import com.kaanburaksener.octoUML.src.model.nodes.AbstractNode;
import com.kaanburaksener.octoUML.src.view.BubbleView;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by kaanburaksener on 05/05/17.
 *
 * This class handles any changes made on NodeView and projects it to the related source code.
 */
public class NodeViewParser {
    private AbstractStructure existingAbstractStructure;
    private CompilationUnit compilationUnit;
    private AbstractNode refNode;
    private AbstractDiagramController diagramController;
    private List<MethodStructure> methods;
    private List<AttributeStructure> attributes;
    private List<String> values;
    private String nodeViewMethods;
    private String nodeViewAttributes;
    private String nodeViewValues;
    private List<MethodStructure> methodsToBeDeleted;
    private List<MethodStructure> methodsToBeAdded;
    private List<AttributeStructure> attributesToBeDeleted;
    private List<AttributeStructure> attributesToBeAdded;
    private List<String> valuesToBeDeleted;
    private List<String> valuesToBeAdded;

    public NodeViewParser(String nodeViewMethods, String nodeViewAttributes, AbstractNode refNode, ASTNodeController astNodeController) {
        this.nodeViewMethods = nodeViewMethods;
        this.nodeViewAttributes = nodeViewAttributes;
        this.refNode = refNode;
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

    public NodeViewParser(String nodeViewValues, AbstractNode refNode, ASTNodeController astNodeController) {
        this.nodeViewValues = nodeViewValues;
        this.refNode = refNode;
        this.diagramController = astNodeController.getDiagramController();
        this.existingAbstractStructure = refNode.getRefExistingNode();
        this.compilationUnit = existingAbstractStructure.getCompilationUnit();
        this.values = new ArrayList<>();
        this.valuesToBeDeleted = new ArrayList<>();
        this.valuesToBeAdded = new ArrayList<>();
    }

    public void projectChangesInNodeView() {
        this.existingAbstractStructure = refNode.getRefExistingNode();
        if(existingAbstractStructure instanceof ClassStructure || existingAbstractStructure instanceof InterfaceStructure) {
            if(nodeViewAttributes != null && !nodeViewAttributes.equals("")) {
                loadNodeViewAttributes();
                checkAnyNewAttribute();
            }
            if(nodeViewMethods != null && !nodeViewMethods.equals("")) {
                loadNodeViewMethods();
                checkAnyNewMethod();
            }
            updateBubble();
        } else if(existingAbstractStructure instanceof EnumerationStructure) {
            if(nodeViewValues != null && !nodeViewValues.equals("")) {
                loadNodeViewValues();
                checkAnyNewValue();
            }
            updateBubble();
        }
    }

    /**
     * This method lists all the methods in a given class or interface node view.
     */
    private void loadNodeViewMethods() {
        if(nodeViewMethods != null && !nodeViewMethods.equals("")) {
            final String NEW_LINE = System.getProperty("line.separator");
            if(nodeViewMethods.contains(NEW_LINE)) { //It means more than one methods
                String methods[] = nodeViewMethods.split("[\\r\\n]+");
                for(String method : methods) {
                    if(!method.equals("")) {
                        String sign = "" + method.charAt(0);
                        String name = method.substring(1, method.indexOf('(')).trim();
                        String parameters = method.substring(method.indexOf('(') + 1, method.indexOf(')')).trim();
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
                }
            } else {
                String sign = "" + nodeViewMethods.charAt(0);
                String name = nodeViewMethods.substring(1, nodeViewMethods.indexOf('(')).trim();
                String parameters = nodeViewMethods.substring(nodeViewMethods.indexOf('(') + 1, nodeViewMethods.indexOf(')')).trim();

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

                String returnType = nodeViewMethods.substring(nodeViewMethods.indexOf(')')).trim();
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
        }
    }

    /**
     * This attributes lists all the methods in a given class or interface node view.
     */
    private void loadNodeViewAttributes() {
        if(nodeViewAttributes != null && !nodeViewAttributes.equals("")) {
            final String NEW_LINE = System.getProperty("line.separator");
            if(nodeViewAttributes.contains(NEW_LINE)) { //It means more than one attributes
                String attributes[] = nodeViewAttributes.split("[\\r\\n]+");
                for(String attribute : attributes) {
                    if(!attribute.equals("")) {
                        String sign = "" + attribute.charAt(0);
                        String parts[] = attribute.substring(1).split(Pattern.quote(":"));
                        String name = parts[0].trim();
                        String dataType = parts[1].trim();

                        AttributeStructure attr = new AttributeStructure(getAccessModifier(sign), dataType, name);
                        this.attributes.add(attr);
                    }
                }
            } else {
                String sign = "" + nodeViewAttributes.charAt(0);
                String parts[] = nodeViewAttributes.substring(1).split(Pattern.quote(":"));
                String name = parts[0].trim();
                String dataType = parts[1].trim();

                AttributeStructure attr = new AttributeStructure(getAccessModifier(sign), dataType, name);
                this.attributes.add(attr);
            }
        }
    }

    /**
     * This method lists all the values in a given enumeration node view.
     */
    private void loadNodeViewValues() {
        if(nodeViewValues != null && !nodeViewValues.equals("")) {
            final String NEW_LINE = System.getProperty("line.separator");
            if(nodeViewValues.contains(NEW_LINE)) { //It means more than one attributes
                String values[] = nodeViewValues.split("[\\r\\n]+");
                for(String value : values) {
                    this.values.add(value.trim());
                }
            } else {
                String value = "" + nodeViewValues.trim();
                this.values.add(value);
            }
        }
    }

    /**
     * It checks any new method to add and the existing method to delete.
     */
    private void checkAnyNewMethod() {
        List<MethodStructure> existingMethods = new ArrayList<>(((ClassStructure)existingAbstractStructure).getAllMethods());
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

        deleteMethodsFromCompilationUnit();
        addMethodsToCompilationUnit();
    }

    /**
     * It checks any new attribute to add and the existing attribute to delete.
     */
    private void checkAnyNewAttribute() {
        List<AttributeStructure> existingAttributes = new ArrayList<>(((ClassStructure)existingAbstractStructure).getAllAttributes());
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

        deleteAttributesFromCompilationUnit();
        addAttributesToCompilationUnit();
    }

    /**
     * It checks any new value to add and the existing values to delete.
     */
    private void checkAnyNewValue() {
        List<String> existingValues = new ArrayList<>(((EnumerationStructure)existingAbstractStructure).getAllValues());
        for(int i = 0; i < existingValues.size(); i++) {
            boolean valueIsFound = false;
            String existingValue = existingValues.get(i);

            for(int j = 0; j < values.size(); j++) {
                String value = values.get(j);
                if(existingValue.equals(value)) {
                    valueIsFound = true;
                }
            }

            if(!valueIsFound) {
                valuesToBeDeleted.add(existingValue);
            }
        }

        for(int i = 0; i < values.size(); i++) {
            boolean valueIsFound = false;
            String value = values.get(i);

            for(int j = 0; j < existingValues.size(); j++) {
                String existingValue = existingValues.get(j);
                if(value.equals(existingValue)) {
                    valueIsFound = true;
                }
            }

            if(!valueIsFound) {
                valuesToBeAdded.add(value);
            }
        }

        deleteValuesFromCompilationUnit();
        addValuesToCompilationUnit();
    }

    /**
     * It adds all the methods need to be added to the existing compilation unit.
     */
    private void addMethodsToCompilationUnit() {
        if(methodsToBeAdded.size() > 0) {
            ClassOrInterfaceDeclaration type = getFileType();

            methodsToBeAdded.stream().forEach(method -> {
                EnumSet<Modifier> modifiers = null;
                for (Modifier modifier : method.getAccessModifiers()) {
                    modifiers = EnumSet.of(modifier);
                }

                MethodDeclaration md = new MethodDeclaration();
                md.setName(method.getName());
                md.setModifiers(modifiers);

                if (method.getReturnType().equals("void")) {
                    md.setType(new VoidType());
                } else {
                    md.setType(JavaParser.parseType(method.getReturnType()));
                }

                if (method.getParameters().size() > 0) {
                    method.getParameters().stream().forEach(p -> {
                        Parameter param = new Parameter(JavaParser.parseType(p.getDataType()), p.getName());
                        md.addParameter(param);
                    });
                }

                type.addMember(md);
            });
        }
    }

    /**
     * It adds all the attributes need to be added to the existing compilation unit.
     */
    private void addAttributesToCompilationUnit() {
        if(attributesToBeAdded.size() > 0) {
            ClassOrInterfaceDeclaration type = getFileType();

            attributesToBeAdded.stream().forEach(attribute -> {
                EnumSet<Modifier> modifiers = null;
                for (Modifier modifier : attribute.getAccessModifiers()) {
                    modifiers = EnumSet.of(modifier);
                }

                VariableDeclarator vd = new VariableDeclarator(JavaParser.parseType(attribute.getDataType()), attribute.getName());
                FieldDeclaration fd = new FieldDeclaration();
                fd.setModifiers(modifiers);
                fd.addVariable(vd);

                type.addMember(fd);
            });
        }
    }

    /**
     * It adds all the values need to be added to the existing compilation unit.
     */
    private void addValuesToCompilationUnit() {
        if(valuesToBeAdded.size() > 0) {
            EnumDeclaration type = compilationUnit.getEnumByName(existingAbstractStructure.getName()).get();

            valuesToBeAdded.stream().forEach(value -> {
                type.addEnumConstant(value);
            });
        }
    }

    private void deleteMethodsFromCompilationUnit() {
        if(methodsToBeDeleted.size() > 0) {
            for(MethodStructure methodStructure : methodsToBeDeleted) {
                compilationUnit.getChildNodesByType(ClassOrInterfaceDeclaration.class).stream().forEach(m -> {
                    new MethodVisitor(methodStructure).visit(m,null);
                });
            }
        }
    }

    private void deleteAttributesFromCompilationUnit() {
        if(attributesToBeDeleted.size() > 0) {
            for (AttributeStructure attributeStructure : attributesToBeDeleted) {
                compilationUnit.getChildNodesByType(ClassOrInterfaceDeclaration.class).stream().forEach(a -> {
                    new AttributeVisitor(attributeStructure).visit(a, null);
                });
            }
        }
    }

    private void deleteValuesFromCompilationUnit() {
        if(valuesToBeDeleted.size() > 0) {
            for (String value : valuesToBeDeleted) {
                compilationUnit.getChildNodesByType(EnumDeclaration.class).stream().forEach(v -> {
                    new ValueVisitor(value).visit(v, null);
                });
            }
        }
    }

    /***
     * Simple visitor implementation for visiting MethodDeclaration nodes to find methods need to be deleted.
     */
    private static class MethodVisitor extends ModifierVisitor<Void> {
        private MethodStructure method;

        public MethodVisitor(MethodStructure method) {
            this.method = method;
        }

        @Override
        public MethodDeclaration visit(MethodDeclaration declarator, Void arg) {
            super.visit(declarator, arg);

            if (declarator.getNameAsString().equals(method.getName()) && declarator.getType().toString().equals(method.getReturnType())) {
                if (method.getParameters().size() > 0) {
                    List<ParameterStructure> parameters = new ArrayList<>();
                    List<Parameter> unstructuredParameters = declarator.getParameters();
                    unstructuredParameters.forEach(up -> {
                        ParameterStructure parameter = new ParameterStructure(up.getType().toString(), up.getNameAsString());
                        parameters.add(parameter);
                    });
                    if (equalParameters(parameters, method.getParameters())) {
                        return null;
                    }
                } else {
                    return null;
                }
            }

            return declarator;
        }
    }

    /***
     * Simple visitor implementation for visiting AttributeDeclaration nodes to find attributes need to be deleted.
     */
    private static class AttributeVisitor extends ModifierVisitor<Void> {
        private AttributeStructure attribute;

        public AttributeVisitor(AttributeStructure attribute) {
            this.attribute = attribute;
        }

        @Override
        public VariableDeclarator visit(VariableDeclarator declarator, Void arg) {
            super.visit(declarator, arg);

            if(declarator.getNameAsString().equals(attribute.getName()) && declarator.getType().toString().equals(attribute.getDataType())) {
               return null;
            }

            return declarator;
        }
    }

    /***
     * Simple visitor implementation for visiting EnumConstantDeclaration nodes to find values need to be deleted.
     */
    private static class ValueVisitor extends ModifierVisitor<Void> {
        private String value;

        public ValueVisitor(String value) {
            this.value = value;
        }

        @Override
        public EnumConstantDeclaration visit(EnumConstantDeclaration declarator, Void arg) {
            super.visit(declarator, arg);

            if(declarator.getNameAsString().equals(value)) {
                return null;
            }

            return declarator;
        }
    }

    /***
     * Updates BubbleView and changes the new Compilation Unit with the old one.
     */
    private void updateBubble() {
        if(existingAbstractStructure instanceof ClassStructure || existingAbstractStructure instanceof InterfaceStructure) {
            ((ClassStructure)existingAbstractStructure).updateCompilationUnit(compilationUnit);
        } else if(existingAbstractStructure instanceof EnumerationStructure) {
            ((EnumerationStructure)existingAbstractStructure).updateCompilationUnit(compilationUnit);
        }

        BubbleView bubbleView = diagramController.findBubbleView(refNode);
        bubbleView.getRefNode().setSourceCodeText(compilationUnit.toString());
    }

    private ClassOrInterfaceDeclaration getFileType() {
        ClassOrInterfaceDeclaration type = null;

        if(existingAbstractStructure instanceof InterfaceStructure) {
            type = compilationUnit.getInterfaceByName(existingAbstractStructure.getName()).get();
        } else {
            type = compilationUnit.getClassByName(existingAbstractStructure.getName()).get();
        }

        return type;
    }

    /**
     * It checks whether the all parameters are equal or not.
     */
    private static boolean equalParameters(List<ParameterStructure> first, List<ParameterStructure> second){
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

    /**
     * It returns the access modifier of a given sign.
     */
    public static List<Modifier> getAccessModifier(String sign) {
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
}