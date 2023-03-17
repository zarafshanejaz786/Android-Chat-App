package models;

public class MessageModel {
    private String uId;
    private String msgText;
    private long msgTime;
    private String imageUri; // New field for image URI

    public MessageModel() {}

    public MessageModel(String uId, String msgText, long msgTime) {
        this.uId = uId;
        this.msgText = msgText;
        this.msgTime = msgTime;
    }

    public MessageModel(String uId, String msgText, long msgTime, String imageUri) {
        this.uId = uId;
        this.msgText = msgText;
        this.msgTime = msgTime;
        this.imageUri = imageUri;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

}
