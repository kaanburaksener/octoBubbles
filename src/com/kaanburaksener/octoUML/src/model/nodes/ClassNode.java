package com.kaanburaksener.octoUML.src.model.nodes;

import com.kaanburaksener.octoUML.src.util.Constants;

import java.io.Serializable;
/**
 * Represents a UML class.
 */
public class ClassNode extends AbstractNode implements Serializable
{
    private String type = "CLASS";
    private String attributes;
    private String operations;

    public ClassNode(double x, double y, double width, double height)
    {
        super(x, y, width, height );
        //Don't accept nodes with size less than minWidth * minHeight.
        this.width = width < CLASS_MIN_WIDTH ? CLASS_MIN_WIDTH : width;
        this.height = height < CLASS_MIN_HEIGHT ? CLASS_MIN_HEIGHT : height;
    }

    public void setAttributes(String pAttributes){
        attributes = pAttributes;
        changes.firePropertyChange(Constants.changeClassNodeAttributes, null, attributes);
        remoteChanges.firePropertyChange(Constants.changeClassNodeAttributes, null, attributes);
    }

    public void setOperations(String pOperations){
        operations = pOperations;
        changes.firePropertyChange(Constants.changeClassNodeOperations, null, operations);
        remoteChanges.firePropertyChange(Constants.changeClassNodeOperations, null, operations);
    }

    public void setType(String pType){
        type = pType;
        changes.firePropertyChange(Constants.changeClassNodeType, null, type);
        remoteChanges.firePropertyChange(Constants.changeClassNodeType, null, type);
    }

    public void remoteSetAttributes(String pAttributes){
        attributes = pAttributes;
        changes.firePropertyChange(Constants.changeClassNodeAttributes, null, attributes);
    }

    public void remoteSetOperations(String pOperations){
        operations = pOperations;
        changes.firePropertyChange(Constants.changeClassNodeOperations, null, operations);
    }

    public void remoteSetType(String pType){
        type = pType;
        changes.firePropertyChange(Constants.changeClassNodeType, null, type);
    }

    public String getAttributes(){
        return attributes;
    }

    public String getOperations(){
        return operations;
    }

    public String getType(){
        return type;
    }

    @Override
    public ClassNode copy(){
        ClassNode newCopy = new ClassNode(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        newCopy.setTranslateX(this.getTranslateX());
        newCopy.setTranslateY(this.getTranslateY());
        newCopy.setScaleX(this.getScaleX());
        newCopy.setScaleY(this.getScaleY());

        if(this.getTitle() != null){
            newCopy.setTitle(this.getTitle());

        }
        if(this.attributes != null){
            newCopy.setAttributes(this.attributes);
        }
        if(this.operations != null){
            newCopy.setOperations(operations);
        }
        newCopy.setTranslateX(this.getTranslateX());
        newCopy.setTranslateY(this.getTranslateY());
        return newCopy;
    }

    @Override
    public void setHeight(double height) {
        this.height = height < CLASS_MIN_HEIGHT ? CLASS_MIN_HEIGHT : height;
        super.setHeight(height);
    }

    @Override
    public void setWidth(double width) {
        this.width = width < CLASS_MIN_WIDTH ? CLASS_MIN_WIDTH : width;
        super.setWidth(width);
    }

    @Override
    public void remoteSetHeight(double height) {
        this.height = height < CLASS_MIN_HEIGHT ? CLASS_MIN_HEIGHT : height;
        super.remoteSetHeight(height);
    }

    @Override
    public void remoteSetWidth(double width) {
        this.width = width < CLASS_MIN_WIDTH ? CLASS_MIN_WIDTH : width;
        super.remoteSetWidth(width);
    }

    /**
     * No-arg constructor for JavaBean convention
     */
    public ClassNode(){
    }
}
