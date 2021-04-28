package ch.zhaw.threatmodeling.model.threats.patterns;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Inclusions {
    private final List<Inclusion> loadedInclusions;

    public Inclusions(List<Inclusion> inclusions) {
       this.loadedInclusions = Objects.requireNonNullElse(inclusions, Collections.emptyList());
    }

    public boolean matchesAny(String source, String target, boolean requiresTrustBoundaryIntersection) {
        boolean foundMatch = false;
        Iterator<Inclusion> iter = loadedInclusions.iterator();
        while (!foundMatch && iter.hasNext()) {
            Inclusion currentInclusion = iter.next();
            foundMatch = currentInclusion.matches(source, target, requiresTrustBoundaryIntersection);
        }
        return foundMatch;
    }
}
