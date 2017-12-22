package com.fourtwoeight.controllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Loads the fxml for the main page for the AncestrE application
 */
public class MainController {

    // Private Static Fields ===========================================================================================

    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());


    // Public Fields ===================================================================================================



    // Private Fields ==================================================================================================

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
