package com.fourtwoeight.ancestre.command;

import com.fourtwoeight.ancestre.main.StateManager;
import com.fourtwoeight.ancestre.model.Family;
import com.fourtwoeight.ancestre.util.FileManager;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.logging.Logger;

public class SaveAsCommand implements Command {

    // Private Static Fields ===========================================================================================

    /**
     * The logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(SaveAsCommand.class.getName());

    // Public Methods ==================================================================================================

    /**
     * Constructor
     * @param stage the stage to display the file saver
     */
    public SaveAsCommand( Stage stage){
        this.stateManager = StateManager.getInstance();
        this.stage = stage;
    }

    /**
     * Opens the filechooser and saves the current family in the stateManager. Displays error message upon failure
     */
    @Override
    public void execute() {
        LOGGER.fine("Executing SaveAs");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Family");
        fileChooser.setInitialDirectory(this.stateManager.getCurrentFamilyDirectory());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("FAM","*.fam"));
        File familyFile = fileChooser.showSaveDialog(this.stage);

        LOGGER.finer("Saving file as: " + familyFile.getName());
        FileManager fileManager = FileManager.getInstance();
        StringBuilder errorMessage = new StringBuilder();

        // Display error message upon failure
        if(!fileManager.save(this.stateManager.getFamily(),
                familyFile.getName().split("\\.")[0],
                familyFile.getParentFile(),
                errorMessage)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Saving Error");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage.toString());
        }

    }

    /**
     * Does nothing
     */
    @Override
    public void undo() {
        // Nothing to do if undone
    }

    // Private Fields ==================================================================================================
    /**
     * The state manager classs
     */
    private StateManager stateManager;

    /**
     * The stage to display the filechooser
     */
    private Stage stage;

}
