package gui;

import data.Message;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import network.ChatClient;

import javax.swing.border.StrokeBorder;
import java.awt.*;

public class UIControllerMessages extends chatUIcontroller{

    public void printMessageFromServer(Message msg) {
        HBox messageContainer = new HBox();
//        messageContainer.setId("msgid" + msg.getUser().getID());
        messageContainer.setMaxWidth(Double.MAX_VALUE);

        //label for timestamp
        Label msgTimestamp = new Label(msg.getTimestamp() + ": ");
        msgTimestamp.setId("timestamp");
//        msgTimestamp.setStyle("-fx-font-weight: bold");
        msgTimestamp.setMinWidth(Control.USE_PREF_SIZE);

        //label for username
        Label msgUsername = new Label(msg.getUser().getUsername());
        msgUsername.setId(msg.getUser().getID());
//        msgUsername.setStyle("-fx-font-weight: bold");
        msgUsername.setMinWidth(Control.USE_PREF_SIZE);

        //label for message
        Label msgMessage = new Label(msg.getMsg());
        msgMessage.setWrapText(true);

        //HBox for time and username
        HBox timeAndUsername = new HBox();
        timeAndUsername.getChildren().add(msgTimestamp);
        timeAndUsername.getChildren().add(msgUsername);

        //Vbox for everything
        VBox messageToPrint = new VBox();
        messageToPrint.getChildren().add(timeAndUsername);
        messageToPrint.getChildren().add(msgMessage);

        messageToPrint.setPadding(new Insets(2, 5, 2, 5));
        messageToPrint.setStyle("-fx-background-color: #a0e182; ; -fx-background-radius: 5px;" +
                "-fx-border-radius: 5px; -fx-border-color: #00a132");
        messageToPrint.setMinHeight(Control.USE_PREF_SIZE);
        messageToPrint.setMaxWidth(350);

        messageContainer.getChildren().add(messageToPrint);

        if (msg.getUser().getUser().getID().equals(ChatClient.get().getCurrentUser().getID())) {
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            messageToPrint.setAlignment(Pos.CENTER_RIGHT);
            messageToPrint.setStyle("-fx-background-color: #77d5ff; -fx-background-radius: 5px;" +
                    "-fx-border-radius: 5px; -fx-border-color: #37a5af");
        }

        messageContainer.setMargin(messageToPrint, new Insets(5, 5, 5, 5));
        Main.UIcontrol.VBoxRoomsMessages.get(msg.getRoom()).getChildren().add(messageContainer);
        messageContainer.heightProperty().addListener((ChangeListener) (observable, oldvalue, newValue) -> Main.UIcontrol.scrollMessages.setVvalue((Double) newValue));
    }

}
