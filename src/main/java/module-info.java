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
    requires org.apache.commons.text;


    exports ch.zhaw.threatmodeling;
    exports ch.zhaw.threatmodeling.controller;
    exports ch.zhaw.threatmodeling.skin;
    opens ch.zhaw.threatmodeling;
    opens ch.zhaw.threatmodeling.controller;
    opens ch.zhaw.threatmodeling.skin;
    opens ch.zhaw.threatmodeling.persistence.utils.objects;


    opens ch.zhaw.threatmodeling.model;

}
