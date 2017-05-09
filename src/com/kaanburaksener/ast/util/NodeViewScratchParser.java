package com.kaanburaksener.ast.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.VoidType;

import com.kaanburaksener.ast.model.AttributeStructure;
import com.kaanburaksener.ast.model.MethodStructure;
import com.kaanburaksener.ast.model.ParameterStructure;
import com.kaanburaksener.ast.model.nodes.AbstractStructure;
import com.kaanburaksener.ast.model.nodes.ClassStructure;
import com.kaanburaksener.ast.model.nodes.EnumerationStructure;
import com.kaanburaksener.ast.model.nodes.InterfaceStructure;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by kaanburaksener on 08/05/17.
 */
public class NodeViewScratchParser {
    private AbstractStructure existingAbstractStructure;
    private CompilationUnit compilationUnit;
    private String nodeViewMethods;
    private String nodeViewAttributes;
    private String nodeViewValues;
    private List<MethodStructure> methodsToBeAdded;
    private List<AttributeStructure> attributesToBeAdded;
    private List<String> valuesToBeAdded;

    public NodeViewScratchParser(AbstractStructure abstractStructure, CompilationUnit compilationUnit, String nodeViewMethods, String nodeViewAttributes) {
        this.existingAbstractStructure = abstractStructure;
        this.nodeViewMethods = nodeViewMethods;
        this.nodeViewAttributes = nodeViewAttributes;
        this.compilationUnit = compilationUnit;
        this.methodsToBeAdded = new ArrayList<>();
        this.attributesToBeAdded = new ArrayList<>();
    }

    public NodeViewScratchParser(AbstractStructure abstractStructure, CompilationUnit compilationUnit, String nodeViewValues) {
        this.existingAbstractStructure = abstractStructure;
        this.nodeViewValues = nodeViewValues;
        this.compilationUnit = compilationUnit;
        this.valuesToBeAdded = new ArrayList<>();
    }

    public void projectChangesInNodeView() {
        if(nodeViewAttributes != null && !nodeViewAttributes.equals("")) {
            loadNodeViewAttributes();
            addAttributesToCompilationUnit();
        }

        if(nodeViewMethods != null && !nodeViewMethods.equals("")) {
            loadNodeViewMethods();
            addMethodsToCompilationUnit();
        }

        if(nodeViewValues != null && !nodeViewValues.equals("")) {
            loadNodeViewValues();
            addValuesToCompilationUnit();
        }

        existingAbstractStructure.setCompilationUnit(compilationUnit);
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
                            oprt = new MethodStructure(NodeViewParser.getAccessModifier(sign), returnType, name, prmtrs);
                        } else {//Function does not have any parameter
                            oprt = new MethodStructure(NodeViewParser.getAccessModifier(sign), returnType, name);
                        }

                        this.methodsToBeAdded.add(oprt);
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
                    oprt = new MethodStructure(NodeViewParser.getAccessModifier(sign), returnType, name, prmtrs);
                } else {//Function does not have any parameter
                    oprt = new MethodStructure(NodeViewParser.getAccessModifier(sign), returnType, name);
                }

                this.methodsToBeAdded.add(oprt);
            }
        }
    }

    /**
     * This method lists all the attributes in a given class or interface node view.
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

                        AttributeStructure attr = new AttributeStructure(NodeViewParser.getAccessModifier(sign), dataType, name);
                        this.attributesToBeAdded.add(attr);
                    }
                }
            } else {
                String sign = "" + nodeViewAttributes.charAt(0);
                String parts[] = nodeViewAttributes.substring(1).split(Pattern.quote(":"));
                String name = parts[0].trim();
                String dataType = parts[1].trim();

                AttributeStructure attr = new AttributeStructure(NodeViewParser.getAccessModifier(sign), dataType, name);
                this.attributesToBeAdded.add(attr);
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
                    this.valuesToBeAdded.add(value.trim());
                }
            } else {
                String value = "" + nodeViewValues.trim();
                this.valuesToBeAdded.add(value);
            }
        }
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
                ((ClassStructure)existingAbstractStructure).addMethod(method);
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
                ((ClassStructure)existingAbstractStructure).addAttribute(attribute);
            });
        }
    }

    /**
     * It returns the ClassOrInterfaceDeclaration according to the type of AbstractStructure object.
     */
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
     * It adds all the values need to be added to the existing compilation unit.
     */
    private void addValuesToCompilationUnit() {
        if(valuesToBeAdded.size() > 0) {
            EnumDeclaration type = compilationUnit.getEnumByName(existingAbstractStructure.getName()).get();

            valuesToBeAdded.stream().forEach(value -> {
                type.addEnumConstant(value);
                ((EnumerationStructure)existingAbstractStructure).addValue(value);
            });
        }
    }
}
