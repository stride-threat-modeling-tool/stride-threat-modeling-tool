package ch.zhaw.threatmodeling.model.threats.patterns;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Inclusion {
    @JsonProperty
    private String source;
    @JsonProperty
    private String target;
    @JsonProperty
    private boolean requiresTrustBoundaryIntersection;

    public Inclusion(){}

    public Inclusion(String source, String target, boolean requiresTrustBoundaryIntersection) {
        this.source = source;
        this.target = target;
        this.requiresTrustBoundaryIntersection = requiresTrustBoundaryIntersection;
    }

    public boolean matches(String source, String target, boolean intersectTrustBoundary) {
        return  (this.source.equals("")  || source.contains(this.source)) &&
                (this.target.equals("") || target.contains(this.target)) &&
                (!requiresTrustBoundaryIntersection || intersectTrustBoundary);

    }
}
