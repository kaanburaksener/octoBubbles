package com.kaanburaksener.octoUML.src.view;

import com.kaanburaksener.octoUML.src.model.nodes.Bubble;
import com.kaanburaksener.octoUML.src.util.Constants;

import javafx.geometry.Bounds;

import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by kaanburaksener on 19/03/17.
 *
 * Visual representation of Bubble class
 */
public class BubbleView extends GridPane implements PropertyChangeListener {
    private final String CSS = "com/kaanburaksener/octoUML/src/view/fxml/bubble.css";

    private static int objectCounter = 0;

    private Label title;

    private TextArea textArea;

    protected Button cancelBtn, closeBtn, editBtn, saveBtn;

    private Bubble refNode;

    private double x, y;

    public BubbleView(Bubble bubble) {
        this.setId("VIEWBUBBLE_" + objectCounter);
        this.refNode = bubble;

        getStyleClass().add("bubble");
        getStylesheets().add(CSS);

        setLayoutX(refNode.getX());
        setLayoutY(refNode.getY());

        setX(refNode.getX());
        setY(refNode.getY());

        initBubbleActions();
        initBubbleView();

        changeBubbleWidth(refNode.getWidth());
        changeBubbleHeight(refNode.getHeight());

        refNode.addPropertyChangeListener(this);
    }

    private void initBubbleView() {
        Bubble bubble = getRefNode();

        setHgap(5);
        setVgap(5);

        title = new Label(bubble.getTitle());

        textArea.setText(bubble.getSourceCodeText());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        ColumnConstraints col25 = new ColumnConstraints();
        col25.setPercentWidth(25);
        getColumnConstraints().addAll(col25, col25, col25, col25);

        add(title, 0, 1, 3, 1);
        add(closeBtn, 3, 1, 1, 1);
        add(textArea, 0, 2, 4, 1);
        add(cancelBtn, 0, 3, 1, 1);
        add(saveBtn, 1, 3, 2, 1);
        add(editBtn, 0, 3, 1, 1);
    }

    //------------ Init Buttons -------------------------------------------
    private void initBubbleActions() {
        cancelBtn = new Button("");
        cancelBtn.getStyleClass().add("cancel-button");
        cancelBtn.setVisible(false);

        closeBtn = new Button("");
        closeBtn.getStyleClass().add("cancel-button");
        closeBtn.setVisible(true);

        editBtn = new Button("");
        editBtn.getStyleClass().add("edit-button");
        editBtn.setVisible(true);

        saveBtn = new Button("");
        saveBtn.getStyleClass().add("save-button");
        saveBtn.setVisible(false);

        textArea = new TextArea();

        //---------------------- Actions for buttons ----------------------------

        cancelBtn.setOnAction(event ->  {
            textArea.setEditable(false);
            cancelBtn.setVisible(false);
            saveBtn.setVisible(false);
            editBtn.setVisible(true);
        });

        editBtn.setOnAction(event ->  {
            textArea.setEditable(true);
            cancelBtn.setVisible(true);
            saveBtn.setVisible(true);
            editBtn.setVisible(false);
        });
    }

    public Bubble getRefNode(){
        return refNode;
    }

    public void setX(double x) {
        this.x = x;
        setLayoutX(x);
    }

    public void setY(double y) {
        this.y = y;
        setLayoutY(y);
    }

    public double getX() {
        return x;
    }

    public double getY() { return y; }

    public String getTitle() {
        return title.getText();
    }

    public void arrangeLayoutAfterChange() {
        textArea.setEditable(false);
        cancelBtn.setVisible(false);
        saveBtn.setVisible(false);
        editBtn.setVisible(true);
    }

    private void changeBubbleHeight(double height) {
        setHeight(height);
        setPrefHeight(height);

        if(height > 200.0) {
            textArea.setPrefHeight(height - 90.0);
            textArea.setMaxHeight(height - 90.0);
        } else {
            textArea.setPrefHeight(height - 50.0);
            textArea.setMaxHeight(height - 50.0);
        }
    }

    private void changeBubbleWidth(double width) {
        setWidth(width);
        setPrefWidth(width);
        textArea.setPrefWidth(width);
        textArea.setMaxWidth(width);
    }

    public Button getCancelButton () {
        return cancelBtn;
    }

    public Button getCloseButton () {
        return closeBtn;
    }

    public Button getEditButton () {
        return editBtn;
    }

    public Button getSaveButton () {
        return saveBtn;
    }

    public TextArea getTextArea () {
        return textArea;
    }

    public void revertChangeInSourceCode(String oldValue) {
        textArea.setText(oldValue);
    }

    public boolean contains(double x, double y) {
        return (x >= this.getTranslateX() && x <= this.getTranslateX() + this.getWidth() && y >= this.getTranslateY() && y <= this.getTranslateY() + this.getHeight());
    }

    public Bounds getBounds(){
        return getBoundsInParent();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(Constants.changeBubbleX)) {
            this.setX((double)evt.getNewValue());
        } else if (evt.getPropertyName().equals(Constants.changeBubbleY)) {
            this.setY((double)evt.getNewValue());
        } else if (evt.getPropertyName().equals(Constants.changeBubbleWidth)) {
            changeBubbleWidth((double)evt.getNewValue());
        } else if (evt.getPropertyName().equals(Constants.changeBubbleHeight)) {
            changeBubbleHeight((double)evt.getNewValue());
        } else if (evt.getPropertyName().equals(Constants.changeBubbleSourceCode)) {
            textArea.setText((String)evt.getNewValue());
        }
    }
}