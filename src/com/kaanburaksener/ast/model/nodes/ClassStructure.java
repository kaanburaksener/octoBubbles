package com.kaanburaksener.ast.model.nodes;

import com.kaanburaksener.ast.model.AttributeStructure;
import com.kaanburaksener.ast.model.MethodStructure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaanburaksener on 08/02/17.
 */
public class ClassStructure extends AbstractStructure {
    private static final String type = "CLASS";
    private List<MethodStructure> methods;
    private List<AttributeStructure> attributes;

    /**
     * @param name
     * @param path
     */
    public ClassStructure(String name, String path) {
        super(name, path);
        this.methods = new ArrayList<MethodStructure>();
        this.attributes = new ArrayList<AttributeStructure>();
    }

    public List<AttributeStructure> getAllAttributes() {
        return attributes;
    }

    public List<MethodStructure> getAllMethods() {
        return methods;
    }

    public void addMethod(MethodStructure method) {
        methods.add(method);
    }

    public void removeMethod(MethodStructure method) {
        methods.remove(method);
    }

    public void addAttribute(AttributeStructure attribute) {
        attributes.add(attribute);
    }

    public void removeAttribute(AttributeStructure attribute) {
        attributes.remove(attribute);
    }

    public String getType(){
        return type;
    }

    @Override
    public void printStructure() {
        super.printStructure();
        this.printMethods();
        this.printAttributes();
    }

    public void printMethods() {
        methods.stream().forEach(m -> {
            m.printMethodDeclaration();
            System.out.println();
        });
    }

    public void printAttributes() {
        attributes.stream().forEach(a -> {
            a.printAttributeDeclaration();
            System.out.println();
        });
    }
}