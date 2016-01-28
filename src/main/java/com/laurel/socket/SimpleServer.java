package com.laurel.socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * User: laurel
 * Date: 16/1/27
 * Time: 下午3:02
 */
public class SimpleServer {

    public static void main(String[] args) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(9000);
            System.out.println("Server started...");
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    //
                    new HandleClientThread(socket).start();
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
     * client handle thread
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
                    System.out.println("Client message from "
                                            + socket.getInetAddress().getHostName() + ": " + line);
                    // write a reply to client
                    clientWriter.write("Received, message <" + line + ">");
                    clientWriter.newLine();
                    clientWriter.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}