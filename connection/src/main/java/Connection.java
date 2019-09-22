import mediator.MediatorManager;
import server.ServerListener;
import utility.ConsoleHelper;

public class Connection {
    public static void main(String[] args) {
        ServerListener serverListener = new ServerListener();
        Thread serverThread = new Thread(serverListener);
        serverThread.start();

        MediatorManager mediatorManager = new MediatorManager();
        Thread clientsThread = new Thread(mediatorManager);
        clientsThread.start();

        ConsoleHelper.consoleLog("Введите ipAddress другого сервера, либо нажмите enter и ожидайте подключения: ");
        String str = ConsoleHelper.inputKeyboard();
        if (str.matches("(?:\\d{1,3}\\.){3}\\d{1,3}")) {
            MediatorManager.addServerConnection(str);
        }
    }
}
