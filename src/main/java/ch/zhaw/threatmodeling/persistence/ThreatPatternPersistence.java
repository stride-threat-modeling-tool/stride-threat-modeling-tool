package ch.zhaw.threatmodeling.persistence;

import ch.zhaw.threatmodeling.model.threats.patterns.ThreatPattern;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ThreatPatternPersistence {
    private static final String PATTERN_RESOURCE_FILE = "ch/zhaw/threatmodeling/ThreatPatterns.json";
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = Logger.getLogger(ThreatPatternPersistence.class.getName());


    public static List<ThreatPattern> loadThreatPatterns() {
        List<ThreatPattern> patterns = new ArrayList<>();
        URL resource = ThreatPatternPersistence.class.getClassLoader().getResource(PATTERN_RESOURCE_FILE);
        if(resource == null){
            LOGGER.severe("resource file for threat patterns not found");
        } else {
            try(BufferedReader reader = new BufferedReader(new FileReader(resource.getFile()))) {
                String line;
                while((line = reader.readLine()) != null){
                    patterns.add(GSON.fromJson(line, ThreatPattern.class));
                }
            } catch (IOException e){
                LOGGER.severe("An exception occurred during the reading of the threat pattern file");
            }
        }
        return patterns;
    }

    public static void saveThreatPatterns( List<ThreatPattern> patterns) {
       StringBuilder builder = new StringBuilder();
       patterns.forEach(pattern -> builder.append(GSON.toJson(pattern)));
       LOGGER.info(builder.toString());
    }
}
