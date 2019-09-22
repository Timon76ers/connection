package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import mediator.MediatorManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Assistant implements Runnable {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String ip;


    public Assistant(Socket socket) {
        this.socket = socket;
        try {
            this.writer = new PrintWriter(socket.getOutputStream(), true);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.ip = socket.getRemoteSocketAddress().toString().split(":")[0].substring(1);
    }

    @Override
    public void run() {
        while (true) {
            try {
                String str = reader.readLine();
                if (str.equals("i am the server!")) {
                    joinTheNetwork();
                    // Если еще не подключен к серверу оппонента то добавляем его в список
                    if (!MediatorManager.getServerList().contains(ip)) {
                        MediatorManager.addServerConnection(ip);
                    }
                }
                else {
                    if (!str.matches("(?:\\d{0,3}\\.){3}\\d{0,3}.*")) {
                        str = ip + ": " + str;
                    }
                    else str = "$ " + str;
                    ServerListener.sendMessage(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Принимает с инпута: Json - список новых серверов и добавляет их в свой список в  классе MediatorManager
    private void joinTheNetwork(){
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String[] receiverArrayOfServer = objectMapper.readValue(reader.readLine(), String[].class);
            if (receiverArrayOfServer.length > 0) {
                for (int i = 0; i < receiverArrayOfServer.length; i++) {
                    if (!MediatorManager.getServerList().contains(receiverArrayOfServer[i])) {
                        MediatorManager.addServerConnection(receiverArrayOfServer[i]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void speaker(String message) {
        writer.println(message);
    }

    public Socket getSocket() {
        return socket;
    }

    public String getIp() {
        return ip;
    }
}
