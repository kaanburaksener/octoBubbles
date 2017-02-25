package com.kaanburaksener.ast.model;

import com.github.javaparser.ast.Modifier;

/**
 * Created by kaanburaksener on 08/02/17.
 */
public class AttributeStructure {
    private Modifier accessModifier;
    private ParameterStructure parameterStructure;
    private String initializer = "";

    /**
     * @param accessModifier
     * @param dataType
     * @param name
     */
    public AttributeStructure(Modifier accessModifier, String dataType, String name) {
        this.accessModifier = accessModifier;
        this.parameterStructure = new ParameterStructure(dataType, name);
    }

    /**
     * @param accessModifier
     * @param dataType
     * @param name
     * @param initializer
     */
    public AttributeStructure(Modifier accessModifier, String dataType, String name, String initializer) {
        this.accessModifier = accessModifier;
        this.parameterStructure = new ParameterStructure(dataType, name);
        this.initializer = initializer;
    }

    public void printAttributeDeclaration() {
        System.out.print(accessModifier + " " + parameterStructure.dataType + " " + parameterStructure.name);
        if(!initializer.isEmpty()) {
            System.out.print(" = " + initializer);
        }
        System.out.print(";");
    }
}