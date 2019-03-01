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

public class UIControllerUsers extends chatUIcontroller {

    public void printUsers(User user, String room) {
        HBox onlineUser = new HBox(5);
        onlineUser.setStyle("-fx-alignment: CENTER_LEFT");
        Circle userPic = new Circle(6);
        userPic.setId("userPic" + user.getID());

        if (ChatClient.get().getCurrentUser().getID().equals(user.getID())) {
            userPic.setFill(Color.BLUE);
        } else if (!user.getOnlineStatus()) {
            userPic.setFill(Color.LIGHTGRAY);
        } else {
            userPic.setFill(Color.valueOf("#20b755"));
        }

        Label userName = new Label(user.getUsername());
        userName.setId(user.getID());
        userName.setStyle("-fx-text-fill: black; -fx-pref-width: 100px;");

        onlineUser.getChildren().addAll(userPic, userName);
        HBox.setMargin(userPic, new Insets(5, 0, 5, 3));

        Main.UIcontrol.VBoxRoomsUsers.get(room).getChildren().add(onlineUser);
    }

    public void updateUsername(String roomName, NetworkMessage.UserNameChange event) {
        System.out.println("Updated list ");

        Set<Label> userNameMessages = (Set) Main.UIcontrol.VBoxRoomsMessages.get(roomName).lookupAll("#" + event.getUserId());
        userNameMessages.iterator().forEachRemaining(label -> label.setText(event.getNewName()));

        //Går igenom rum och ändrar namn i användarlistan
        Label userNameLabel = (Label) Main.UIcontrol.VBoxRoomsUsers.get(roomName).lookup("#" + event.getUserId());
        userNameLabel.setText(event.getNewName());
    }

    public void userDisconnected(String roomName, String userID) {
        System.out.println("User disconnected");
        //Hitta cirkeln till användaren som disconnectade och byt färg på den

        Circle userPic = (Circle) Main.UIcontrol.VBoxRoomsUsers.get(roomName).lookup("#userPic" + userID);
        userPic.setFill(Color.LIGHTGRAY);
    }

    public void userConnected(String roomName, String userID) {
        //Hitta cirkeln till användaren som anslöt och byt färg på den

        if (!ChatClient.get().getCurrentUser().getID().equals(userID)) {
            Circle userPic = (Circle) Main.UIcontrol.VBoxRoomsUsers.get(roomName).lookup("#userPic" + userID);
            userPic.setFill(Color.valueOf("#20b755"));
        }
    }
    public void removeUserFromRoom(String targetRoom, String userID){
        Label user = (Label) Main.UIcontrol.VBoxRoomsUsers.get(targetRoom).lookup("#" + userID);
        Main.UIcontrol.VBoxRoomsUsers.get(targetRoom).getChildren().remove(user.getParent());
    }
}
