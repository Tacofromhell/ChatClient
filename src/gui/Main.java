package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.ChatClient;

public class Main extends Application {
    public static Stage stage;
    private Parent root;

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {
        root = FXMLLoader.load(getClass().getResource("chatUI.fxml"));
        Main.stage = stage;
        stage.setTitle("ChatApp");

//        try {
//            startSocket();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        ChatClient.get();

        stage.setScene(new Scene(root, 400, 600));

        stage.setOnCloseRequest(e -> ChatClient.get().closeThreads());
        stage.show();
    }

//    public void startSocket() {
//        Runnable task = new Runnable() {
//            @Override
//            public void run() {
//                ChatClient.get();
//            }
//        };
//
//        Thread socketThread = new Thread(task);
//        socketThread.start();
//    }


}
