package chat.common;

import java.io.Serializable;

public class Message implements Serializable {
    private MessageType mType;
    private String content;

    public MessageType getmType() {
        return mType;
    }

    public String getContent() {
        return content;
    }

    public Message(MessageType mType, String content) {
        this.mType = mType;
        this.content = content;
    }
}
