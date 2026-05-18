package factory;

import java.time.LocalDate;
import model.Categoria;
import model.Despesa;
import model.Receita;
import model.TipoTransacao;
import model.Transacao;

/**
 * Factory para criação polimórfica de transações.
 * Isola o Controller e o DAO do conhecimento de subclasses concretas.
 */
public class TransacaoFactory {

    private TransacaoFactory() { /* utilitária — não instanciar */ }

    public static Transacao criar(TipoTransacao tipo, double valor,
                                  String descricao, Categoria categoria,
                                  LocalDate data) {
        return switch (tipo) {
            case RECEITA -> new Receita(valor, descricao, categoria, data);
            case DESPESA -> new Despesa(valor, descricao, categoria, data);
        };
    }
}