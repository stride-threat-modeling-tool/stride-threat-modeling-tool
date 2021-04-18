package ch.zhaw.threatmodeling.persistence.utils.objects;

/**
 * Used for the elements node and joint
 */
public class DataFlowPositionedObject extends DataFlowObject {
    private final double x;
    private final double y;
    private final String text;

    public DataFlowPositionedObject(String type, String text, double x, double y) {
        super(type);
        this.text = text;
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getText() {
        return text;
    }
}
