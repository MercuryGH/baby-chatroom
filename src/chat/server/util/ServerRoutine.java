package chat.server.util;

import java.util.Scanner;

// import chat.server.ServerApp;

public final class ServerRoutine {
    private static final String PORT_NUM_INPUT_PROMPT = "Please input the port number to be listened: ";
    private static final String PORT_NUM_INVALID_PROMPT = "Port number should be an integer between 1 to 65535!\n";

    public static int confirmPort() {
        int port;
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println(PORT_NUM_INPUT_PROMPT);
            port = in.nextInt();
            if (port >= 1 && port <= 65535) {
                break;
            } else {
                System.out.println(PORT_NUM_INVALID_PROMPT);
            }
        }
        in.close();
        return port;
    }
}
