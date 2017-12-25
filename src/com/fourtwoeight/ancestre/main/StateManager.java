package com.fourtwoeight.ancestre.main;

import com.fourtwoeight.ancestre.command.Command;
import com.fourtwoeight.ancestre.model.Family;
import com.fourtwoeight.ancestre.model.Person;
import com.fourtwoeight.ancestre.util.CircularStack;
import org.graphsfx.model.GraphNode;

import java.util.HashMap;

public class StateManager {

    // Private Static Fields ===========================================================================================
    /**
     * Singleton instance
     */
    private static StateManager instance = null;

    // Public Static Methods ===========================================================================================

    /**
     * Returns the singleton instance of the StateManager
     * @return the singleton StateManager
     */
    public static StateManager getInstance(){
        if(instance == null){
            instance = new StateManager();
        }

        return instance;
    }

    // Public Methods ==================================================================================================

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
     * @return the map of persons to nodes
     */
    public HashMap<Person, GraphNode> getNodes() {
        return nodes;
    }


    // Protected Methods ===============================================================================================

    /**
     * Constructor
     */
    protected StateManager(){}


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
     * The mapping of persons to GraphNode for the current tree displayed on the map
     */
    private HashMap<Person, GraphNode> nodes;

    /**
     * The Stack of commands that can be undone
     */
    CircularStack<Command> undoStack;

    /**
     * The Stack of commands that can be redone
     */
    CircularStack<Command> redoStack;

}
