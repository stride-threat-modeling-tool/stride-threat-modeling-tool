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

    public boolean matches(String source, String target, boolean intersectTrustBoundary) {
        return  (this.source.equals("")  || source.equals(this.source)) &&
                (this.target.equals("") || target.equals(this.target)) &&
                (!requiresTrustBoundaryIntersection || intersectTrustBoundary);

    }
}
