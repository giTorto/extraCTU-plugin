package org.extraction.operations;

import com.google.refine.history.Change;
import com.google.refine.model.Column;
import com.google.refine.model.Project;
import com.google.refine.model.Row;
import com.google.refine.model.changes.CellAtRow;
import com.google.refine.model.changes.ColumnAdditionChange;
import com.google.refine.model.changes.ColumnRemovalChange;
import com.google.refine.util.JSONUtilities;
import com.google.refine.util.Pool;
import org.apache.commons.lang.ArrayUtils;
import org.extraction.services.Oggetto;
import org.json.*;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Writer;
import java.util.*;

/**
 * After a change resulting from extraction,this class modify the table, adding a column and a cell
 * with the Objects extracted before in EstrazProcess
 * @author Giuliano Tortoreto
 */
public class EstrazChange implements Change {
    private final int columnIndex;
    private final Oggetto[] objects;
    private final String operazione;
    private final String country;
    private final List<Integer> addedRowIds;
    private final String[] servizi = {"e-mails","URLs","sites"};

    /**
     * Creates a new <tt>Change</tt>
     *
     * @param columnIndex The index of the column used for object finded
     * @param operazione  The names of the used services
     * @param objects    The extracted named objects per row and service
     */
    public EstrazChange(final int columnIndex, final String operazione,final String country, final Oggetto[] objects) {
        this.columnIndex = columnIndex;
        this.operazione = operazione;
        this.objects = objects;
        this.addedRowIds = new ArrayList<Integer>();
        this.country=country;
    }


