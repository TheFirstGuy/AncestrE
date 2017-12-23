package com.fourtwoeight.ancestre.util;

import com.fourtwoeight.ancestre.model.Person;

public class FileSaver {

    // Private Static Fields ===========================================================================================

    private static FileSaver instance = null;

    // Public Methods ==================================================================================================


    public FileSaver(){

    }

    public static FileSaver getInstance(){
        if(instance == null){
            instance = new FileSaver();
        }

        return instance;
    }


    public boolean savePerson(Person person){
        boolean saved = false;

        return saved;
    }




}
