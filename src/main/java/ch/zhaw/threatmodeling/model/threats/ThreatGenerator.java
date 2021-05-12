package ch.zhaw.threatmodeling.model.threats;

import ch.zhaw.threatmodeling.model.threats.patterns.ThreatPatterns;
import ch.zhaw.threatmodeling.persistence.ThreatPatternPersistence;
import ch.zhaw.threatmodeling.skin.DataFlowElement;
import ch.zhaw.threatmodeling.skin.connection.DataFlowConnectionSkin;
import ch.zhaw.threatmodeling.skin.connection.TrustBoundaryConnectionSkin;
import ch.zhaw.threatmodeling.skin.joint.DataFlowJointSkin;
import ch.zhaw.threatmodeling.skin.joint.TrustBoundaryJointSkin;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import ch.zhaw.threatmodeling.skin.utils.DataFlowConnectionCommands;
import ch.zhaw.threatmodeling.skin.utils.intersection.QuadraticSplineUtils;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GModel;
import javafx.beans.value.ChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class ThreatGenerator {
    private static final Logger LOGGER = Logger.getLogger(ThreatGenerator.class.getName());

    private final Threats threats;
    private final GModel model;
    private final SkinLookup skinLookup;
    private final ThreatPatterns threatPatterns;

    public ThreatGenerator(GModel model, SkinLookup skinLookup) {
        this.skinLookup = skinLookup;
        this.model = model;
        this.threatPatterns = ThreatPatternPersistence.loadThreatPatterns();
        threats = new Threats();
        LOGGER.info("loaded threats " + threatPatterns.size());

    }

    public void generateAllThreats() {
        final List<Threat> newlyGeneratedThreats = new ArrayList<>();
        for (GConnection con : model.getConnections()) {
            // Ignore TrustBoundaries
            if (!(DataFlowConnectionCommands.getType(con, skinLookup).equals(TrustBoundaryJointSkin.ELEMENT_TYPE))) {
                final GenericNodeSkin target = (GenericNodeSkin) skinLookup.lookupNode(con.getTarget().getParent());
                final GenericNodeSkin source = (GenericNodeSkin) skinLookup.lookupNode(con.getSource().getParent());
                final DataFlowJointSkin joint = (DataFlowJointSkin) skinLookup.lookupJoint(con.getJoints().get(0));
                final boolean intersectsTrustBoundary = intersectsTrustBoundary(con);
                if (!joint.getText().equals(TrustBoundaryJointSkin.ELEMENT_TYPE)) {
                    threatPatterns.forEach(threatPattern -> {
                        if (threatPattern.shouldBeGenerated(source.getType(), target.getType(), intersectsTrustBoundary , null, null, null)) {
                            newlyGeneratedThreats.add(threatPattern.generate(
                                    threats.size() + newlyGeneratedThreats.size() + 1,
                                    joint,
                                    con,
                                    source,
                                    target
                            ));
                        }
                    });
                }
            }
        }
        addAllUniqueNewThreats(newlyGeneratedThreats);
        removeThreatsNotFulfillingPatternConditions(newlyGeneratedThreats);
    }

    private void removeThreatsNotFulfillingPatternConditions(List<Threat> newlyGeneratedThreats) {
        Iterator<Threat> iter = threats.iterator();
        while(iter.hasNext()){
            if(!newlyGeneratedThreats.contains(iter.next())){
                iter.remove();
            }
        }
    }

    private boolean intersectsTrustBoundary(GConnection connection) {
        boolean intersects = false;
        DataFlowConnectionSkin dataFlowConnectionSkin = (DataFlowConnectionSkin) skinLookup.lookupConnection(connection);
        List<TrustBoundaryConnectionSkin> trustBoundaries = new ArrayList<>();

        // Get all trust boundary skins
        for (GConnection con : model.getConnections()) {
            if (DataFlowConnectionCommands.getType(con, skinLookup).equals(TrustBoundaryJointSkin.ELEMENT_TYPE)) {
                trustBoundaries.add((TrustBoundaryConnectionSkin) skinLookup.lookupConnection(con));
            }
        }

        // Check all existing trust boundaries in the scene against the dataflow
        for (TrustBoundaryConnectionSkin trustBoundaryConnectionSkin : trustBoundaries) {
            if (QuadraticSplineUtils.checkIntersection(dataFlowConnectionSkin, trustBoundaryConnectionSkin)) {
                // Abort as soon as the first intersection with ANY trust boundary has been found
                intersects = true;
                break;
            }
        }
        return intersects;
    }

    private void addAllUniqueNewThreats(List<Threat> newlyGeneratedThreats) {
        newlyGeneratedThreats.forEach(threat -> {
            if(threats.all()
                    .stream()
                    .noneMatch(t ->
                            t.equals(threat)))
            {
                threats.add(threat);
            }
        });
    }

    public static ChangeListener<String> createElementTextChangeListener(Threat threat, String key, DataFlowElement element) {
       return  (observableValue, s, t1) -> {
           if (!threat.isModified()) {
               threat.updateTemplate(key, element.getText());
               threat.updateThreat();
           }
       };
    }

    public Threats getThreats() {
        return threats;
    }
}
