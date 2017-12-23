package com.fourtwoeight.ancestre.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class Person {


    // Public Static Fields ============================================================================================
    public enum SEX{MALE, FEMALE}

    // Private Static Fields ===========================================================================================
    private static final String DATE_FORMAT = "MMMMM dd yyyy";

    // Public Methods ==================================================================================================

    /**
     * Constructor
     * @param firstName the first name of the person
     * @param middleNames the middle name of the person
     * @param lastName the last name of the person
     * @param birthDate the date the person was born
     * @param deathDate the date the person died
     */
    public Person(String firstName,
                  ArrayList<String> middleNames,
                  String lastName,
                  SEX sex,
                  Calendar birthDate,
                  Calendar deathDate){
        // Assign parameters
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.lastName = lastName;
        this.sex = sex;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
    }

    /**
     * @return if the person has a deathdate.
     */
    public boolean isAlive(){
        return deathDate == null;
    }

    /**
     * Returns the formatted time period which this Person lived/lives
     * @return the time period which the Person lived
     */
    public String getLifeTime(){
        StringBuilder builder = new StringBuilder();
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        builder.append(format.format(this.birthDate.getTime()));
        builder.append(" - ");

        if(this.deathDate != null){
            builder.append(format.format(this.deathDate.getTime()));
        }
        else{
            builder.append("Present");
        }

        return builder.toString();
    }

    /**
     * Returns a list of Persons who are siblings (half siblings included) for this person
     * @return the list of siblings
     */
    public List<Person> getSiblings(){
        ArrayList<Person> siblings = new ArrayList<>();

        if(this.father != null){
            siblings.addAll(this.father.getChildren());
        }

        if(this.mother != null){
            siblings.addAll(this.mother.getChildren());
        }

        return siblings;
    }

    /**
     * @return the current birthday, null if not set
     */
    public Calendar getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the date the person was born
     * @param birthDate the date the person was born
     */
    public void setBirthDate(Calendar birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * @return the date the person died
     */
    public Calendar getDeathDate() {
        return deathDate;
    }


    /**
     * Sets the date the person died
     * @param deathDate the date the person died
     */
    public void setDeathDate(Calendar deathDate) {
        this.deathDate = deathDate;
    }

    /**
     * @return the person's mother
     */
    public Person getMother() {
        return mother;
    }

    /**
     * Sets the mother of the person
     * @param mother the mother of the person
     */
    public void setMother(Person mother) {
        this.mother = mother;
    }

    /**
     * @return the father of the person
     */
    public Person getFather() {
        return father;
    }

    /**
     * Sets the father of the person
     * @param father the father of the person
     */
    public void setFather(Person father) {
        this.father = father;
    }

    /**
     * @return the current spouse for the person
     */
    public Person getCurrentSpouse() {
        return currentSpouse;
    }

    /**
     * Sets the current spouse for the person
     * @param currentSpouse the person the person is currently in a relationship with
     */
    public void setCurrentSpouse(Person currentSpouse) {
        this.currentSpouse = currentSpouse;
    }

    /**
     * @return the list of spouses this person has had
     */
    public List<Person> getSpouses() {
        return spouses;
    }

    /**
     * Adds a spouse to the list of spouses
     * @param spouse the spouse to add
     */
    public void addSpouse(Person spouse){
        if(!this.spouses.contains(spouse)){
            this.spouses.add(spouse);
        }
    }

    /**
     * @return the list of children that the person has
     */
    public List<Person> getChildren() {
        return children;
    }

    /**
     * Adds a Person to the list of children the person has add
     * @param child the Person to be added as a child
     */
    public void addChild(Person child){
        if(!this.children.contains(child)){
            this.children.add(child);
        }
    }

    /**
     * Returns the full name of the person
     * @return the full name of the person
     */
    public String getFullName(){
        StringBuilder builder = new StringBuilder();
        builder.append(this.firstName);
        builder.append(" ");

        // Append Middle names
        for(String middle: middleNames){
            builder.append(middle);
            builder.append(" ");
        }

        builder.append(this.lastName);

        return builder.toString();
    }

    /**
     * @return the first name of the person
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the person
     * @param firstName the name to set as the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the middle names this person has
     */
    public List<String> getMiddleNames() {
        return middleNames;
    }


    public void addMiddleName(String middleName){
        // Verify that the name is not already in list
        boolean contains = false;
        for(String name : this.middleNames){
            if(name.equals(middleName)){ contains = true;}
        }

        // Add to list of middle names if not already in list
        if(!contains){
            this.middleNames.add(middleName);
        }
    }

    /**
     * @return the last name of the person
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the person
     * @param lastName the last name of the person
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the description of the person
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the person
     * @param description The description of the person
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The path to the image
     */
    public String getImagePath(){
        return imagePath;
    }

    /**
     * @param imagePath the path to the image for the Person
     */
    public void setImagePath(String imagePath){
        this.imagePath = imagePath;
    }

    /**
     * @return The sex of the person
     */
    public SEX getSex() {
        return sex;
    }

    /**
     * Sets the sex of the person
     * @param sex the sex of the person
     */
    public void setSex(SEX sex) {
        this.sex = sex;
    }

    /**
     * @return The UUID as a string for the person
     */
    public String getUUIDString(){
        return this.uuid.toString();
    }

    /**
     * @return The UUID for the person
     */
    public UUID getUUID(){
        return this.uuid;
    }

    /**
     * Sets the UUID for the Person
     * @param uuid the UUID to give the Person
     */
    public void setUUID(UUID uuid){
        this.uuid = uuid;
    }


    // Private Fields ==================================================================================================

    /**
     * The date the person was born
     **/
    private Calendar birthDate;

    /**
     * The date the person died
     **/
    private Calendar deathDate;

    /**
     * The mother of the person
     */
    private Person mother;

    /**
     * The father of the person
     */
    private Person father;

    /**
     * The current spouse of the person
     */
    private Person currentSpouse;

    /**
     * The list of spouses (including the current spouse this person had)
     */
    private ArrayList<Person> spouses = new ArrayList<Person>();

    /**
     * The list of children this person had
     */
    private ArrayList<Person> children = new ArrayList<Person>();

    /**
     * The first name of the person
     */
    private String firstName;

    /**
     * The list of middle names this person has
     */
    private ArrayList<String> middleNames = new ArrayList<String>();

    /**
     * The last name of the person
     */
    private String lastName;

    /**
     * The description of the person.
     */
    private String description;

    /**
     * The path to the profile image
     */
    private String imagePath;

    /**
     * The sex of the person
     */
    private SEX sex;

    /**
     * The unique identifier for the person
     */
    private UUID uuid;
}
