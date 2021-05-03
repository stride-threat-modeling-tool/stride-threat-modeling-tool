package ch.zhaw.threatmodeling.model.threats.patterns;

import java.util.List;

public class ThreatPatterns {
    private final List<ThreatPattern> all;

    public ThreatPatterns(List<ThreatPattern> all) {
        this.all = all;
    }

    public List<ThreatPattern> getAll() {
        return all;
    }

    public int size() {
        return all.size();
    }

    public void forEach(java.util.function.Consumer<? super ThreatPattern> action){
        all.forEach(action);
    }
}
