package ch.zhaw.threatmodeling.persistence.utils.objects;

public abstract class DataFlowObject {
    private String type;

    protected DataFlowObject(String type) {
        this.type = type;
    }

    public DataFlowObject(){}

    public String getType() {
        return type;
    }
}
