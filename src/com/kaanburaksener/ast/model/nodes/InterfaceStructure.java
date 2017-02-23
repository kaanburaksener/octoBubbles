package com.kaanburaksener.ast.model.nodes;

/**
 * Created by kaanburaksener on 16/02/17.
 */
public class InterfaceStructure extends ClassStructure {
    private static final String type = "INTERFACE";

    /**
     * @param name
     * @param path
     */
    public InterfaceStructure(String name, String path) {
        super(name, path);
    }

    public String getType(){
        return type;
    }
}
