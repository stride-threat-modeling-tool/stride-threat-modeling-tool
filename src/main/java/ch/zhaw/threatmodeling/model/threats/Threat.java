package ch.zhaw.threatmodeling.model.threats;

import ch.zhaw.threatmodeling.model.enums.ThreatPriority;
import ch.zhaw.threatmodeling.model.enums.STRIDECategory;
import ch.zhaw.threatmodeling.model.enums.State;
import ch.zhaw.threatmodeling.persistence.utils.objects.ThreatObject;
import ch.zhaw.threatmodeling.skin.DataFlowElement;
import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import de.tesis.dynaware.grapheditor.model.GConnection;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Threat {
    private static final Logger LOGGER = Logger.getLogger("Threat");
    public static final ThreatPriority DEFAULT_THREAT_PRIORITY = ThreatPriority.HIGH;

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final ObjectProperty<State> state = new SimpleObjectProperty<>();
    private final StringProperty title = new SimpleStringProperty();
    private final String titleTemplate;
    private final String descriptionTemplate;
    private final Map<String, String> templateMap = new HashMap<>();
    private final ObjectProperty<STRIDECategory> category = new SimpleObjectProperty<>();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty justification = new SimpleStringProperty();
    private final ObjectProperty<DataFlowElement> interaction = new SimpleObjectProperty<>();
    private final ObjectProperty<ThreatPriority> priority = new SimpleObjectProperty<>();
    private final GConnection connection;
    private final GenericNodeSkin nodeName1;
    private final GenericNodeSkin nodeName2;


    public GenericNodeSkin getNodeName1() {
        return nodeName1;
    }

    public GenericNodeSkin getNodeName2() {
        return nodeName2;
    }

    private boolean modified = false;


    private void initTemplateMap(){
        updateTemplate(ThreatConstants.SOURCE_NAME_TEMPLATE, getNodeName1().getText());
        updateTemplate(ThreatConstants.TARGET_NAME_TEMPLATE, getNodeName2().getText());
        updateTemplate(ThreatConstants.FLOW_NAME_TEMPLATE, getInteraction().getText());
    }

    public Threat(int id,
                  State state,
                  STRIDECategory category,
                  String titleTemplate,
                  String descriptionTemplate,
                  String justification,
                  DataFlowElement interaction,
                  GConnection connection,
                  GenericNodeSkin nodeName1,
                  GenericNodeSkin nodeName2
    ) {
        this.titleTemplate = titleTemplate;
        this.descriptionTemplate = descriptionTemplate;
        setId(id);
        setState(state);
        setCategory(category);
        setJustification(justification);
        setInteraction(interaction);
        this.connection = connection;
        this.nodeName1 = nodeName1;
        this.nodeName2 = nodeName2;
        setPriority(DEFAULT_THREAT_PRIORITY);
        initThreat();
    }

    private void initThreat() {
        initTemplateMap();
        updateThreat();
        nodeName1.textProperty().addListener(ThreatGenerator.createElementTextChangeListener(this, ThreatConstants.SOURCE_NAME_TEMPLATE, nodeName1));
        nodeName2.textProperty().addListener(ThreatGenerator.createElementTextChangeListener(this, ThreatConstants.TARGET_NAME_TEMPLATE, nodeName2));
        interaction.get().textProperty().addListener(ThreatGenerator.createElementTextChangeListener(this, ThreatConstants.FLOW_NAME_TEMPLATE, interaction.get()));
    }

    public Threat(ThreatObject restoredThreat,
                  DataFlowElement interaction,
                  GConnection connection,
                  GenericNodeSkin nodeName1,
                  GenericNodeSkin nodeName2){
        setId(restoredThreat.getId());
        setState(restoredThreat.getState());
        setTitle(restoredThreat.getTitle());
        titleTemplate = restoredThreat.getTitleTemplate();
        descriptionTemplate = restoredThreat.getDescriptionTemplate();
        setDescription(restoredThreat.getDescription());
        setCategory(restoredThreat.getCategory());
        setJustification(restoredThreat.getJustification());
        setPriority(restoredThreat.getPriority());
        setInteraction(interaction);
        this.connection = connection;
        this.nodeName1 = nodeName1;
        this.nodeName2 = nodeName2;
        initThreat();

    }

    public void updateThreat() {
        String updatedTitle = titleTemplate;
        String updatedDescription = descriptionTemplate;
        for(Map.Entry<String, String> entry :templateMap.entrySet()) {
            updatedTitle = updatedTitle.replace(entry.getKey(), entry.getValue());
            updatedDescription = updatedDescription.replace(entry.getKey(), entry.getValue());
        }
        setTitle(updatedTitle);
        setDescription(updatedDescription);
    }

    public void updateTemplate(String key, String value) {
        templateMap.put(key, value);
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

    public String getTitleTemplate() {
        return titleTemplate;
    }

    public String getDescriptionTemplate() {
        return descriptionTemplate;
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
}
