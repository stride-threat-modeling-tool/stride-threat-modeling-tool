package ch.zhaw.skin;

import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public interface DataFlowElement {
    String getElementType();

    String getText();

    StringProperty textProperty();

    void setText(String text);

    void setHasBeenSelectedHandler(EventHandler<MouseEvent> hasBeenSelectedHandler);
}
