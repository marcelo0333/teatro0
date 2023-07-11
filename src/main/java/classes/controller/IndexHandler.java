package classes.controller;

import classes.server.Server;
import classes.model.Assento;

import java.io.InputStream;
import java.util.Map;

public class IndexHandler {

    byte[] contentBytes = null;

    public byte[] handle(Map<String, String> query) {
        contentBytes = this.getBytes("index.html");

        String html = new String(contentBytes);

        StringBuilder elementos = new StringBuilder();

        for (Assento assento : Server.assentos.values()) {
            StringBuilder elemento = new StringBuilder("<a");

            if (!assento.isOcupado()) {
                elemento.append(" class=\"assento\"");
                elemento.append(" href=\"/reservar?id=").append(assento.getId()).append("\"");
                elemento.append(">").append(assento.getId()).append("</a>");
                elementos.append(elemento).append("\n\n");
            } else {
                elemento.append(" class=\"assento-ocupado\"");
                elemento.append(" data-nome=\"").append(assento.getNome()).append("\"");
                elemento.append(" data-data=\"").append(assento.getData()).append("\"");
                elemento.append(" data-hora=\"").append(assento.getHora()).append("\"");
                elemento.append(">").append(assento.getId()).append("</a>");
                elementos.append(elemento).append("\n");
            }
        }

        html = html.replace("<!-- ASSENTOS -->", elementos.toString());

        if (query.containsKey("reservaConfirmada")) {
            String mensagem = "<div class=\"alert alert-success d-flex align-items-center\" role=\"alert\">\n" +
                    "  <img class=\"bi flex-shrink-0 me-2\" src=\"https://static.vecteezy.com/system/resources/previews/017/350/120/original/green-check-mark-png.png\" alt=\"V em verde para confirmar sucesso.\" width=32 height=32></img>\n" +
                    "  <div>\n" +
                    "    Assento reservado com sucesso!\n" +
                    "  </div>\n" +
                    "</div>";
            html = html.replace("<!-- MENSAGEM -->", mensagem);
        } else if (query.containsKey("reservaErro")){
            String mensagem = "<div class=\"alert alert-danger d-flex align-items-center\" role=\"alert\">\n" +
                    "  <img class=\"bi flex-shrink-0 me-2\" src=\"https://www.freeiconspng.com/thumbs/error-icon/error-icon-4.png\" alt=\"X para confirmar erro.\" width=32 height=32></img>\n" +
                    "  <div>\n" +
                    "    Erro ao reservar assento!\n" +
                    "  </div>\n" +
                    "</div>";
            html = html.replace("<!-- MENSAGEM -->", mensagem);
        }

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
}
