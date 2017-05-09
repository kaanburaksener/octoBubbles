package com.kaanburaksener.ast.model.nodes;

import com.github.javaparser.ast.CompilationUnit;

import com.kaanburaksener.ast.util.NodeParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaanburaksener on 16/02/17.
 */
public class EnumerationStructure extends AbstractStructure {
    private static final String type = "ENUM";
    private List<String> values;

    /**
     * @param name
     * @param path
     */
    public EnumerationStructure(String name, String path) {
        super(name, path);
        this.values = new ArrayList<>();
    }

    public List<String> getAllValues() {
        return values;
    }

    public void addValue(String value) {
        values.add(value);
    }

    public void removeValue(String value) {
        values.remove(value);
    }

    public String getType(){
        return type;
    }

    public void updateCompilationUnit(CompilationUnit compilationUnit) {
        super.updateCompilationUnit(compilationUnit);
        clearEnumerationStructureMembers();
        reloadEnumerationStructureMembers();
    }

    public void clearEnumerationStructureMembers() {
        values.clear();
    }

    public void reloadEnumerationStructureMembers() {
        NodeParser.loadNodeAttributesOrValues(this);
    }

    @Override
    public void printStructure() {
        super.printStructure();
        this.printValues();
    }

    public void printValues() {
        if(values.size() > 0) {
            values.stream().forEach(v -> {
                System.out.println(v);
            });
        }
    }
}