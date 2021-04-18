package ch.zhaw.threatmodeling.model;

import ch.zhaw.threatmodeling.model.enums.ThreatPriority;
import ch.zhaw.threatmodeling.model.enums.STRIDECategory;
import ch.zhaw.threatmodeling.model.enums.State;
import ch.zhaw.threatmodeling.skin.DataFlowElement;
import de.tesis.dynaware.grapheditor.model.GConnection;
import javafx.beans.property.*;
import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Threat {
    private static final Logger LOGGER = Logger.getLogger("Threat");
    public static final ThreatPriority DEFAULT_THREAT_PRIORITY = ThreatPriority.HIGH;

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final ObjectProperty<State> state = new SimpleObjectProperty<>();
    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<STRIDECategory> category = new SimpleObjectProperty<>();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty justification = new SimpleStringProperty();
    private final ObjectProperty<DataFlowElement> interaction = new SimpleObjectProperty<>();
    private final ObjectProperty<ThreatPriority> priority = new SimpleObjectProperty<>();
    private final GConnection connection;
    private final Map<String, String> templateMap = new HashMap<>();
    private boolean modified = false;


    public Threat(int id,
                  State state,
                  String title,
                  STRIDECategory category,
                  String description,
                  String justification,
                  DataFlowElement interaction,
                  GConnection connection) {
        setId(id);
        setState(state);
        setTitle(title);
        setCategory(category);
        setDescription(description);
        setJustification(justification);
        setInteraction(interaction);
        this.connection = connection;
        setPriority(DEFAULT_THREAT_PRIORITY);
    }

    public void updateThreatElementNames(String oldName, String newName) {
        LOGGER.info("update threat: from name " + oldName + " to name " + newName);
        updateThreatElementNames();
    }

    public void updateThreatElementNames() {
        final StringSubstitutor substitutor = new StringSubstitutor(templateMap);
        description.set(substitutor.replace(description.get()));
        title.set(substitutor.replace(title.get()));
    }

    public StringProperty getDescriptionProperty() {
        return description;
    }

    public Property<STRIDECategory> getCategoryProperty() {
        return category;
    }

    public Property<State> getStateProperty() {
        return state;
    }

    public Property<ThreatPriority> getPriorityProperty() {
        return priority;
    }

    public StringProperty getTitleProperty() {
        return title;
    }

    public StringProperty getJustificationProperty() {
        return justification;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public State getState() {
        return state.get();
    }

    public void setState(State state) {
        this.state.set(state);
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public STRIDECategory getCategory() {
        return category.get();
    }

    public void setCategory(STRIDECategory category) {
        this.category.set(category);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getJustification() {
        return justification.get();
    }

    public void setJustification(String justification) {
        this.justification.set(justification);
    }

    public DataFlowElement getInteraction() {
        return interaction.get();
    }

    public void setInteraction(DataFlowElement interaction) {
        this.interaction.set(interaction);
    }

    public ThreatPriority getPriority() {
        return priority.get();
    }

    public void setPriority(ThreatPriority threatPriority) {
        this.priority.set(threatPriority);
    }


    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public GConnection getConnection() {
        return connection;
    }

    public Map<String, String> getTemplateMap() {
        return templateMap;
    }
}
