package org.extraction.commands;

import com.google.refine.commands.Command;
import org.json.JSONTokener;
import org.json.JSONWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Abstract class
 *
 *  @author Ruben Verborogh
 *  */
public abstract class EstrazioneCommand extends Command {

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        final JSONWriter writer = createResponseWriter(response);
        try {
            get(request, writer);
        } catch (Exception error) {
            error.printStackTrace();
            throw new ServletException(error);
        }
    }


    @Override
    public void doPut(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        final JSONWriter writer = createResponseWriter(response);
        final JSONTokener tokener = new JSONTokener(request.getReader());
        try {
            put(request, tokener.nextValue(), writer);
        } catch (Exception error) {
            error.printStackTrace();
            throw new ServletException(error);
        }
    }


    protected JSONWriter createResponseWriter(final HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "application/json");
        return new JSONWriter(response.getWriter());
    }


    public void get(final HttpServletRequest request, final JSONWriter response) throws Exception {
    }


    public void put(final HttpServletRequest request, final Object body, final JSONWriter response) throws Exception {
    }
}
