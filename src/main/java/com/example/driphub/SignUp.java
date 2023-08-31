package com.example.driphub;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUp {
    // three variables used by Connection class to create connection with database
    private static final String url = "jdbc:mysql://localhost:3306/drip_hub";
    private static final String username = "root"; //default username of xampp SQL database
    private static final String password = "";  //default password of xampp SQL database
    public static Scene createSignUpScene(Stage primaryStage) {
        Label error = new Label("");
        error.setTextFill(Color.RED);
        error.setLayoutX(420); error.setLayoutY(165);


        Label name = new Label("Name: ");
        name.setLayoutX(370); name.setLayoutY(210);
        TextField nameTxt = new TextField();
        nameTxt.setLayoutX(420); nameTxt.setLayoutY(210);

        Label email = new Label("Email: ");
        email.setLayoutX(370); email.setLayoutY(250);
        TextField emailTxt = new TextField();
        emailTxt.setLayoutX(420); emailTxt.setLayoutY(250);

        Label user = new Label("Username: ");
        user.setLayoutX(350); user.setLayoutY(290);
        TextField userTxt = new TextField();
        userTxt.setLayoutX(420); userTxt.setLayoutY(290);

        Label pass = new Label("Password: ");
        pass.setLayoutX(350); pass.setLayoutY(330);
        PasswordField passTxt = new PasswordField();
        passTxt.setLayoutX(420); passTxt.setLayoutY(330);

        Label confPass = new Label("Confirm Password: ");
        confPass.setLayoutX(305); confPass.setLayoutY(370);
        PasswordField confPassTxt = new PasswordField();
        confPassTxt.setLayoutX(420); confPassTxt.setLayoutY(370);

        URL imageURL = SignUp.class.getResource("/posts/logo.png");
        ImageView img = new ImageView(imageURL.toExternalForm());
        img.setFitHeight(200);  img.setFitWidth(200);
        img.setLayoutX(370);   img.setLayoutY(0);

        Label type = new Label("Select account type");
        type.setLayoutX(305); type.setLayoutY(410);

        // combo box to let buyer choose the type of account they want
        ComboBox typeCombo = new ComboBox();
        typeCombo.getItems().addAll("Buyer","Seller");
        typeCombo.setValue("Buyer");
        typeCombo.setLayoutX(420); typeCombo.setLayoutY(410);


        Button signup = new Button("Sign Up");
        signup.setLayoutX(350); signup.setLayoutY(450);
        //this is where all the action happens
        signup.setOnAction(e-> {
                    //store all the data user entered into variables
                    String nameDB = nameTxt.getText();
                    String userDB = userTxt.getText();
                    String passDB = passTxt.getText();
                    String confpassDB = confPassTxt.getText(); //confirm password field
                    String emailDB = emailTxt.getText();
                    String typeDB = String.valueOf(typeCombo.getValue());

                    //check if password length is more than 8 and if it is equal to confirm password
                    if (passDB.length() >= 8) {
                        if (passDB.equals(confpassDB)) {
                            try {
                                //create connection
                                Connection connection = DriverManager.getConnection(url, username, password);
                                 // sql Update statement
                                String sql = "INSERT INTO users(name, username, password, email, type) VALUES (?, ?, ?, ?, ?)";
                                PreparedStatement statement = connection.prepareStatement(sql);

                                statement.setString(1, nameDB);
                                statement.setString(2, userDB);
                                statement.setString(3, passDB);
                                statement.setString(4, emailDB);
                                statement.setString(5, typeDB);
                                statement.executeUpdate();
                                statement.close();
                                connection.close();
                                // change scene to buyer or seller depending on type user entered
                                if (typeDB.equals("Seller")) {
                                    primaryStage.setScene(Seller.createSellerScene(primaryStage));
                                } else if(typeDB.equals("Buyer")) {
                                    primaryStage.setScene((Buyer.createBuyerScene((primaryStage))));
                                }
                            // catch duplicate primary key entered error
                            } catch (java.sql.SQLIntegrityConstraintViolationException x) {
                                error.setText("Username already in use");
                            } catch (SQLException ex) {
                                error.setText("An unexpected error occurred");
                            }
                        } else {
                            error.setText("Both passwords should match");
                        }
                    } else {
                        error.setText("Password should be least 8 characters");
                    }
                }
        );
        // change scene to sign in scene
        Button already = new Button("Already have an account?");
        already.setLayoutX(420); already.setLayoutY(450);
        already.setOnAction(e->{
            primaryStage.setScene( SignIn.createSignInScene(primaryStage));
        });

        Pane signUpPane = new Pane();
        signUpPane.getChildren().addAll(img,error,name,nameTxt,email,emailTxt,user,userTxt,pass,passTxt,confPass,confPassTxt,type,typeCombo,signup,already);
        return new Scene(signUpPane, 900, 600);
    }
}