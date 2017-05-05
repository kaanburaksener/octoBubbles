package com.kaanburaksener.ast.model;

import com.github.javaparser.ast.Modifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaanburaksener on 08/02/17.
 */
public class MethodStructure {
    private List<Modifier> accessModifiers;
    private String returnType;
    private String name;
    private List<ParameterStructure> parameters = new ArrayList<>();

    /**
     * @param accessModifiers
     * @param returnType
     * @param name
     *
     * This constructor should be used when a method doesn't take any parameter
     */
    public MethodStructure(List<Modifier> accessModifiers, String returnType, String name) {
        this.accessModifiers = accessModifiers;
        this.returnType = returnType;
        this.name = name;
    }

    /**
     * @param accessModifiers
     * @param returnType
     * @param name
     * @param parameters
     */
    public MethodStructure(List<Modifier> accessModifiers, String returnType, String name, List<ParameterStructure> parameters) {
        this.accessModifiers = accessModifiers;
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
    }

    public List<Modifier> getAccessModifiers() {
        return accessModifiers;
    }

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public List<ParameterStructure> getParameters() {
        return parameters;
    }

    public String castMethodToUMLNotation() {
        StringBuilder method = new StringBuilder();

        method.append(name + " ( ");

        if(parameters.size() > 0) {
            for(int i = 0; i < parameters.size() - 1; i++) {
                method.append(parameters.get(i).name + " : " + parameters.get(i).dataType + " , ");
            }
            method.append(parameters.get(parameters.size() - 1).name + " : " + parameters.get(parameters.size() - 1).dataType);
        }

        method.append(" )");

        if(!returnType.equals("void")) {
            method.append(" : " + returnType);
        }

        return method.toString();
    }

    public void printMethodDeclaration() {
        accessModifiers.stream().forEach(am -> {
            System.out.print(am + " ");
        });
        System.out.print(" " + returnType + " " + name + "(");
        parameters.stream().forEach(p -> {
            System.out.print(p.dataType + " " + p.name + ",");
        });
        System.out.print(")");
    }
}