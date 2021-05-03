package ch.zhaw.threatmodeling.persistence.utils.objects;

import java.util.List;

public class STRIDEModel {
    private List<DataFlowNodeObject> savableNodes;
    private List<DataFlowConnectionObject> savableConnections;

    public STRIDEModel(List<DataFlowNodeObject> savableNodes, List<DataFlowConnectionObject> savableConnections) {
        this.savableNodes = savableNodes;
        this.savableConnections = savableConnections;
    }

    public List<DataFlowNodeObject> getSavableNodes(){
        return savableNodes;
    }

    public List<DataFlowConnectionObject> getSavableConnections() {
        return savableConnections;
    }
}
