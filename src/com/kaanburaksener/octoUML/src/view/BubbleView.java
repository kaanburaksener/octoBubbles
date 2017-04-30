package com.kaanburaksener.octoUML.src.view;

import com.kaanburaksener.octoUML.src.model.nodes.Bubble;
import com.kaanburaksener.octoUML.src.util.Constants;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

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

    protected Button closeBtn, cancelBtn, saveBtn, extendBtn;

    private Bubble refNode;

    private double x;
    private double y;

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
        add(extendBtn, 0, 3, 1, 1);
    }

    //------------ Init Buttons -------------------------------------------
    private void initBubbleActions() {
        textArea = new TextArea();

        closeBtn = new Button("");
        closeBtn.getStyleClass().add("cancel-button");
        closeBtn.setVisible(true);

        extendBtn = new Button("");
        extendBtn.getStyleClass().add("extend-button");
        extendBtn.setVisible(true);

        cancelBtn = new Button("");
        cancelBtn.getStyleClass().add("cancel-button");
        cancelBtn.setVisible(false);

        saveBtn = new Button("");
        saveBtn.getStyleClass().add("save-button");
        saveBtn.setVisible(false);

        //---------------------- Actions for buttons ----------------------------
        /*closeBtn.setOnAction(event -> {

        });*/

        extendBtn.setOnAction(event ->  {
            textArea.setEditable(true);
            cancelBtn.setVisible(true);
            saveBtn.setVisible(true);
            extendBtn.setVisible(false);
        });

        cancelBtn.setOnAction(event ->  {
            textArea.setEditable(false);
            extendBtn.setVisible(true);
            cancelBtn.setVisible(false);
            saveBtn.setVisible(false);
        });

        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                saveBtn.setOnAction(event -> {
                    textArea.setEditable(false);
                    extendBtn.setVisible(true);
                    cancelBtn.setVisible(false);
                    saveBtn.setVisible(false);
                    textArea.setText(newValue);
                });
            }
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

    private void changeBubbleHeight(double height) {
        setHeight(height);
        setPrefHeight(height);
        textArea.setPrefHeight(height - 90.0);
        textArea.setMaxHeight(height - 90.0);
    }

    private void changeBubbleWidth(double width) {
        setWidth(width);
        setPrefWidth(width);
        textArea.setPrefWidth(width);
        textArea.setMaxWidth(width);
    }

    public Button getCloseButton () {
        return closeBtn;
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