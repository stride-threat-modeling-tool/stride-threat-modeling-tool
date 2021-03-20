package ch.zhaw.threatmodeling.skin;

import javafx.css.PseudoClass;

/**
 * Some constants used by the grey skins.
 */
public class DataFlowSkinConstants {

    public static final String DFD_NODE = "dfd-node";
    public static final String DFD_LEFT_CONNECTOR = "dfd-left";
    public static final String DFD_TOP_CONNECTOR = "dfd-top";
    public static final String DFD_RIGHT_CONNECTOR = "dfd-right";
    public static final String DFD_BOTTOM_CONNECTOR = "dfd-bottom";


    public static final PseudoClass PSEUDO_CLASS_ALLOWED = PseudoClass.getPseudoClass("allowed");
    public static final PseudoClass PSEUDO_CLASS_FORBIDDEN = PseudoClass.getPseudoClass("forbidden");
    public static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");
}
