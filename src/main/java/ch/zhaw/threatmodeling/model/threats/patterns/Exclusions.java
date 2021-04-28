package ch.zhaw.threatmodeling.model.threats.patterns;

import java.util.*;

public class Exclusions {
    private final List<Exclusion> loadedExclusions;

    public Exclusions(List<Exclusion> loadedExclusions) {
        this.loadedExclusions = Objects.requireNonNullElse(loadedExclusions, Collections.emptyList());

    }

    public boolean matchesAny(String source, String target, Map<String, String> sourceAttributes, Map<String, String> targetAttributes, Map<String, String> flowExclusionAttributes) {
        boolean foundMatch = false;
        Iterator<Exclusion> iter = loadedExclusions.iterator();
        while (!foundMatch && iter.hasNext()) {
            Exclusion currentExclusion = iter.next();
            foundMatch = currentExclusion.matches(source, target, sourceAttributes, targetAttributes, flowExclusionAttributes);
        }
        return foundMatch;
    }
}
