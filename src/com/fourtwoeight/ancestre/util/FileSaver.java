package com.fourtwoeight.ancestre.util;

import com.fourtwoeight.ancestre.model.Family;
import com.fourtwoeight.ancestre.model.Person;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Logger;

public class FileSaver {

    // Private Static Fields ===========================================================================================

    private static Logger LOGGER = Logger.getLogger(FileSaver.class.getName());


    // Public Methods ==================================================================================================


    public FileSaver(){

    }

    public void initialize(){
        try {
            this.personContext = JAXBContext.newInstance(Person.class);
            this.personMarshaller = this.personContext.createMarshaller();
            this.personMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            this.familyContext = JAXBContext.newInstance(Family.class);
            this.familyMarshaller = this.familyContext.createMarshaller();
            this.familyMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

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
        try {
            familyMarshaller.marshal(family, fileStream);
            saved = true;
        } catch (JAXBException e) {
            LOGGER.severe("Unable to save family: " + family.getFamilyName() + ". Caught Exception: " + e);
        }
        return saved;
    }



    // Private Fields ==================================================================================================

    private JAXBContext personContext;

    private Marshaller personMarshaller;

    private JAXBContext familyContext;

    private Marshaller familyMarshaller;
}
