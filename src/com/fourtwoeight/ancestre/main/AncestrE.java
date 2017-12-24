package com.fourtwoeight.ancestre.main;

import com.fourtwoeight.ancestre.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AncestrE extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        stateManager = StateManager.getInstance();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../../../resources/fxml/main.fxml"));
        Parent root = fxmlLoader.load();
        MainController mainController = fxmlLoader.getController();
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
