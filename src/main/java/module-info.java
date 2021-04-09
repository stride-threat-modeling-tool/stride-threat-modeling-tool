module STRIDEModel {
    requires javafx.fxml;
    requires fx.graph.editor.core;
    requires fx.graph.editor.api;
    requires org.eclipse.emf.ecore;
    requires org.eclipse.emf.common;
    requires org.eclipse.emf.ecore.xmi;
    requires org.eclipse.emf.edit;
    requires java.logging;
    requires com.google.gson;


    exports ch.zhaw.threatmodeling  to javafx.graphics, javafx.fxml;
    exports ch.zhaw.threatmodeling.controller to javafx.graphics, javafx.fxml;
    exports ch.zhaw.threatmodeling.skin to javafx.graphics, javafx.fxml;
    opens ch.zhaw.threatmodeling to javafx.fxml;
    opens ch.zhaw.threatmodeling.controller to javafx.fxml;
    opens ch.zhaw.threatmodeling.skin to javafx.fxml;
    opens ch.zhaw.threatmodeling.persistence.utils.objects to com.google.gson;


    opens ch.zhaw.threatmodeling.model to javafx.base;

}
