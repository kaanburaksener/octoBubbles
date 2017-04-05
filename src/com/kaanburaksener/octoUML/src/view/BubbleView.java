package com.kaanburaksener.octoUML.src.view;

import com.github.javaparser.ast.CompilationUnit;
import com.kaanburaksener.octoUML.src.model.nodes.AbstractNode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/**
 * Created by kaanburaksener on 19/03/17.
 */
public class BubbleView extends GridPane {
    private final String CSS = "com/kaanburaksener/octoUML/src/view/fxml/bubble.css";
    private TextArea textArea = new TextArea();
    private TextField textField = new TextField();
    private Label title;
    private String tempOldValue;
    private boolean isTempSet = false;
    private double height;

    protected Button cancelBtn, editBtn, saveBtn;

    public BubbleView(CompilationUnit sourceCode, String className, double width) {
        getStyleClass().add("bubble");
        getStylesheets().add(CSS);

        this.height = calculateHeight(sourceCode);

        setPrefSize(width, height);
        setHgap(5);
        setVgap(5);

        Image image = new Image("com/kaanburaksener/octoUML/src/icons/cancel-icon.png");
        cancelBtn = new Button("");
        cancelBtn.getStyleClass().add("cancel-button");
        cancelBtn.setGraphic(new ImageView(image));
        cancelBtn.setVisible(false);
        cancelBtn.setOnAction(event ->  {
            textArea.setEditable(false);
            editBtn.setVisible(true);
            cancelBtn.setVisible(false);
            saveBtn.setVisible(false);
            textArea.setText(tempOldValue);
        });

        image = new Image("com/kaanburaksener/octoUML/src/icons/edit-icon.png");
        editBtn = new Button("");
        editBtn.getStyleClass().add("edit-button");
        editBtn.setGraphic(new ImageView(image));
        editBtn.setOnAction(event -> {
            textArea.setEditable(true);
            editBtn.setVisible(false);
            cancelBtn.setVisible(true);
            saveBtn.setVisible(true);
        });

        image = new Image("com/kaanburaksener/octoUML/src/icons/save-icon.png");
        saveBtn = new Button("");
        saveBtn.getStyleClass().add("save-button");
        saveBtn.setGraphic(new ImageView(image));
        saveBtn.setVisible(false);

        title = new Label(className);

        textArea.setText(sourceCode.toString());
        textArea.setPrefSize(width, height - 90.0);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                if(!isTempSet) {
                    tempOldValue = oldValue;
                    isTempSet = true;
                }

                saveBtn.setOnAction(event -> {
                    textArea.setEditable(false);
                    editBtn.setVisible(true);
                    cancelBtn.setVisible(false);
                    saveBtn.setVisible(false);
                    textArea.setText(newValue);
                    tempOldValue = newValue;
                    isTempSet = false;
                });
            }
        });

        ColumnConstraints col25 = new ColumnConstraints();
        col25.setPercentWidth(25);
        getColumnConstraints().addAll(col25, col25, col25, col25);

        add(title, 0, 1, 4, 1);
        add(textArea, 0, 2, 4, 1);
        add(cancelBtn, 0, 3, 1, 1);
        add(saveBtn, 1, 3, 2, 1);
        add(editBtn, 0, 3, 1, 1);
    }

    public BubbleView getRefNode(){
        return this;
    }

    private double calculateHeight(CompilationUnit sourceCode) {
        int totalNumberOfLine = sourceCode.getRange().get().end.line;
        double coefficient;

        if(totalNumberOfLine >= 50) {
            coefficient = 5.0;
        } else {
            coefficient = 15.0;
        }

        return totalNumberOfLine * coefficient;
    }
}