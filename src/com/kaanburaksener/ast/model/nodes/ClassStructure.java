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
    private List<String> extendsList;
    private List<String> implementsList;

    /**
     * @param name
     * @param path
     */
    public ClassStructure(String name, String path) {
        super(name, path);
        this.methods = new ArrayList<MethodStructure>();
        this.attributes = new ArrayList<AttributeStructure>();
        this.extendsList = new ArrayList<String>();
        this.implementsList = new ArrayList<String>();
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

    public void addExtendsClass(String className) {
        extendsList.add(className);
    }

    public void removeExtendsClass(String className) {
        extendsList.remove(className);
    }

    public void addImplementsClass(String className) {
        implementsList.add(className);
    }

    public void removeImplementsClass(String className) {
        implementsList.remove(className);
    }

    public String getType(){
        return type;
    }

    @Override
    public void printStructure() {
        super.printStructure();
        this.printAttributes();
        this.printMethods();
        this.printExtends();
        this.printImplements();
    }

    public void printAttributes() {
        if(attributes.size() > 0) {
            attributes.stream().forEach(a -> {
                a.printAttributeDeclaration();
                System.out.println();
            });
        }
    }

    public void printExtends() {
        if(extendsList.size() > 0) {
            System.out.print("Extends -> ");

            extendsList.stream().forEach(e -> {
                System.out.println(e + ", ");
            });
        }
    }

    public void printImplements() {
        if(implementsList.size() > 0) {
            System.out.print("Implements -> ");

            implementsList.stream().forEach(i -> {
                System.out.println(i + ", ");
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