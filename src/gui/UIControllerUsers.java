package gui;

import data.NetworkMessage;
import data.User;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import network.ChatClient;

import java.util.Set;

public class UIControllerUsers extends chatUIcontroller{

    public void printUsers(User user, String room) {
        HBox onlineUser = new HBox(5);
        onlineUser.setStyle("-fx-alignment: CENTER_LEFT");
        Circle userPic;

        if (ChatClient.get().getCurrentUser().getID().equals(user.getID())) {
            userPic = new Circle(6, Color.GREEN);
        } else {
            userPic = new Circle(6, Color.LIGHTGRAY);
        }

        Label userName = new Label(user.getUsername());
        userName.setId(user.getID());
        userName.setStyle("-fx-text-fill: black;" +
                "-fx-pref-width: 100px;");

        onlineUser.getChildren().addAll(userPic, userName);
        onlineUser.setMargin(userPic, new Insets(5, 0, 5, 3));

        Main.UIcontrol.VBoxRoomsUsers.get(room).getChildren().add(onlineUser);
    }

    public void updateUsername(NetworkMessage.UserNameChange event) {
        System.out.println("Updated list ");

        Main.UIcontrol.VBoxRoomsMessages.forEach((room, vbox) -> {
            Set<Label> userNameLabel = (Set)vbox.lookupAll("#" + event.getUserId());
            userNameLabel.iterator().forEachRemaining(label -> label.setText(event.getNewName()));
        });

        //Går igenom rum och ändrar namn i användarlistan
        Main.UIcontrol.VBoxRoomsUsers.forEach((room, vbox) -> {
            Label userNameLabel = (Label) vbox.lookup("#" + event.getUserId());
            userNameLabel.setText(event.getNewName());
        });

    }
}
