package com.example.driphub;

import com.lowagie.text.Element;
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
import java.util.Random;

import java.io.FileOutputStream;
import java.net.URL;
import java.sql.*;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import static javafx.scene.text.Font.*;


public class Cart {
    // three variables used by Connection class to create connection with database
    private static final String url = "jdbc:mysql://localhost:3306/drip_hub";
    private static final String username = "root"; //default username of xampp SQL database
    private static final String password = "";  //default password of xampp SQL database
    //sum variable used to get total amount of purchases
    static double sum = 0;

    public static Scene createCartScene(Stage primaryStage) {
        BorderPane pane = new BorderPane();

        //left logo image for borderpane
        URL imageURL = Seller.class.getResource("/posts/logo.png");
        ImageView img = new ImageView(imageURL.toExternalForm());
        img.setFitHeight(200);
        img.setFitWidth(200);
        img.setLayoutX(370);
        img.setLayoutY(0);

        //center pane for all content
        ScrollPane scrollPane = new ScrollPane();
        VBox centerPane = new VBox();
        centerPane.setAlignment(Pos.TOP_CENTER);
        centerPane.setSpacing(30);
        Label title = new Label("Your Cart");
        title.setFont(font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));
        centerPane.getChildren().add(title);
        // loops through the arraylist to print all items and get total price
        for(String item: Buyer.cartItems){
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                //sql query to get all items where item path is the index of arraylist
                String sqlQuery = "SELECT * FROM items WHERE item_path = ?";
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
                statement.setString(1,item);
                ResultSet resultSet = statement.executeQuery();
                if(resultSet.next()) {
                    String itemName = resultSet.getString("item_name");
                    String itemPath = resultSet.getString("item_path");
                    String itemPrice = resultSet.getString("item_price");

                    HBox contentPane = new HBox();
                    contentPane.setSpacing(20);
                    contentPane.setAlignment(Pos.TOP_CENTER);
                    contentPane.setMaxWidth(400);
                    contentPane.setPadding(new Insets (20));
                    contentPane.setStyle("-fx-background-color:  rgb(255, 255, 222);");

                    VBox container = new VBox();
                    container.setSpacing(20);

                    Label name = new Label(itemName);
                    name.setFont(font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 15));
                    Label price = new Label(itemPrice + "$");
                    price.setFont(font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 15));
                    Button remove = new Button("Remove");
                    remove.setOnAction(e->{
                        // remove item from arraylist set sum to zero and reload page
                        Buyer.cartItems.remove(item);
                        sum = 0;
                        primaryStage.setScene(Cart.createCartScene(primaryStage));
                    });
                    remove.setStyle(" -fx-background-color: #ff0000;  -fx-text-fill: white; -fx-pref-width: 100px; -fx-font-size: 14px; -fx-pref-height: 30px; ");
                    container.getChildren().addAll(name,price,remove);

