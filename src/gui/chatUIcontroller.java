package gui;

import data.NetworkMessage;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class chatUIcontroller {


    @FXML
    public Map<String, VBox> VBoxRoomsMessages = new HashMap<>();
    @FXML
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

    public void initialize() {
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
        //Changed layout of message because it was needed in order to update username when someone changed theirs
        HBox messageContainer = new HBox();
//        messageContainer.setId("msgid" + msg.getUser().getID());
        messageContainer.setMaxWidth(Double.MAX_VALUE);

        //label for timestamp
        Label msgTimestamp = new Label(msg.getTimestamp() + ": ");
        msgTimestamp.setId("timestamp");
        msgTimestamp.setStyle("-fx-font-weight: bold");
        msgTimestamp.setMinWidth(Control.USE_PREF_SIZE);

        //label for username
        Label msgUsername = new Label(msg.getUser().getUsername());
        msgUsername.setId(msg.getUser().getID());
        msgUsername.setStyle("-fx-font-weight: bold");
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
        messageToPrint.setStyle("-fx-background-color: honeydew; -fx-background-radius: 5px;");
        messageToPrint.setMinHeight(Control.USE_PREF_SIZE);
        messageToPrint.setMaxWidth(350);

        messageContainer.getChildren().add(messageToPrint);

        if (msg.getUser().getUser().getID().equals(ChatClient.get().getCurrentUser().getID())) {
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            messageToPrint.setAlignment(Pos.CENTER_RIGHT);
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
        }
        newUsername.setText("");
    }

    public void sendNewUsernameEnter(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
            sendNewUsernameButton();
        }
    }
    public void printUser(User user, String room) {
        //Skriver ut en användare i användarlistan till höger
        HBox onlineUser = new HBox(5);
        onlineUser.setStyle("-fx-alignment: CENTER_LEFT");

        Label userName = new Label(user.getUsername());
        userName.setId(user.getID());
        userName.setStyle("-fx-text-fill: black;" +
                "-fx-pref-width: 100px;");
        Circle userPic = new Circle(10);
        userPic.setId("userPic" + user.getID());

        if (ChatClient.get().getCurrentUser().getID().equals(user.getID())) {
            //If user = currentUser
            userPic.setFill(Color.GREEN);
        } else if(!user.getOnlineStatus()){
            //if user is offline
            userPic.setFill(Color.BLACK);
        } else {
            //rest of users
            userPic.setFill(Color.LIGHTGRAY);
        }

        onlineUser.getChildren().addAll(userPic, userName);
        onlineUser.setMargin(userPic, new Insets(5, 0, 5, 3));

        VBoxRoomsUsers.get(room).getChildren().add(onlineUser);
   }

    public void updateUsername(NetworkMessage.UserNameChange event) {
//        NetworkMessage.UserNameChange newUsername = event;


        //Lägg till alla element som har id = newUsername.getUserId i ett set
        //Loopa setet och ändra texten till det nya usernamet


        //Går igenom meddelanden och ändrar namn på dom

        VBoxRoomsMessages.forEach((room, vbox) -> {
            Set<Label> userNameLabel = (Set)vbox.lookupAll("#" + event.getUserId());
            userNameLabel.iterator().forEachRemaining(label -> label.setText(event.getNewName()));
        });

        //Går igenom rum och ändrar namn i användarlistan
        VBoxRoomsUsers.forEach((room, vbox) -> {
            Label userNameLabel = (Label) vbox.lookup("#" + event.getUserId());
            userNameLabel.setText(event.getNewName());
        });

    }

    public void userDisconnected(String userID){

        //Hitta cirkeln till användaren som disconnectade och byt färg på den

        Circle userPic = (Circle) Main.stage.getScene().lookup("#userPic" + userID);
        userPic.setFill(Color.BLACK);
    }

    public void printServerEvent(String username, String userID, String typeOfEvent) {
        //Changed layout of message because it was needed in order to update username when someone changed theirs

        HBox messageContainer = new HBox();
//        messageContainer.setId("msgid" + msg.getUser().getID());
        messageContainer.setMaxWidth(Double.MAX_VALUE);


        //label for username
        Label eventUsername = new Label(username);
        eventUsername.setId(userID);
        eventUsername.setMinWidth(Control.USE_PREF_SIZE);

        //label for message
        Label eventMessage = new Label();
        eventMessage.setWrapText(true);

        switch (typeOfEvent){
            case "nameChange": eventMessage.setText(" changed name.");
                    break;
            case "disconnect": eventMessage.setText(" disconnected.");
            break;
        }

        messageContainer.setPadding(new Insets(2, 5, 2, 5));
        messageContainer.setStyle("-fx-background-color: lightgray; -fx-background-radius: 5px;");
        messageContainer.setMinHeight(Control.USE_PREF_SIZE);
        messageContainer.setMaxWidth(350);

        messageContainer.getChildren().add(eventUsername);
        messageContainer.getChildren().add(eventMessage);


        messageContainer.setAlignment(Pos.CENTER);
//        messageContainer.setMargin(messageToPrint, new Insets(5, 5, 0, 5));

//        VBoxRoomsMessages.get("general").getChildren().add(messageContainer);

//        ChatClient.get().getCurrentUser().getJoinedRooms().forEach(room -> {
//            System.out.println("VILKET ROOOOOOOOM: " +room);
//            VBoxRoomsMessages.get(room).getChildren().add(messageContainer);
//
//        });
        messageContainer.heightProperty().addListener((ChangeListener) (observable, oldvalue, newValue) -> scrollMessages.setVvalue((Double) newValue));
    }
}
