package ch.zhaw.threatmodeling.model;

import ch.zhaw.threatmodeling.model.enums.STRIDECategory;
import ch.zhaw.threatmodeling.model.enums.State;
import ch.zhaw.threatmodeling.skin.nodes.datastore.DataStoreNodeSkin;
import de.tesis.dynaware.grapheditor.GNodeSkin;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.logging.Logger;

public class ThreatGenerator {
    private static final Logger LOGGER = Logger.getLogger(ThreatGenerator.class.getName());

    private final ObservableList<Threat> threats = FXCollections.observableArrayList();
    private final GModel model;
    private final SkinLookup skinLookup;

    public ThreatGenerator(GModel model, SkinLookup skinLookup) {
        this.skinLookup = skinLookup;
        this.model = model;
    }

    public ObservableList<Threat> getThreats() {
        return threats;
    }
    public void generateAllThreats(){
        threats.clear();
        for (GConnection con : model.getConnections()) {
            final GNodeSkin target = skinLookup.lookupNode(con.getTarget().getParent());
            final  GNodeSkin source = skinLookup.lookupNode(con.getSource().getParent());
            LOGGER.info("Target is a " + target.getItem().getType());
            LOGGER.info("Source is a " + source.getItem().getType());

            if(target.getItem().getType().equals(DataStoreNodeSkin.TITLE_TEXT)) {
                threats.add(generateDataStoreDestinationSpoofingThreat((DataStoreNodeSkin) target));
            }
            if(source.getItem().getType().equals(DataStoreNodeSkin.TITLE_TEXT)){
                threats.add(generateDataStoreSourceSpoofingThreat((DataStoreNodeSkin) source, ((DataStoreNodeSkin)target).getText()));
            }
        }

    }

    private Threat generateDataStoreGenericSpoofingThreat(DataStoreNodeSkin store, String srcDest, String name, String name2, String specificText){
        return new Threat(0,
                State.NOT_STARTED,
                "Spoofing of " + srcDest + " Data Store " + store.getText(),
                STRIDECategory.SPOOFING,
                String.format(ThreatConstants.DATA_STORE_SPOOFING_GENERIC_TEXT, name, specificText, name2,srcDest.toLowerCase()),
                "",
                null
                );
    }

    private Threat generateDataStoreDestinationSpoofingThreat(DataStoreNodeSkin store) {
        return generateDataStoreGenericSpoofingThreat(
                store,
                "Destination",
                store.getText(),
                store.getText(),
                ThreatConstants.DATA_STORE_SPOOFING_DESTINATION_TEXT);
    }

    private Threat generateDataStoreSourceSpoofingThreat(DataStoreNodeSkin store, String targetName) {
        return generateDataStoreGenericSpoofingThreat(
                store,
                "Source",
                store.getText(),
                targetName,
                ThreatConstants.DATA_STORE_SPOOFING_SOURCE_TEXT);
    }

}
