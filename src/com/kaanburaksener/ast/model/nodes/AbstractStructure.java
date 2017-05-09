package com.kaanburaksener.ast.model.nodes;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;

import java.io.BufferedWriter;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;

/**
 * Created by kaanburaksener on 16/02/17.
 */
public class AbstractStructure {
    private static final double MAX_BUBBLE_HEIGHT = 475.0;
    private static final double MIN_BUBBLE_HEIGHT = 200.0;
    private final String targetFolderPath = "test-source-code";
    private static final String type = "ABSTRACT";
    private List<Modifier> accessModifiers;
    private String name;
    private String path;
    private CompilationUnit compilationUnit;

    public AbstractStructure(String name, String path) {
        this.name = name;
        this.path = path;
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

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    public void setCompilationUnit(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
        overwrite();
    }

    public void updateCompilationUnit(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
        overwrite();
    }

    public void overwrite() {
        try {
            Path javaFilePath = Paths.get(this.targetFolderPath + path);
            try (BufferedWriter writer = Files.newBufferedWriter(javaFilePath, StandardCharsets.UTF_8)) {
                writer.write(compilationUnit.toString());
            } catch (IOException x) {
                System.err.format("IOException: %s%n", x);
            }
        } catch (Exception e) {
            System.out.println("Error occured while opening the given file: " + e.getMessage());
        }
    }

    public double calculateHeight() {
        final double COEFFICIENT = 20.0;
        int totalNumberOfLine = compilationUnit.getRange().get().end.line;
        double total = totalNumberOfLine * COEFFICIENT;

        if(total > MAX_BUBBLE_HEIGHT || total == 1) { //After we create the compilation unit from scratch, somehow end of line returns as 1
            total = MAX_BUBBLE_HEIGHT;
        }

        if(total < MIN_BUBBLE_HEIGHT) {
            total = MIN_BUBBLE_HEIGHT;
        }

        return total;
    }

    public void printStructure() {
        System.out.print("Modifiers: ");
        accessModifiers.stream().forEach(am-> {
                System.out.print(am + " ");
        });
        System.out.println(", name: " + name + ", path: " + path);
        System.out.println();
        System.out.println(compilationUnit.toString());
        System.out.println();
    }
}
