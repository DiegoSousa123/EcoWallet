package factory;

import java.time.LocalDate;

import model.Categoria;
import model.Despesa;
import model.Receita;
import model.Transacao;

public class TransacaoFactory {

    public static Transacao criar(String tipo, double valor, String descricao, Categoria categoria) {
        return criar(tipo, valor, descricao, LocalDate.now(), categoria);
    }

    public static Transacao criar(String tipo, double valor, String descricao, LocalDate data, Categoria categoria) {
        if (tipo.equalsIgnoreCase("Receita")) {
            return new Receita(valor, descricao, categoria, data);
        } else if (tipo.equalsIgnoreCase("Despesa")) {
            return new Despesa(valor, descricao, categoria, data);
        } else {
            throw new IllegalArgumentException("Tipo inválido: " + tipo);
        }
    }
}
