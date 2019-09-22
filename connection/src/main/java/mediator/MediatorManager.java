package mediator;

import utility.ConsoleHelper;
import java.util.concurrent.CopyOnWriteArrayList;

public class MediatorManager implements Runnable{
    private static CopyOnWriteArrayList<String> serverList = new CopyOnWriteArrayList<>();
    private static CopyOnWriteArrayList<Mediator> mediators = new CopyOnWriteArrayList<>();
    private static int count = 0;

    @Override
    public void run() {
        while (true) {
            if (serverList.size() > count) {
                count++;
                Mediator mediator = new Mediator(serverList.get(serverList.size() - 1), 3000);
                mediators.add(mediator);
                new Thread(mediator).start();
                ConsoleHelper.consoleLog("Запущена новая клиентская нить");
            }
        }
    }


    public static void addServerConnection(String ipAddress) {
        serverList.add(ipAddress);
    }

    public static CopyOnWriteArrayList<String> getServerList() {
        return serverList;
    }

    public static CopyOnWriteArrayList<Mediator> getMediators() {
        return mediators;
    }
}
