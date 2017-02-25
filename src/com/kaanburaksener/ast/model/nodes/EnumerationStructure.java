package com.kaanburaksener.ast.model.nodes;

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
        this.values = new ArrayList<String>();
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

    @Override
    public void printStructure() {
        super.printStructure();
        this.printValues();
    }

    public void printValues() {
        values.stream().forEach(v -> {
            System.out.println(v);
        });
    }
}