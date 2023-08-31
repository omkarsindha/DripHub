package com.example.driphub;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.*;

public class Seller {
    // three variables used by Connection class to create connection with database
    private static final String url = "jdbc:mysql://localhost:3306/drip_hub";
    private static final String username = "root"; //default username of xampp SQL database
    private static final String password = "";  //default password of xampp SQL database
    public static Scene createSellerScene(Stage primaryStage){
        //Main pane
        BorderPane pane = new BorderPane();

        //Image for left pane
        //url used to get absolute path
        URL imageURL = Seller.class.getResource("/posts/logo.png");
        ImageView img = new ImageView(imageURL.toExternalForm());
        img.setFitHeight(200);  img.setFitWidth(200);
        img.setLayoutX(370);   img.setLayoutY(0);
        //Printing all items from database to center pane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setStyle("-fx-background-color:  rgb(255, 255, 222);");
        VBox centerPane = new VBox();
        //styling for center pane
        centerPane.setAlignment(Pos.CENTER);
        centerPane.setSpacing(30);
        centerPane.setStyle("-fx-background-color:  rgb(255, 255, 222);");
        scrollPane.setPadding(new Insets(20));
        Label itemsLbl = new Label("Your Products");
        itemsLbl.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));
        centerPane.getChildren().addAll(itemsLbl);
        //method to print content panes
        printContentpanes(centerPane);
        scrollPane.setContent(centerPane);
        scrollPane.setFitToWidth(true);


        // Sell pane for right pane
        Label title = new Label("Sell Item");
        title.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));

        //button to choose image
        Button choose = new Button("Choose image");
        choose.setPrefHeight(150);
        choose.setPrefWidth(150);

        Label selectedLbl = new Label("Image Name");
        //making it array because it was throwing some error when set to file variable
        final File[] selectedFile = {null};
        choose.setOnAction(e->{
            //file chooser to choose image
            FileChooser fileChooser = new FileChooser();
            //setting filters for file type
            FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg");
            fileChooser.getExtensionFilters().add(imageFilter);
            //0th index because there is only one file selected
            selectedFile[0] = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile[0] != null) {
                choose.setText("");
                choose.setStyle("-fx-border-color: transparent; -fx-background-color: transparent;");
                ImageView itemImage = new ImageView(selectedFile[0].getPath());
                itemImage.setFitWidth(150); itemImage.setFitHeight(150);
                //change button to image selected
                choose.setGraphic(itemImage);
                choose.setAlignment(Pos.CENTER);
                selectedLbl.setText(selectedFile[0].getName());
            }
        });

        //get user data fields
        Label nameLbl = new Label("Item name: ");
        TextField nameTxt = new TextField();
        HBox namePane = new HBox();
        namePane.getChildren().addAll(nameLbl,nameTxt);

        Label priceLbl = new Label("Item price:  ");
        TextField priceTxt = new TextField();
        HBox princePane = new HBox();
        princePane.getChildren().addAll(priceLbl,priceTxt);

        Label error = new Label("");
        error.setTextFill(Color.RED);
        Button sell = new Button("Sell");
        sell.setStyle(" -fx-background-color: #0000ff;  -fx-text-fill: white; -fx-pref-width: 100px; -fx-font-size: 14px; -fx-pref-height: 30px; ");

        sell.setPrefWidth(50);
        sell.setOnAction(e->{
            //check if user has entered all three fields
            if(!priceTxt.getText().equals("") && !nameTxt.getText().equals("") && selectedFile[0]!=null) {
                try {
                    String nameDB = nameTxt.getText();
                    double priceDB = Double.parseDouble(priceTxt.getText());

                    File destinationFolder = new File("src/main/resources/posts");
                    String randomFileName = String.valueOf((Math.random()*9000) + 1000)+".jpeg";
                    String itemPath = "/posts/" + randomFileName;
                    String destinationPath = destinationFolder.getAbsolutePath()+"/"+randomFileName;
                    File destination = new File(destinationPath);
                    //copy the selected file to the posts folder under resources
                    Files.copy(selectedFile[0].toPath(), destination.toPath());
                    try {
                        Connection connection = DriverManager.getConnection(url, username, password);
                         // inserting the sold item into the items table in database
                        String sql = "INSERT INTO items(item_name, item_owner, item_path, item_price) VALUES (?, ?, ?, ?)";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, nameDB);
                        statement.setString(2, Runner.currentUser);
                        statement.setString(3, itemPath);
                        statement.setString(4, String.valueOf(priceDB));
                        statement.executeUpdate();
                        error.setTextFill(Color.BLACK);
                        error.setText("Product posted successfully!");

                        //updating the center pane to show sold item
                        VBox contentPane = new VBox();
                        contentPane.setAlignment(Pos.CENTER);
                        contentPane.setSpacing(10);
                        contentPane.setPadding(new Insets(20));
                        Label itemName = new Label(nameDB);
                        itemName.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 15));
                        Label itemPrice = new Label("Price: " + priceDB + "$");
                        itemPrice.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 15));
                        Button deleteButton = new Button("Delete");
                        deleteButton.setOnAction(ex->{
                            try{
                                //sql update query to delete item from table
                                String delSql = "DELETE FROM items WHERE item_path = ? AND item_owner = ?";
                                PreparedStatement statement1 = connection.prepareStatement(delSql);
                                statement1.setString(1,itemPath);
                                statement1.setString(2, Runner.currentUser);
                                statement1.executeUpdate();
                                centerPane.getChildren().remove(contentPane);

                            }catch (SQLException exep){
                                error.setText("An error occurred while deleting item");
                            }
                        });
                        deleteButton.setStyle(" -fx-background-color: #ff0000;  -fx-text-fill: white; -fx-pref-width: 80px; -fx-font-size: 14px; -fx-pref-height: 30px; ");

                        ImageView itemImg = new ImageView(selectedFile[0].getAbsolutePath());
                        //code to preserve aspect ratio
                        Image timePass = new Image(selectedFile[0].getAbsolutePath());
                        double aspectRatio = timePass.getWidth() / timePass.getHeight();
                        itemImg.setFitWidth(300);
                        itemImg.setFitHeight(300/aspectRatio);

                        contentPane.getChildren().addAll(itemName,itemImg,itemPrice,deleteButton);
                        contentPane.setStyle("-fx-background-color: rgb(230, 230, 230);");
                        centerPane.getChildren().add(contentPane);

                    }catch (Exception ex){
                        error.setText("Error occurred while uploading image");
                    }
                } catch (NumberFormatException ex) {
                    error.setText("Price should be numeric!");
                } catch (IOException ex) {
                    error.setText("Error occurred while saving file");
                }
            }
            else{
                error.setText("Name or price or image not chosen");
            }
        });
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(" -fx-background-color: #ff0000;  -fx-text-fill: white; -fx-pref-width: 100px; -fx-font-size: 14px; -fx-pref-height: 30px; ");

        logoutBtn.setOnAction(e->{
            primaryStage.setScene(SignIn.createSignInScene(primaryStage));
        });
        VBox rightPane = new VBox();
        rightPane.setAlignment(Pos.TOP_CENTER);
        rightPane.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding: 10px;");
        rightPane.setPadding(new Insets(50));
        rightPane.setSpacing(20);
        rightPane.getChildren().addAll(title,choose,selectedLbl,namePane,princePane,sell,error,logoutBtn);

        pane.setLeft(img);
        pane.setRight(rightPane);
        pane.setCenter(scrollPane);
        return new Scene(pane,900,600);
    }

    //method to create content panes in center pane
    public static void printContentpanes(VBox centerPane){
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            // show only items that the current user owns
            String sqlQuery = "SELECT * FROM items WHERE item_owner = ?";
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1,Runner.currentUser);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                VBox contentPane = new VBox();
                contentPane.setAlignment(Pos.CENTER);
                contentPane.setSpacing(10);
                contentPane.setPadding(new Insets(20));
                contentPane.setStyle("-fx-background-color: rgb(230, 230, 230);");
                Label itemName = new Label(resultSet.getString("item_name"));
                itemName.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 15));
                Label itemPrice = new Label("Price: " + resultSet.getString("item_price") + "$");
                itemPrice.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 15));
                String itemPath = resultSet.getString("item_path");
                Button deleteButton = new Button("Delete");
                deleteButton.setOnAction(ex->{
                    try{
                        //delete item query
                        String delSql = "DELETE FROM items WHERE item_path = ? AND item_owner = ?";
                        PreparedStatement statement1 = connection.prepareStatement(delSql);
                        statement1.setString(1, itemPath);
                        statement1.setString(2, Runner.currentUser);
                        statement1.executeUpdate();
                        centerPane.getChildren().remove(contentPane);

                    }catch (SQLException exep){
                        exep.printStackTrace();
                    }
                });
                deleteButton.setStyle(" -fx-background-color: #ff0000;  -fx-text-fill: white; -fx-pref-width: 80px; -fx-font-size: 14px; -fx-pref-height: 30px; ");

                URL itemImageURL = Seller.class.getResource(itemPath);
                if(itemImageURL != null) {
                    ImageView itemImg = new ImageView(itemImageURL.toExternalForm());
                    //code to preserve aspect ratio
                    Image timePass = new Image(itemImageURL.toExternalForm());
                    double aspectRatio = timePass.getWidth() / timePass.getHeight();
                    itemImg.setFitWidth(300);
                    itemImg.setFitHeight(300/aspectRatio);
                    contentPane.getChildren().addAll(itemName, itemImg, itemPrice,deleteButton);
                }
                centerPane.getChildren().add(contentPane);
            }

        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

}
