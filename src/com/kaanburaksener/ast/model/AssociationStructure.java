package com.kaanburaksener.ast.model;

/**
 * Created by kaanburaksener on 28/02/17.
 */
public class AssociationStructure {
    private AssociationType type;
    private String className;

    public AssociationStructure(AssociationType type, String className) {
        this.type = type;
        this.className = className;
    }

    public AssociationType getType() {
        return type;
    }

    public String getAssociatedClassName() {
        return className;
    }
}
