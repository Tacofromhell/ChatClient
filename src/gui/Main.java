package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.ChatClient;

public class Main extends Application {
    public static Stage stage;
    public static chatUIcontroller UIcontrol;

    private Parent root;

    @Override
    public void start(Stage stage) throws Exception {
        ChatClient.get();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chatUI.fxml"));
        root = loader.load();

        UIcontrol = loader.getController();
        stage.setUserData(UIcontrol);


        Main.stage = stage;
        stage.setTitle("ChatApp");


        stage.setScene(new Scene(root));
        stage.setMinWidth(650);
        stage.setMinHeight(650);
        stage.setOnCloseRequest(e -> ChatClient.get().closeThreads());
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
