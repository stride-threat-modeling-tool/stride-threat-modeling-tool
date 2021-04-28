package ch.zhaw.threatmodeling.model;

import ch.zhaw.threatmodeling.model.enums.STRIDECategory;
import ch.zhaw.threatmodeling.model.enums.State;
import ch.zhaw.threatmodeling.model.threats.Threat;
import ch.zhaw.threatmodeling.model.threats.ThreatConstants;
import ch.zhaw.threatmodeling.model.threats.ThreatGenerator;
import ch.zhaw.threatmodeling.skin.joint.DataFlowJointSkin;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import de.tesis.dynaware.grapheditor.model.GConnection;

import java.util.ArrayList;
import java.util.List;

public class DataStoreThreatGenerator {
    private DataStoreThreatGenerator() {
    }

    public static List<Threat> generateTargetThreats(int id, GenericNodeSkin source, GenericNodeSkin target, DataFlowJointSkin joint, GConnection con) {
        List<Threat> generatedThreats = new ArrayList<>();
        generatedThreats.add(generateDataStoreGenericSpoofingThreat(
                id,
                ThreatConstants.DATA_STORE_SPOOFING_TARGET_ID,
                "Destination",
                ThreatConstants.DATA_STORE_SPOOFING_DESTINATION_TEXT,
                joint,
                con,
                target,
                source));

        return generatedThreats;
    }

    public static List<Threat> generateSourceThreats(int id, GenericNodeSkin source, GenericNodeSkin target, DataFlowJointSkin joint, GConnection con) {
        List<Threat> generatedThreats = new ArrayList<>();
        generatedThreats.add(generateDataStoreGenericSpoofingThreat(
                id,
                ThreatConstants.DATA_STORE_SPOOFING_SOURCE_ID,
                "Source",
                ThreatConstants.DATA_STORE_SPOOFING_SOURCE_TEXT,
                joint,
                con,
                source,
                target));
        return generatedThreats;
    }

    private static Threat generateDataStoreGenericSpoofingThreat(int id, String typeId, String srcDest, String specificText, DataFlowJointSkin joint, GConnection con, GenericNodeSkin name1, GenericNodeSkin name2) {
        String destTemplate = "${srcOrDest}";
        String name1Template = "${name1}";
        String name2Template = "${name2}";
        String specificTemplate = "${specificText}";
        Threat generatedThreat = new Threat(
                id,
                typeId,
                State.NOT_STARTED,
                STRIDECategory.SPOOFING,
                String.format("Spoofing of %s Data Store %s", destTemplate, name1Template),
                ThreatConstants.DATA_STORE_SPOOFING_GENERIC_TEXT,
                "",
                joint,
                con,
                name1,
                name2
        );
        generatedThreat.addTemplate(destTemplate, srcDest);
        generatedThreat.addTemplate(name1Template, name1.getText());
        generatedThreat.addTemplate(name2Template, name2.getText());
        generatedThreat.addTemplate(specificTemplate, specificText);

        generatedThreat.updateThreat();
        name1.textProperty().addListener(ThreatGenerator.createThreatTitleChangeListener(generatedThreat, name1Template, name1));
        name2.textProperty().addListener(ThreatGenerator.createThreatTitleChangeListener(generatedThreat, name2Template, name2));

        return generatedThreat;
    }
}
