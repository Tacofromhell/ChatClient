package gui;

import javafx.fxml.FXML;
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
    TextField newMessage;
    @FXML
    Button sendMessage;
    @FXML
    VBox users;

    public void initialize() {

    }

    public void printMessageFromServer(String msg) {
        printMessages.setText(printMessages.getText().concat("\n" + msg));
        printMessages.setScrollTop(Double.MAX_VALUE);
    }

    public void sendMessageButton() {
//        send input string to network method in ChatClient
        ChatClient.get().sendMessageToServer(newMessage.getText());

        newMessage.setText("");
    }

    public void sendMessageEnter(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
//            send input string to network method in ChatClient
            ChatClient.get().sendMessageToServer(newMessage.getText());

            newMessage.setText("");
        }
    }

    public void printUsers(){
        HBox onlineUser = new HBox(5);
        onlineUser.setStyle("-fx-alignment: CENTER_LEFT");
        Circle userPic = new Circle(13, Color.LIGHTGRAY);
        Label userName = new Label();
        userName.setStyle("-fx-background-color: lightgrey;" +
                "-fx-pref-width: 200px;");

        onlineUser.getChildren().addAll(userPic, userName);

        users.getChildren().add(onlineUser);
    }


}
