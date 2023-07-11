package classes.controller;

import java.io.InputStream;
import java.util.Map;

public class ErrorHandler {
    byte[] contentBytes = null;
    String header = "";

    public byte[] handle(Map<String, String> query) {
        if (contentBytes == null) {
            contentBytes = this.getBytes("pageError.html");
            return contentBytes;
        }
        return null;
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

    public String gerarHeader() {
        header = "HTTP/1.1 404 Not Found\r\nContent-Type: text/html; charset=UTF-8\r\n\r\n";
        return header;
    }
}
