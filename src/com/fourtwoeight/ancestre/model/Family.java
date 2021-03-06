package com.fourtwoeight.ancestre.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@XmlRootElement
public class Family {

    // Public Methods ==================================================================================================

    /**
     * Default Constructor
     */
    public Family(){}

    /**
     * Constructor
     * @param familyName the name of the family
     */
    public Family(String familyName){
        this.familyName = familyName;
    }

    @XmlAttribute
    public String getFamilyName(){
        return this.familyName;
    }

    /**
     * Returns the person mapped to the uuid
     * @param uuid the uuid string of the person
     * @return the Person mapped to the uuid
     */
    public Person getPerson(String uuid){
        return familyMembers.get(uuid);
    }

    /**
     * Returns a list of family members
     * @return the list of family members
     */
    @XmlElement
    public List<Person> getFamilyMembers(){
        return new LinkedList<Person>(familyMembers.values());
    }

    /**
     * Returns whether the person is part of the family
     * @param person the person to check from family
     * @return 'true' if the person is in the family, 'false' otherwise
     */
    public boolean isMember(Person person){
        return familyMembers.containsValue(person);
    }

    /**
     * Adds a person to the family
     * @param person the person to add
     */
    public void addPerson(Person person){
        familyMembers.put(person.getUUIDString(), person);
    }

    /**
     * Removes a person from the family
     * @param person the person to remove
     */
    public void removePerson(Person person){ familyMembers.remove(person);}
    /**
     * Finds and generates a list of people who are the ancestors for the passed in person
     * @param person the person to get ancestry for
     * @return the list of Persons who are the ancestors for the person
     */
    public List<Person> getAncestors(Person person){
        HashSet<Person> ancestors = new HashSet<Person>();
        getAncestorsHelper(person, ancestors);

        return new LinkedList<Person>(ancestors);
    }

    /**
     * Finds and generates a list of people who are the descendants for the passed in person
     * @param person the person to get descendants for
     * @return the list of Persons who are the descendants for the person
     */
    public List<Person> getDescendants(Person person){
        HashSet<Person> descendants = new HashSet<Person>();
        getDescendantsHelper(person, descendants);

        return new LinkedList<Person>(descendants);
    }

    // Private Methods =================================================================================================

    /**
     * Helper method for getAncestors which recursively finds all the ancestors for the person.
     * @param person Person to get parents for
     * @param ancestors Set of ancestors to add too.
     */
    public void getAncestorsHelper(Person person, Set<Person> ancestors ){
        Person father = person.getFather();
        Person mother = person.getMother();

        // Gets the ancestors for the father
        if(father != null){
            if(!ancestors.contains(father)){
                ancestors.add(father);
                getAncestorsHelper(father, ancestors);
            }
        }

        // Gets the ancestors for the mother
        if(mother != null){
            if(!ancestors.contains(mother)){
                ancestors.add(mother);
                getAncestorsHelper(mother, ancestors);
            }
        }
    }

    /**
     * Helper method for the getDescendants. Recursively finds all descendants for the person.
     * @param person the person to get the descendants for
     * @param descendants the set of descendants
     */
    public void getDescendantsHelper(Person person, Set<Person> descendants){
        List<Person> children = person.getChildren();

        for(Person child : children){
            if(!descendants.contains(child)){
                descendants.add(child);
                getDescendantsHelper(child, descendants);
            }
        }
    }

    // Private Fields ==================================================================================================

    private String familyName;

    private HashMap<String, Person> familyMembers = new HashMap<>();
}
