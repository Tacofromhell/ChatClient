package data;

import java.io.Serializable;

public abstract class NetworkMessage implements Serializable {
    private static final long serialVersionUID = -4057760186023784992L;

        public static class ClientConnect extends NetworkMessage{
            String userId;

            public ClientConnect(String userId){
                this.userId = userId;
            }
        }

    public static class ClientDisconnect extends NetworkMessage{
        String userId;

        public ClientDisconnect(String userId){
            this.userId = userId;
        }


    }

        public static class RoomCreate extends NetworkMessage{
            String roomName;
            public RoomCreate(String roomName){
                this.roomName = roomName;
            }
        }

        public static class RoomDelete extends NetworkMessage{
            String targetRoom;
            public RoomDelete(String targetRoom){
                this.targetRoom = targetRoom;
            }
        }

    public static class RoomJoin extends NetworkMessage{
        String targetRoom;
        User user;
        public RoomJoin(String targetRoom, User user) {
            this.targetRoom = targetRoom;
            this.user = user;
        }
    }

    public static class RoomLeave extends NetworkMessage{
        String targetRoom;
        String userId;
        public RoomLeave(String targetRoom, String userId){
            this.targetRoom = targetRoom;
            this.userId = userId;
        }
    }

        public static class UserNameChange extends NetworkMessage{
        private static final long serialVersionUID = 2206362119107373026L;
        String newName;
        String userId;

        public UserNameChange(String newName, String userId){
            this.newName = newName;
            this.userId = userId;
        }

        public String getNewName(){
            return this.newName;
        }

        public String getUserId(){
            return this.userId;
        }
    }

    }//END OF CLASS

