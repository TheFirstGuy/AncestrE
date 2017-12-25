package com.fourtwoeight.ancestre.controllers;


import com.fourtwoeight.ancestre.model.Family;
import com.fourtwoeight.ancestre.model.Person;

import static com.fourtwoeight.ancestre.model.Person.SEX.FEMALE;
import static com.fourtwoeight.ancestre.model.Person.SEX.MALE;

import com.fourtwoeight.ancestre.ui.GraphGenerator;
import com.fourtwoeight.ancestre.util.FileSaver;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import org.graphsfx.graph.CircularReferenceException;
import org.graphsfx.graph.TreeGraph;
import org.graphsfx.model.GraphNode;

import java.io.File;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Loads the fxml for the main page for the AncestrE application
 */
public class MainController {

    // Private Static Fields ===========================================================================================

    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    private static final String DEFAULT_MALE_PATH = "resources/images/default_male.png";

    private static final String DEFAULT_FEMALE_PATH = "resources/images/default_female.png";


    // Public Fields ===================================================================================================


    /**
     * Initializes the main screen
     */
    public void initialize(){
        // Initialize graph
        this.graphContainer.getChildren().add(treeGraph);

        // Set bindings
        setPortaitBindings();
        this.viewport.radiusProperty().bind(this.portriatRadius);
        this.viewport.centerXProperty().bind(this.portraitCenterX);
        this.viewport.centerYProperty().bind(this.portraitCenterY);

        // Initialize portrait
        Image image = new Image(DEFAULT_FEMALE_PATH);
        this.portrait.setImage(image);
        this.portrait.setClip(this.viewport);

        Person me = new Person("Urs",
                new ArrayList<String>() {{add("Nelson");}},
                "Evora",
                MALE,
                Calendar.getInstance(),
                null
                );

        me.setUUID(UUID.randomUUID());

        Person dad = new Person("Dad",
                new ArrayList<String>(){{add("E");}},
                "Evora",
                MALE,
                Calendar.getInstance(),
                null);
        dad.setUUID(UUID.randomUUID());

        Person mom = new Person("Mom",
                new ArrayList<String>(),
                "Evora",
                FEMALE,
                Calendar.getInstance(),
                null);
        mom.setUUID(UUID.randomUUID());

        me.setDescription("He was a cool guy.\nHe did lots of things.");

        Family family = new Family("MyFamily");
        family.addPerson(me);
        family.addPerson(dad);
        family.addPerson(mom);
        System.out.println(family.getFamilyMembers().size());
        FileSaver fileSaver = new FileSaver();

        try {
            FileOutputStream fileOut = new FileOutputStream("Person.xml");
            fileSaver.initialize();
            fileSaver.saveFamily(family, fileOut);
        } catch( Exception e){

        }

        me.setFather(dad);
        me.setMother(mom);
        setPersonPane(me);
        setGraphPane(me);
    }


    public void setGraphPane(Person person){
        try{
            HashMap<Person, GraphNode> nodes = new HashMap<Person, GraphNode>();
            GraphGenerator.generateAncestry(person, this.treeGraph, nodes);

        } catch (CircularReferenceException e){
            LOGGER.warning("Received Exception: " + e.getClass().getName() + " for Person " + person.getFullName());
        }

    }

    /**
     * Populates the person description panel
     */
    public void setPersonPane(Person person){
        setPersonImage(person);
        this.nameLabel.setText(person.getFullName());
        this.lifetimeLabel.setText(person.getLifeTime());
        descriptionArea.setText(person.getDescription());

        // Add spouses
        this.spousesList.getItems().clear();
        for(Person spouse: person.getSpouses()){
            this.spousesList.getItems().add(spouse.getFullName());
        }

        // Add Children
        this.childrenList.getItems().clear();
        for(Person child: person.getChildren()){
            this.childrenList.getItems().add(child.getFullName());
        }

        // Add Siblings
        this.siblingList.getItems().clear();
        for(Person sibling: person.getSiblings()){
            this.siblingList.getItems().add(sibling.getFullName());
        }



    }



    // Private Methods =================================================================================================

    /**
     * Sets the binding properties to be the center position of the portrait (ImageView). This allows the viewport
     * to be centered correctly.
     */
    private void setPortaitBindings(){
        this.portraitCenterX.bind(new DoubleBinding() {
            {
                super.bind(MainController.this.portrait.xProperty(),
                        MainController.this.portrait.fitWidthProperty());
            }

            @Override
            protected double computeValue() {
                return MainController.this.portrait.xProperty().get() +
                        (MainController.this.portrait.fitWidthProperty().get() / 2);
            }
        });

        this.portraitCenterY.bind(new DoubleBinding() {
            {
                super.bind(MainController.this.portrait.yProperty(),
                        MainController.this.portrait.fitHeightProperty());
            }

            @Override
            protected double computeValue() {
                return MainController.this.portrait.yProperty().get() +
                        (MainController.this.portrait.fitHeightProperty().get() / 2);
            }
        });

        this.portriatRadius.bind(new DoubleBinding() {
            {
                super.bind(MainController.this.portrait.fitWidthProperty(),
                        MainController.this.portrait.fitHeightProperty());
            }

            @Override
            protected double computeValue() {
                double radius, width, height;

                width = MainController.this.portrait.fitWidthProperty().get();
                height = MainController.this.portrait.fitHeightProperty().get();

                // The larger value is the radius
                radius = (width > height) ? width : height;

                return radius;
            }
        });
    }

    /**
     * Sets the image for the person description pane
     * @param person The person to source the image from
     */
    private void setPersonImage(Person person){
        // Set the image
        File imageFile;
        Image image;

        // Set up default image
        if( person.getSex() == MALE){
            image = new Image(DEFAULT_MALE_PATH);
        }
        else{
            image = new Image(DEFAULT_FEMALE_PATH);
        }

        // Set up specific image if available
        if(person.getImagePath() != null) {
            imageFile = new File(person.getImagePath());

            // Check if image path is correct
            if(imageFile.exists() && imageFile.canRead()){
                image = new Image(person.getImagePath());
            }
        }

        this.portrait.setImage(image);
    }

    // Private Fields ==================================================================================================

    private TreeGraph treeGraph = new TreeGraph();


    private HashMap<Person, GraphNode> nodes;
    private Circle viewport = new Circle(150);

    private DoubleProperty portraitCenterX = new SimpleDoubleProperty();

    private DoubleProperty portraitCenterY = new SimpleDoubleProperty();

    private DoubleProperty portriatRadius = new SimpleDoubleProperty();

    @FXML
    private TableView<String> index;

    @FXML
    private Pane graphContainer;

    @FXML
    private ImageView portrait;

    @FXML
    private VBox detailsPane;

    @FXML
    private Label nameLabel;

    @FXML
    private Label lifetimeLabel;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ListView<String> spousesList;

    @FXML
    private ListView<String> siblingList;

    @FXML
    private ListView<String> childrenList;
}
