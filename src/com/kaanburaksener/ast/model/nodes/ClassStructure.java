package com.kaanburaksener.ast.model.nodes;

import com.github.javaparser.ast.CompilationUnit;

import com.kaanburaksener.ast.model.AssociationStructure;
import com.kaanburaksener.ast.model.AssociationType;
import com.kaanburaksener.ast.model.AttributeStructure;
import com.kaanburaksener.ast.model.MethodStructure;
import com.kaanburaksener.ast.util.NodeParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaanburaksener on 08/02/17.
 */
public class ClassStructure extends AbstractStructure {
    private static final String type = "CLASS";
    private List<MethodStructure> methods;
    private List<AttributeStructure> attributes;
    private List<AssociationStructure> associationList;

    /**
     * @param name
     * @param path
     */
    public ClassStructure(String name, String path) {
        super(name, path);
        this.methods = new ArrayList<>();
        this.attributes = new ArrayList<>();
        this.associationList = new ArrayList<>();
    }

    public void addAssociation(AssociationType type, String className) {
        associationList.add(new AssociationStructure(type,className));
    }

    public void removeAssociation(AssociationType type, String className) {
        associationList.remove(new AssociationStructure(type, className));
    }

    public void addAttribute(AttributeStructure attribute) {
        attributes.add(attribute);
    }

    public void removeAttribute(AttributeStructure attribute) {
        attributes.remove(attribute);
    }

    public void addMethod(MethodStructure method) {
        methods.add(method);
    }

    public void removeMethod(MethodStructure method) {
        methods.remove(method);
    }

    public List<AssociationStructure> getAllAssociations() {
        return associationList;
    }

    public List<AttributeStructure> getAllAttributes() {
        return attributes;
    }

    public List<MethodStructure> getAllMethods() {
        return methods;
    }

    public String getType(){
        return type;
    }

    public void updateCompilationUnit(CompilationUnit compilationUnit) {
        super.updateCompilationUnit(compilationUnit);
        clearClassStructureMembers();
        reloadClassStructureMembers();
    }

    public void clearClassStructureMembers() {
        methods.clear();
        attributes.clear();
        associationList.clear();
    }

    public void reloadClassStructureMembers() {
        NodeParser.loadNodeAttributesOrValues(this);
        NodeParser.loadNodeMethods(this);
    }

    @Override
    public void printStructure() {
        super.printStructure();
        this.printAssociations();
        this.printAttributes();
        this.printMethods();
    }

    public void printAssociations() {
        if(associationList.size() > 0) {
            associationList.stream().forEach(a -> {
                System.out.println(a.getAssociatedClassName() + " (" + a.getType() + "), ");
            });
        }
    }

    public void printAttributes() {
        if(attributes.size() > 0) {
            attributes.stream().forEach(a -> {
                a.printAttributeDeclaration();
                System.out.println();
            });
        }
    }

    public void printMethods() {
        if(methods.size() > 0) {
            methods.stream().forEach(m -> {
                m.printMethodDeclaration();
                System.out.println();
            });
        }
    }
}