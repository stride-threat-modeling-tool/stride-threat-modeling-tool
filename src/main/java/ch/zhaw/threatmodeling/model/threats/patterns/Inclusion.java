package ch.zhaw.threatmodeling.model.threats.patterns;

public class Inclusion {
    private final String source;
    private final String target;
    private final boolean requiresTrustBoundaryIntersection;

    public Inclusion(String source, String target, boolean requiresTrustBoundaryIntersection) {
        this.source = source;
        this.target = target;
        this.requiresTrustBoundaryIntersection = requiresTrustBoundaryIntersection;
    }

    public boolean matches(String source, String target, boolean requiresTrustBoundaryIntersection) {
        return source.equals(this.source) && target.equals(this.target) && requiresTrustBoundaryIntersection == this.requiresTrustBoundaryIntersection;
    }
}
