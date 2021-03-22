package ch.zhaw.threatmodeling.model.enums;

public enum State {
    NOT_STARTED("Not Started"),
    NEEDS_INVESTIGATION("Needs Investigation"),
    NOT_APPLICABLE("Not Applicable"),
    MITIGATED("Mitigated");


    private final String text;

    private String getText(){
        return text;
    }

    State(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return getText();
    }
}
