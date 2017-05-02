package com.kaanburaksener.octoUML.src.model.nodes;

import com.github.javaparser.ast.CompilationUnit;
import com.kaanburaksener.octoUML.src.util.Constants;

import java.io.Serializable;

/**
 * Created by kaanburaksener on 24/04/17.
 */
public class Bubble extends AbstractNode implements Serializable {
    private String type = "BUBBLE";
    private String title;
    private String sourceCodeText;
    private CompilationUnit sourceCode;
    private double x, y, width, height, translateX, translateY;
    protected static int objectCount = 0; //Used to ID instance
    private AbstractNode refNode;

    public Bubble(double x, double y, double width, double height, String title, CompilationUnit sourceCode, AbstractNode refNode)
    {
        super(x, y, width, height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.title = title;
        this.sourceCode = sourceCode;
        this.setSourceCodeText(sourceCode.toString());
        this.refNode = refNode;
    }

    public void setSourceCodeText(String sourceCodeText){
        this.sourceCodeText = sourceCodeText;
        changes.firePropertyChange(Constants.changeBubbleSourceCode, null, sourceCodeText);
        remoteChanges.firePropertyChange(Constants.changeBubbleSourceCode, null, sourceCodeText);
    }

    public void setType(String type){
        this.type = type;
        changes.firePropertyChange(Constants.changeBubbleType, null, type);
        remoteChanges.firePropertyChange(Constants.changeBubbleType, null, type);
    }

    public void remoteSetSourceCodeText(String sourceCodeText){
        this.sourceCodeText = sourceCodeText;
        changes.firePropertyChange(Constants.changeBubbleSourceCode, null, sourceCodeText);
    }

    public void remoteSetType(String type){
        this.type = type;
        changes.firePropertyChange(Constants.changeBubbleType, null, type);
    }

    public String getSourceCodeText(){
        return sourceCodeText;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public AbstractNode copy() {
        return null;
    }

    public CompilationUnit getSourceCode() {
        return sourceCode;
    }

    public String getType() {
        return type;
    }

    public AbstractNode getRefNode() { return refNode; }

    public void setX(double x){
        this.x = x;
        changes.firePropertyChange(Constants.changeBubbleX, null, this.x);
        remoteChanges.firePropertyChange(Constants.changeBubbleX, null, this.x);
    }

    public void setY(double y){
        this.y = y;
        changes.firePropertyChange(Constants.changeBubbleY, null, this.y);
        remoteChanges.firePropertyChange(Constants.changeBubbleY, null, this.y);
    }

    /**
     * Sets the height of the node. If less than MIN_HEIGHT, height is set to MIN_HEIGHT.
     * @param height
     */
    public void setHeight(double height){
        this.height = height;
        changes.firePropertyChange(Constants.changeBubbleHeight, null, this.height);
        remoteChanges.firePropertyChange(Constants.changeBubbleHeight, null, this.height);
    }

    /**
     * Sets the width of the node. If less than MIN_WIDTH, width is set to MIN_WIDTH.
     * @param width
     */
    public void setWidth(double width){
        this.width = width;
        changes.firePropertyChange(Constants.changeBubbleWidth, null, this.width);
        remoteChanges.firePropertyChange(Constants.changeBubbleWidth, null, this.width);
    }

    public void remoteSetHeight(double height){
        changes.firePropertyChange(Constants.changeBubbleHeight, null, this.height);
    }
    public void remoteSetWidth(double width){
        changes.firePropertyChange(Constants.changeBubbleWidth, null, this.width);
    }

    /**
     * No-arg constructor for JavaBean convention
     */
    public Bubble() {}
}
