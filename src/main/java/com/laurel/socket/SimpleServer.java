package com.laurel.socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: laurel
 * Date: 16/1/27
 * Time: 下午3:02
 */
public class SimpleServer {

    private static List<Socket> clients = new ArrayList<Socket>();

    /**
     * 启动服务器
     */
    private void start() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(9000);
            System.out.println("Server started...");

            // 启动守护线程
            new ServerMessageSender().start();

            while (true) {
                try {
                    // 接收客户端
                    Socket socket = serverSocket.accept();
                    System.out.println("A new client is connected...");

                    // 创建一个新的线程用来处理新来的客户端
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
     * <p>守护线程</p>
     * 监听服务端控制台的输入情况. 一旦发现有输入, 立即将输入的内容发送给所有客户端
     */
    private class ServerMessageSender extends Thread {
        @Override
        public void run() {
            // 读取控制台输入
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            String message;
            try {
                while ((message = consoleReader.readLine()) != null) {
                    // 发送服务器控制台的输入内容给所有客户端
                    sendMessageToAllClients(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <p>客户端处理线程</p>
     * 每接收到一个客户端, 服务器就为它单独创建一个线程处理它.
     */
    private static class HandleClientThread extends Thread {
        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        private Socket socket;

        public HandleClientThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                String line;
                while ((line = clientReader.readLine()) != null) {
                    if (Constants.CLIENT_REQUEST_CONNECT.equals(line)) {
                        // 成功接收时, 自动回复成功标记
                        clientWriter.write(Constants.SERVER_REPLY_RECEIVE_OK);
                        clientWriter.newLine();
                        clientWriter.flush();
                    } else {
                        // 消息格式: 时间 客户端名称: 内容
                        line = dateFormat.format(new Date()) + "\t" + socket.getInetAddress().getHostName() + ": " + line;
                        System.out.println(line);
                        // 将每个客户端的消息转发给所有与服务器建立连接的客户端, 类似于QQ讨论组或QQ群
                        sendMessageToAllClients(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送消息给所有客户端
     *
     * @param message 消息
     * @throws IOException
     */
    private static void sendMessageToAllClients(String message) throws IOException {
        for (Socket client : clients) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            writer.write(message);
            writer.newLine();
            writer.flush();
        }
    }

    public static void main(String[] args) {
        new SimpleServer().start();
    }

}