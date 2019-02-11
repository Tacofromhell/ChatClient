package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class chatUIcontroller implements Initializable {

    @FXML
    TextArea printMessages;
    @FXML
    TextField newMessage;
    @FXML
    Button sendMessage;

    public void printMessageFromServer(String msg) {
        printMessages.setText(printMessages.getText().concat("\n" + msg));
    }

    public void sendMessageButton() {
        printMessages.setText(printMessages.getText().concat("\n" + newMessage.getText()));
        newMessage.setText("");
    }

    public void sendMessageEnter(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
            printMessages.setText(printMessages.getText().concat("\n" + newMessage.getText()));
            newMessage.setText("");
        }
    }

    public void initialize() {
        printMessages.textProperty().addListener(e -> {
            printMessages.setScrollTop(Double.MAX_VALUE);
        });
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
