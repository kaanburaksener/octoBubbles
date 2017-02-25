package com.kaanburaksener.ast.model.nodes;

import com.github.javaparser.ast.Modifier;

import java.util.List;

/**
 * Created by kaanburaksener on 16/02/17.
 */
public class AbstractStructure {
    private int id;
    private List<Modifier> accessModifiers; //It should be turned into List<Modifier>, Any node can have more than one modifiers!!!
    private String name;
    private String path;

    public AbstractStructure(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void printStructure() {
        System.out.print("modifiers: ");
        accessModifiers.stream().forEach(am-> {
                System.out.print(am + " ");
        });
        System.out.println(", name: " + name + ", path: " + path);
    }
}
