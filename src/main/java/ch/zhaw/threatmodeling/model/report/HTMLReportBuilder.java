package ch.zhaw.threatmodeling.model.report;

import ch.zhaw.threatmodeling.model.enums.State;
import ch.zhaw.threatmodeling.model.threats.Threat;
import ch.zhaw.threatmodeling.model.threats.Threats;
import ch.zhaw.threatmodeling.persistence.HTMLReportPersistence;
import ch.zhaw.threatmodeling.skin.joint.TrustBoundaryJointSkin;
import ch.zhaw.threatmodeling.skin.utils.DataFlowConnectionCommands;
import ch.zhaw.threatmodeling.skin.utils.SnapshotUtils;
import de.tesis.dynaware.grapheditor.SkinLookup;
import de.tesis.dynaware.grapheditor.core.view.GraphEditorView;
import de.tesis.dynaware.grapheditor.model.GConnection;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class HTMLReportBuilder {
    private static final Logger LOGGER = Logger.getLogger("Report builder");
    private static final String TITLE = "Threat Modeling Report";
    private static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss";

    private final Threats threats;
    private final SkinLookup skinLookup;
    private final List<GConnection> connections;
    private final GraphEditorView graphEditorView;
    private final HTMLReportPersistence persistence;

    public HTMLReportBuilder(Threats threats, SkinLookup skinLookup, List<GConnection> connections, GraphEditorView graphEditorView, HTMLReportPersistence persistence) {
        this.threats = threats;
        this.skinLookup = skinLookup;
        this.connections = connections;
        this.graphEditorView = graphEditorView;
        this.persistence = persistence;
    }

    public void buildReport() {
        String path = persistence.getFilePath();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, false))) {
            startDocument(writer);
            writeTitle(writer, TITLE, 1);
            writeCreationDate(writer);
            writeThreatsSummary(writer);
            writeSeparationLine(writer);
            writeTitle(writer, "Diagram", 2);
            addDiagramImage(writer, connections);
            for(GConnection connection: connections){
                if(!DataFlowConnectionCommands.getType(connection, skinLookup).equals(TrustBoundaryJointSkin.ELEMENT_TYPE)){
                    writeInteractionSummary(writer, connection);
                }
            }
            endDocument(writer);
        } catch (IOException e) {
            LOGGER.warning("Could not generate report" + e.getMessage());
        }
    }

    private void writeInteractionSummary(BufferedWriter writer, GConnection con) throws IOException {
        List<Threat> threatList =  threats.getAllForConnection(con);
        if(!threatList.isEmpty()) {
            writeTitle(writer, "Interaction: " + DataFlowConnectionCommands.getJointLabel(con, skinLookup), 3);
            addImageForInteraction(writer, con);
            int threatCnt = 1;
            for(Threat threat: threatList){
                writeThreatDetails(writer, threat, threatCnt++);
            }
            writeSeparationLine(writer);
        }
    }

    private void writeThreatDetails(BufferedWriter writer, Threat threat, int number) throws IOException {
        writeLine(writer, "<div class=\"threat-detail\">");
        writeTitle(writer, buildThreatDetailsTitle(threat, number), 3);
        writeTableStart(writer);
        writeTableRow2Columns(writer, "<strong>Category:</strong>", threat.getCategory().toString());
        writeTableRow2Columns(writer, "<strong>Description:</strong>", threat.getDescription());
        writeTableRow2Columns(writer, "<strong>Justification:</strong>",
                threat.getJustification().isBlank() ?  Threat.NO_JUSTIFICATION_PROVIDED: threat.getJustification());
        writeTableEnd(writer);
        writeLine(writer, "</div>");
        writeLine(writer, "<br>");

    }

    private String buildThreatDetailsTitle(Threat threat, int number){
        return number + ". " + threat.getTitle() +
                "&nbsp; [State: " + threat.getState().toString() + "] " +
                "[Priority: " + threat.getPriority().toString() + "]";
    }


    private void addImageForInteraction(BufferedWriter writer, GConnection con) throws IOException {
        String encodedImage = SnapshotUtils.takeSnapshot(Arrays.asList(con), graphEditorView, skinLookup);
        writeBase64Image(
                writer,
                encodedImage,
                "Picture of an interaction"
        );
    }

    private void writeSeparationLine(BufferedWriter writer) throws IOException {
        writeLine(writer, "<hr>");
    }

    private void addDiagramImage(BufferedWriter writer, List<GConnection> connections) throws IOException {
        String encodedImage = SnapshotUtils.takeSnapshot(connections, graphEditorView, skinLookup);
        writeBase64Image(
                writer,
                encodedImage,
                "Picture of the diagram");
    }

    private void writeBase64Image(BufferedWriter writer, String base64Image, String alt) throws IOException {
        writeLine(writer, "<img src=\"data:image/png;base64," +
                base64Image +
                "\" alt=\"" +
                alt +
                "\">");
    }

    private void writeTableStart(BufferedWriter writer) throws IOException {
        writeLine(writer, "<table>\n<tbody>");
    }

    private void writeTableEnd(BufferedWriter writer) throws IOException {
        writeLine(writer, "</table>\n</tbody>");
    }

    private void writeThreatsSummary(BufferedWriter writer) throws IOException{
        writeTitle(writer, "Threat Model Summary", 3);
        writeTableStart(writer);
        for(State state: State.values()) {
            writeThreatSummaryRow(writer, state);
        }
        writeThreatsSum(writer);
        writeTableEnd(writer);
    }

    private void writeThreatsSum(BufferedWriter writer) throws IOException {
        writeTableRow2Columns(writer, "Total", String.valueOf(threats.size()));

    }

    private void writeTableRow2Columns(BufferedWriter writer, String col1, String col2) throws IOException {
        writeLine(writer, "<tr>");
        writeLine(writer, "<td>");
        writeLine(writer, col1);
        writeLine(writer, "</td>");
        writeLine(writer, "<td>");
        writeLine(writer, col2);
        writeLine(writer, "</td>");
        writeLine(writer, "</tr>");
    }

    private void writeThreatSummaryRow(BufferedWriter writer, State state) throws IOException {
        writeTableRow2Columns(writer, state.toString(), String.valueOf(threats.withStateCount(state)));
    }

    private void writeCreationDate(BufferedWriter writer) throws IOException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        LocalDateTime now = LocalDateTime.now();
        writeLine(writer, "<span>");
        writeLine(writer, "Created on " + dateTimeFormatter.format(now));
        writeLine(writer, "</span>");
    }

    private void writeTitle(BufferedWriter writer, String title, int size) throws IOException{
        writeLine(writer, "<h" + size + ">");
        writeLine(writer, title);
        writeLine(writer, "</h" + size + ">");
    }

    private void startDocument(BufferedWriter writer) throws IOException {
        writeLine(writer, "<!DOCTYPE html>\n<html>\n<head>\n<title>");
        writeLine(writer, TITLE);
        writeLine(writer, "</title>\n");
        writeLine(writer, "<style>");
        writeLine(writer, "td { vertical-align: top;}");
        writeLine(writer, ".threat-detail{ background: rgba(200,200,200,0.1);}");
        writeLine(writer, "body { font-family: \"Segoe UI Light\", \"Helvetica\", \"sans-serif\"}");
        writeLine(writer,"</style>\n</head>\n<body>");
    }

    private void endDocument(BufferedWriter writer) throws IOException {
        writeLine(writer, "</body>");
        writeLine(writer, "</html>");
    }

    private void writeLine(BufferedWriter writer, String text) throws IOException {
        writer.append(text);
        writer.newLine();
    }


}
