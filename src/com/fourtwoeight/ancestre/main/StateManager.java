package com.fourtwoeight.ancestre.main;

import com.fourtwoeight.ancestre.command.Command;
import com.fourtwoeight.ancestre.model.Family;
import com.fourtwoeight.ancestre.model.Person;
import com.fourtwoeight.ancestre.util.CircularStack;
import org.graphsfx.model.GraphNode;

import java.io.File;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

public class StateManager implements Runnable{

    // Private Static Fields ===========================================================================================
    /**
     * Singleton instance
     */
    private static StateManager instance = null;

    /**
     * The Logger for the StateManager
     */
    private static Logger LOGGER = Logger.getLogger(StateManager.class.getName());

    /**
     * The default family save file directory relative path
     */
    private static final String DEFAULT_FAMILY_DIRECTORY = "userData/families/";

    // Public Static Methods ===========================================================================================

    /**
     * Returns the singleton instance of the StateManager
     * @return the singleton StateManager
     */
    public static StateManager getInstance(){
        LOGGER.fine("Getting StateManager instance");
        if(instance == null){
            LOGGER.finer("Creating new StateManager instance");
            instance = new StateManager();
        }

        return instance;
    }

    // Public Methods ==================================================================================================

    /**
     * Loops and executes commands in the command queue
     */
    @Override
    public void run() {
        while(true){
            if(!this.commandQueue.isEmpty()){
                Command command = this.commandQueue.poll();
                command.execute();
                this.undoStack.add(command);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                LOGGER.severe("StateManager thread interrupted. Caught Exception: " + e.toString());
            }
        }
    }

    /**
     * Adds a command to the command queue
     * @param command the command to be added
     */
    public void addCommand(Command command){
        this.commandQueue.add(command);
    }

    /**
     * @return the person that is currently selected
     */
    public Person getSelectedPerson() {
        return selectedPerson;
    }

    /**
     * Sets the selected person
     * @param selectedPerson the selected person
     */
    public void setSelectedPerson(Person selectedPerson) {
        this.selectedPerson = selectedPerson;
    }

    /**
     * @return the family that is currently loaded
     */
    public Family getFamily() {
        return family;
    }

    /**
     * Changes the currently loaded family
     * @param family the newly loaded family
     */
    public void setFamily(Family family) {
        this.family = family;
    }

    /**
     * @return the family file that corresponds to the currently loaded family
     */
    public File getFamilyFile() {
        return familyFile;
    }

    /**
     * Returns the parent directory for the current family file. If no family file is set then returns the default
     * family directory file.
     * @return the parent directory for the current family file.
     */
    public File getCurrentFamilyDirectory(){
        if(this.familyFile != null){
            return this.familyFile.getParentFile();
        }
        else{
            return new File(DEFAULT_FAMILY_DIRECTORY);
        }
    }

    /**
     * Sets the family file which corresponds to the currently loaded family
     * @param familyFile the family file to set
     */
    public void setFamilyFile(File familyFile) {
        this.familyFile = familyFile;
    }

    /**
     * @return the map of persons to nodes
     */
    public HashMap<Person, GraphNode> getNodes() {
        return nodes;
    }


    // Protected Methods ===============================================================================================

    /**
     * Constructor
     */
    protected StateManager(){
        this.commandQueue = new ArrayBlockingQueue<Command>(100);
        this.undoStack = new CircularStack<>(100);
        this.redoStack = new CircularStack<>(100);
    }


    // Private Fields ==================================================================================================
    /**
     * The selected person
     */
    private Person selectedPerson;

    /**
     * The current Family that is loaded
     */
    private Family family;

    /**
     * The save file for the family
     */
    private File familyFile;

    /**
     * The mapping of persons to GraphNode for the current tree displayed on the map
     */
    private HashMap<Person, GraphNode> nodes;

    Queue<Command> commandQueue;

    /**
     * The Stack of commands that can be undone
     */
    CircularStack<Command> undoStack;

    /**
     * The Stack of commands that can be redone
     */
    CircularStack<Command> redoStack;


}
