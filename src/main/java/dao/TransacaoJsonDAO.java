package dao;
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
import org.json.simple.parser.ParseException;

public class TransacaoJsonDAO implements TransacaoDAO {

    private static final String FILE_PATH = "transacoes.json";

    @Override
    @SuppressWarnings("unchecked")
    public void salvar(List<Transacao> transacoes) {
        JSONArray jsonArray = new JSONArray();

        for (Transacao t : transacoes) {
            JSONObject obj = new JSONObject();

            // O tipo da classe é armazenado para permitir a recriação correta via Factory
            obj.put("tipo", t.getClass().getSimpleName());
            obj.put("descricao", t.getDescricao());
            obj.put("valor", t.getValor());
            obj.put("data", t.getData().toString());
            obj.put("categoria", t.getCategoria().name());

            jsonArray.add(obj);
        }

        try (FileWriter file = new FileWriter(FILE_PATH)) {
            file.write(jsonArray.toJSONString());
            file.flush();
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo JSON: " + e.getMessage());
        }
    }

    @Override
    public List<Transacao> carregar() {
        List<Transacao> transacoes = new ArrayList<>();
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(FILE_PATH)) {
            Object obj = parser.parse(reader);
            JSONArray jsonArray = (JSONArray) obj;

            for (Object item : jsonArray) {
                JSONObject jsonObj = (JSONObject) item;

                String tipo = (String) jsonObj.get("tipo");
                String descricao = (String) jsonObj.get("descricao");
                double valor = ((Number) jsonObj.get("valor")).doubleValue();
                LocalDate data = LocalDate.parse((String) jsonObj.get("data"));
                Categoria categoria = Categoria.valueOf((String) jsonObj.get("categoria"));

                // DELEGAÇÃO: O DAO aciona a Factory para criar o objeto correto
                Transacao transacao = TransacaoFactory.criar(tipo, valor, descricao, data, categoria);

                if (transacao != null) {
                    transacoes.add(transacao);
                }
            }
        } catch (IOException e) {
            System.out.println("Arquivo transacoes.json não encontrado. Uma nova base será criada no primeiro salvamento.");
        } catch (ParseException e) {
            System.err.println("Erro ao realizar o parse do JSON: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado ao carregar transações: " + e.getMessage());
        }

        return transacoes;
    }
}