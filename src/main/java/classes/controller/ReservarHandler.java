package classes.controller;

import classes.service.ReservarService;

import java.io.InputStream;
import java.util.Map;

public class ReservarHandler {

    byte[] contentBytes = null;
    boolean ocupado = false;

    public byte[] handle(Map<String, String> query) {
        contentBytes = this.getBytes("reservar.html");

        String html = new String(contentBytes);

        html = html.replace("{{id}}", query.get("id"));

        contentBytes = html.getBytes();

        return contentBytes;
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

    public String escreveHeader(Map<String, String> query){
        if(new ReservarService().isOcupado(query)){
            return "HTTP/1.1 302 Found\r\nContent-Type: text/html; charset=UTF-8\r\nLocation: /?reservaErro\r\n";
        }
        return "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=UTF-8\r\n\r\n";
    }
}
