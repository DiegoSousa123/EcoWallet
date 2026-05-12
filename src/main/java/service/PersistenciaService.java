package service;

import model.Categoria;
import model.Transacao;
import factory.TransacaoFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
                double valor = (double) json.get("valor");
                String descricao = (String) json.get("descricao");
                Categoria categoria = Categoria.valueOf((String) json.get("categoria"));
                lista.add(TransacaoFactory.criar(tipo, valor, descricao, categoria));
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return lista;
    }
}