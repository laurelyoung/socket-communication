package com.laurel.socket;

import java.io.*;
import java.net.Socket;

/**
 * User: laurel
 * Date: 16/1/27
 * Time: 下午3:04
 */
public class SimpleClient {

    /**
     * start up client
     */
    private void start() {
        Socket socket;
        try {
            socket = new Socket("127.0.0.1", 9000);
            System.out.println("A client started...");

            // start up a daemon thread to send messages to server
            new ClientMessageSender(socket).start();

            // write something to server
            BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            // get reply from server
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            /**
             * replace write("\n) with newLine(), newLine() is compatible for all operating systems.
             * serverWriter.write("\n");  -->  serverWriter.newLine();
             */
            // say hello to server
            serverWriter.write("Hello, server.");
            serverWriter.newLine();
            serverWriter.flush();

            // get server's reply
            String reply;
            while ((reply = serverReader.readLine()) != null) {
                // 不是服务器的自动回复, 说明是服务器控制台输入的消息, 这时才打印服务器传回的消息
                if (!Constants.SERVER_REPLY_RECEIVE_OK.equals(reply)) {
                    System.out.println("Server say: " + reply);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A daemon thread to send messages to server
     */
    private class ClientMessageSender extends Thread {
        private Socket socket;

        ClientMessageSender(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // get words from client console
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

                // send message to server
                BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                String msg;
                while ((msg = consoleReader.readLine()) != null) {
                    if ("88".equals(msg)) {
                        break;
                    }
                    serverWriter.write(msg);
                    serverWriter.newLine();
                    serverWriter.flush();
                }
            } catch (IOException e) {
                System.err.println("client send message to server failed" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new SimpleClient().start();
    }
}