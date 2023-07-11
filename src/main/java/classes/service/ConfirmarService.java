package classes.service;

import java.util.Map;

public class ConfirmarService {

    public boolean isCampoVazio(Map<String, String> query){
        String nome = query.get("nome");
        String dataHora = query.get("data_hora");
        if (nome == null || nome.isEmpty() || dataHora == null || dataHora.isEmpty()) {
            return true;
        }
        return false;
    }
}
