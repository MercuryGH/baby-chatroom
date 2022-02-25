package chat.client;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import chat.common.Message;
import chat.common.MessageType;

public class ChatPanel extends JPanel {
    private static final String FONT_NAME = "宋体";

    private static final Color CHAT_TEXT_BG_COLOR = new Color(0xD0, 0xD0, 0xD0);
    private static final String DISCONNECT_PROMPT = "已与服务器断开连接，请重新登陆！";

    private JTextArea userListArea; // updatable state
    private JTextArea chatTextArea; // updatable state

    public ChatPanel(ClientSocket clientSocket) {
        this.setBackground(new Color(0xE8, 0xE8, 0xD0)); // This color should be good
        this.setFocusable(true);
        this.setVisible(true);
        this.setLayout(null); // 绝对坐标 Layout

        Font inputTextFont = new Font(FONT_NAME, Font.PLAIN, 30);
        Font chatTextFont = new Font(FONT_NAME, Font.PLAIN, 15);

        // this.chatTextArea = new JTextArea("", 100, 80); // 聊天区
        this.chatTextArea = new JTextArea(""); // 聊天区
        this.chatTextArea.setFont(chatTextFont);
        this.chatTextArea.setBackground(CHAT_TEXT_BG_COLOR);
        this.chatTextArea.setEditable(false);

        // this.userListArea = new JTextArea("", 50, 80); // 用户列表
        this.userListArea = new JTextArea(""); // 用户列表
        this.userListArea.setBackground(CHAT_TEXT_BG_COLOR);
        this.userListArea.setEditable(false);

        JScrollPane chatAreaScroll = new JScrollPane(chatTextArea); // 聊天区滚轮
        chatAreaScroll.setBounds(0, 0, 950, 580);
        this.add(chatAreaScroll);

        JScrollPane userListScroll = new JScrollPane(userListArea); // 用户列表滚轮
        userListScroll.setBounds(970, 0, 200, 580);
        this.add(userListScroll);

        JTextArea textInputArea = new JTextArea(""); // 输入框
        textInputArea.setBounds(0, 600, 1200, 100);
        textInputArea.setFont(inputTextFont);
        this.add(textInputArea);

        JButton sendBtn = new JButton("发送");
        sendBtn.setBounds(1075, 710, 100, 30);
        sendBtn.addActionListener(e -> {
            String inputText = textInputArea.getText().trim();
            if (inputText.length() == 0) { // empty input
                return;
            }
            Message sentMsg = new Message(MessageType.PLAIN_TEXT, inputText);
            clientSocket.sendMsg(sentMsg);
            textInputArea.setText("");
        });
        this.add(sendBtn);

        JButton clearBtn = new JButton("清空");
        clearBtn.setBounds(970, 710, 100, 30);
        clearBtn.addActionListener(e -> {
            chatTextArea.setText("");
        });
        this.add(clearBtn);
    }

    public void disconnect() {
        JOptionPane.showMessageDialog(null, DISCONNECT_PROMPT, "Error", JOptionPane.ERROR_MESSAGE);
        // Nothing more to be done
    }

    public void displayPlainText(String content) {
        this.chatTextArea.append(content);
        this.chatTextArea.setCaretPosition(this.chatTextArea.getText().length() - 1); // point to the end of the text
    }

    public void refreshUserList(String content) {
        this.userListArea.setText("");
        this.userListArea.append(content);
        this.userListArea.setCaretPosition(this.userListArea.getText().length() - 1);
    }
}
