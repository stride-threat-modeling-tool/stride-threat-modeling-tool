package ch.zhaw.skin;

import de.tesis.dynaware.grapheditor.GJointSkin;
import de.tesis.dynaware.grapheditor.model.GJoint;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.util.logging.Logger;

public class DataFlowJointSkin extends GJointSkin {
    private static final Logger LOGGER = Logger.getLogger("JOINT SKIN");

    public static final String DATAFLOW_JOINT_CLASS = "data-flow-joint";
    public static final int WIDTH_OFFSET = 10;
    public static final int MAX_LENGTH = 45;


    private String text = "DATAFLOW JOINT";

    /**
     * Creates a new {@link GJointSkin}.
     *
     * @param joint the {@link GJoint} represented by the skin
     */
    public DataFlowJointSkin(GJoint joint) {
        super(joint);

        Label label = new Label(text);
        StackPane pane = new StackPane();
        label.boundsInLocalProperty().addListener((observableValue, bounds, t1) -> {
            getRoot().resize(label.getWidth() + WIDTH_OFFSET, label.getHeight());
        });

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
