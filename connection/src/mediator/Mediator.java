package mediator;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import utility.ConsoleHelper;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Mediator implements Runnable{
    private String ip;
    private int port;
    private PrintWriter writer;
    private Socket socket = new Socket();
    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();


    public Mediator(String ip, int port) {
        this.ip = ip;
        this.port = port;
        try {
            this.socket.connect(new InetSocketAddress(ip, port), 5000);
            ConsoleHelper.consoleLog("Создано соединение с сервером " + ip);
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        writer.println("i am the server!");
        sendServerList();
        while (true) {
            if (!queue.isEmpty()) {
                writer.println(queue.poll());
            }
        }
    }

    // Упаковывает коллекцию serverList из класса MediatorManager в Json формат и отпраляет своему асистенту сервера
    private void sendServerList() {
        try {
            StringWriter writerString = new StringWriter();

            CopyOnWriteArrayList copyList = (CopyOnWriteArrayList) MediatorManager.getServerList().clone();
            copyList.remove(ip);

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(writerString, copyList);
            writer.println(writerString.toString());
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Принимает сообшение из класса ServerList метода sendMessage и добавляет в очередь
    public synchronized void putToQueue(String str) {
        try {
            queue.put(str);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mediator mediator = (Mediator) o;
        return port == mediator.port &&
                Objects.equals(ip, mediator.ip) &&
                Objects.equals(writer, mediator.writer) &&
                Objects.equals(socket, mediator.socket) &&
                Objects.equals(queue, mediator.queue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port, writer, socket, queue);
    }
}
