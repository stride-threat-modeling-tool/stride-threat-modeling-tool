package ch.zhaw.threatmodeling.model.threats;

import ch.zhaw.threatmodeling.model.threats.patterns.ThreatPattern;
import ch.zhaw.threatmodeling.persistence.ThreatPatternPersistence;
import ch.zhaw.threatmodeling.skin.joint.DataFlowJointSkin;
import ch.zhaw.threatmodeling.skin.joint.TrustBoundaryJointSkin;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ThreatGenerator {
    private static final Logger LOGGER = Logger.getLogger(ThreatGenerator.class.getName());

    private final ObjectProperty<ObservableList<Threat>> threatsProperty = new SimpleObjectProperty<>();
    private final GModel model;
    private final SkinLookup skinLookup;
    private final List<ThreatPattern> threatPatterns;

    public ThreatGenerator(GModel model, SkinLookup skinLookup) {
        this.skinLookup = skinLookup;
        this.model = model;
        this.threatPatterns = ThreatPatternPersistence.loadThreatPatterns();
        clearThreats();

    }

    private void clearThreats() {
        setThreats(FXCollections.observableArrayList());
    }

    public ObservableList<Threat> getThreats() {
        return threatsProperty.get();
    }

    private void setThreats(ObservableList<Threat> threats) {
        threatsProperty.set(threats);
    }

    public ObjectProperty<ObservableList<Threat>> getThreatsProperty() {
        return threatsProperty;
    }

    public void generateAllThreats() {
        final List<Threat> newlyGeneratedThreats = new ArrayList<>();
        for (GConnection con : model.getConnections()) {
            final GenericNodeSkin target = (GenericNodeSkin) skinLookup.lookupNode(con.getTarget().getParent());
            final GenericNodeSkin source = (GenericNodeSkin) skinLookup.lookupNode(con.getSource().getParent());
            final DataFlowJointSkin joint = (DataFlowJointSkin) skinLookup.lookupJoint(con.getJoints().get(0));
            if(!joint.getText().equals(TrustBoundaryJointSkin.ELEMENT_TYPE)) {
                threatPatterns.forEach(threatPattern -> {
                    LOGGER.info(source.getType());
                    LOGGER.info( target.getType());
                    if(threatPattern.shouldBeGenerated(source.getType(), target.getType(), intersectsTrustBoundary(con), null, null, null)) {
                        newlyGeneratedThreats.add(threatPattern.generate(
                                getThreats().size() + newlyGeneratedThreats.size() + 1,
                                joint,
                                con,
                                source,
                                target
                        ));
                    }
                });
            }
        }
        addAllUniqueNewThreats(newlyGeneratedThreats);
    }

    //TODO
    private boolean intersectsTrustBoundary(GConnection connection) {
        boolean intersects = false;
        return true;
    }

    private void addAllUniqueNewThreats(List<Threat> newlyGeneratedThreats) {
        newlyGeneratedThreats.forEach(threat -> {
            if(getThreats()
                    .stream()
                    .noneMatch(t ->
                            t.getTypeId().equals(threat.getTypeId()) &&
                            t.getNodeName1() == threat.getNodeName1()&&
                            t.getNodeName2() == threat.getNodeName2()))
            {
                getThreats().add(threat);
            }
        });
    }

    public static ChangeListener<String> createThreatTitleChangeListener(Threat threat, String key, GenericNodeSkin linkedNode) {
       return  (observableValue, s, t1) -> {
           if (!threat.isModified()) {
               threat.addTemplate(key, linkedNode.getText());
               threat.updateThreat();
           }
       };
    }

    public List<Threat> getAllThreatsForConnection(GConnection connection) {
        return getThreats().filtered(threat -> threat.getConnection().equals(connection));
    }
}
