package ch.zhaw.threatmodeling.model.threats.patterns;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Inclusions {
    @JsonProperty
    private final List<Inclusion> loadedInclusions;

    public Inclusions(List<Inclusion> inclusions) {
        this.loadedInclusions = Objects.requireNonNullElse(inclusions, Collections.emptyList());
    }

    public Inclusions() {
        this.loadedInclusions = new ArrayList<>();
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
