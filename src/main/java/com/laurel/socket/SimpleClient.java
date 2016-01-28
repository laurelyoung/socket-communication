package com.laurel.socket;

import java.io.*;
import java.net.Socket;

/**
 * User: laurel
 * Date: 16/1/27
 * Time: 下午3:04
 */
public class SimpleClient {

    public static void main(String[] args) {
        Socket socket;
        try {
            socket = new Socket("127.0.0.1", 9000);
            System.out.println("A client started...");

            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();

            // get words from client console
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            // write something to server
            BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(os));
            // get reply from server
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(is));

            /**
             * replace write("\n) with newLine(), newLine() is compatible for all operating systems.
             * serverWriter.write("\n");  -->  serverWriter.newLine();
             */
            // say hello to server
            serverWriter.write("Hello, server.");
            serverWriter.newLine();
            serverWriter.flush();


            int i = 0; // counter
            int talkLimit = 10; // talk times limit

            // get server's reply
            String reply;
            while ((reply = serverReader.readLine()) != null) {
                System.out.println("Server: " + reply);
                System.out.print("Client: ");

                // suspend and wait for user's input
                String msg;
                if ((msg = consoleReader.readLine()) != null) {
                    if ("88".equals(msg) || i++ == talkLimit) {
                        break;
                    }
                    serverWriter.write(msg);
                    serverWriter.newLine();
                    serverWriter.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}