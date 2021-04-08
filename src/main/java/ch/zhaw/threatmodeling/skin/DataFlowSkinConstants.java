package ch.zhaw.threatmodeling.skin;

import ch.zhaw.threatmodeling.skin.joint.DataFlowJointSkin;
import ch.zhaw.threatmodeling.skin.joint.TrustBoundaryJointSkin;
import javafx.css.PseudoClass;

import java.util.Arrays;
import java.util.List;

/**
 * Some constants used by the grey skins.
 */
public class DataFlowSkinConstants {
    private DataFlowSkinConstants(){}

    public static final String DFD_NODE = "dfd-node";
    public static final String DFD_TRUST_BOUNDARY_NODE = "dfd-trust-boundary-node";

    public static final String DFD_LEFT_CENTER_CONNECTOR = "dfd-left-center";
    public static final String DFD_RIGHT_CENTER_CONNECTOR = "dfd-right-center";
    public static final String DFD_TOP_LEFT_CONNECTOR = "dfd-upper-left-corner";
    public static final String DFD_TOP_CENTER_CONNECTOR = "dfd-top-center";
    public static final String DFD_TOP_RIGHT_CONNECTOR = "dfd-upper-right-corner";
    public static final String DFD_BOTTOM_LEFT_CONNECTOR = "dfd-lower-left-corner";
    public static final String DFD_BOTTOM_CENTER_CONNECTOR = "dfd-bottom-center";
    public static final String DFD_BOTTOM_RIGHT_CONNECTOR = "dfd-lower-right-corner";

    public static final String DFD_TRUST_BOUNDARY_CONNECTOR = "dfd-trust-boundary-connector";
    public static final String DFD_TRUST_BOUNDARY_CONNECTION = "dfd-trust-boundary-connection";
    public static final String DFD_TRUST_BOUNDARY_JOINT = "dfd-trust-boundary-joint";
    public static final double DFD_JOINT_SPAWN_OFFSET = 22.5;

    public static final PseudoClass PSEUDO_CLASS_ALLOWED = PseudoClass.getPseudoClass("allowed");
    public static final PseudoClass PSEUDO_CLASS_FORBIDDEN = PseudoClass.getPseudoClass("forbidden");
    public static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");

    /*
    defines the order in which the connectors are laid out, changing it can have unexpected consequences.
    If this is changed, at least getConnectors Position of GenericEllipseNode has to be modified.
     */
    public static final List<String> DFD_CONNECTOR_LAYOUT_ORDER = Arrays.asList(DFD_TOP_RIGHT_CONNECTOR,
            DFD_RIGHT_CENTER_CONNECTOR,
            DFD_BOTTOM_RIGHT_CONNECTOR,
            DFD_BOTTOM_CENTER_CONNECTOR,
            DFD_BOTTOM_LEFT_CONNECTOR,
            DFD_LEFT_CENTER_CONNECTOR,
            DFD_TOP_LEFT_CONNECTOR,
            DFD_TOP_CENTER_CONNECTOR);
    public static final List<String> DFD_CONNECTION_TYPES = Arrays.asList(
            DataFlowJointSkin.ELEMENT_TYPE,
            TrustBoundaryJointSkin.ELEMENT_TYPE
            );
}
