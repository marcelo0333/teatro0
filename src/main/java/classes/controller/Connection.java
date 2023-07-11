package classes.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import classes.service.ConfirmarService;

public class Connection implements Runnable {
    private Socket socket;

    public Connection(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (InputStream in = this.socket.getInputStream();
             OutputStream out = this.socket.getOutputStream();
             Scanner scanner = new Scanner(in)) {

            if (!scanner.hasNext()) {
                return;
            }

            String method = scanner.next();
            String path = scanner.next();

            System.out.println(method + " " + path);

            String[] dirAndParams = path.split("\\?");

            String recurso = dirAndParams[0];

            Map<String, String> query = parseQuery(dirAndParams.length > 1
                    ? dirAndParams[1].split("&")
                    : null);

            byte[] contentBytes = null;

            String header = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=UTF-8\r\n\r\n";

            if (recurso.startsWith("/css/")) {
                contentBytes = getBytes(recurso);

                if (contentBytes != null)
                    header = header.replace("text/html", "text/css");
            }

            if (recurso.equals("/")) {
                contentBytes = new IndexHandler().handle(query);
            }

            if (recurso.equals("/reservar")) {
                ReservarHandler reservarHandler = new ReservarHandler();
                contentBytes = reservarHandler.handle(query);
                header = reservarHandler.escreveHeader(query);
            }

            if (recurso.equals("/confirmar")) {
                ConfirmarHandler confirmarHandler = new ConfirmarHandler();
                contentBytes = confirmarHandler.handle(query, socket);
                header = confirmarHandler.escreverHeader(new ConfirmarService().isCampoVazio(query));
            }

            if (contentBytes == null) {
                ErrorHandler errorHandler = new ErrorHandler();
                contentBytes = errorHandler.handle(query);
                header = errorHandler.gerarHeader();
            }

            out.write(header.getBytes());
            out.write(contentBytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, String> parseQuery(String[] query) throws UnsupportedEncodingException {
        Map<String, String> parametrosConsulta = new HashMap<>();

        if (query != null) {
            for (String parametro : query) {
                String[] parChaveValor = parametro.split("=");

                String chave = parChaveValor[0];
                String valor = (parChaveValor.length > 1) ? URLDecoder.decode(parChaveValor[1], "UTF-8") : null;

                parametrosConsulta.put(chave, valor);
            }
        }

        return parametrosConsulta;
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
}
