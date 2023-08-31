package com.example.driphub;
import javafx.application.Application;
import javafx.stage.Stage;

public class Runner extends Application {
    public static String currentUser = "";   //gets value when a user signs in
    public void start(Stage primaryStage) {
        primaryStage.setTitle("DripHub");
        //passes primary stage to another class, that has method that takes Stage as argument and returns a scene.
        primaryStage.setScene(SignIn.createSignInScene(primaryStage));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
