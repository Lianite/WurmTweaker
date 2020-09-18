var modSupportInterface = require("../modSupport-interface.js");

var Logger = Packages.java.util.logging.Logger;
var logger = Logger.getLogger("scriptRunnerMods.TheWinterExpanse.item-adjustment-script");

function onServerStarted() {
  // Make sure the database table exists before trying to interact with it
  modSupportInterface.initializeDatabase();

  // This updates the built-in item templates as well as mod added item templates with server specific overrides
  modSupportInterface.UpdateExistingItems();
}