package com.kaanburaksener.octoUML.src.model.nodes;

import com.kaanburaksener.octoUML.src.util.Constants;

import java.io.Serializable;

/**
 * Created by kaanburaksener on 12/02/17.
 *
 * Represents an enumeration class.
 */
public class EnumerationNode extends AbstractNode implements Serializable
{
    private static final String TYPE = "ENUM";
    private String values;

    public EnumerationNode(double x, double y, double width, double height){
        super(x, y, width, height);
        //Don't accept nodes with size less than minWidth * minHeight.
        this.width = width < CLASS_MIN_WIDTH ? CLASS_MIN_WIDTH : width;
        this.height = height < CLASS_MIN_HEIGHT ? CLASS_MIN_HEIGHT : height;
    }

    public void setValues(String pValues){
        values = pValues;
        changes.firePropertyChange(Constants.changeEnumerationNodeValues, null, values);
        remoteChanges.firePropertyChange(Constants.changeEnumerationNodeValues, null, values);
    }

    public String getValues(){
        return values;
    }

    public void remoteSetValues(String pValues){
        values = pValues;
        changes.firePropertyChange(Constants.changeEnumerationNodeValues, null, values);
    }

    @Override
    public EnumerationNode copy(){
        EnumerationNode newCopy = new EnumerationNode(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        newCopy.setTranslateX(this.getTranslateX());
        newCopy.setTranslateY(this.getTranslateY());
        newCopy.setScaleX(this.getScaleX());
        newCopy.setScaleY(this.getScaleY());

        if(this.getTitle() != null){
            newCopy.setTitle(this.getTitle());

        }
        if(this.values != null){
            newCopy.setValues(this.values);
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
    public EnumerationNode(){
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public void printNode() {
        System.out.println(values);
    }
}
