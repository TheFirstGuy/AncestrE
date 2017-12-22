package com.fourtwoeight.main;

import com.fourtwoeight.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AncestrE extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../../resources/fxml/main.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        MainController mainController = fxmlLoader.<MainController>getController();
        primaryStage.setTitle("AncestrE");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
