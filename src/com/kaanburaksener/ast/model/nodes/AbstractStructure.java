package com.kaanburaksener.ast.model.nodes;

import com.github.javaparser.ast.Modifier;

/**
 * Created by kaanburaksener on 16/02/17.
 */
public class AbstractStructure {
    private int id;
    private Modifier accessModifier;
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

    public Modifier getAccessModifier() {
        return accessModifier;
    }

    public void setAccessModifier(Modifier accessModifier) {
        this.accessModifier = accessModifier;
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
        System.out.println("modifier:" + accessModifier + ", name: " + name + ", path: " + path);
    }
}
