package data;

import java.util.concurrent.LinkedBlockingDeque;

import data.NetworkMessage.*;

public class DataHandler {

    private DataHandlerHelper helper = new DataHandlerHelper();
    private LinkedBlockingDeque<Object> dataQueue = new LinkedBlockingDeque<>();

    public DataHandler() {

        Thread handleData = new Thread(this::handleDataQueue);
        handleData.setDaemon(true);
        handleData.start();
    }

    public void addToQueue(Object obj) {
        this.dataQueue.addLast(obj);
    }

    private void handleDataQueue() {
        while (true) {
            if (dataQueue.size() > 0) {

                Object data = dataQueue.poll();

                if (data instanceof Message) {
                    System.out.println("Received " + data);
                    helper.receivedMessage(data);

                } else if (data instanceof Room) {
//                    helper.receivedRoom(data);

                } else if (data instanceof User) {
                    helper.receivedUser(data);

                } else if (data instanceof ClientConnect) {
                    helper.receivedClientConnected((ClientConnect) data);

                } else if (data instanceof ClientDisconnect) {
                    helper.receivedClientDisconnected((ClientDisconnect) data);

                } else if (data instanceof RoomCreate) {
                    helper.receivedRoomCreated((RoomCreate) data);

                } else if (data instanceof RoomDelete) {

                } else if (data instanceof RoomJoin) {
                    helper.receivedUserJoinedRoom(((RoomJoin) data).getTargetRoom(), ((RoomJoin) data).getUser(), ((RoomJoin) data).getRoom());

                } else if (data instanceof RoomLeave) {

                } else if (data instanceof UserNameChange) {
                    helper.receivedUserChangedName(data);
                }
            } else {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
