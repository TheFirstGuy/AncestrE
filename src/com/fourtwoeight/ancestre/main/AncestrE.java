package com.fourtwoeight.ancestre.main;

import com.fourtwoeight.ancestre.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.logging.LogManager;

public class AncestrE extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        // Load logger configurations
        final LogManager logManager = LogManager.getLogManager();
        try(final InputStream inputStream = getClass().getResourceAsStream("../../../../config/logging.properties")){
            logManager.readConfiguration(inputStream);
            inputStream.close();
        }
        stateManager = StateManager.getInstance();

        // Load the main FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../../../resources/fxml/main.fxml"));
        Parent root = fxmlLoader.load();
        MainController mainController = fxmlLoader.getController();
        mainController.initialize(primaryStage);

        // Start the stateManager thread
        Thread stateManagerThread = new Thread(this.stateManager);
        stateManagerThread.start();

        // Display the application
        primaryStage.setTitle("AncestrE");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Private Fields ==================================================================================================

    private StateManager stateManager;
}
