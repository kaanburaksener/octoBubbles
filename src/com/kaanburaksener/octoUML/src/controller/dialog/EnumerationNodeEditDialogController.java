package com.kaanburaksener.octoUML.src.controller.dialog;

import com.kaanburaksener.octoUML.src.model.nodes.EnumerationNode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


/**
 * Created by kaanburaksener on 13/02/17.
 **
 * Dialog to edit details of an EnumerationNode
 */
public class EnumerationNodeEditDialogController {
    @FXML
    private TextField titleField;
    @FXML
    private TextArea valuesArea;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    private EnumerationNode node;
    private boolean okClicked = false;

    /**
     * Initializes the controller class. This method is automatically called
     * after the classDiagramView.fxml file has been loaded.
     */
    @FXML
    private void initialize() {}

    /**
     * Sets the node to be edited in the dialog.
     *
     * @param node
     */
    public void setNode(EnumerationNode node) {
        this.node = node;

        titleField.setText(this.node.getTitle());
        valuesArea.setText(this.node.getValues());
    }

    public Button getOkButton() {
        return okButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public String getTitle() {
        return titleField.getText();
    }

    public String getValues(){
        return valuesArea.getText();
    }

    public boolean hasTitledChanged(){
        if(this.node.getTitle() == null){
            return titleField.getText() != null;
        } else {
            return !this.node.getTitle().equals(titleField.getText());
        }
    }

    public boolean hasValuesChanged(){
        if(this.node.getValues() == null){
            return valuesArea.getText() != null;
        } else {
            return !this.node.getValues().equals(valuesArea.getText());
        }
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    private void handleOk() {
        if (isInputValid()) {
            okClicked = true;
        }
    }

    /**
     * Called when the user clicks cancel.
     */

    private void handleCancel() {
    }

    /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        return true; //Use if we want to
    }
}
