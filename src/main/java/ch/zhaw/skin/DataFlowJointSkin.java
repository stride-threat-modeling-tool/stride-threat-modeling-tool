package ch.zhaw.skin;

import de.tesis.dynaware.grapheditor.GJointSkin;
import de.tesis.dynaware.grapheditor.model.GJoint;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.util.logging.Logger;

public class DataFlowJointSkin extends GJointSkin implements DataFlowElement {
    private static final Logger LOGGER = Logger.getLogger("JOINT SKIN");

    public static final String DATAFLOW_JOINT_CLASS = "data-flow-joint";
    public static final int WIDTH_OFFSET = 10;
    public static final int MAX_LENGTH = 45;
    public static final String ELEMENT_TYPE = "Data Flow";

    private final StringProperty text = new SimpleStringProperty();
    private final Label label = new Label();
    public StringProperty textProperty() {
        return text;
    }

    @Override
    public String getText() {
        return text.get();
    }

    @Override
    public void setText(String newText) {
        text.set(newText);
    }

    /**
     * Creates a new {@link GJointSkin}.
     *
     * @param joint the {@link GJoint} represented by the skin
     */
    public DataFlowJointSkin(GJoint joint) {
        super(joint);
        setText("data flow");
        StackPane pane = new StackPane();
        label.boundsInLocalProperty().addListener((observableValue, bounds, t1) -> {
            getRoot().resize(label.getWidth() + WIDTH_OFFSET, label.getHeight());
        });
        label.textProperty().bindBidirectional(textProperty());

        pane.getChildren().add(label);
        pane.setMouseTransparent(true);

        getRoot().resize(pane.getWidth(), pane.getHeight());
        getRoot().getChildren().add(pane);
        getRoot().getStyleClass().add(DATAFLOW_JOINT_CLASS);

    }

    @Override
    public double getWidth() {
        return MAX_LENGTH;
    }

    @Override
    public double getHeight() {
        return MAX_LENGTH;
    }

    @Override
    protected void selectionChanged(boolean isSelected) {
    }

    @Override
    public String getElementType() {
        return ELEMENT_TYPE;
    }

    @Override
    public void setHasBeenSelectedHandler(EventHandler<MouseEvent> hasBeenSelectedHandler) {
        getRoot().setOnMouseClicked(hasBeenSelectedHandler);
    }


}
