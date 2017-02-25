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
    private List<ParameterStructure> parameters;

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
        this.parameters = new ArrayList<ParameterStructure>();
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
        this.parameters = new ArrayList<ParameterStructure>();
        this.parameters = parameters;
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