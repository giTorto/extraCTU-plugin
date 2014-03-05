package org.extraction.operations;

import com.google.refine.browsing.Engine;
import com.google.refine.browsing.RowVisitor;
import com.google.refine.history.HistoryEntry;
import com.google.refine.model.*;
import com.google.refine.process.LongRunningProcess;
import org.apache.log4j.Logger;
import org.extraction.services.Oggetto;
import org.extraction.services.Operazione;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Process that executes object extraction
 * @author Giuliano Tortoreto
 */
public class EstrazProcess extends LongRunningProcess implements Runnable {
    private final static Logger LOGGER = Logger.getLogger(EstrazProcess.class);
    private final static Oggetto EMPTY_RESULT_SET = new Oggetto();
    private final String prefisso;
    private final Project project;
    private final Column column;
    private final String operazione;
    private final AbstractOperation parentOperation;
    private final JSONObject engineConfig;
    private final long historyEntryId;
    private final String country;

    /**
     * Creates a new <tt>NERProcess</tt>
     *
     * @param project         The project
     * @param column          The column on which named-entity recognition is performed
     * @param parentOperation The operation that createStrings this process
     * @param description     The description of this operation
     * @param engineConfig    The faceted browsing engine configuration
     */
    protected EstrazProcess(final Project project, final Column column,
                            final String operazione, final String prefisso,final String country,
                            final AbstractOperation parentOperation, final String description,
                            final JSONObject engineConfig) {
        super(description);
        this.project = project;
        this.column = column;
        this.operazione = operazione;
        this.parentOperation = parentOperation;
        this.engineConfig = engineConfig;
        this.country = country;
        historyEntryId = HistoryEntry.allocateID();
        this.prefisso = prefisso;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        final int columnIndex = project.columnModel.getColumnIndexByName(column.getName()) + 1;

        final Oggetto[] namedEntities = performExtraction();

        if (!_canceled) {
            project.history.addEntry(new HistoryEntry(historyEntryId, project, _description, parentOperation, new EstrazChange(columnIndex, operazione,country, namedEntities)));
            project.processManager.onDoneProcess(this);
        }
    }

    /**
     * Performs object extraction on all rows
     * @return The extracted objects per row
     */
    protected Oggetto[] performExtraction() {
        // Count all rows
        final int rowsTotal = project.rows.size();
        // Get the cell index of the column in which to perform entity extraction
        final int cellIndex = column.getCellIndex();
        // Get the filtered rows
        final Set<Integer> filteredRowIndices = getFilteredRowIndices();
        final int rowsFiltered = filteredRowIndices.size();

        // Go through each row and extract entities if the row is part of the filter
        final Oggetto[] namedEntities = new Oggetto[rowsTotal];
        int rowsProcessed = 0;
        for (int rowIndex = 0; rowIndex < rowsTotal; rowIndex++) {
            // Initialize to the empty result set, in case no entities are extracted
            namedEntities[rowIndex] = EMPTY_RESULT_SET;
            // If the row is part of the filter, extract entities
            if (filteredRowIndices.contains(rowIndex)) {
                final Row row = project.rows.get(rowIndex);
                // Determine the text value of the cell
                final Cell cell = row.getCell(cellIndex);
                final Serializable cellValue = cell == null ? null : cell.value;
                final String text = cellValue == null ? "" : cellValue.toString().trim();
                // Perform extraction if the text is not empty
                if (text.length() > 0) {
                    LOGGER.info(String.format("Extracting objects in column %s on row %d of %d.",
                            column.getName(), rowsProcessed + 1, rowsFiltered));
                    namedEntities[rowIndex] = performExtraction(text);
                }
                _progress = 100 * ++rowsProcessed / rowsFiltered;
            }
            // Exit directly if the process has been cancelled
            if (_canceled)
                return null;
        }
        return namedEntities;
    }


    /**
     * Performs objects extraction on the specified text
     * @param text The text
     * @return The extracted named entities per service
     */
    protected Oggetto performExtraction(final String text) {
        // The execution of the services happens in parallel.
        // Create the extractors and corresponding threads
        final Extractor extractore = new Extractor(text, operazione,prefisso,country);

        extractore.start();
        try {
            extractore.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        // Wait for all threads to finish and collect their results
        final Oggetto estratto;

        estratto = extractore.getEntity();
        return estratto;
    }


    /**
     * Gets the indices of all rows that are part of the active selection filter
     * @return The filtered rows
     */
    protected Set<Integer> getFilteredRowIndices() {
        // Load the faceted browsing engine and configuration (including row filters)
        final Engine engine = new Engine(project);
        try {
            engine.initializeFromJSON(engineConfig);
        } catch (Exception e) {
        }

        // Collect indices of rows that belong to the filter
        final HashSet<Integer> filteredRowIndices = new HashSet<Integer>(project.rows.size());
        engine.getAllFilteredRows().accept(project, new RowVisitor() {
            @Override
            public boolean visit(final Project project, final int rowIndex, final Row row) {
                filteredRowIndices.add(rowIndex);
                return false;
            }

            @Override
            public void start(Project project) {
            }

            @Override
            public void end(Project project) {
            }
        });
        return filteredRowIndices;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Runnable getRunnable() {
        return this;
    }

    /**
     * Thread that executes a named-entity recognition service
     */
    protected static class Extractor extends Thread {
        private final static Oggetto EMPTY_ENTITY_SET = new Oggetto();

        private final String text;
        private final String operazione;
        private final String prefisso;
        private final String country;
        private Oggetto entity;

        /**
         * Creates a new <tt>Extractor</tt>
         *
         * @param text The text to analyze
         * @param prefisso
         */
        public Extractor(final String text, final String operazione, String prefisso,String country) {
            this.text = text;
            this.operazione = operazione;
            this.prefisso = prefisso;
            this.country=country;
            this.entity = EMPTY_ENTITY_SET;
        }


        /**
         * Gets the named entities the service extracted from the text
         *
         * @return The extracted named entities
         */
        public Oggetto getEntity() {
            return entity;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {

                Operazione op = new Operazione(operazione,prefisso,country);
                entity = op.extract(text);

            } catch (Exception error) {
                LOGGER.error("The extractor failed", error);
            }
        }
    }
}
