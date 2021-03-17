package ch.zhaw.skin;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public interface DataFlowElement {
    String getElementType();

    String getText();

    void setText(String text);

    void setHasBeenSelectedHandler(EventHandler<MouseEvent> hasBeenSelectedHandler);
}
