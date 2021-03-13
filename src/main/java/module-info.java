module STRIDEModel {
    requires javafx.fxml;
    requires fx.graph.editor.core;
    requires fx.graph.editor.api;
    requires org.eclipse.emf.ecore;
    requires org.eclipse.emf.common;
    requires org.eclipse.emf.ecore.xmi;
    requires org.eclipse.emf.edit;
    requires java.logging;


    exports ch.zhaw  to javafx.graphics, javafx.fxml;
    exports ch.zhaw.controller to javafx.graphics, javafx.fxml;
    exports ch.zhaw.skin to javafx.graphics, javafx.fxml;
    opens ch.zhaw to javafx.fxml;
    opens ch.zhaw.controller to javafx.fxml;
    opens ch.zhaw.skin to javafx.fxml;

}
