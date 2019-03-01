package network;

import data.NetworkMessage;

public class UpdateActiveRoom implements Runnable {

    @Override
    public void run() {
        while (true) {
            SocketStreamHelper.sendData(
                    new NetworkMessage.UserActiveRoom(ChatClient.get().getCurrentUser().getActiveRoom()),
                    ChatClient.get().getDataOut()
            );
//          update active room every 30 seconds
            try {
                Thread.sleep(1000 * 30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
