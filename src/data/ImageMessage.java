
package data;

public class ImageMessage extends Message {
    private static final long serialVersionUID = 9074378074142758642L;

    private String imageName;
    private byte[] imageData;

    public ImageMessage(String imageName, byte[] imageData, User user, String room){
        super("", user, room);
        this.imageName = imageName;
        this.imageData = imageData;
    }

    public byte[] getImageData(){
        return imageData;
    }

    public String getImageName()
    {
        return imageName;
    }
}
