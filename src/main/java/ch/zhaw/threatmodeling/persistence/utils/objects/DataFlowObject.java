package ch.zhaw.threatmodeling.persistence.utils.objects;

public abstract class  DataFlowObject {
    private final String type;

    protected DataFlowObject(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
