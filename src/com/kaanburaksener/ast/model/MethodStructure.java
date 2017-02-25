package com.kaanburaksener.ast.model;

import com.github.javaparser.ast.Modifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaanburaksener on 08/02/17.
 */
public class MethodStructure {
    private Modifier accessModifier;
    private String returnType;
    private String name;
    private List<ParameterStructure> parameters;

    /**
     * @param accessModifier
     * @param returnType
     * @param name
     *
     * This constructor should be used when a method doesn't take any parameter
     */
    public MethodStructure(Modifier accessModifier, String returnType, String name) {
        this.accessModifier = accessModifier;
        this.returnType = returnType;
        this.name = name;
        this.parameters = new ArrayList<ParameterStructure>();
    }

    /**
     * @param accessModifier
     * @param returnType
     * @param name
     * @param parameters
     */
    public MethodStructure(Modifier accessModifier, String returnType, String name, List<ParameterStructure> parameters) {
        this.accessModifier = accessModifier;
        this.returnType = returnType;
        this.name = name;
        this.parameters = new ArrayList<ParameterStructure>();
        this.parameters = parameters;
    }

    public void printMethodDeclaration() {
        System.out.print(accessModifier + " " + returnType + " " + name + "(");
        parameters.stream().forEach(p -> {
            System.out.print(p.dataType + " " + p.name + ",");
        });
        System.out.print(")");
    }
}