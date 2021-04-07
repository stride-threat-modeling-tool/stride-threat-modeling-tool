package ch.zhaw.threatmodeling.selections.utils;

import de.tesis.dynaware.grapheditor.model.GConnection;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class ConnectionMaps {
    Map<GConnection, Pair<String, String>> connectionTypeTextMap = new HashMap<>();

    public Map<GConnection, Pair<String, String>> getConnectionTypeTextMap() {
        return connectionTypeTextMap;
    }


    public void putConnection(GConnection connection, String type, String text){
        connectionTypeTextMap.put(connection , new Pair<>(type, text));
    }

    public String getConnectionType(GConnection connection){
        return connectionTypeTextMap.get(connection).getKey();
    }

    public String getConnectionText(GConnection connection){
        return connectionTypeTextMap.get(connection).getValue();
    }
}
