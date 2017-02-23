package com.kaanburaksener.ast.model;

import com.github.javaparser.ast.Modifier;
import com.kaanburaksener.ast.model.ParameterStructure;

/**
 * Created by kaanburaksener on 08/02/17.
 */
public class AttributeStructure {
    private Modifier accessModifier;
    private ParameterStructure parameterStructure;

    /**
     * @param accessModifier
     * @param dataType
     * @param name
     */
    public AttributeStructure(Modifier accessModifier, String dataType, String name) {
        this.accessModifier = accessModifier;
        this.parameterStructure = new ParameterStructure(dataType, name);
    }
}