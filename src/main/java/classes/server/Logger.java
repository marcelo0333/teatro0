package classes.server;

import java.io.File;
import java.io.FileWriter;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import classes.model.Assento;

public class Logger {
    private String logString = "";
    private Semaphore vazio = new Semaphore(1000);
    private Semaphore cheio = new Semaphore(0);
    private Semaphore mutex = new Semaphore(1);

    private File file = new File("log.txt");

    private Socket socket;
    private Assento assento;

    public Logger() {
        try {
            if (file.createNewFile()) {
                System.out.println("Criado arquivo de log: " + this.file.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void log(Socket socket, Assento assento) {
        this.socket = socket;
        this.assento = assento;

        Thread produz = new Thread(new ProduzLog());
        Thread armazena = new Thread(new ArmazenaLog());

        produz.start();
        armazena.start();
    }

    private class ProduzLog implements Runnable {
        @Override
        public void run() {
            try {
                mutex.acquire();

                String ip = socket.getInetAddress().toString();

                logString = "----- NOVA RESERVA -----\n";
                logString += "IP: " + ip + "\n";
                logString += "NOME: " + assento.getNome() + "\n";
                logString += "ASSENTO: " + assento.getId() + "\n";
                logString += "DATA: " + assento.getData() + " AS " + assento.getHora() + "\n";
                logString += "------------------------\n";

                vazio.acquire(logString.length());
                cheio.release();
                mutex.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ArmazenaLog implements Runnable {
        @Override
        public void run() {
            try {
                mutex.acquire();
                cheio.acquire();

                vazio.release(logString.length());

                FileWriter writer = new FileWriter(file.getName(), true);

                writer.write(logString);

                writer.close();

                mutex.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
