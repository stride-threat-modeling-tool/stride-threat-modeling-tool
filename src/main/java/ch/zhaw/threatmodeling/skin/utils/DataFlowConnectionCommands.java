package ch.zhaw.threatmodeling.skin.utils;

import ch.zhaw.threatmodeling.skin.DataFlowSkinConstants;
import ch.zhaw.threatmodeling.skin.joint.DataFlowJointSkin;
import ch.zhaw.threatmodeling.skin.joint.TrustBoundaryJointSkin;
import de.tesis.dynaware.grapheditor.GJointSkin;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.model.GConnection;
import javafx.util.Pair;

public class DataFlowConnectionCommands {
    private DataFlowConnectionCommands(){}

    public static String getJointLabel(GConnection connection, SkinLookup skinLookup){
        String result = "";
        GJointSkin skin = skinLookup.lookupJoint(connection.getJoints().get(0));
        if(skin instanceof DataFlowJointSkin){
            DataFlowJointSkin jointSkin = (DataFlowJointSkin)skin;
            result = jointSkin.getText();
        }
        if(skin instanceof  TrustBoundaryJointSkin){
            TrustBoundaryJointSkin jointSkin = (TrustBoundaryJointSkin)skin;
            result = jointSkin.getText();
        }
        return result;
    }

    public static String getType(GConnection connection, SkinLookup skinLookup){
        String result = "";
        GJointSkin skin = skinLookup.lookupJoint(connection.getJoints().get(0));
        if(skin instanceof DataFlowJointSkin){
            result = DataFlowJointSkin.ELEMENT_TYPE;
        }
        if(skin instanceof  TrustBoundaryJointSkin){
            result = TrustBoundaryJointSkin.ELEMENT_TYPE;
        }
        return result;
    }

    public static Pair<String, String> getTypeAndJointLabel(GConnection connection, SkinLookup skinLookup){
        return new Pair<>(getType(connection, skinLookup), getJointLabel(connection, skinLookup));
    }

    public static boolean isConnectionType(String type) {
        return DataFlowSkinConstants.DFD_CONNECTION_TYPES.contains(type);
    }
}
