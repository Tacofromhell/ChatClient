package gui;

import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import network.ChatClient;


public class chatUIcontroller {

    @FXML
    TextArea printMessages;
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

    public void printMessageFromServer(String msg) {
        printMessages.setText(printMessages.getText().concat(msg + "\n"));
        printMessages.setScrollTop(Double.MAX_VALUE);
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
