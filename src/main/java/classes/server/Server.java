package classes.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import classes.model.Assento;
import classes.controller.Connection;

public class Server {
    public static Map<Integer, Assento> assentos = new HashMap<>();
    public static Semaphore mutex = new Semaphore(1);

    public static Logger logger = new Logger();

    public static void main(String[] args) {
        for (int id = 1; id <= 20; id++) {
            assentos.put(id, new Assento(id));
        }

        ServerSocket server = null;
        try {
            server = new ServerSocket(8080);
            System.out.println("Rodando servidor em http://localhost:8080");

            while (true) {
                Socket socket = server.accept();
                Connection connection = new Connection(socket);
                Thread thread = new Thread(connection);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
