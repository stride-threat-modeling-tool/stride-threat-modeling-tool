package ch.zhaw.threatmodeling.model.threats;

import ch.zhaw.threatmodeling.model.threats.patterns.ThreatPattern;
import de.tesis.dynaware.grapheditor.model.GConnection;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Iterator;
import java.util.List;

public class Threats {
    private final ObjectProperty<ObservableList<Threat>> threatList = new SimpleObjectProperty<>();

    public Threats() {
       clear();
    }

    public void clear(){
        threatList.set(FXCollections.observableArrayList());
    }

    public ObjectProperty<ObservableList<Threat>> getListProperty() {
        return threatList;
    }

    public ObservableList<Threat> all() {
        return threatList.get();
    }

    public int size() {
        return all().size();
    }

    public void add(Threat threat){
        all().add(threat);
    }

    public void forEach(java.util.function.Consumer<? super Threat> action){
        all().forEach(action);
    }

    public Iterator<Threat> iterator(){
        return all().iterator();
    }

    public void remove(Threat threat){
        all().remove(threat);
    }

    public List<Threat> getAllThreatsForConnection(GConnection connection) {
        return all().filtered(threat -> threat.getConnection().equals(connection));
    }

}
