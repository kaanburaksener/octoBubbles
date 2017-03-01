package com.kaanburaksener.octoUML.src.view.nodes;

import com.kaanburaksener.octoUML.src.model.nodes.EnumerationNode;
import com.kaanburaksener.octoUML.src.util.Constants;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.beans.PropertyChangeEvent;

/**
 * Created by kaanburaksener on 13/02/17.
 **
 * Visual representation of EnumerationNode class.
 **/
public class EnumerationNodeView extends AbstractNodeView implements NodeView
{
    private Label type;
    private Label title;
    private Label values;

    private Rectangle rectangle;

    private StackPane container;
    private VBox titlePane;
    private VBox vbox;

    private Separator firstLine;

    private Line shortHandleLine;
    private Line longHandleLine;

    private final int STROKE_WIDTH = 1;

    public EnumerationNodeView(EnumerationNode node) {
        super(node);
        //setChangeListeners();

        container = new StackPane();
        rectangle = new Rectangle();
        vbox = new VBox();
        container.getChildren().addAll(rectangle, vbox);

        initVBox();
        createRectangles();
        changeHeight(node.getHeight());
        changeWidth(node.getWidth());
        initLooks();

        this.getChildren().add(container);

        this.setTranslateX(node.getTranslateX());
        this.setTranslateY(node.getTranslateY());
        createHandles();
    }

    private void createRectangles(){
        EnumerationNode node = (EnumerationNode) getRefNode();
        changeHeight(node.getHeight());
        changeWidth(node.getWidth());
        rectangle.setX(node.getX());
        rectangle.setY(node.getY());
    }

    private void changeHeight(double height){
        setHeight(height);
        rectangle.setHeight(height);
    }

    private void changeWidth(double width){
        setWidth(width);
        rectangle.setWidth(width);
        container.setMaxWidth(width);
        container.setPrefWidth(width);

        vbox.setMaxWidth(width);
        vbox.setPrefWidth(width);
        firstLine.setMaxWidth(width);
        firstLine.setPrefWidth(width);

        type.setMaxWidth(width);
        type.setPrefWidth(width);

        title.setMaxWidth(width);
        title.setPrefWidth(width);

        values.setMaxWidth(width);
        values.setPrefWidth(width);
    }

    private void createHandles(){
        shortHandleLine = new Line();
        longHandleLine = new Line();

        shortHandleLine.startXProperty().bind(rectangle.widthProperty().subtract(7));
        shortHandleLine.startYProperty().bind(rectangle.heightProperty().subtract(3));
        shortHandleLine.endXProperty().bind(rectangle.widthProperty().subtract(3));
        shortHandleLine.endYProperty().bind(rectangle.heightProperty().subtract(7));
        longHandleLine.startXProperty().bind(rectangle.widthProperty().subtract(15));
        longHandleLine.startYProperty().bind(rectangle.heightProperty().subtract(3));
        longHandleLine.endXProperty().bind(rectangle.widthProperty().subtract(3));
        longHandleLine.endYProperty().bind(rectangle.heightProperty().subtract(15));

        this.getChildren().addAll(shortHandleLine, longHandleLine);
    }

    private void initVBox(){
        EnumerationNode node = (EnumerationNode) getRefNode();

        vbox.setPadding(new Insets(5, 0, 5, 0));
        vbox.setSpacing(5);

        titlePane = new VBox();
        titlePane.setSpacing(5);

        firstLine = new Separator();
        firstLine.setMaxWidth(node.getWidth());

        type = new Label();
        type.setFont(Font.font("Helvetica", FontWeight.BOLD, 13));
        type.setText("<<" + node.getType().toLowerCase() + ">>");
        type.setTextFill(Color.web("#E48178"));
        type.setAlignment(Pos.TOP_CENTER);

        title = new Label();
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        title.setManaged(false);

        if(node.getTitle() != null) {
            title.setText(node.getTitle());
            title.setVisible(true);
            title.setManaged(true);
        }

        title.setAlignment(Pos.BOTTOM_CENTER);

        values = new Label(node.getValues());
        values.setFont(Font.font("Verdana", 10));

        titlePane.getChildren().add(type);
        titlePane.getChildren().add(title);
        vbox.getChildren().addAll(titlePane, firstLine, values);
    }

    private void initLooks(){
        rectangle.setStrokeWidth(STROKE_WIDTH);
        rectangle.setFill(Color.LIGHTSKYBLUE);
        rectangle.setStroke(Color.BLACK);
        StackPane.setAlignment(title, Pos.CENTER);
        VBox.setMargin(values, new Insets(5,0,0,5));
    }

    public void setSelected(boolean selected){
        if(selected){
            rectangle.setStrokeWidth(2);
            setStroke(Constants.selected_color);
        } else {
            rectangle.setStrokeWidth(1);
            setStroke(Color.BLACK);
        }
    }

    public void setStrokeWidth(double scale){
        rectangle.setStrokeWidth(scale);
    }

    public void setFill(Paint p) {
        rectangle.setFill(p);
    }

    public void setStroke(Paint p) {
        rectangle.setStroke(p);
    }

    public Bounds getBounds(){
        return container.getBoundsInParent();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);

        if (evt.getPropertyName().equals(Constants.changeNodeX)) {
            setX((double) evt.getNewValue());
        } else if (evt.getPropertyName().equals(Constants.changeNodeY)) {
            setY((double) evt.getNewValue());
        } else if (evt.getPropertyName().equals(Constants.changeNodeWidth)) {
            changeWidth((double) evt.getNewValue());
        } else if (evt.getPropertyName().equals(Constants.changeNodeHeight)) {
            changeHeight((double) evt.getNewValue());
        } else if (evt.getPropertyName().equals(Constants.changeNodeTitle)) {
            title.setText((String) evt.getNewValue());
            title.setManaged(true);
            if (title.getText() == null || title.getText().equals("")) {
                firstLine.setVisible(false);
            } else {
                firstLine.setVisible(true);
            }
        } else if (evt.getPropertyName().equals(Constants.changeEnumerationNodeValues)) {
            values.setText((String) evt.getNewValue());
        }
    }
}