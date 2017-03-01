package com.kaanburaksener.ast.model;

import com.github.javaparser.ast.Modifier;

import java.util.List;

/**
 * Created by kaanburaksener on 08/02/17.
 */
public class AttributeStructure {
    private List<Modifier> accessModifiers;
    private ParameterStructure parameterStructure;
    private String initializer = "";

    /**
     * @param accessModifiers
     * @param dataType
     * @param name
     */
    public AttributeStructure(List<Modifier> accessModifiers, String dataType, String name) {
        this.accessModifiers = accessModifiers;
        this.parameterStructure = new ParameterStructure(dataType, name);
    }

    /**
     * @param accessModifiers
     * @param dataType
     * @param name
     * @param initializer
     */
    public AttributeStructure(List<Modifier> accessModifiers, String dataType, String name, String initializer) {
        this.accessModifiers = accessModifiers;
        this.parameterStructure = new ParameterStructure(dataType, name);
        this.initializer = initializer;
    }

    public String getDataType() {
        return parameterStructure.dataType;
    }

    public String getName() {
        return parameterStructure.name;
    }

    public void printAttributeDeclaration() {
        accessModifiers.stream().forEach(am -> {
            System.out.print(am + " ");
        });
        System.out.print(" " + parameterStructure.dataType + " " + parameterStructure.name);
        if(!initializer.isEmpty()) {
            System.out.print(" = " + initializer);
        }
        System.out.print(";");
    }
}