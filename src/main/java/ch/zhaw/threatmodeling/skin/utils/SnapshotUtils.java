package ch.zhaw.threatmodeling.skin.utils;

import de.tesis.dynaware.grapheditor.GJointSkin;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorView;
import de.tesis.dynaware.grapheditor.model.GConnection;
import de.tesis.dynaware.grapheditor.model.GJoint;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.embed.swing.SwingFXUtils;

import javafx.geometry.BoundingBox;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

public class SnapshotUtils {
    private static final Logger LOGGER = Logger.getLogger("Snapshot Utils");

    private static final double BOUNDING_BOX_OFFSET = 50.0;

    private SnapshotUtils(){}

    private static WritableImage cropImage(WritableImage image, int x, int y, int width, int height) {
       PixelReader reader = image.getPixelReader();
       return new WritableImage(reader, x, y, width, height);
    }

    private static String encodeImageToBase64New(byte[] buffer) {
        return Base64.getEncoder().encodeToString(buffer);
    }

    private static byte[] convertSnapshotToPNGByteBuffer(WritableImage writableImage) {
        // Convert the image into a PNG
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
        BufferedImage imageRGB = new BufferedImage(
                bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.OPAQUE);
        Graphics2D graphics = imageRGB.createGraphics();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        graphics.drawImage(bufferedImage, 0, 0, null);
        try {
            ImageIO.write(imageRGB, "png", os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return os.toByteArray();
    }

    public static WritableImage takeSnapshotOfView(Node node) {
        SnapshotParameters param = new SnapshotParameters();
        param.setDepthBuffer(true);
        return node.snapshot(param, null);
    }

    public static String takeSnapshot(List<GConnection> connections, GraphEditorView graphEditorView, SkinLookup skinLookup) {
        // We take a snapshot of the whole graph editor view
        WritableImage image = takeSnapshotOfView(graphEditorView);

        // Find bounding box that surrounds all elements attached to the connections
        final BoundingBox boundingBox = calculateBoundingBox(connections, skinLookup);

        // Crop snapshot
        WritableImage croppedImage = cropImage(image,
                (int) boundingBox.getMinX(),
                (int) boundingBox.getMinY(),
                (int) boundingBox.getWidth(),
                (int) boundingBox.getHeight());

        // Convert snapshot to png byte array
        final byte[] buffer = convertSnapshotToPNGByteBuffer(croppedImage);

        // Convert byte buffer to base64 String
        return encodeImageToBase64New(buffer);
    }

    private static BoundingBox calculateBoundingBox(List<GConnection> connections, SkinLookup skinLookup) {
        double topLeftX = Integer.MAX_VALUE;
        double topLeftY = Integer.MAX_VALUE;
        double bottomRightX = 0;
        double bottomRightY = 0;

        List<GJoint> joints = new ArrayList<>();
        List<GNode> nodes = new ArrayList<>();

        // Get all joints and nodes
        for (GConnection con : connections) {
            joints.add(con.getJoints().get(0));
            nodes.add(con.getSource().getParent());
            nodes.add(con.getTarget().getParent());
        }

        // Find x & y values for top left and bottom right point of the bounding box
        // that contains all elements in the view
        for (GNode node : nodes) {
            if (node.getX() < topLeftX) {
                topLeftX = node.getX();
            }
            if (node.getY() < topLeftY) {
                topLeftY = node.getY();
            }
            if (node.getX() + node.getWidth() > bottomRightX) {
                bottomRightX = node.getX() + node.getWidth();
            }
            if (node.getY() + node.getHeight() > bottomRightY) {
                bottomRightY = node.getY() + node.getHeight();
            }
        }

        for (GJoint joint : joints) {
            GJointSkin jointSkin = skinLookup.lookupJoint(joint);
            if (joint.getX() < topLeftX) {
                topLeftX = joint.getX();
            }
            if (joint.getY() < topLeftY) {
                topLeftY = joint.getY();
            }
            if (joint.getX() + jointSkin.getWidth() > bottomRightX) {
                bottomRightX = joint.getX() + jointSkin.getWidth();
            }
            if (joint.getY() + jointSkin.getHeight() > bottomRightY) {
                bottomRightY = joint.getY() + jointSkin.getHeight();
            }
        }

        return createBoundingBox(topLeftX, topLeftY, bottomRightX, bottomRightY);
    }

    private static BoundingBox createBoundingBox(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
        final double x, y, width, height;

        // Add a margin to all sides of the bounding box to make the image look nicer
        // This way the borders of the bounding box don't touch the nodes, joints etc.
        // but leave some space around them

        if (topLeftX - BOUNDING_BOX_OFFSET <= 0) {
            topLeftX = 0;
        } else {
            topLeftX -= BOUNDING_BOX_OFFSET;
        }
        if (topLeftY - BOUNDING_BOX_OFFSET <= 0) {
            topLeftY = 0;
        } else {
            topLeftY -= BOUNDING_BOX_OFFSET;
        }
        bottomRightX += BOUNDING_BOX_OFFSET;
        bottomRightY += BOUNDING_BOX_OFFSET;

        // Create bounding box
        x = topLeftX;
        y = topLeftY;
        width = bottomRightX - topLeftX;
        height = bottomRightY - topLeftY;

        return new BoundingBox(x, y, width, height);
    }
}
