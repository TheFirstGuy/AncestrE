package com.fourtwoeight.ancestre.command;

import com.fourtwoeight.ancestre.model.Family;
import com.fourtwoeight.ancestre.model.Person;

public class AddPersonCommand implements Command {

    public AddPersonCommand(Person person, Family family){
        this.person = person;
        this.family = family;
    }

    /**
     * Adds a person to the family
     */
    @Override
    public void execute() {
        this.family.addPerson(this.person);
    }

    /**
     * Removes the previously added person
     */
    @Override
    public void undo() {
        this.family.removePerson(this.person);
    }

    // Private Fields ==================================================================================================

    private Family family;

    private Person person;
}
