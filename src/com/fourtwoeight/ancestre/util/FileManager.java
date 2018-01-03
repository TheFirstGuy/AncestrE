package com.fourtwoeight.ancestre.util;

import com.fourtwoeight.ancestre.model.Family;
import com.fourtwoeight.ancestre.model.Person;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.logging.Logger;

public class FileManager {

    // Private Static Fields ===========================================================================================

    private static FileManager instance;

    private static final Logger LOGGER = Logger.getLogger(FileManager.class.getName());

    private static String UUID = "uuid";
    private static String RELATIONSHIPS = "Relationships";
    private static String PERSON = "Person";
    private static String FATHER = "Father";
    private static String MOTHER = "Mother";
    private static String CUR_SPOUSE = "CurrentSpouse";
    private static String SPOUSES = "Spouses";
    private static String CHILDREN = "Children";

    // Public Methods ==================================================================================================


    /**
     * @return The singleton instance of the FileManager
     */
    public static FileManager getInstance() {
        if(instance == null){
            instance = new FileManager();
        }
        return instance;
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
     * Loads a family from the provided family file
     * @param family the reference to the family object to load data into
     * @param familyFile the family file to load
     * @param errorMsg the error message to alert the user if not saved successfully
     * @return 'true' if loaded successfully. 'false' otherwise
     */
    public boolean load(Family family, File familyFile, StringBuilder errorMsg){
        boolean loaded = false;
        // clear out error message
        errorMsg.delete(0, errorMsg.length());

        if(familyFile.isFile()){
            if(familyFile.canRead()){

                // Load the family
                try (FileInputStream familyInputStream = new FileInputStream(familyFile)) {
                    loaded = loadFamily(family, familyInputStream);
                    familyInputStream.close();
                } catch( Exception e){
                    errorMsg.append("Unable to load .fam file.");
                    LOGGER.severe("Caught exception: " + e.toString());
                }

                if(loaded){
                    // Get the parse the familyFile name
                    String[] split = familyFile.getName().split("\\.");
                    String familyFileName = (split.length > 0) ? split[0] : familyFile.getName();

                    File relationshipFile = new File(familyFile.getParent() +
                            "/" + familyFileName + ".rel");

                    LOGGER.finest("Loading relationship file with path: " + relationshipFile.getAbsolutePath());

                    // Load the relationship file
                    try(FileInputStream relationshipInputStream = new FileInputStream(relationshipFile)){
                        loaded = loadRelationships(family, relationshipInputStream);
                        relationshipInputStream.close();
                    }catch (Exception e){
                        errorMsg.append("Unable to to load .rel file.");
                        LOGGER.severe("Caught exception: " + e.toString());
                    }
                }
            }
            else{
                errorMsg.append("The selected file: " + familyFile.getName() + " is not readable.");
                LOGGER.warning(errorMsg.toString());
            }
        }
        else{
            errorMsg.append("The selected file: " + familyFile.getName() + " is not a file.");
            LOGGER.warning(errorMsg.toString());
        }

        if(!loaded && errorMsg.length() == 0){
            errorMsg.append("Unable to load from file: " + familyFile.getName());
        }

        return loaded;
    }

    /**
     * Saves the family and all supporting files
     * @param family the family to be saved
     * @param fileName the name of the family file to be saved
     * @param directory the directory to save the family
     * @param errorMsg the error message to alert the user if not saved successfully
     * @return 'true' if saved successfully. 'false' otherwise.
     */
    public boolean save(Family family, String fileName, File directory, StringBuilder errorMsg){
        boolean saved = false;
        // clear out error message
        errorMsg.delete(0, errorMsg.length());

        // Create the family file
        if(directory.isDirectory()){
            // Create Family and Relationship File
            File familyFile = new File(directory.getAbsolutePath() + "/" + fileName +".fam");
            File relationshipFile = new File(directory.getAbsolutePath() + "/" + fileName + ".rel");

            // Save the family file
            try (FileOutputStream familyOutputStream = new FileOutputStream(familyFile)) {
                saved = saveFamily(family, familyOutputStream);
                familyOutputStream.close();
            } catch (Exception e){
                errorMsg.append("Unable to save .fam file.");
                LOGGER.severe("Caught exception: " + e.toString());
            }

            // Save the relationship file
            if(saved){
               try(FileOutputStream relationshipOutputStream = new FileOutputStream(relationshipFile)){
                    saved = saveFamilyRelationships(family, relationshipOutputStream);
                    relationshipOutputStream.close();
               } catch (Exception e){
                   errorMsg.append("Unable to save .rel file.");
                   LOGGER.severe("Caught exception: " + e.toString());
               }
            }
        }
        else{
            errorMsg.append(directory.getName() + " is not a directory.");
            LOGGER.warning(errorMsg.toString());
        }

        if(!saved & errorMsg.length() == 0){
            errorMsg.append("Unable to save family: " + family.getFamilyName());
        }
        return saved;
    }




    // Private Methods =================================================================================================

    /**
     * Private Constructor
     */
    private FileManager(){
        LOGGER.fine("Initializing FileManager.");
        try {
            // Set up person context and marshaller
            this.personContext = JAXBContext.newInstance(Person.class);
            this.personMarshaller = this.personContext.createMarshaller();
            this.personMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Set up family context and marshaller
            this.familyContext = JAXBContext.newInstance(Family.class);
            this.familyMarshaller = this.familyContext.createMarshaller();
            this.familyMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            this.familyUnmarshaller = this.familyContext.createUnmarshaller();

            // Set up DocumentBuilderFactory
            this.documentBuilderFactory = DocumentBuilderFactory.newInstance();

            // Set up TransformerFactory
            this.transformerFactory = TransformerFactory.newInstance();
        } catch (JAXBException e) {
            LOGGER.severe("Unable to initialize FileManager. Caught Exception: " + e);
        }
    }

    /**
     * Saves a family object to the provided fileStream
     * @param family the family to be saved
     * @param fileStream the file to be saved too
     * @return Whether the family was saved successfully
     */
    private boolean saveFamily(Family family, FileOutputStream fileStream){
        LOGGER.fine("Entering saveFamily()");
        boolean saved = false;
        try {
            familyMarshaller.marshal(family, fileStream);
            saved = true;
        } catch (Exception e) {
            LOGGER.severe("Unable to save family: " + family.getFamilyName() + ". Caught Exception: " + e);
        }
        return saved;
    }

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
        LOGGER.finer("Generating list of UUID elements.");
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

    /**
     * Loads a .fam file into a family object
     * @puaram family the family to load into
     * @param fileInputStream the file stream to load from
     * @return 'true' if family was loaded successfully. 'false' otherwise.
     */
    private boolean loadFamily(Family family, FileInputStream fileInputStream){
        LOGGER.info("Loading family.");
        boolean loaded = false;
        try {
            family = (Family)this.familyUnmarshaller.unmarshal(fileInputStream);
            loaded = true;
            LOGGER.finer("Unmarshalled family.");
        } catch (JAXBException e) {
            LOGGER.severe("Caught Exception: " + e.toString());
        }
        return loaded;
    }

    /**
     * Loads a .rel file and updates the family object to have the correct relationships
     * @param family the family to update
     * @param fileInputStream the file stream to load from
     * @return 'true' if the relationship data was loaded successfully. 'false' otherwise.
     */
    private boolean loadRelationships(Family family, FileInputStream fileInputStream){
        LOGGER.fine("Loading relationships.");
        boolean loaded = true;

        Document document;
        Element rootElement;
        NodeList nodeList;

        try {
            DocumentBuilder builder = this.documentBuilderFactory.newDocumentBuilder();
            document = builder.parse(fileInputStream);

            nodeList = document.getElementsByTagName(RELATIONSHIPS);

            if(nodeList.getLength() == 1){
                rootElement = (Element) nodeList.item(0);
                NodeList personNodes = rootElement.getChildNodes();
                Node personNode = null;

                LOGGER.finer("Extracting data for each person.");
                for(int i = 0; i < personNodes.getLength(); i++){
                    personNode = personNodes.item(i);
                    loaded &= extractRelationships(personNode, family);
                    System.out.println(loaded);
                }
            }
            else{
                LOGGER.severe("Could not extract relationship element.");
                loaded = false;
            }

        } catch (Exception e) {
            LOGGER.severe("Caught Exception: " + e.toString());
        }

        return loaded;
    }

    /**
     * Extracts the relationship data for a specific person
     * @param personNode the person node being extracted
     * @param family the family object to be updated
     * @return 'true' if all data was extracted successfully. 'false' otherwise.
     */
    private boolean extractRelationships(Node personNode, Family family){
        LOGGER.finer("Extracting relationships for person node");
        boolean extracted = false;

        Person person;
        Element personElement;
        String personUUID;

        if(personNode.getNodeName() == PERSON){
            // Identify person
            personElement = (Element) personNode;
            personUUID = personElement.getAttribute(UUID);
            person = family.getPerson(personUUID);

            // Extract relationships for each person
            if(person != null){
                extracted = extractFather(personElement, person, family);
                System.out.println(extracted);
                extracted &= extractMother(personElement, person, family);
                System.out.println(extracted);
                extracted &= extractCurrentSpouse(personElement, person, family);
                System.out.println(extracted);
                extracted &= extractSpouses(personElement, person, family);
                System.out.println(extracted);
                extracted &= extractChildren(personElement, person, family);
                System.out.println(extracted);
            }
            else{
                LOGGER.warning("Unable to find person for UUID: " + personElement.getAttribute(UUID));
            }

        }
        // not a person node
        else{
            extracted = true;
        }
        System.out.println("ExtractRelationships: " + extracted);
        return extracted;
    }

    /**
     * Attempts to extract the father of the person element passed in
     * @param personElement the person element being extracted
     * @param person the person object with the same uuid as the personElement
     * @param family the family object to be updated
     * @return 'true' if no parsing errors were found
     */
    private boolean extractFather(Element personElement, Person person, Family family){
        LOGGER.finer("Extracting father for " + person.getFullName() + ".");
        boolean extracted = false;
        NodeList nodeList = personElement.getElementsByTagName(FATHER);

        // Check if the node exists
        if(nodeList.getLength() == 1){
            Element fatherElement = (Element)nodeList.item(0);

            Person father = family.getPerson(fatherElement.getAttribute(UUID));

            // Set the extracted father
            if(father != null){
                person.setFather(father);
                extracted = true;
            }
            else{
                LOGGER.warning("Unable to find person for UUID: " + personElement.getAttribute(UUID));
            }
        }
        // there was not father to extract
        else{
            extracted = true;
        }
        return extracted;
    }

    /**
     * Attempts to extract the mother of the person element passed in
     * @param personElement the person element being extracted
     * @param person the person object with the same uuid as the personElement
     * @param family the family object to be updated
     * @return 'true' if no parsing errors were found
     */
    private boolean extractMother(Element personElement, Person person, Family family){
        LOGGER.finer("Extracting Mother for " + person.getFullName() + ".");
        boolean extracted = false;
        NodeList nodeList = personElement.getElementsByTagName(MOTHER);

        // Check if the node exists
        if(nodeList.getLength() == 1){
            Element motherElement = (Element)nodeList.item(0);

            Person mother = family.getPerson(motherElement.getAttribute(UUID));

            // Set the extracted father
            if(mother != null){
                person.setMother(mother);
                extracted = true;
            }
            else{
                LOGGER.warning("Unable to find person for UUID: " + personElement.getAttribute(UUID));
            }
        }
        // There was not mother to extract
        else{
            extracted = true;
        }
        return extracted;
    }

    /**
     * Attempts to extract the currentSpouse of the person element passed in
     * @param personElement the person element being extracted
     * @param person the person object with the same uuid as the personElement
     * @param family the family object to be updated
     * @return 'true' if no parsing errors were found
     */
    private boolean extractCurrentSpouse(Element personElement, Person person, Family family){
        LOGGER.finer("Extracting current spouse for " + person.getFullName() + ".");
        boolean extracted = false;
        NodeList nodeList = personElement.getElementsByTagName(CUR_SPOUSE);

        // Check if the node exists
        if(nodeList.getLength() == 1){
            Element curSpouseElement = (Element)nodeList.item(0);

            Person currentSpouse = family.getPerson(curSpouseElement.getAttribute(UUID));

            // Set the extracted father
            if(currentSpouse != null){
                person.setCurrentSpouse(currentSpouse);
                extracted = true;
            }
            else{
                LOGGER.warning("Unable to find person for UUID: " + personElement.getAttribute(UUID));
            }
        }
        // There was not mother to extract
        else{
            extracted = true;
        }
        return extracted;
    }

    /**
     * Attempts to extract the list of spouses of the person element passed in
     * @param personElement the person element being extracted
     * @param person the person object with the same uuid as the personElement
     * @param family the family object to be updated
     * @return 'true' if no parsing errors were found
     */
    private boolean extractSpouses(Element personElement, Person person, Family family){
        LOGGER.finer("Extracting spouses for " + person.getFullName() + ".");
        boolean extracted = false;
        NodeList nodeList = personElement.getElementsByTagName(SPOUSES);

        // Check if the node exists
        if(nodeList.getLength() == 1){
            Element spousesElement = (Element)nodeList.item(0);

            NodeList spouseList = spousesElement.getElementsByTagName(UUID);
            Element spouseElement;
            Person spouse;

            for(int i = 0; i < spouseList.getLength(); i++){
                spouseElement = (Element)spouseList.item(i);
                spouse = family.getPerson(spouseElement.getTextContent());

                // Set the spouse
                if(spouse != null){
                    person.addSpouse(spouse);
                    extracted = true;
                }
                else{
                    LOGGER.warning("Unable to find person for UUID: " + personElement.getAttribute(UUID));
                }
            }
        }
        // No spouses to extract
        else{
            extracted = true;
        }
        return extracted;
    }

    /**
     * Attempts to extract the list of children of the person element passed in
     * @param personElement the person element being extracted
     * @param person the person object with the same uuid as the personElement
     * @param family the family object to be updated
     * @return 'true' if no parsing errors were found
     */
    private boolean extractChildren(Element personElement, Person person, Family family){
        LOGGER.finer("Extracting children for " + person.getFullName() + ".");
        boolean extracted = false;
        NodeList nodeList = personElement.getElementsByTagName(CHILDREN);

        // Check if the node exists
        if(nodeList.getLength() == 1){
            Element childrenElement = (Element)nodeList.item(0);

            NodeList childrenList = childrenElement.getElementsByTagName(UUID);
            Element childElement;
            Person child;

            for(int i = 0; i < childrenList.getLength(); i++){
                childElement = (Element)childrenList.item(i);
                child = family.getPerson(childElement.getTextContent());

                // Set the spouse
                if(child != null){
                    person.addSpouse(child);
                    extracted = true;
                }
                else{
                    LOGGER.warning("Unable to find person for UUID: " + personElement.getAttribute(UUID));
                }
            }
        }
        // No children to extract
        else{
            extracted = true;
        }


        return extracted;
    }

    // Private Fields ==================================================================================================

    private JAXBContext personContext;

    private Marshaller personMarshaller;

    private JAXBContext familyContext;

    private Marshaller familyMarshaller;

    private Unmarshaller familyUnmarshaller;

    private DocumentBuilderFactory documentBuilderFactory;

    private TransformerFactory transformerFactory;

    private Transformer transformer;
}
