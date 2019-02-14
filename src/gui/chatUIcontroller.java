package gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import network.ChatClient;
import network.Message;
import network.User;


public class chatUIcontroller {

    @FXML
    ScrollPane scrollMessages;
    @FXML
    VBox printMessages;
    @FXML
    TextField userInputNewMessage;
    @FXML
    TextField newUsername;
    @FXML
    Button sendMessage;
    @FXML
    VBox users;
    @FXML
    TextField currentUsername;

    public void initialize() {
        updateUsername();
        System.out.println(Thread.currentThread().toString());
    }

    public void printMessageFromServer(Message msg, User user) {
        HBox messageContainer = new HBox();
        Label messageToPrint = new Label( msg.getTimestamp() + " " + msg.getUser().getUsername() + ": " + msg.getMsg());
        messageToPrint.setPadding(new Insets(2, 5, 2, 5));
        messageToPrint.setStyle("-fx-background-color: lightgrey; -fx-background-radius: 5px;");
        messageToPrint.setWrapText(true);

//        messageToPrint.maxHeight("Infinity");

        messageContainer.getChildren().add(messageToPrint);
//        messageContainer.setMaxWidth(printMessages.getWidth());
        messageContainer.setMinHeight(messageToPrint.getHeight());
        messageContainer.setMargin(messageToPrint, new Insets(5, 5,0,5));

        printMessages.getChildren().add(messageContainer);

        messageContainer.heightProperty().addListener((ChangeListener) (observable, oldvalue, newValue) -> scrollMessages.setVvalue((Double)newValue ));


    }

    public void sendMessageButton() {
//        send input string to network method in ChatClient
        if (userInputNewMessage.getText().trim().length() > 0)
            ChatClient.get().sendMessageToServer(ChatClient.get().getCurrentUser(), userInputNewMessage.getText());

        userInputNewMessage.setText("");
    }

    public void sendMessageEnter(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
//            send input string to network method in ChatClient
            if (userInputNewMessage.getText().trim().length() > 0)
                ChatClient.get().sendMessageToServer(ChatClient.get().getCurrentUser(), userInputNewMessage.getText());

            userInputNewMessage.setText("");
        }
    }

    public void sendNewUsernameEnter(KeyEvent key){
        if(key.getCode().equals(KeyCode.ENTER)){

            if(newUsername.getText().trim().length() > 0){
                ChatClient.get().getCurrentUser().setUsername(newUsername.getText());
                System.out.println("Changed username to: " + ChatClient.get().getCurrentUser().getUsername());
                ChatClient.get().sendUserToServer();
            }

            newUsername.setText("");
            updateUsername();
        }

    }

    public void printUsers(int i) {
        HBox onlineUser = new HBox(5);
        onlineUser.setStyle("-fx-alignment: CENTER_LEFT");
        Circle userPic = new Circle(13, Color.LIGHTGRAY);
        Label userName = new Label("" + i);
        userName.setStyle("-fx-background-color: lightgrey;" +
                "-fx-pref-width: 200px;");

        onlineUser.getChildren().addAll(userPic, userName);

        users.getChildren().add(onlineUser);
    }

    public void updateUsername(){
        currentUsername.setText(ChatClient.get().getCurrentUser().getUsername());
    }


}
