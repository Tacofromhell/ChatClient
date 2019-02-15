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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chatUI.fxml"));
        root = loader.load();

        UIcontrol = loader.getController();
        stage.setUserData(UIcontrol);

        Main.stage = stage;
        stage.setTitle("ChatApp");
        stage.setResizable(false);
//        initialize server connection by calling singleton
        ChatClient.get();

        for(int i = 0; i < 30; i++){
            UIcontrol.printUsers(i);
        }

        stage.setScene(new Scene(root));

        stage.setOnCloseRequest(e -> ChatClient.get().closeThreads());
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