                    URL itemImageURL = Seller.class.getResource(itemPath);
                    if (itemImageURL != null) {
                        ImageView itemImg = new ImageView(itemImageURL.toExternalForm());
                        itemImg.setFitWidth(200);
                        itemImg.setPreserveRatio(true);
                        contentPane.getChildren().addAll(itemImg,container);
                        centerPane.getChildren().add(contentPane);
                    }
                }


            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            scrollPane.setContent(centerPane);

        }
        scrollPane.setContent(centerPane);
        scrollPane.setFitToWidth(true);

        //right pane for border pane
        VBox rightPane = new VBox();
        rightPane.setSpacing(20);
        rightPane.setAlignment(Pos.TOP_CENTER);
        rightPane.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding: 10px;");

        Label checkout = new Label("Checkout");
        checkout.setFont(font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));

        VBox items = new VBox();
        items.setSpacing(10);
        items.setAlignment(Pos.CENTER);
        //loops through arraylist to get all item names and get total
        for(String item: Buyer.cartItems) {
            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                String sqlQuery = "SELECT * FROM items WHERE item_path = ?";
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
                statement.setString(1, item);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String itemName = resultSet.getString("item_name");
                    double itemPrice = Double.parseDouble(resultSet.getString("item_price"));
                    sum += itemPrice;
                    Label itemLabel = new Label(itemName + " : " + itemPrice + "$");
                    itemLabel.setFont(font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 14));
                    items.getChildren().add(itemLabel);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        //go to buy pane
        Button goBuy = new Button("Buy");
        goBuy.setStyle(" -fx-background-color: #0000ff;  -fx-text-fill: white; -fx-pref-width: 65px; -fx-font-size: 14px; -fx-pref-height: 30px; ");
        goBuy.setOnAction(e->{
            primaryStage.setScene(Buyer.createBuyerScene(primaryStage));
        });
        //logout button
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(" -fx-background-color: #ff0000;  -fx-text-fill: white; -fx-pref-width: 65px; -fx-font-size: 14px; -fx-pref-height: 30px; ");
        logoutBtn.setOnAction(e->{
            primaryStage.setScene(SignIn.createSignInScene(primaryStage));
        });
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(20);
        buttons.getChildren().addAll(goBuy,logoutBtn);

        Label dash = new Label("--------------------");
        dash.setFont(font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 14));
        Label total = new Label("Total = " + sum +"$");
        total.setFont(font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 14));
        items.getChildren().addAll(dash,total);

        //checkout button
        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.setStyle(" -fx-background-color: #008000;  -fx-text-fill: white; -fx-pref-width: 100px; -fx-font-size: 14px; -fx-pref-height: 30px; ");
        checkoutBtn.setOnAction(e->{
                    if(!Buyer.cartItems.isEmpty()){

                        try {
                            //make a document class object
                            Document receipt = new Document();

                            //give a random name to file
                            Random random = new Random();
                            String filename = "receipts/receipt" + random.nextInt(100000) + ".pdf";
                            //make pdf file
                            PdfWriter.getInstance(receipt, new FileOutputStream(filename));
                            receipt.open();

                            // Style for the heading
                            Paragraph heading = new Paragraph("Receipt\n\n\n");
                            heading.setAlignment(Element.ALIGN_CENTER);
                            receipt.add(heading);

                            //adding content to receipt as per items in cart
                            for (String item : Buyer.cartItems) {
                                try {
                                    Connection connection = DriverManager.getConnection(url, username, password);
                                    String sqlQuery = "SELECT * FROM items WHERE item_path = ?";
                                    PreparedStatement statement = connection.prepareStatement(sqlQuery);
                                    statement.setString(1, item);
                                    ResultSet resultSet = statement.executeQuery();
                                    if (resultSet.next()) {
                                        String itemName = resultSet.getString("item_name");
                                        String itemPrice = resultSet.getString("item_price");
                                        String itemOwner = resultSet.getString("item_owner");
                                        String itemID = String.valueOf(random.nextInt(100000000));
                                        // insert the item into sold table(useful for return)
                                        String sqlInsertQuery = "INSERT INTO sold(item_name, item_owner, item_newowner, item_path, item_price, item_id) VALUES (?, ?, ?, ?, ?, ?)";
                                        PreparedStatement insertStatement = connection.prepareStatement(sqlInsertQuery);
                                        insertStatement.setString(1, itemName);
                                        insertStatement.setString(2, itemOwner);
                                        insertStatement.setString(3, Runner.currentUser);
                                        insertStatement.setString(4, item);
                                        insertStatement.setString(5, itemPrice);
                                        insertStatement.setString(6,itemID);
                                        insertStatement.executeUpdate();

                                        Paragraph individual = new Paragraph(itemName + " : " + itemPrice +"$  ID: " +itemID);
                                        individual.setAlignment(Element.ALIGN_CENTER);
                                        receipt.add(individual);
                                       //delete item from items table because it is sold
                                        String sqlDeleteQuery = "DELETE FROM items WHERE item_path = ?";
                                        PreparedStatement deleteStatement = connection.prepareStatement(sqlDeleteQuery);
                                        deleteStatement.setString(1,item);
                                        deleteStatement.executeUpdate();
                                    }
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                            }

                            Paragraph line = new Paragraph("-------------------");
                            line.setAlignment(Element.ALIGN_CENTER);
                            receipt.add(line);
                            //print total in pdf
                            Paragraph sumParagraph = new Paragraph("Total = " + sum);
                            sumParagraph.setAlignment(Element.ALIGN_CENTER);
                            receipt.add(sumParagraph);
                            receipt.close();

                            //alert successful checkout
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Successfully Checked Out");
                            alert.setHeaderText(null);
                            alert.setContentText("You have checked out successfully and your receipt has been generated as " + filename);
                            alert.showAndWait();
                            Buyer.cartItems.clear();
                            primaryStage.setScene(Buyer.createBuyerScene(primaryStage));

                        } catch (DocumentException | java.io.IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    //alert cart empty
                    else{
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Empty Cart");
                        alert.setHeaderText(null);
                        alert.setContentText("Your cart is empty please add some items to checkout!");
                        alert.showAndWait();
                    }
        });

        rightPane.setPadding(new Insets(25));
        rightPane.getChildren().addAll(buttons,checkout,items,checkoutBtn);

        pane.setRight(rightPane);
        pane.setCenter(scrollPane);
        pane.setLeft(img);
        return new Scene(pane,900,600);
    }
}
