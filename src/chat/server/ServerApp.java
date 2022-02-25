package chat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import chat.common.Message;
import chat.common.MessageType;
import chat.server.util.ServerRoutine;

public class ServerApp {
    private static final String LISTENING_PROMPT = "Server Socket is listening port %d.";
    private static final String USER_LOGIN_BROADCAST_PROMPT = "「%s」 加入了聊天室。\n";
    private static final String USER_LOGIN_SELF_PROMPT = "User %s from %s has logged in.";
    private static final String NETCAT_CONNECTION_WARNING = "Someone from %s is doing something tricky.";
    private static final String ERROR_WHILE_SENDING_MSG = "Cannot send to client %s. Target is offline.";
    private static final String USER_LOGOUT_BROADCAST_PROMPT = "「%s」 离开了聊天室。\n";

    private int port = 5011;
    private LinkedList<ClientConnection> clientList = new LinkedList<>();  // 比ArrayList性能稍好（本Server不需要随机访问）
    private ServerSocket socket;

    public ServerApp() {
        this.port = ServerRoutine.confirmPort(); // 用户输入监听的端口号
        this.initSocket();
    }

    public ServerApp(int port) {
        this.port = port;
        this.initSocket();
    }

    private void initSocket() {
        try {
            this.socket = new ServerSocket(this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        System.out.println(String.format(LISTENING_PROMPT, this.socket.getLocalPort()));
        while (true) {
            try {
                Socket clientSocket = this.socket.accept();

                ClientConnection clientConnection = new ClientConnection(clientSocket);
                boolean readValid = clientConnection.readIndicator(); // 读取Socket输入的用户名
                if (readValid == false) { // 读取不合法
                    continue;
                }
                this.clientList.add(clientConnection);
                new Thread(clientConnection).start();

                String clientIpPort = clientConnection.getClientIpPort();
                this.broadcastText("服务器", String.format(USER_LOGIN_BROADCAST_PROMPT, clientConnection.userName));
                this.broadcastUserList();
                System.out.println(String.format(USER_LOGIN_SELF_PROMPT, clientConnection.userName, clientIpPort));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean removeClient(String ipPort) {
        for (ClientConnection c : this.clientList) {
            if (c.getClientIpPort().equals(ipPort)) {
                this.clientList.remove(c);
                return true;
            }
        }
        return false;
    }

    /**
     * 向客户端列表广播纯文本消息。要求clientList在此过程中不改变（同步）
     * @param senderName 广播消息的发送者
     * @param content 广播消息的正文
     */
    public synchronized void broadcastText(String senderName, String content) {
        final String curTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
        final String textSent = String.format("[%s] %s: %s\n", curTime, senderName, content);

        Message msg = new Message(MessageType.PLAIN_TEXT, textSent);
        this.clientList.forEach(e -> e.sendMsg(msg)); // 广播消息
    }

    /** 
     * 向客户端列表广播当前服务端掌握的客户端列表。比较笨的方法。
     * @see broadcastText()
    */
    public synchronized void broadcastUserList() {
        StringBuilder userListStr = new StringBuilder();
        this.clientList.forEach(e -> {
            userListStr.append(String.format("%s, %s\n", e.userName, e.loginTime));
        }); // 每行格式：用户名, 登录时间\n
        Message msg = new Message(MessageType.USER_LIST, userListStr.toString());
        this.clientList.forEach(e -> e.sendMsg(msg));
    }

    private class ClientConnection implements Runnable { 
        Socket clientSocket;

        String userName; // 该客户端的用户名
        String loginTime; // 该客户端的登录时间

        public String getClientIpPort() { // e.g. 1.1.1.1:4444
            return clientSocket.getInetAddress().toString() + ":" + clientSocket.getPort();
        }

        public ClientConnection(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public boolean readIndicator() {
            try {
                ObjectInputStream socketInput = new ObjectInputStream(clientSocket.getInputStream());
                this.userName = (String) socketInput.readObject();
            } catch (StreamCorruptedException e) { // readObject 失败了
                System.out.println(String.format(NETCAT_CONNECTION_WARNING, clientSocket.getInetAddress().toString()));
                return false;
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
                return false;
            }
            this.loginTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // 获取当前时间
            return true;
        }

        public void sendMsg(Message msg) {
            // System.out.println(msg.getContent()); Debug purpose
            if (this.clientSocket.isConnected()) {
                try {
                    ObjectOutputStream socketOutput = new ObjectOutputStream(this.clientSocket.getOutputStream());
                    socketOutput.writeObject(msg);
                } catch (IOException e) { // 目标已下线
                    System.out.println(String.format(ERROR_WHILE_SENDING_MSG, this.getClientIpPort()));
                }
            }
        }

        @Override
        public void run() {
            while (true) {
                // ObjectOutputStream socketOutput;
                try {
                    ObjectInputStream socketInput = new ObjectInputStream(clientSocket.getInputStream());
                    Message message = (Message) socketInput.readObject();
                    broadcastText(this.userName, message.getContent());
                    broadcastUserList();
                } catch (ClassNotFoundException | IOException e) { // 目标已下线
                    System.out.println(String.format(ERROR_WHILE_SENDING_MSG, this.getClientIpPort()));
                    removeClient(this.getClientIpPort());  // 不再建立连接
                    broadcastText("服务器", String.format(USER_LOGOUT_BROADCAST_PROMPT, this.userName));
                    broadcastUserList();
                    break;  // 线程结束
                }
            }
        }
    }

    public static void main(String[] args) {
        // ServerApp app = new ServerApp(5011);
        ServerApp app = new ServerApp();
        app.listen();
    }
}