    @Override
    public void apply(final Project project) {
        synchronized (project) {
            System.out.println("I'm applying");
            final int[] cellIndexes = createColumn(project);
            insertValues(project, cellIndexes);
            project.update();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void revert(final Project project) {
        synchronized (project) {
            deleteRows(project);
            deleteColumns(project);
            project.update();
        }
    }

    public void save(final Writer writer, final Properties options) throws IOException {
        final JSONWriter json = new JSONWriter(writer);
        try {
            json.object();
            json.key("column");
            json.value(this.columnIndex);
            json.key("operazione");
            json.value(this.operazione);
            json.key("country");
            json.value(this.country);

            json.key("objects");

            /* Objects array */
            {
                json.array();
                /* Rows array */
                for (final Oggetto ogg : objects) {
                    /* Objects finded */
                    ogg.writeTo(json);
                }
                json.endArray();
            }
            json.key("addedRows");

            /* Added row numbers array */
            {
                json.array();
                for (Integer addedRowId : addedRowIds)
                    json.value(addedRowId.intValue());
                json.endArray();
            }

            json.endObject();
        } catch (JSONException error) {
            throw new IOException(error);
        }
    }

    /**
     * Create a <tt>Change</tt> from a configuration reader
     *
     * @param reader The reader
     * @param pool   (unused but required)
     * @return A new <tt>NERChange</tt>
     * @throws Exception If the configuration is in an unexpected format
     */
    static public Change load(LineNumberReader reader, Pool pool) throws Exception {
        /* Parse JSON line */
        final JSONTokener tokener = new JSONTokener(reader.readLine());
        final JSONObject changeJson = (JSONObject) tokener.nextValue();
        
        /* Simple properties */
        final int columnIndex = changeJson.getInt("column");
        final String operazione = changeJson.getString("operazione");
        final String country = changeJson.getString("country");


        /* Objects array */
        final JSONArray EntitiesJson = changeJson.getJSONArray("objects");
        final Oggetto[] Entities = new Oggetto[EntitiesJson.length()];
        /* Rows array */
        for (int i = 0; i < Entities.length; i++) {

            JSONObject rowResults = EntitiesJson.getJSONObject(i);
            JSONArray Results = rowResults.getJSONArray("array");
            Entities[i] = new Oggetto();
            for (int j = 0; j < Results.length(); j++) {
                Entities[i].addOggettoTrovato(Results.getString(j));

            }
        }

        /* Reconstruct change object */
        final EstrazChange change = new EstrazChange(columnIndex, operazione, country, Entities);
        for (final int addedRowId : JSONUtilities.getIntArray(changeJson, "addedRows"))
            change.addedRowIds.add(addedRowId);
        return change;
    }


    /**
     * Delete the columns where the named entities have been stored
     *
     * @param project The project
     */
    protected void deleteColumns(final Project project) {

        new ColumnRemovalChange(columnIndex).apply(project);
    }


    /**
     * Insert the extracted named entities into rows with the specified cell indexes
     *
     * @param project     The project
     * @param cellIndexes The cell indexes of the rows that will contain the named entities
     */
    protected void insertValues(final Project project, final int[] cellIndexes) {
        final List<Row> rows = project.rows;
        // Make sure there are rows
        if (rows.isEmpty())
            return;

        // Make sure all rows have enough cells, creating new ones as necessary
        final Integer maxCellIndex = Collections.max(Arrays.asList((ArrayUtils.toObject(cellIndexes))));
        final int minRowSize = maxCellIndex + 1;
        int rowNumber = 0;
        addedRowIds.clear();

        int c = 0;
        for (final Oggetto row : objects) {
            // Create new blank rows if objects don't fit on a single line
            int maxobject = 1;

            if(row.getOggettiTrovati()!=null)
                maxobject = Math.max(maxobject, row.getOggettiTrovati().size());

            for (int i = 1; i < maxobject; i++) {
                final Row entityRow = new Row(minRowSize);
                final int entityRowId = rowNumber + i;
                for (int j = 0; j < minRowSize; j++)
                    entityRow.cells.add(null);
                rows.add(entityRowId, entityRow);
                addedRowIds.add(entityRowId);
            }

            // Place all objects
            c++;

            final ArrayList<String> oggetti = row.getOggettiTrovati();
            for (int r = 0; r < oggetti.size(); r++) {
                Row riga = rows.get(rowNumber + r);
                riga.cells.set(cellIndexes[0], row.toCell(r));
            }


            // Advance to the next original row
            rowNumber += maxobject;

            addedRowIds.clear();
        }

    }

    /**
     * Delete rows that were added to contain extracted named objects
     *
     * @param project The project
     */
    protected void deleteRows(final Project project) {
        final List<Row> rows = project.rows;
        // Traverse rows IDs in reverse, from high to low,
        // to avoid index shifts as rows get deleted.
        for (int i = addedRowIds.size() - 1; i >= 0; i--) {
            final int addedRowId = addedRowIds.get(i);
            if (addedRowId >= rows.size())
                throw new IndexOutOfBoundsException(String.format("Needed to remove row %d, "
                        + "but only %d rows were available.", addedRowId, rows.size()));
            rows.remove(addedRowId);
        }
        addedRowIds.clear();
    }

    /**
     * Subclass of <tt>ColumnAdditionChange</tt>
     * that provides access to the cell index of the created column
     */
    protected static class CustomColumnAdditionChange extends ColumnAdditionChange {
        /**
         * Create a new <tt>CustomColumnAdditionChange</tt>
         * @param columnName  The column name
         * @param columnIndex The column index
         * @param newCells    The new cells
         */
        public CustomColumnAdditionChange(final String columnName, final int columnIndex,
                                          final List<CellAtRow> newCells) {
            super(columnName, columnIndex, newCells);
        }

        /**
         * Gets the cell index of the created column
         *
         * @return The cell index
         */
        public int getCellIndex() {
            if (_newCellIndex < 0)
                throw new IllegalStateException("The cell index has not yet been set.");
            return _newCellIndex;
        }
    }

    /**
     * Create the columns where the objects will be stored
     *
     * @param project The project
     * @return The cell indexes of the created columns
     */
    protected int[] createColumn(final Project project) {
        // Create empty cells that will populate each row
        final int rowCount = project.rows.size();
        final ArrayList<CellAtRow> emptyCells = new ArrayList<CellAtRow>(rowCount);
        for (int r = 0; r < rowCount; r++)
            emptyCells.add(new CellAtRow(r, null));

        // Create rows
        final int[] cellIndexes = new int[1];
        final CustomColumnAdditionChange change;

        Column column;
        String nomeColonna;
        if ("".equals(country)){
            nomeColonna = operazione;
            while(project.columnModel.getColumnByName(nomeColonna)!=null){
                nomeColonna = nomeColonna+"_new";
            }
        }else{
            nomeColonna = operazione+"_"+country;
            while(project.columnModel.getColumnByName(nomeColonna)!=null){
                nomeColonna = nomeColonna+"_new";
            }
        }

        change= new CustomColumnAdditionChange(nomeColonna, columnIndex + 0, emptyCells);
        change.apply(project);
        cellIndexes[0] = change.getCellIndex();

        // Return cell indexes of created rows
        return cellIndexes;
    }


}
