package ch.zhaw.threatmodeling.persistence.utils.objects;

import java.util.ArrayList;
import java.util.List;

public class STRIDEModel {
    private final List<DataFlowNodeObject> savableNodes;
    private final List<DataFlowConnectionObject> savableConnections;
    private final List<ThreatObject> savableThreats;

    public STRIDEModel(){
        this.savableNodes = new ArrayList<>();
        this.savableConnections = new ArrayList<>();
        this.savableThreats = new ArrayList<>();
    }

    public STRIDEModel(List<DataFlowNodeObject> savableNodes, List<DataFlowConnectionObject> savableConnections, List<ThreatObject> savableThreats) {
        this.savableNodes = savableNodes;
        this.savableConnections = savableConnections;
        this.savableThreats = savableThreats;
    }

    public List<DataFlowNodeObject> getSavableNodes(){
        return savableNodes;
    }

    public List<DataFlowConnectionObject> getSavableConnections() {
        return savableConnections;
    }

    public List<ThreatObject> getSavableThreats() {
        return savableThreats;
    }
}
