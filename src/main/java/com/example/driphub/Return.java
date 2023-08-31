package com.example.driphub;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.net.URL;
import java.sql.*;
import java.util.Random;

public class Return {
    // three variables used by Connection class to create connection with database
    private static final String url = "jdbc:mysql://localhost:3306/drip_hub";
    private static final String username = "root"; //default username of xampp SQL database
    private static final String password = "";  //default password of xampp SQL database
    public static Scene createReturnScene(Stage primaryStage){
        BorderPane pane = new BorderPane();

        // URL to make code more portable
        URL imageURL = Seller.class.getResource("/posts/logo.png");
        ImageView img = new ImageView(imageURL.toExternalForm());
        img.setFitHeight(200);
        img.setFitWidth(200);
        img.setLayoutX(370);
        img.setLayoutY(0);

        img.setLayoutY(0);
        VBox center = new VBox();
        center.setAlignment(Pos.CENTER);
        center.setSpacing(20);
        Label error = new Label("");
        error.setTextFill(Color.RED);
        HBox contain = new HBox();
        contain.setAlignment(Pos.CENTER);
        Label ret = new Label("Enter Sell ID:   ");
        TextField enterID = new TextField();
        Button retBtn = new Button("Return");
        retBtn.setStyle(" -fx-background-color: #008000;  -fx-text-fill: white; -fx-pref-width: 100px; -fx-font-size: 14px; -fx-pref-height: 30px; ");
        retBtn.setOnAction(e-> {

            try {
                int id = Integer.parseInt(enterID.getText());
                try {
                    System.out.println("hi");
                    Connection connection = DriverManager.getConnection(url, username, password);
                    //select from sql sold table where owner is the current owner and id entered is the one entered by user
                    String sqlQuery = " SELECT * FROM sold WHERE item_id = ? AND item_newowner = ?";
                    PreparedStatement statement = connection.prepareStatement(sqlQuery);
                    statement.setString(1, String.valueOf(id));
                    statement.setString(2,Runner.currentUser);
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        String itemName = resultSet.getString("item_name");
                        String itemOwner = resultSet.getString("item_owner");
                        String itemPath = resultSet.getString("item_path");
                        String itemPrice = resultSet.getString("item_price");

                        try {
                            // making object of document class
                            Document returnReceipt = new Document();
                            //giving random name to file
                            Random random = new Random();
                            String filename = "receipts/return" + random.nextInt(100000) + ".pdf";
                            //making pdf
                            PdfWriter.getInstance(returnReceipt, new FileOutputStream(filename));
                             //open receipt to edit it
                            returnReceipt.open();

                            Paragraph heading = new Paragraph("Return Receipt\n\n\n");
                            heading.setAlignment(Element.ALIGN_CENTER);
                            returnReceipt.add(heading);

                            Paragraph item = new Paragraph(itemName + " : " + itemPrice + "$  ID: " + id);
                            item.setAlignment(Element.ALIGN_CENTER);
                            returnReceipt.add(item);

                            Paragraph dash = new Paragraph("-------------------------------");
                            dash.setAlignment(Element.ALIGN_CENTER);
                            returnReceipt.add(dash);

                            Paragraph balance = new Paragraph("Your balance:  +" + itemPrice);
                            balance.setAlignment(Element.ALIGN_CENTER);
                            returnReceipt.add(balance);

                            returnReceipt.close();

                        } catch (DocumentException | java.io.IOException ex) {
                            error.setText("Error while generating receipt");
                        }
                        //inserting item back into items table
                        String sqlInsertQuery = "INSERT INTO items(item_name, item_owner, item_path, item_price) VALUES (?, ?, ?, ?)";
                        PreparedStatement insertStatement = connection.prepareStatement(sqlInsertQuery);
                        insertStatement.setString(1, itemName);
                        insertStatement.setString(2, itemOwner);
                        insertStatement.setString(3, itemPath);
                        insertStatement.setString(4, itemPrice);
                        insertStatement.executeUpdate();
                        // deleting item from sold table
                        String sqlDeleteQuery = "DELETE FROM sold WHERE item_id = ?";
                        PreparedStatement deleteStatement = connection.prepareStatement(sqlDeleteQuery);
                        deleteStatement.setString(1, itemName);
                        deleteStatement.executeUpdate();
                        //alert successful return of item
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Successfully Returned Item");
                        alert.setHeaderText(null);
                        alert.setContentText("You have successfully returned the item");
                        alert.showAndWait();
                        primaryStage.setScene(Buyer.createBuyerScene(primaryStage));
                    } else {
                        error.setText("Cannot find item.");
                    }
                } catch (SQLException ex) {
                    error.setText("Error occurred in connecting to database");
                }
            } catch (NumberFormatException ex) {
                error.setText("ID should be numbers.");
            }
        });
        contain.getChildren().addAll(ret,enterID);
        center.getChildren().addAll(error,contain,retBtn);
        Button back = new Button("Back");
        back.setStyle(" -fx-background-color: #008000;  -fx-text-fill: white; -fx-pref-width: 100px; -fx-font-size: 14px; -fx-pref-height: 30px; ");
        back.setOnAction(e->{
            primaryStage.setScene(Buyer.createBuyerScene(primaryStage));
        });
        HBox buttons = new HBox(back);
        buttons.setPadding(new Insets(20));

        pane.setLeft(img);
        pane.setCenter(center);
        pane.setRight(buttons);
        return new Scene(pane,900,600);
    }
}
