package ch.zhaw.threatmodeling.model.threats;

public class ThreatConstants {
    private ThreatConstants(){}

    public static final String DATA_STORE_SPOOFING_GENERIC_TEXT = "${name1} may be spoofed by an attacker and this may lead to ${specificText} ${name2}." +
            " Consider using a standard authentication mechanism to identify the ${srcOrDest} data store.";
    public static final String DATA_STORE_SPOOFING_DESTINATION_TEXT = "data being written to the attacker's target instead of";
    public static final String DATA_STORE_SPOOFING_SOURCE_TEXT = "incorrect data delivered to";
    public static final String DATA_STORE_SPOOFING_TARGET_ID = "TargetDataStoreSpoofing";
    public static final String DATA_STORE_SPOOFING_SOURCE_ID = "SourceDataStoreSpoofing";

    public static final String SOURCE_NAME_TEMPLATE = "{source.Name}";
    public static final String TARGET_NAME_TEMPLATE = "{target.Name}";
    public static final String FLOW_NAME_TEMPLATE = "{flow.Name}";
}