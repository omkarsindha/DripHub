package com.example.driphub;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignIn{
    // three variables used by Connection class to create connection with database
    private static final String url = "jdbc:mysql://localhost:3306/drip_hub";
    private static final String username = "root"; //default username of xampp SQL database
    private static final String password = "";  //default password of xampp SQL database

    //a method that takes stage as argument(so that it set stage if we want to change scene) and returns sign up scene
    public static Scene createSignInScene(Stage primaryStage){

        Label error = new Label("");
        error.setTextFill(Color.RED);
        error.setLayoutX(420); error.setLayoutY(200);

        Label user = new Label("Username: ");
        user.setLayoutX(350); user.setLayoutY(230);

        Label pass = new Label("Password: ");
        pass.setLayoutX(350); pass.setLayoutY(290);

        TextField userTxt = new TextField();
        userTxt.setLayoutX(420); userTxt.setLayoutY(230);

        PasswordField passTxt = new PasswordField();
        passTxt.setLayoutX(420); passTxt.setLayoutY(290);


        //the URL class is to get the absolute path from relative path from resource folder in project
        URL imageURL = SignIn.class.getResource("/posts/logo.png");
        ImageView img = new ImageView(imageURL.toExternalForm());
        img.setFitHeight(200);  img.setFitWidth(200);
        img.setLayoutX(370);   img.setLayoutY(0);

        Button signin = new Button("Sign In");
        signin.setLayoutX(360); signin.setLayoutY(340);
        //this is where all the action happens
        signin.setOnAction(e->{
            // store user entered data into String variables
            String userDB = userTxt.getText();
            //current user is helpful when item is bought or sold
            Runner.currentUser = userDB;
            String passDB = passTxt.getText();
            //create connection and catch errors
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                //SQl query to find user in database
                String sqlQuery = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
                statement.setString(1, userDB);
                statement.setString(2, passDB);
                ResultSet resultSet = statement.executeQuery();
                //if no row found, found variable is false
                boolean found = resultSet.next();
                if(found){
                    String userType = resultSet.getString("type");
                    //direct user to buyer or seller scene depending on type of account
                    if (userType.equals("Seller")) {
                        primaryStage.setScene(Seller.createSellerScene(primaryStage));
                    } else if(userType.equals("Buyer")) {
                        primaryStage.setScene((Buyer.createBuyerScene((primaryStage))));
                    }
                }
                else{
                    error.setText("User or password is incorrect");
                }
                statement.close();
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        Button dont = new Button("Don't have an account?");
        dont.setLayoutX(420); dont.setLayoutY(340);
        //change scene to sign up
        dont.setOnAction(e->{
            primaryStage.setScene( SignUp.createSignUpScene(primaryStage));
        });

        Pane pane = new Pane();
        pane.getChildren().addAll(signin,dont,user,pass,userTxt,passTxt,img,error);
        return new Scene(pane,900,600);
    }

}
