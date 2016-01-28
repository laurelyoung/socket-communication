package com.laurel.socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * User: laurel
 * Date: 16/1/27
 * Time: 下午3:02
 */
public class SimpleServer {

    private static List<Socket> clients = new ArrayList<Socket>();

    /**
     * start up server
     */
    private void start() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(9000);
            System.out.println("Server started...");

            // start up a daemon thread to send messages to all clients
            new ServerMessageSender().start();

            while (true) {
                try {
                    // receive a new client
                    Socket socket = serverSocket.accept();

                    // start up a new thread to handle new client request
                    new HandleClientThread(socket).start();
                    clients.add(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A daemon thread to send messages to all clients
     */
    private class ServerMessageSender extends Thread {
        @Override
        public void run() {
            // get messages from server console
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            String message;
            try {
                while ((message = consoleReader.readLine()) != null) {
                    for (Socket client : clients) {
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                        writer.write(message);
                        writer.newLine();
                        writer.flush();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A client handle thread
     */
    private static class HandleClientThread extends Thread {
        private Socket socket;

        public HandleClientThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();

                // get words from client socket
                BufferedReader clientReader = new BufferedReader(new InputStreamReader(is));
                // write replay to client
                BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(os));

                String line;
                while ((line = clientReader.readLine()) != null) {
                    System.out.println("Client "
                            + socket.getInetAddress().getHostName() + " say: " + line);
                    // 成功接收时, 自动回复成功标记
                    clientWriter.write(Constants.SERVER_REPLY_RECEIVE_OK);
                    clientWriter.newLine();
                    clientWriter.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new SimpleServer().start();
    }

}