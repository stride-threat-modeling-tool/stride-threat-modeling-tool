package ch.zhaw.threatmodeling.model;

import ch.zhaw.threatmodeling.model.enums.ThreatPriority;
import ch.zhaw.threatmodeling.model.enums.STRIDECategory;
import ch.zhaw.threatmodeling.model.enums.State;
import ch.zhaw.threatmodeling.skin.DataFlowElement;
import javafx.beans.property.*;

public class Threat {
    public static final ThreatPriority DEFAULT_THREAT_PRIORITY = ThreatPriority.HIGH;

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final ObjectProperty<State> state = new SimpleObjectProperty<>();
    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<STRIDECategory> category= new SimpleObjectProperty<>();
    private final StringProperty description  = new SimpleStringProperty();
    private final StringProperty justification  = new SimpleStringProperty();
    private final ObjectProperty<DataFlowElement> interaction = new SimpleObjectProperty<>();
    private final ObjectProperty<ThreatPriority> priority  = new SimpleObjectProperty<>();


    public Threat(int id,
                  State state,
                  String title,
                  STRIDECategory category,
                  String description,
                  String justification,
                  DataFlowElement interaction) {
       setId(id);
       setState(state);
       setTitle(title);
       setCategory(category);
       setDescription(description);
       setJustification(justification);
       setInteraction(interaction);
       setPriority(DEFAULT_THREAT_PRIORITY);
    }

    public StringProperty getDescriptionProperty() {
        return description;
    }

    public Property<STRIDECategory> getCategoryProperty() {
        return category;
    }

    public Property<State> getStateProperty(){
        return state;
    }

    public Property<ThreatPriority> getPriorityProperty(){
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



}
