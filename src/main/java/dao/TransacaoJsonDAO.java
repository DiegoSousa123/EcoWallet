package dao;

import factory.TransacaoFactory;
import model.Categoria;
import model.TipoTransacao;
import model.Transacao;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do DAO de transações usando JSON (json-simple).
 *
 * Correções aplicadas no merge:
 * - Escrita agora usa OutputStreamWriter com UTF-8 explícito.
 * - Import desnecessário de FileAlreadyExistsException removido.
 * - Logs de System.out removidos da produção (apenas erros via System.err).
 * - try-with-resources adicionado no método salvar().
 */
public class TransacaoJsonDAO implements TransacaoDAO {

    private static final String FILE_PATH = "transacoes.json";

    @Override
    @SuppressWarnings("unchecked")
    public void salvar(List<Transacao> transacoes) {
        JSONArray jsonArray = new JSONArray();

        for (Transacao t : transacoes) {
            JSONObject obj = new JSONObject();
            obj.put("tipo",      t.getTipo().name());      // persiste o name() do enum, não o toString()
            obj.put("descricao", t.getDescricao());
            obj.put("valor",     t.getValor());
            obj.put("data",      t.getData().toString());  // ISO-8601: yyyy-MM-dd
            obj.put("categoria", t.getCategoria().name()); // persiste o name() do enum
            jsonArray.add(obj);
        }

        File arquivo = new File(FILE_PATH);
        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream(arquivo), StandardCharsets.UTF_8)) {
            writer.write(jsonArray.toJSONString());
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo JSON: " + e.getMessage());
        }
    }

    @Override
    public List<Transacao> carregar() {
        List<Transacao> transacoes = new ArrayList<>();
        File arquivo = new File(FILE_PATH);

        if (!arquivo.exists()) {
            // Primeira execução — arquivo ainda não existe, retorna lista vazia
            return transacoes;
        }

        JSONParser parser = new JSONParser();
        try (Reader reader = new InputStreamReader(
                new FileInputStream(arquivo), StandardCharsets.UTF_8)) {

            Object parsed = parser.parse(reader);
            JSONArray jsonArray = (JSONArray) parsed;

            for (Object item : jsonArray) {
                JSONObject jsonObj = (JSONObject) item;

                TipoTransacao tipo      = TipoTransacao.valueOf((String) jsonObj.get("tipo"));
                String        descricao = (String) jsonObj.get("descricao");
                double        valor     = ((Number) jsonObj.get("valor")).doubleValue();
                LocalDate     data      = LocalDate.parse((String) jsonObj.get("data"));
                Categoria     categoria = Categoria.valueOf((String) jsonObj.get("categoria"));

                Transacao t = TransacaoFactory.criar(tipo, valor, descricao, categoria, data);
                if (t != null) transacoes.add(t);
            }

        } catch (IOException e) {
            System.err.println("Erro de leitura em " + FILE_PATH + ": " + e.getMessage());
        } catch (ParseException e) {
            System.err.println("Erro de parse JSON em " + FILE_PATH + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado ao carregar transações: " + e.getMessage());
        }

        return transacoes;
    }
}