package org.extraction.commands;

import com.google.refine.commands.EngineDependentCommand;
import com.google.refine.model.AbstractOperation;
import com.google.refine.model.Column;
import com.google.refine.model.Project;
import org.extraction.operations.EstrazOperation;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * Command that starts a object extraction operation
 *
 * @author Giuliano Tortoreto
 */
public class ExtractionCommand extends EngineDependentCommand {


    @Override
    protected AbstractOperation createOperation(Project project, HttpServletRequest request, JSONObject engineConfig) throws Exception {
        final String columnName = request.getParameter("column");
        logger.debug("Starting createOperation");
        final String parametri = request.getParameter("services");
        final String prefisso = request.getParameter("prefix");
        final String country = request.getParameter("country");
        final Column column = project.columnModel.getColumnByName(columnName);


        try {
            return new EstrazOperation(column, parametri,prefisso,country, getEngineConfig(request));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
