package chat.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import chat.common.Message;

public class ClientSocket { // 单例
    private String ip;
    private int port;
    private String userName;

    private Socket socket;

    private ChatPanel chatPanel; // View

    public ClientSocket(String ip, int port, String userName) {
        this.ip = ip;
        this.port = port;
        this.userName = userName;
    }

    public boolean login() {
        try {
            this.socket = new Socket(this.ip, this.port);
        } catch (IOException e) {
            return false;
        }

        try {
            ObjectOutputStream socketOut = new ObjectOutputStream(this.socket.getOutputStream());
            socketOut.writeObject(this.userName); // send userName to server (hello!)
        } catch (IOException e) {
            return false;
        }

        new Thread(() -> {
            while (true) {
                try {
                    ObjectInputStream socketIn = new ObjectInputStream(socket.getInputStream());
                    Message serverMsg = (Message) socketIn.readObject();
                    String content = serverMsg.getContent();
                    switch (serverMsg.getmType()) {
                    case PLAIN_TEXT:
                        chatPanel.displayPlainText(content);
                        break;
                    case USER_LIST:
                        chatPanel.refreshUserList(content);
                        break;
                    }
                } catch (ClassNotFoundException | IOException e) {
                    chatPanel.disconnect();
                    break;  // Server down, no more effort
                }
            }
        }).start();

        this.chatPanel = new ChatPanel(this);
        return true;
    }

    public void sendMsg(Message msg) {
        try {
            ObjectOutputStream socketOut = new ObjectOutputStream(this.socket.getOutputStream());
            socketOut.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatPanel getChatPanel() {
        return chatPanel;
    }

    public String getUserName() {
        return userName;
    }
}
