package ch.zhaw.threatmodeling.persistence.utils.objects;

/**
 * Helper class to prepare nodes to be translated into JSON
 */
public class DataFlowNodeObject extends DataFlowPositionedObject {
    private final double width;
    private final double height;

    public DataFlowNodeObject(String type, String text, double x, double y, double width, double height) {
        super(type, text, x, y);
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
