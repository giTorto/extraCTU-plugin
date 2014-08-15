package org.extraction.operations;

import com.google.refine.model.AbstractOperation;
import com.google.refine.model.Column;
import com.google.refine.model.Project;
import com.google.refine.operations.EngineDependentOperation;
import com.google.refine.operations.OperationRegistry;
import com.google.refine.process.Process;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.util.Properties;

/**
 * Operation that starts a objects recognition process.
 * This class calls/create the EstrazProcess that performs objects extraction
 * @author Giuliano Tortoreto
 */
public class EstrazOperation extends EngineDependentOperation {

    private final Column column;
    private final String tipOp;
    private final String prefisso;
    private final String country;
    private final String types[] = {"emails", "sites", "numbers"};

    /**
     * Creates a new <tt>EstrazOperation</tt>
     * @param column       The column on which the oject extraction is performed
     * @param engineConfig The faceted browsing engine configuration
     */
    public EstrazOperation(final Column column, final String tipOp,final String prefisso,final String country, final JSONObject engineConfig) {
        super(engineConfig);
        this.column = column;
        this.tipOp = tipOp;
        this.country = country;
        this.prefisso = prefisso;

    }

    static public AbstractOperation reconstruct(Project project, JSONObject obj) throws Exception {
        JSONObject engineConfig = obj.getJSONObject("engineConfig");
        final String tipo; String prefix; String country;
        tipo = obj.getString("tipOp");
        prefix = obj.getString("prefix");
        country= obj.getString("country");

        return new EstrazOperation(project.columnModel.getColumnByName(obj.getString("column")), tipo,prefix,country, engineConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final JSONWriter writer, final Properties options) throws JSONException {
        writer.object();
        writer.key("op");
        writer.value(OperationRegistry.s_opClassToName.get(getClass()));
        writer.key("description");
        writer.value(getBriefDescription(null));
        writer.key("engineConfig");
        writer.value(getEngineConfig());
        writer.key("column");
        writer.value(column.getName());
        writer.key("tipOp");
        writer.value(tipOp);

        writer.key("prefix");
        writer.value(prefisso);

        writer.key("country");
        writer.value(country);

        writer.endObject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getBriefDescription(final Project project) {
        return String.format("Extracting " + tipOp + " in column %s", column.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Process createProcess(final Project project, final Properties options) throws Exception {
        return new EstrazProcess(project, column, tipOp, prefisso,country, this, getBriefDescription(project), getEngineConfig());
    }
}