package ch.zhaw.threatmodeling.model;

import ch.zhaw.threatmodeling.model.enums.STRIDECategory;
import ch.zhaw.threatmodeling.model.enums.State;
import ch.zhaw.threatmodeling.skin.joint.DataFlowJointSkin;
import ch.zhaw.threatmodeling.skin.nodes.datastore.DataStoreNodeSkin;
import de.tesis.dynaware.grapheditor.GNodeSkin;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.logging.Logger;

public class ThreatGenerator {
    private static final Logger LOGGER = Logger.getLogger(ThreatGenerator.class.getName());

    private final ObjectProperty<ObservableList<Threat>> threatsProperty = new SimpleObjectProperty<>();
    private final GModel model;
    private final SkinLookup skinLookup;

    public ThreatGenerator(GModel model, SkinLookup skinLookup) {
        this.skinLookup = skinLookup;
        this.model = model;
        clearThreats();
    }

    private void clearThreats(){
        setThreats(FXCollections.observableArrayList());
    }

    public ObservableList<Threat> getThreats() {
        return threatsProperty.get();
    }

    private void setThreats(ObservableList<Threat> threats) {
        threatsProperty.set(threats);
    }

    public ObjectProperty<ObservableList<Threat>> getThreatsProperty(){
        return threatsProperty;
    }
    public void generateAllThreats(){
        for (GConnection con : model.getConnections()) {
            final GNodeSkin target = skinLookup.lookupNode(con.getTarget().getParent());
            final  GNodeSkin source = skinLookup.lookupNode(con.getSource().getParent());
            final DataFlowJointSkin joint = (DataFlowJointSkin) skinLookup.lookupJoint(con.getJoints().get(0));
            LOGGER.info("Target is a " + target.getItem().getType());
            LOGGER.info("Source is a " + source.getItem().getType());

            if(target.getItem().getType().equals(DataStoreNodeSkin.TITLE_TEXT)) {
                getThreats().add(generateDataStoreDestinationSpoofingThreat((DataStoreNodeSkin) target, joint));
            }
            if(source.getItem().getType().equals(DataStoreNodeSkin.TITLE_TEXT)){
                getThreats().add(generateDataStoreSourceSpoofingThreat((DataStoreNodeSkin) source, ((DataStoreNodeSkin)target).getText(), joint));
            }
        }

    }

    private Threat generateDataStoreGenericSpoofingThreat(DataStoreNodeSkin store, String srcDest, String name, String name2, String specificText,   DataFlowJointSkin joint){
        return new Threat(getThreats().size() + 1,
                State.NOT_STARTED,
                "Spoofing of " + srcDest + " Data Store " + store.getText(),
                STRIDECategory.SPOOFING,
                String.format(ThreatConstants.DATA_STORE_SPOOFING_GENERIC_TEXT, name, specificText, name2,srcDest.toLowerCase()),
                "",
                joint
                );
    }

    private Threat generateDataStoreDestinationSpoofingThreat(DataStoreNodeSkin store, DataFlowJointSkin joint) {
        return generateDataStoreGenericSpoofingThreat(
                store,
                "Destination",
                store.getText(),
                store.getText(),
                ThreatConstants.DATA_STORE_SPOOFING_DESTINATION_TEXT,
                joint);
    }

    private Threat generateDataStoreSourceSpoofingThreat(DataStoreNodeSkin store, String targetName,  DataFlowJointSkin joint) {
        return generateDataStoreGenericSpoofingThreat(
                store,
                "Source",
                store.getText(),
                targetName,
                ThreatConstants.DATA_STORE_SPOOFING_SOURCE_TEXT,
                joint);
    }

}
