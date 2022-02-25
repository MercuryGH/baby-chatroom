package chat.client;

import javax.swing.JFrame;

public class ClientApp {
   private static final String TITLE = "Chat Client";
   private static final String PROMPT = " (Server: %s:%d, User name: %s)";
   public static final int LOGIN_WINDOW_SIZE_X = 800;
   public static final int LOGIN_WINDOW_SIZE_Y = 600;
   public static final int CHAT_WINDOW_SIZE_X = 1200;
   public static final int CHAT_WINDOW_SIZE_Y = 800;

   private static JFrame appFrame = new JFrame(TITLE);

   public void setLoginFrame() {
      LoginPanel loginPanel = new LoginPanel(this);
      appFrame.setSize(LOGIN_WINDOW_SIZE_X, LOGIN_WINDOW_SIZE_Y);
      appFrame.setResizable(false);
      appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      appFrame.add(loginPanel);

      appFrame.setLocationRelativeTo(null);
      appFrame.setVisible(true);
   }

   public boolean setChatPanel(String ip, int port, String userName) {
      ClientSocket clientSocket = new ClientSocket(ip, port, userName);
      boolean connected = clientSocket.login();
      if (connected == false) {
         return false;
      }

      appFrame.setSize(CHAT_WINDOW_SIZE_X, CHAT_WINDOW_SIZE_Y);
      appFrame.add(clientSocket.getChatPanel());
      appFrame.setTitle(TITLE + String.format(PROMPT, ip, port, clientSocket.getUserName()));

      return true;
   }

   public static void main(String[] args) {
      ClientApp clientApp = new ClientApp();
      clientApp.setLoginFrame();
   }
}
