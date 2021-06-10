package ch.zhaw.threatmodeling.model.threats.patterns;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Exclusion {
    @JsonProperty
    private String source;
    @JsonProperty
    private String target;
    @JsonProperty
    private Map<String, String> sourceExclusionAttributes;
    @JsonProperty
    private Map<String, String> flowExclusionAttributes;
    @JsonProperty
    private Map<String, String> targetExclusionAttributes;

    public Exclusion(){

    }

    public Exclusion(String source, String target, Map<String, String> sourceExclusionAttributes, Map<String, String> flowExclusionAttributes, Map<String, String> targetExclusionAttributes) {
        this.source = source;
        this.target = target;
        this.sourceExclusionAttributes = sourceExclusionAttributes;
        this.flowExclusionAttributes = flowExclusionAttributes;
        this.targetExclusionAttributes = targetExclusionAttributes;
    }

    /**
     * Is already in place for a future expansion, but alas is not implemented properly at the moment
     * @param source element type of source
     * @param target element type of target
     * @param sourceAttributes all attributes and corresponding values of the source element
     * @param targetAttributes all attributes and corresponding values of the target element
     * @return if these parameters match the criteria for exclusion
     */
    public boolean matches(String source, String target, Map<String, String> sourceAttributes, Map<String, String> targetAttributes, Map<String, String> flowExclusionAttributes) {
        return false;
    }
}
