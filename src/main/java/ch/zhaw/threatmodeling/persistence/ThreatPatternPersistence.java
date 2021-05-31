package ch.zhaw.threatmodeling.persistence;

import ch.zhaw.threatmodeling.model.threats.patterns.ThreatPatterns;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class ThreatPatternPersistence {
    private static final String PATTERN_RESOURCE_FILE = "ch/zhaw/threatmodeling/ThreatPatterns.json";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger LOGGER = Logger.getLogger(ThreatPatternPersistence.class.getName());

    private ThreatPatternPersistence(){}

    public static ThreatPatterns loadThreatPatterns() {
        ThreatPatterns patterns = null;
        InputStream resource = ThreatPatternPersistence.class.getClassLoader().getResourceAsStream(PATTERN_RESOURCE_FILE);
        if(resource == null){
            LOGGER.severe("resource file for threat patterns not found");
        } else {
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8))) {
                patterns = mapper.readValue(reader, ThreatPatterns.class);
            } catch (IOException e){
                LOGGER.severe("An exception occurred during the reading of the threat pattern file " + e.getMessage());
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
                mapper.writerWithDefaultPrettyPrinter().writeValue(writer, patterns);
            } catch (IOException e) {
                LOGGER.warning("Could not save model" + e.getMessage());
            }
        }
    }
}
