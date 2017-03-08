package com.kaanburaksener.ast.model.nodes;

import com.github.javaparser.ast.Modifier;

import java.util.List;

/**
 * Created by kaanburaksener on 16/02/17.
 */
public class AbstractStructure {
    private static final String type = "ABSTRACT";
    private String id;
    private List<Modifier> accessModifiers;
    private String name;
    private String path;

    public AbstractStructure(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Modifier> getAccessModifiers() {
        return accessModifiers;
    }

    public void setAccessModifiers(List<Modifier> accessModifiers) {
        this.accessModifiers = accessModifiers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType(){
        return type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void printStructure() {
        System.out.print("Modifiers: ");
        accessModifiers.stream().forEach(am-> {
                System.out.print(am + " ");
        });
        System.out.println(", name: " + name + ", path: " + path);
    }
}
