package ch.zhaw.threatmodeling.persistence.utils.objects;

import ch.zhaw.threatmodeling.model.enums.STRIDECategory;
import ch.zhaw.threatmodeling.model.enums.State;
import ch.zhaw.threatmodeling.model.enums.ThreatPriority;

public class ThreatObject {
    private final int id;
    private final State state;
    private final String title;
    private final String titleTemplate;
    private final String descriptionTemplate;
    private final String description;
    private final STRIDECategory category;
    private final String justification;
    private final ThreatPriority priority;
    private final int connectionIndex;
    private final int nodeName1Index;
    private final int nodeName2Index;

    public ThreatObject(
            int id,
            State state,
            String title,
            String titleTemplate,
            String descriptionTemplate,
            STRIDECategory category,
            String description,
            String justification,
            ThreatPriority priority,
            int connectionIndex,
            int nodeName1Index,
            int nodeName2Index) {
        this.id = id;
        this.state = state;
        this.title = title;
        this.titleTemplate = titleTemplate;
        this.descriptionTemplate = descriptionTemplate;
        this.category = category;
        this.description = description;
        this.justification = justification;
        this.priority = priority;
        this.connectionIndex = connectionIndex;
        this.nodeName1Index = nodeName1Index;
        this.nodeName2Index = nodeName2Index;
    }

    public int getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleTemplate() {
        return titleTemplate;
    }

    public String getDescriptionTemplate() {
        return descriptionTemplate;
    }

    public STRIDECategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getJustification() {
        return justification;
    }

    public ThreatPriority getPriority() {
        return priority;
    }

    public int getConnectionIndex() {
        return connectionIndex;
    }

    public int getNodeName1Index() {
        return nodeName1Index;
    }

    public int getNodeName2Index() {
        return nodeName2Index;
    }
}
