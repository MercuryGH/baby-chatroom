package chat.client;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginPanel extends JPanel {
    private static final String FONT_NAME = "宋体";
    private static final int HALF_WIDTH = ClientApp.LOGIN_WINDOW_SIZE_X / 2;

    public LoginPanel(ClientApp clientApp) {
        this.setBackground(new Color(0xE8, 0xE8, 0xD0)); // This color should be good
        this.setFocusable(true);
        this.setVisible(true);
        this.setLayout(null);  // 绝对坐标 Layout

        // GridLayout捣鼓不出来，只好手动指定Layout
        Font labelFont = new Font(FONT_NAME, Font.PLAIN, 30);
        Font titleFont = new Font(FONT_NAME, Font.BOLD, 50);
        // Font buttonFont = new Font(Font_NAME, )

        JLabel titleLabel = new JLabel("登录聊天室");
        titleLabel.setBounds(HALF_WIDTH - 130, 50, 400, 50);
        titleLabel.setFont(titleFont);
        this.add(titleLabel);

        JLabel ipLabel = new JLabel("服务器IP地址：");
        ipLabel.setBounds(HALF_WIDTH - 300, 150, 400, 50);
        ipLabel.setFont(labelFont);
        this.add(ipLabel);

        JLabel portLabel = new JLabel("服务器端口号：");
        portLabel.setBounds(HALF_WIDTH - 300, 250, 400, 50);
        portLabel.setFont(labelFont);
        this.add(portLabel);

        JLabel userNameLabel = new JLabel("用户名：");
        userNameLabel.setBounds(HALF_WIDTH - 220, 350, 400, 50);
        userNameLabel.setFont(labelFont);
        this.add(userNameLabel);

        JTextField ipTextField = new JTextField("127.0.0.1");
        ipTextField.setBounds(HALF_WIDTH - 100, 150, 300, 50);
        ipTextField.setFont(labelFont);
        this.add(ipTextField);

        JTextField portTextField = new JTextField("5011");
        portTextField.setBounds(HALF_WIDTH - 100, 250, 300, 50);
        portTextField.setFont(labelFont);
        this.add(portTextField);

        JTextField userNameTextField = new JTextField("匿名");
        userNameTextField.setBounds(HALF_WIDTH - 100, 350, 300, 50);
        userNameTextField.setFont(labelFont);
        this.add(userNameTextField);

        JButton loginButton = new JButton("登录");
        loginButton.setBounds(HALF_WIDTH - 200, 450, 400, 50);
        loginButton.setFont(labelFont);
        loginButton.setBackground(new Color(0x66, 0xCC, 0xFF));
        loginButton.addActionListener(e -> {
            String ip = ipTextField.getText().trim();
            String port = portTextField.getText().trim();
            String userName = userNameTextField.getText().trim();
            if (ip.length() == 0) {
                JOptionPane.showMessageDialog(null, "服务器IP地址不能为空！", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (port.length() == 0) {
                JOptionPane.showMessageDialog(null, "端口号不能为空！", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (userName.length() == 0) {
                JOptionPane.showMessageDialog(null, "用户名不能为空！", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int portNum = Integer.parseInt(port);

            if (clientApp.setChatPanel(ip, portNum, userName) == true) {
                setVisible(false); // close login page
            } else {
                JOptionPane.showMessageDialog(null, "服务器连接失败！", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        this.add(loginButton);
    }
}
