package ch.zhaw.threatmodeling.skin.utils;

import ch.zhaw.threatmodeling.skin.nodes.generic.GenericNodeSkin;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.model.GNode;
import javafx.util.Pair;

public class DataFlowNodeCommands {
    private DataFlowNodeCommands(){}

    public static String getTypeOfNode(GNode node, SkinLookup skinLookup){
        return ((GenericNodeSkin) skinLookup.lookupNode(node)).getType();
    }

    public static String getTextOfNode(GNode node, SkinLookup skinLookup){
        return ((GenericNodeSkin) skinLookup.lookupNode(node)).getText();
    }

    public static Pair<String, String> getTypeAndTextOfNode(GNode node, SkinLookup skinLookup){
        return new Pair<>(getTypeOfNode(node, skinLookup), getTextOfNode(node, skinLookup));
    }
}
