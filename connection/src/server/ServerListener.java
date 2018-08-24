package server;

import mediator.Mediator;
import mediator.MediatorManager;
import utility.ConsoleHelper;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerListener implements Runnable{
    private static CopyOnWriteArrayList<Assistant> assistants = new CopyOnWriteArrayList<>();

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(3000)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ConsoleHelper.consoleLog("Соединение установлено! К вашему серверу подключился: " + socket.getRemoteSocketAddress().toString().split(":")[0].substring(1));
                Assistant assistant = new Assistant(socket);
                assistants.add(assistant);
                new Thread(assistant).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void sendMessage(String message) {
        ConsoleHelper.consoleLog(message);
        //Отправить сообщение другим серверам
        if (!MediatorManager.getMediators().isEmpty() && !message.startsWith("$")) {
            for (Mediator mediator : MediatorManager.getMediators()) {
                if (!message.startsWith(mediator.getIp())) { // Что бы не отпралять сообщение обратно его отправителю
                    mediator.putToQueue(message);
                }
            }
        }
        // Отправить сообщение своим клиентам
        for (Assistant assistant : assistants) {
            if (!isEqualPorts(assistant.getSocket().getPort())) {
//                if (!message.startsWith(assistant.getIp())) {
//                    assistant.speaker(message);
//                }
                assistant.speaker(message);
            }
        }
    }

    // Исключаем клиентов-медиаторов
    private static Boolean isEqualPorts(int port) {
        if (!MediatorManager.getMediators().isEmpty()) {
            for (Mediator mediator : MediatorManager.getMediators()) {
                if (mediator.getSocket().getLocalPort() == port) {
                    return true;
                }
            }
        }
        return false;
    }
}
