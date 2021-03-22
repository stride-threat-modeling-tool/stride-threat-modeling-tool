package ch.zhaw.threatmodeling.model;

import ch.zhaw.threatmodeling.model.enums.STRIDECategory;
import ch.zhaw.threatmodeling.model.enums.State;
import ch.zhaw.threatmodeling.skin.DataFlowElement;

public class Threat {
    public static final String DEFAULT_PRIORITY = "High";

    private int id;
    private State state;
    private String title;
    private STRIDECategory category;
    private String description;
    private String justification;
    private DataFlowElement interaction;
    private String priority;


    public Threat(int id,
                  State state,
                  String title,
                  STRIDECategory category,
                  String description,
                  String justification,
                  DataFlowElement interaction) {
        this.id = id;
        this.state = state;
        this.title = title;
        this.category = category;
        this.description = description;
        this.justification = justification;
        this.interaction = interaction;
        this.priority = DEFAULT_PRIORITY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public STRIDECategory getCategory() {
        return category;
    }

    public void setCategory(STRIDECategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public DataFlowElement getInteraction() {
        return interaction;
    }

    public void setInteraction(DataFlowElement interaction) {
        this.interaction = interaction;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }


}
