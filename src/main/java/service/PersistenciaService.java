package service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import factory.TransacaoFactory;
import model.Categoria;
import model.Transacao;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class PersistenciaService {
    private static final String ARQUIVO = "transacoes.json";

    public void salvar(List<Transacao> transacoes) {
        JSONArray array = new JSONArray();
        for (Transacao t : transacoes) {
            JSONObject obj = new JSONObject();
            obj.put("tipo", t.tipoTransacao());
            obj.put("valor", t.getValor());
            obj.put("descricao", t.getDescricao());
            obj.put("categoria", t.getCategoria().name());
            obj.put("data", t.getData().toString());
            array.add(obj);
        }
        try (FileWriter writer = new FileWriter(ARQUIVO)) {
            writer.write(array.toJSONString());
        } catch (IOException e) {
            System.out.println("Erro ao salvar: " + e.getMessage());
        }
    }

    public List<Transacao> carregar() {
        List<Transacao> lista = new ArrayList<>();
        try (FileReader reader = new FileReader(ARQUIVO)) {
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(reader);
            for (Object obj : array) {
                JSONObject json = (JSONObject) obj;
                String tipo = (String) json.get("tipo");
                double valor = ((Number) json.get("valor")).doubleValue();
                String descricao = (String) json.get("descricao");
                Categoria categoria = Categoria.valueOf((String) json.get("categoria"));
                String dataTexto = (String) json.get("data");
                LocalDate data = dataTexto == null ? LocalDate.now() : LocalDate.parse(dataTexto);
                lista.add(TransacaoFactory.criar(tipo, valor, descricao, data, categoria));
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return lista;
    }
}
