package classes.controller;

import classes.server.Server;
import classes.model.Assento;
import classes.service.ConfirmarService;

import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

public class ConfirmarHandler {

    byte[] contentBytes = null;
    String header;

    public byte[] handle(Map<String, String> query, Socket socket) throws InterruptedException {
        ConfirmarService cs = new ConfirmarService();
        contentBytes = "<p>Redirecionando...</p>".getBytes();

        Server.mutex.acquire();

        int id = Integer.parseInt(query.get("id"));
        Assento assento = Server.assentos.get(id);

        byte[] bytes = new byte[0];

        if (assento != null) {
            if (!cs.isCampoVazio(query)) {
                String nome = query.get("nome");
                String[] dataHoraSplit = query.get("data_hora").split("T");
                String data = dataHoraSplit[0];
                String hora = dataHoraSplit[1];

                assento.setNome(nome);
                assento.setData(data);
                assento.setHora(hora);
                assento.setOcupado(true);

                Server.logger.log(socket, assento);

                System.out.println("LOG Nova reserva adicionada: " + assento.getId() + " | " + assento.getNome());

                return contentBytes;
            }

        } else {
            return bytes;
        }

        Server.mutex.release();

        return bytes;
    }

    private byte[] getBytes(String recurso) {
        if (recurso.startsWith("/"))
            recurso = recurso.substring(1);

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(recurso);

        if (is != null) {
            try {
                return is.readAllBytes();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public String escreverHeader(boolean b) {
        if (!b) {
            header = "HTTP/1.1 302 Found\r\nContent-Type: text/html; charset=UTF-8\r\nLocation: /?reservaConfirmada\r\n";
        } else {
            header = "HTTP/1.1 302 Found\r\nContent-Type: text/html; charset=UTF-8\r\nLocation: /?reservaErro\r\n";
        }

        return header;
    }
}
