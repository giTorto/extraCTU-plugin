var logger = Packages.org.slf4j.LoggerFactory.getLogger("extraction-extension"),
    File = Packages.java.io.File,
    refineServlet = Packages.com.google.refine.RefineServlet,
    operationRegistry = Packages.com.google.refine.operations.OperationRegistry,
    estra = Packages.org.extraction,
    services = estra.services,
    commands = estra.commands;

/* Initialize the extension. */
function init() {


  logger.info("Initializing commands");//i seguenti comandi verranno chiamati
  register("estrazioni", new commands.ExtractionCommand()); //comando chiamato in post


  refineServlet.registerClassMapping(
    "com.google.refine.model.changes.DataExtensionChange",
    "org.extraction.operations.EstrazChange"
  );
  
  refineServlet.cacheClass(Packages.org.extraction.operations.EstrazChange);
  
  operationRegistry.registerOperation(
    module, "extraction", Packages.org.extraction.operations.EstrazOperation
  );

  logger.info("Initializing client resources");
  var resourceManager = Packages.com.google.refine.ClientSideResourceManager;
  resourceManager.addPaths(
    "project/scripts",
    module, [
      "scripts/config.js",
      "scripts/util.js",
      "dialogs/about.js",
      "dialogs/extraction.js",
      "scripts/menus.js",
    ]
  );
  resourceManager.addPaths(
    "project/styles",
    module, [
      "styles/main.less",
      "dialogs/dialogs.less",
      "dialogs/about.less",
      "dialogs/extraction.less",
    ]
  );


}

function register(path, command) {
  refineServlet.registerCommand(module, path, command);
}
