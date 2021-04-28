package ch.zhaw.threatmodeling.model.threats.patterns;

import ch.zhaw.threatmodeling.model.enums.STRIDECategory;
import ch.zhaw.threatmodeling.model.enums.State;
import ch.zhaw.threatmodeling.model.threats.Threat;
import ch.zhaw.threatmodeling.model.threats.ThreatConstants;
import ch.zhaw.threatmodeling.model.threats.ThreatGenerator;
import ch.zhaw.threatmodeling.skin.DataFlowElement;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import de.tesis.dynaware.grapheditor.model.GConnection;

import java.util.Map;

public class ThreatPattern {
    private final String id;
    private final Inclusions inclusions;
    private final Exclusions exclusions;
    private final String descriptionTemplate;
    private final String titleTemplate;
    private final STRIDECategory strideCategory;

    public ThreatPattern(String id, Inclusions inclusions, Exclusions exclusions, String descriptionTemplate, String titleTemplate, STRIDECategory strideCategory) {
        this.id = id;
        this.inclusions = inclusions;
        this.exclusions = exclusions;
        this.descriptionTemplate = descriptionTemplate;
        this.titleTemplate = titleTemplate;
        this.strideCategory = strideCategory;
    }

    public boolean shouldBeGenerated(String source,
                                     String target,
                                     boolean intersectsTrustBoundary,
                                     Map<String, String> sourceAttributes,
                                     Map<String, String> targetAttributes,
                                     Map<String, String> flowExclusionAttributes) {

        return inclusions.matchesAny(source,target, intersectsTrustBoundary)
                && !exclusions.matchesAny(source, target, sourceAttributes, targetAttributes, flowExclusionAttributes);
    }

    public Threat generate(int numberId, DataFlowElement interaction, GConnection connection, GenericNodeSkin node1, GenericNodeSkin node2) {
        Threat threat = new Threat(
                numberId,
                this.id,
                State.NOT_STARTED,
                this.strideCategory,
                this.titleTemplate,
                this.descriptionTemplate,
                "",
                interaction,
                connection,
                node1,
                node2
        );
        threat.addTemplate(ThreatConstants.SOURCE_NAME_TEMPLATE, node1.getText());
        threat.addTemplate(ThreatConstants.TARGET_NAME_TEMPLATE, node2.getText());
        threat.updateThreat();
        node1.textProperty().addListener(ThreatGenerator.createThreatTitleChangeListener(threat, ThreatConstants.SOURCE_NAME_TEMPLATE, node1));
        node2.textProperty().addListener(ThreatGenerator.createThreatTitleChangeListener(threat, ThreatConstants.TARGET_NAME_TEMPLATE, node2));
        return threat;
    }
}
