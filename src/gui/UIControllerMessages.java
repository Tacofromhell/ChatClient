package gui;

import data.Message;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import network.ChatClient;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;

public class UIControllerMessages extends chatUIcontroller {

    public void printMessageFromServer(Message msg) {
        VBox containerMessageAndTimestamp = new VBox();
        HBox messageContainer = new HBox();
        messageContainer.setMaxWidth(Double.MAX_VALUE);

        //label for timestamp
        Label msgTimestamp = new Label(msg.getTimestamp());
        msgTimestamp.setId("timestamp");
        msgTimestamp.setTextFill(Color.GRAY);
        msgTimestamp.setPadding(new Insets(0, 8, 5, 8));
        msgTimestamp.setMinWidth(Control.USE_PREF_SIZE);

        //label for username
        Label msgUsername = new Label(msg.getUser().getUsername());
        msgUsername.setId(msg.getUser().getID());
        msgUsername.setStyle("-fx-font-weight: bold");
        msgUsername.setMinWidth(Control.USE_PREF_SIZE);

        //image
        Image img = null;
        if (msg.getImageBuffer() != null) {
            img = new Image(new ByteArrayInputStream(msg.getImageBuffer()));
        }

        //label for message
        Label msgMessage = null;
        if (msg.getMsg() != null) {
            msgMessage = new Label(msg.getMsg());
            msgMessage.setWrapText(true);
        }

        //HBox for time and username
        HBox hboxUsername = new HBox();
        hboxUsername.getChildren().add(msgUsername);

        //Vbox for everything
        VBox messageToPrint = new VBox();
        messageToPrint.setId("vbox" + msg.getUser().getID());
        messageToPrint.getChildren().add(hboxUsername);
        if (msgMessage != null) {
            messageToPrint.getChildren().add(msgMessage);
        } else if (img != null) {
            ImageView imageView = new ImageView(img);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            imageView.setOnMouseClicked(event -> {
                        if (Desktop.isDesktopSupported()) {
                            try {
                                try (FileOutputStream fileOutputStream = new FileOutputStream("imageToShow.jpg")) {
                                    fileOutputStream.write(msg.getImageBuffer());
                                    Desktop.getDesktop().open(new File("imageToShow.jpg"));
                                }
                            } catch (IOException e) {
                                System.out.println("Could not open image" + e.getLocalizedMessage());
                            }
                        }
                        });
                        messageToPrint.getChildren().add(imageView);
                    }

                    //styling
                    messageToPrint.setPadding(new Insets(2, 5, 2, 5));
            messageToPrint.setStyle("-fx-background-color: #a0e182; ; -fx-background-radius: 5px;" +
                    "-fx-border-radius: 5px; -fx-border-color: #00a132");
            messageToPrint.setMinHeight(Control.USE_PREF_SIZE);
            messageToPrint.setMaxWidth(Main.UIcontrol.scrollMessages.getWidth() - 50);


            messageContainer.getChildren().add(messageToPrint);
            containerMessageAndTimestamp.getChildren().add(messageContainer);
            containerMessageAndTimestamp.getChildren().add(msgTimestamp);

            //If sender is this user
            if (msg.getUser().getUser().getID().equals(ChatClient.get().getCurrentUser().getID())) {
                containerMessageAndTimestamp.setAlignment(Pos.CENTER_RIGHT);
                messageContainer.setAlignment(Pos.CENTER_RIGHT);
                hboxUsername.setAlignment(Pos.CENTER_RIGHT);
                messageToPrint.setAlignment(Pos.CENTER_RIGHT);
                messageToPrint.setStyle("-fx-background-color: #77d5ff; -fx-background-radius: 5px;" +
                        "-fx-border-radius: 5px; -fx-border-color: #37a5af");
            }

            HBox.setMargin(messageToPrint, new Insets(0, 5, 0, 5));

            Main.UIcontrol.VBoxRoomsMessages.get(msg.getRoom()).getChildren().add(containerMessageAndTimestamp);
            //Listener to ensure that scoll always is at bottom
            messageContainer.heightProperty().addListener((ChangeListener) (observable, oldvalue, newValue) -> Main.UIcontrol.scrollMessages.setVvalue((Double) newValue));
            //Listener to check if user changes window-width
            Main.UIcontrol.scrollMessages.widthProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> messageToPrint.setMaxWidth((Double) newValue - 50));
        }
    }
