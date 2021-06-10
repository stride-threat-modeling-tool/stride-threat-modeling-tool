package ch.zhaw.threatmodeling.persistence.utils.objects;


/**
 * Helper class to prepare connections to be translated into JSON
 */
public class DataFlowConnectionObject extends DataFlowObject {
    private  int sourceNodeIndex;
    private  int targetNodeIndex;
    private  int sourceConnectorIndex;
    private  int targetConnectorIndex;

    private  DataFlowPositionedObject joint;

    public DataFlowConnectionObject(){}

    public DataFlowConnectionObject(String type, int sourceNodeIndex, int targetNodeIndex, int sourceConnectorIndex, int targetConnectorIndex, DataFlowPositionedObject joint) {
        super(type);
        this.sourceNodeIndex = sourceNodeIndex;
        this.targetNodeIndex = targetNodeIndex;
        this.sourceConnectorIndex = sourceConnectorIndex;
        this.targetConnectorIndex = targetConnectorIndex;
        this.joint = joint;
    }

    public int getSourceNodeIndex() {
        return sourceNodeIndex;
    }

    public int getTargetNodeIndex() {
        return targetNodeIndex;
    }

    public int getSourceConnectorIndex() {
        return sourceConnectorIndex;
    }

    public int getTargetConnectorIndex() {
        return targetConnectorIndex;
    }

    public DataFlowPositionedObject getJoint() {
        return joint;
    }
}
