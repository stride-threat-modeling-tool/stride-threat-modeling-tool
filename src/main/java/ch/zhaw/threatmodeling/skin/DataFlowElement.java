package ch.zhaw.threatmodeling.skin;

import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public interface DataFlowElement {
    StringProperty typeProperty();

    String getText();

    StringProperty textProperty();

    void setText(String text);

    void setHasBeenSelectedHandler(EventHandler<MouseEvent> hasBeenSelectedHandler);
}
