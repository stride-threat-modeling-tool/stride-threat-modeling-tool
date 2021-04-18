package ch.zhaw.threatmodeling.model.enums;

public enum STRIDECategory {
    SPOOFING("Spoofing"),
    TAMPERING("Tampering"),
    REPUDIATION("Repudiation"),
    INFORMATION_DISCLOSURE("Information Disclosure"),
    ELEVATION_OF_PRIVILEGE("Elevation Of Privilege"),
    DENIAL_OF_SERVICE("Denial Of Service");

    private final String text;

    private String getText() {
        return text;
    }

    STRIDECategory(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return getText();
    }
}
