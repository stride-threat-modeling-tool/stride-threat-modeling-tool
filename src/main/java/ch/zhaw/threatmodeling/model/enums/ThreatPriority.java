package ch.zhaw.threatmodeling.model.enums;

public enum ThreatPriority {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low");

    private final String text;

    private String getText(){
        return text;
    }

    ThreatPriority(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return getText();
    }
}
