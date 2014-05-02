package org.extraction.services;

import com.google.refine.model.Cell;
import org.json.JSONException;
import org.json.JSONWriter;

import java.util.ArrayList;

/**
 * This class contains found objects and its functions allow to write found objects in a JSON representation
 * @author Giuliano Tortoreto
 */
public class Oggetto {
    private final static String[] EMPTY_TYPE_SET = new String[0];
    private ArrayList<String> oggettiTrovati = new ArrayList<String>();

    public ArrayList<String> getOggettiTrovati() {
        return oggettiTrovati;
    }

    public boolean addOggettoTrovato(String oggetto) {
        oggettiTrovati.add(oggetto);
        return true;
    }

    /**
     * Writes the objects in a JSON representation
     * @param json The JSON writer
     * @throws JSONException if an error occurs during writing
     */
    public void writeTo(final JSONWriter json) throws JSONException {
        for (String oggetto : oggettiTrovati) {
            json.object();
            json.key("oggettoTrovato");
            json.value(oggetto);
            json.endObject();
        }

    }

    /**
     * Convert found objects into a Refine worksheet cell
     * @return The cell
     */
    public Cell toCell(int indiceOggetto) {
        // Return the cell, adding a reconciliation value if a match was found
        return new Cell(getOggettiTrovati().get(indiceOggetto), null);
    }

}
