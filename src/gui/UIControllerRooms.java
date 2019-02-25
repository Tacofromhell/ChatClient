package gui;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

import network.ChatClient;

public class UIControllerRooms extends chatUIcontroller{

    public void switchContent(String room) {
        Main.UIcontrol.scrollMessages.setContent(Main.UIcontrol.VBoxRoomsMessages.get(room));
        Main.UIcontrol.scrollUsers.setContent(Main.UIcontrol.VBoxRoomsUsers.get(room));

        ChatClient.get().getCurrentUser().setActiveRoom(room);
    }
}
