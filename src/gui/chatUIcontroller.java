package gui;

import data.NetworkMessage;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import network.ChatClient;
import data.Message;
import data.Room;
import data.User;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;


public class chatUIcontroller {

    public Map<String, VBox> VBoxRoomsMessages = new HashMap<>();
    public Map<String, VBox> VBoxRoomsUsers = new HashMap<>();

    @FXML
    HBox roomButtonsHolder;
    @FXML
    ScrollPane scrollMessages;
    @FXML
    ScrollPane scrollUsers;
    @FXML
    TextField userInputNewMessage;
    @FXML
    TextField newUsername;
    @FXML
    Button sendMessage;
    @FXML
    VBox users;
    @FXML
    Text currentUsername;
    @FXML
    Button changeUser_btn;

    //REDUNDANT: Moved to User.java
    //String activeRoom = "general";

    public void initialize() {

//        observableListRooms.addListener((ListChangeListener<? super Room>) c -> {
//            while(c.next()){
//                System.out.println("CHANGE DETECTED");
//                Platform.runLater(() -> updateUserList());
//            }
//        });
    }

    public void initRooms() {
        for (String room : ChatClient.get().getCurrentUser().getJoinedRooms()) {
            VBox tempMsg = new VBox();
            tempMsg.setId(room);
            VBoxRoomsMessages.putIfAbsent(room, tempMsg);
            VBox tempUsr = new VBox();
            tempUsr.setId(room);
            VBoxRoomsUsers.putIfAbsent(room, tempUsr);

            Button b = new Button(room);
            b.setOnAction((ActionEvent e) -> switchContent(b.getText()));
            roomButtonsHolder.getChildren().add(b);
        }

        scrollMessages.setContent(VBoxRoomsMessages.get(
                ChatClient.get().getCurrentUser().getActiveRoom()
        ));
        scrollUsers.setContent(VBoxRoomsUsers.get(
                ChatClient.get().getCurrentUser().getActiveRoom()
        ));
    }

    public void printMessageFromServer(Message msg) {
        HBox messageContainer = new HBox();
        messageContainer.setMaxWidth(Double.MAX_VALUE);

        Label messageToPrint = new Label(msg.getTimestamp() + " " + msg.getUser().getUsername() + ": " + msg.getMsg());
        messageToPrint.setPadding(new Insets(2, 5, 2, 5));
        messageToPrint.setStyle("-fx-background-color: honeydew; -fx-background-radius: 5px;");
        messageToPrint.setWrapText(true);
        messageToPrint.setMinHeight(Control.USE_PREF_SIZE);
        messageToPrint.setMaxWidth(300);
        messageContainer.getChildren().add(messageToPrint);

        if (msg.getUser().getUser().getID().equals(ChatClient.get().getCurrentUser().getID())) {
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            messageToPrint.setStyle("-fx-background-color: #27d5ff; -fx-background-radius: 5px;");
        }

        messageContainer.setMargin(messageToPrint, new Insets(5, 5, 0, 5));
        VBoxRoomsMessages.get(msg.getRoom()).getChildren().add(messageContainer);
        messageContainer.heightProperty().addListener((ChangeListener) (observable, oldvalue, newValue) -> scrollMessages.setVvalue((Double) newValue));
    }

    public void switchContent(String room) {
        scrollMessages.setContent(VBoxRoomsMessages.get(room));
        scrollUsers.setContent(VBoxRoomsUsers.get(room));

        ChatClient.get().getCurrentUser().setActiveRoom(room);
    }

    public void sendMessageButton() {
//        send input string to network method in ChatClient
        if (userInputNewMessage.getText().trim().length() > 0)
            ChatClient.get().sendMessageToServer(ChatClient.get().getCurrentUser(), userInputNewMessage.getText(), ChatClient.get().getCurrentUser().getActiveRoom());

        userInputNewMessage.setText("");
    }

    public void sendMessageEnter(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
//            send input string to network method in ChatClient
            if (userInputNewMessage.getText().trim().length() > 0)
                ChatClient.get().sendMessageToServer(ChatClient.get().getCurrentUser(), userInputNewMessage.getText(), ChatClient.get().getCurrentUser().getActiveRoom());

            userInputNewMessage.setText("");
        }
    }

    public void sendNewUsernameButton() {
        if (newUsername.getText().trim().length() > 0) {
            ChatClient.get().getCurrentUser().setUsername(newUsername.getText());
            System.out.println("Changed username to: " + ChatClient.get().getCurrentUser().getUsername());
            ChatClient.get().sendEventToServer(
                    new NetworkMessage.UserNameChange(
                            ChatClient.get().getCurrentUser().getUsername(),
                            ChatClient.get().getCurrentUser().getID()));

//            updateUserList();
            ChatClient.get().sendUserToServer();
        }

        newUsername.setText("");
    }

    public void sendNewUsernameEnter(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
            sendNewUsernameButton();
        }

    }


    public void printUsers(User user, String room) {
        HBox onlineUser = new HBox(5);
        onlineUser.setStyle("-fx-alignment: CENTER_LEFT");
        Circle userPic;

        if (ChatClient.get().getCurrentUser().getID().equals(user.getID())) {
            userPic = new Circle(10, Color.GREEN);
        } else {
            userPic = new Circle(10, Color.LIGHTGRAY);
        }

        Label userName = new Label(user.getUsername());
        userName.setId(user.getID());
        userName.setStyle("-fx-text-fill: black;" +
                "-fx-pref-width: 100px;");

        onlineUser.getChildren().addAll(userPic, userName);
        onlineUser.setMargin(userPic, new Insets(5, 0, 5, 3));

        VBoxRoomsUsers.get(room).getChildren().add(onlineUser);
    }

    public void updateUsername(NetworkMessage.UserNameChange event) {
        System.out.println("Updated list ");
        NetworkMessage.UserNameChange newUsername = event;
//        users.getChildren().clear();

//        for (Room room : ChatClient.get().getRooms()) {
//            VBoxRoomsUsers.get(room.getRoomName()).getChildren().clear();

//        }

        VBoxRoomsUsers.forEach((room, vbox) -> vbox.getChildren().forEach(System.out::println));


//                .flatMap(as ->
//                    forEach(user -> {
//                if(user.getId().equals(newUsername.getUserId())){
//                    user.set
//                }
//
//            })
//        });

//        ChatClient.get().getRooms().
//                forEach(room -> room.getUsers().stream()
//                        .filter(user -> user.getID().equals(userChangedName.getID()))
//                        .forEach(user -> user.setUsername(userChangedName.getUsername())));
//
//
//
//        ChatClient.get().getRooms().stream()
//                .forEach(room -> room.getUsers().stream()
//                        .map(user -> user.getID()));
//
//            room.getUsers().stream()
//                    .filter(user -> user.getOnlineStatus() == true)
//                    .peek(user -> System.out.println("Username: " + user.getUsername()))
//                    .forEach(user -> {
////                        ChatClient.get().sendUserToServer();
//                        printUsers(user, room.getRoomName());
//                    });
//        ChatClient.get().getRooms().stream()
//                .flatMap(room -> room.getUsers().stream())
//                .filter(user -> user.getOnlineStatus() == true)
//                .peek(user -> System.out.println("Username: " + user.getUsername()))
//                .forEach(user -> {
////                        ChatClient.get().sendUserToServer();
//                    printUsers(user);
//                });
    }
}
