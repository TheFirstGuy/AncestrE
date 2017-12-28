package com.fourtwoeight.ancestre.util;

import com.fourtwoeight.ancestre.model.Family;
import com.fourtwoeight.ancestre.model.Person;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class FileSaver {

    // Private Static Fields ===========================================================================================

    private static Logger LOGGER = Logger.getLogger(FileSaver.class.getName());

    private static String UUID = "uuid";
    private static String RELATIONSHIPS = "Relationships";
    private static String PERSON = "Person";
    private static String FATHER = "Father";
    private static String MOTHER = "Mother";
    private static String CUR_SPOUSE = "CurrentSpouse";
    private static String SPOUSES = "Spouses";
    private static String SIBLINGS = "Siblings";
    private static String CHILDREN = "Children";

    // Public Methods ==================================================================================================


    public FileSaver(){

    }

    /**
     * Initializes the FileSaver
     */
    public void initialize(){
        try {
            // Set up person context and marshaller
            this.personContext = JAXBContext.newInstance(Person.class);
            this.personMarshaller = this.personContext.createMarshaller();
            this.personMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Set up family context and marshaller
            this.familyContext = JAXBContext.newInstance(Family.class);
            this.familyMarshaller = this.familyContext.createMarshaller();
            this.familyMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Set up DocumentBuilderFactory
            this.documentBuilderFactory = DocumentBuilderFactory.newInstance();

            // Set up TransformerFactory
            this.transformerFactory = TransformerFactory.newInstance();
        } catch (JAXBException e) {
            LOGGER.severe("Unable to initialize FileSaver. Caught Exception: " + e);
        }
    }

    /**
     * Saves a person object to the provided fileStream
     * @param person the Person to be saved
     * @param fileStream the file to be saved too
     * @return Whether the person was saved successfully
     */
    public boolean savePerson(Person person, FileOutputStream fileStream){
        boolean saved = false;

        try {
            personMarshaller.marshal(person, fileStream);
            saved = true;
        } catch (JAXBException e) {
            LOGGER.severe("Unable to save person: " + person.getFullName() + ". Caught Exception: " + e);
        }

        return saved;
    }


    /**
     * Saves a family object to the provided fileStream
     * @param family the family to be saved
     * @param fileStream the file to be saved too
     * @return Whether the family was saved successfully
     */
    public boolean saveFamily(Family family, FileOutputStream fileStream){
        boolean saved = false;
//        try {
//            familyMarshaller.marshal(family, fileStream);
//            saved = true;
//        } catch (JAXBException e) {
//            LOGGER.severe("Unable to save family: " + family.getFamilyName() + ". Caught Exception: " + e);
//        }
        saveFamilyRelationships(family, fileStream);
        return saved;
    }


    // Private Methods =================================================================================================

    /**
     * Helper method to write out a file which describes the relationships between different family members
     * @param family the family to save
     * @param fileStream the fileStream to save the file too
     * @return 'true' if the file was saved successfully, 'false' otherwise
     */
    private boolean saveFamilyRelationships(Family family, FileOutputStream fileStream){
        boolean saved = true;

        Document document;
        Element rootElement;
        Element personElement;
        Element fatherElement;
        Element motherElement;
        Element currentSpouseElement;

        try {
            DocumentBuilder docBuilder = this.documentBuilderFactory.newDocumentBuilder();

            document = docBuilder.newDocument();

            rootElement = document.createElement(RELATIONSHIPS);

            List<Person> familyMemebers = family.getFamilyMembers();

            // Create relationship for each family member
            for(Person person : familyMemebers){

                // Create the person
                personElement = document.createElement(PERSON);
                personElement.setAttribute(UUID, person.getUUIDString());

                // Set up parents
                if(person.getFather() != null){
                    fatherElement = document.createElement(FATHER);
                    fatherElement.setAttribute(UUID, person.getFather().getUUIDString());
                    personElement.appendChild(fatherElement);
                }

                if(person.getMother() != null){
                    motherElement = document.createElement(MOTHER);
                    motherElement.setAttribute(UUID,person.getMother().getUUIDString());
                    personElement.appendChild(motherElement);
                }

                // Set up current spouse
                if(person.getCurrentSpouse() != null){
                    currentSpouseElement = document.createElement(CUR_SPOUSE);
                    currentSpouseElement.setAttribute(UUID,person.getCurrentSpouse().getUUIDString());
                    personElement.appendChild(currentSpouseElement);
                }

                // Set up spouses
                generateListOfUUIDElements(person.getSpouses(), document, personElement, SPOUSES);

                // Set up Siblings
                generateListOfUUIDElements(person.getSiblings(), document, personElement, SIBLINGS);

                // Set up Children
                generateListOfUUIDElements(person.getChildren(), document, personElement, CHILDREN);

                rootElement.appendChild(personElement);
            }

            document.appendChild(rootElement);

            // Set up transformer
            Transformer transformer = this.transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");


            // Write to file
            transformer.transform(new DOMSource(document), new StreamResult(fileStream));

        } catch (Exception e) {
            LOGGER.severe("Unable to save family relationship file for family: " + family.getFamilyName() + ". "
             + "Caught Exception: " + e);
            saved = false;
        }

        return saved;
    }

    /**
     * Helper method which loops through a list of persons, creating a uuid for each person and appending to the
     * rootElement
     * @param people the list to create uuids from
     * @param document the document to create elements from
     * @param rootElement the root element to append too
     * @param elementName the name of the element which the list of people represents
     */
    private void generateListOfUUIDElements(List<Person> people, Document document, Element rootElement, String elementName){

        Element uuidElement = null;
        Element element = null;
        if(!people.isEmpty()){
            element = document.createElement(elementName);

            for(Person person: people){
                uuidElement = document.createElement(UUID);
                uuidElement.appendChild(document.createTextNode(person.getUUIDString()));
                element.appendChild(uuidElement);
            }
            rootElement.appendChild(element);
        }
    }

    // Private Fields ==================================================================================================

    private JAXBContext personContext;

    private Marshaller personMarshaller;

    private JAXBContext familyContext;

    private Marshaller familyMarshaller;

    private DocumentBuilderFactory documentBuilderFactory;

    private TransformerFactory transformerFactory;

    private Transformer transformer;
}
