package com.example.driphub;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;

public class Buyer {
    // three variables used by Connection class to create connection with database
    private static final String url = "jdbc:mysql://localhost:3306/drip_hub";
    private static final String username = "root"; //default username of xampp SQL database
    private static final String password = "";  //default password of xampp SQL database

    //Array list to save all items that are added to cart
    public static ArrayList<String> cartItems= new ArrayList<String>();
    public static Scene createBuyerScene(Stage primaryStage) {
        //Main pane
        BorderPane pane = new BorderPane();
        //logo image for left pane
        URL imageURL = Seller.class.getResource("/posts/logo.png");
        ImageView img = new ImageView(imageURL.toExternalForm());
        img.setFitHeight(200);
        img.setFitWidth(200);
        img.setLayoutX(370);
        img.setLayoutY(0);

        //Printing all items from database to center pane
        Label cartAddedLabel = new Label("");
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:  rgb(255, 255, 222);");
        VBox centerPane = new VBox();
        centerPane.setAlignment(Pos.CENTER);
        centerPane.setSpacing(30);
        centerPane.setStyle("-fx-background-color:  rgb(255, 255, 222);");
        Label itemsLbl = new Label("Products Available");
        itemsLbl.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));
        centerPane.getChildren().addAll(itemsLbl);
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            //sql query to select all items
            String sqlQuery = "SELECT * FROM items";
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = statement.executeQuery();
            //printing all items from database
            while (resultSet.next()) {
                VBox contentPane = new VBox();
                contentPane.setMaxWidth(340);
                contentPane.setAlignment(Pos.CENTER);
                contentPane.setSpacing(10);
                contentPane.setPadding(new Insets(20));
                contentPane.setStyle("-fx-background-color: rgb(230, 230, 230);");
                Label itemName = new Label(resultSet.getString("item_name"));
                itemName.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 15));
                Label itemPrice = new Label("Price: " + resultSet.getString("item_price") + "$");
                itemPrice.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 15));

                String itemPath = resultSet.getString("item_path");

                Button addToCart = new Button("Add to Cart");
                addToCart.setOnAction(ex->{
                    //check if the cart already contains the item
                    if(!cartItems.contains(itemPath)) {
                        //add the path of file to arraylist that uniquely identify the item
                        cartItems.add(itemPath);
                        cartAddedLabel.setText("Successfully added to cart");
                        cartAddedLabel.setTextFill(Color.BLACK);
                        //timeline so that the user gets a msg that the item has been added to cart
                        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2000), ae -> {
                            cartAddedLabel.setText("");
                        }));
                        timeline.play();
                    }
                    else{
                        cartAddedLabel.setTextFill(Color.RED);
                        cartAddedLabel.setText("Already in cart");
                        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2000), ae -> {
                            cartAddedLabel.setText("");
                            cartAddedLabel.setTextFill(Color.BLACK);
                        }));
                        timeline.play();
                    }
                });
                addToCart.setStyle(" -fx-background-color: #0000ff;  -fx-text-fill: white; -fx-pref-width: 100px; -fx-font-size: 14px; -fx-pref-height: 30px; ");

                //URL class used to make project portable
                URL itemImageURL = Seller.class.getResource(itemPath);
                System.out.println(itemImageURL);

                if (itemImageURL != null) {
                    ImageView itemImg = new ImageView(itemImageURL.toExternalForm());
                    //code to preserve image aspect ratio
                    Image timePass = new Image(itemImageURL.toExternalForm());
                    double aspectRatio = timePass.getWidth() / timePass.getHeight();
                    itemImg.setFitWidth(300);
                    itemImg.setFitHeight(300 / aspectRatio);
                    contentPane.getChildren().addAll(itemName, itemImg, itemPrice,addToCart);
                }
                centerPane.getChildren().add(contentPane);
            }
            scrollPane.setContent(centerPane);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        scrollPane.setContent(centerPane);
        scrollPane.setFitToWidth(true);

        // Filters pane for right pane
        Label title = new Label("Filters");
        title.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));

        VBox filter1 = new VBox();
        Label filter1Title = new Label("Price");
        filter1Title.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 15));
        //creating minimum and maximum fields
        Label minLbl = new Label("Minimum price: ");
        Label maxLbl = new Label("Maximum price: ");
        TextField minTxt = new TextField();
        minTxt.setMaxWidth(100);
        TextField maxTxt = new TextField();
        maxTxt.setMaxWidth(100);
        HBox minPane = new HBox();
        minPane.setAlignment(Pos.CENTER);
        HBox maxPane = new HBox();
        maxPane.setAlignment(Pos.CENTER);
        minPane.getChildren().addAll(minLbl,minTxt);
        maxPane.getChildren().addAll(maxLbl,maxTxt);
        Button filter1Enter = new Button("Apply");
        filter1Enter.setStyle(" -fx-background-color: #008000;  -fx-text-fill: white; -fx-pref-width: 100px; -fx-font-size: 14px; -fx-pref-height: 30px; ");

        Label filter1Error = new Label("");
        filter1Error.setTextFill(Color.RED);
        filter1.setSpacing(10);
        filter1.setAlignment(Pos.CENTER);
        filter1.getChildren().addAll(filter1Title,minPane,maxPane,filter1Enter,filter1Error,cartAddedLabel);
        //set action for filter button click
        filter1Enter.setOnAction(e->{
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                //sql query to get items after applied filters
                String sqlQuery = "SELECT * FROM items WHERE  item_price BETWEEN ? AND ?";
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
                try {
                    int minPrice = Integer.parseInt(minTxt.getText());
                    int maxPrice = Integer.parseInt(maxTxt.getText());
                    statement.setInt(1, minPrice);
                    statement.setInt(2, maxPrice);

                    ResultSet resultSet = statement.executeQuery();

                    centerPane.getChildren().clear();
                    centerPane.setAlignment(Pos.CENTER);
                    centerPane.setSpacing(30);
                    centerPane.getChildren().add(itemsLbl);
                    centerPane.setStyle("-fx-background-color:  rgb(255, 255, 222);");
                    //printing all items again after applying filters
                    while (resultSet.next()) {
                        VBox contentPane = new VBox();
                        contentPane.setMaxWidth(340);
                        contentPane.setAlignment(Pos.CENTER);
                        contentPane.setSpacing(10);
                        contentPane.setPadding(new Insets(20));
                        contentPane.setStyle("-fx-background-color: rgb(230, 230, 230);");
                        Label itemName = new Label(resultSet.getString("item_name"));
                        itemName.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 15));
                        Label itemPrice = new Label("Price: " + resultSet.getString("item_price") + "$");
                        itemPrice.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 15));
                        String itemPath = resultSet.getString("item_path");
                       //add to cart set on action
                        Button addToCart = new Button("Add to Cart");
                        addToCart.setOnAction(ex->{
                            if(!cartItems.contains(itemPath)) {
                                cartItems.add(itemPath);
                                cartAddedLabel.setText("Successfully added to cart");
                                cartAddedLabel.setTextFill(Color.BLACK);
                                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2000), ae -> {
                                    cartAddedLabel.setText("");
                                }));
                                timeline.play();
                            }
                            else{
                                cartAddedLabel.setTextFill(Color.RED);
                                cartAddedLabel.setText("Already in cart");
                                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2000), ae -> {
                                    cartAddedLabel.setText("");
                                    cartAddedLabel.setTextFill(Color.BLACK);
                                }));
                                timeline.play();
                            }
                        });
                        addToCart.setStyle(" -fx-background-color: #0000ff;  -fx-text-fill: white; -fx-pref-width: 100px; -fx-font-size: 14px; -fx-pref-height: 30px; ");
                         //URL to make project more portable
                        URL itemImageURL = Seller.class.getResource(itemPath);
                        if (itemImageURL != null) {
                            ImageView itemImg = new ImageView(itemImageURL.toExternalForm());
                            Image timePass = new Image(itemImageURL.toExternalForm());
                            double aspectRatio = timePass.getWidth() / timePass.getHeight();
                            itemImg.setFitWidth(300);
                            itemImg.setFitHeight(300 / aspectRatio);
                            contentPane.getChildren().addAll(itemName, itemImg, itemPrice, addToCart);
                        }
                        centerPane.getChildren().add(contentPane);
                        scrollPane.setContent(centerPane);
                        scrollPane.setStyle("-fx-background-color:  rgb(255, 255, 222);");
                    }
                }catch (NumberFormatException ex){
                    filter1Error.setText("Price should be an integer!");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            scrollPane.setContent(centerPane);
            scrollPane.setFitToWidth(true);
        });

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(" -fx-background-color: #ff0000;  -fx-text-fill: white; -fx-pref-width: 65px; -fx-font-size: 14px; -fx-pref-height: 30px; ");
        logoutBtn.setOnAction(e -> {
            primaryStage.setScene(SignIn.createSignInScene(primaryStage));
        });

        //go to cart page button
        Button goCart = new Button("Cart");
        goCart.setStyle(" -fx-background-color: #0000ff;  -fx-text-fill: white; -fx-pref-width: 65px; -fx-font-size: 14px; -fx-pref-height: 30px; ");
        goCart.setOnAction(e->{
                primaryStage.setScene(Cart.createCartScene(primaryStage));
        });
        //go to return page button
        Button goReturn = new Button("Return");
        goReturn.setOnAction(e->{
            primaryStage.setScene(Return.createReturnScene(primaryStage));
        });
        goReturn.setStyle(" -fx-background-color: #0000ff;  -fx-text-fill: white; -fx-pref-width: 65px; -fx-font-size: 14px; -fx-pref-height: 30px; ");
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.TOP_CENTER);
        buttons.setSpacing(5);
        buttons.getChildren().addAll(goCart,goReturn,logoutBtn);

        //adding everything to right pane of the border pane
        VBox rightPane = new VBox();
        rightPane.setAlignment(Pos.TOP_CENTER);
        rightPane.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding: 10px;");
        rightPane.setPadding(new Insets(25));
        rightPane.setSpacing(20);
        rightPane.getChildren().addAll(buttons,title, filter1);
        pane.setLeft(img);
        pane.setRight(rightPane);
        pane.setCenter(scrollPane);
        return new Scene(pane, 900, 600);
    }
}