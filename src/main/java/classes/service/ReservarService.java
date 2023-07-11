package classes.service;

import classes.server.Server;
import classes.model.Assento;

import java.util.Map;

public class ReservarService {

    public boolean isOcupado(Map<String, String> query) {
        int id = Integer.parseInt(query.get("id"));
        Assento assento = Server.assentos.get(id);
        return assento.isOcupado();
    }

}
