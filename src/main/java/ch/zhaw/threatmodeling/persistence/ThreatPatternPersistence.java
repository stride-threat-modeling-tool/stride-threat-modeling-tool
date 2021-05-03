package ch.zhaw.threatmodeling.persistence;

import ch.zhaw.threatmodeling.model.threats.patterns.ThreatPatterns;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

public class ThreatPatternPersistence {
    private static final String PATTERN_RESOURCE_FILE = "ch/zhaw/threatmodeling/ThreatPatterns.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = Logger.getLogger(ThreatPatternPersistence.class.getName());

    private ThreatPatternPersistence(){}

    public static ThreatPatterns loadThreatPatterns() {
        ThreatPatterns patterns = null;
        URL resource = ThreatPatternPersistence.class.getClassLoader().getResource(PATTERN_RESOURCE_FILE);
        if(resource == null){
            LOGGER.severe("resource file for threat patterns not found");
        } else {
            try(BufferedReader reader = new BufferedReader(new FileReader(resource.getFile()))) {
               patterns = GSON.fromJson(reader, ThreatPatterns.class);
            } catch (JsonIOException | IOException e){
                LOGGER.severe("An exception occurred during the reading of the threat pattern file");
            }
        }
        return patterns;
    }

    public static void saveThreatPatterns(ThreatPatterns patterns) {
        URL resource = ThreatPatternPersistence.class.getClassLoader().getResource(PATTERN_RESOURCE_FILE);
        if(resource == null){
            LOGGER.severe("resource file for threat patterns not found");
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(resource.getFile(), false))) {
                GSON.toJson(patterns, writer);
            } catch (JsonIOException | IOException e) {
                LOGGER.warning("Could not save model" + e.getMessage());
            }
        }
    }
}
