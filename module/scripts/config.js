/* Expose configuration */
var EstrazExtension = {};
EstrazExtension.commandPath = "/command/extraction/";
EstrazExtension.servicesPath = EstrazExtension.commandPath + "services";

// Register a dummy reconciliation service that will be used to display named entities
ReconciliationManager.registerService({
  name: "Extraction",
  url: "Extraction",
  // By setting the URL to "{{id}}",
  // this whole string will be replaced with the actual URL
  view: { url: "{{id}}" },
});
